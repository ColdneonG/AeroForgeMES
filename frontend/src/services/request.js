import axios from 'axios'

const TOKEN_KEY = 'fan-mes-token'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 12000
})

request.interceptors.request.use((config) => {
  const token = typeof window !== 'undefined' ? window.localStorage.getItem(TOKEN_KEY) : ''

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

request.interceptors.response.use(
  (response) => {
    const payload = response.data

    if (payload && typeof payload === 'object' && 'code' in payload && String(payload.code) !== '0') {
      const error = new Error(payload.message || 'Request failed')
      error.response = response
      error.payload = payload
      throw error
    }

    return payload?.data ?? payload
  },
  (error) => Promise.reject(error)
)

export default request
