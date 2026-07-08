import axios from 'axios'
import router from '../router'
import { authState, clearAuthSession } from '../stores/auth'

const request = axios.create({
  baseURL: import.meta.env.VITE_MES_GATEWAY || '',
  timeout: 12000
})

request.interceptors.request.use((config) => {
  if (authState.token) {
    config.headers.Authorization = `Bearer ${authState.token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload && typeof payload === 'object' && 'code' in payload && String(payload.code) !== '0') {
      const error = new Error(payload.message || '业务处理失败')
      error.response = response
      throw error
    }
    return payload?.data ?? payload
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      clearAuthSession()
      router.replace({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
    }
    if (status === 403) {
      router.replace({ name: 'forbidden' })
    }

    authState.lastError =
      error.response?.data?.message || error.message || '网关请求失败，请稍后重试'
    return Promise.reject(error)
  }
)

export default request
