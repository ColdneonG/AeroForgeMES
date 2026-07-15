/** 统一通过网关访问后端；开发环境由 Vite 将 /api 代理到 8080。 */
import { clearSession, getValidAccessToken } from './session'

export type ApiEnvelope<T> = { code: number | string; message?: string; data: T }

const API_BASE = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '')

function isSuccess(code: number | string) {
  return code === 0 || code === '0' || code === 200 || code === '200'
}

export class ApiError extends Error {
  constructor(message: string, public readonly status?: number) {
    super(message)
  }
}

function redirectToLogin() {
  const loginPath = `${import.meta.env.BASE_URL}login`.replace(/\/{2,}/g, '/')
  if (window.location.pathname === loginPath) return

  const redirect = `${window.location.pathname}${window.location.search}${window.location.hash}`
  window.location.replace(`${loginPath}?redirect=${encodeURIComponent(redirect)}`)
}

function endExpiredSession() {
  clearSession()
  redirectToLogin()
}

export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  // Login must remain callable after an expired token is found in local storage.
  const isLoginRequest = path.split('?')[0] === '/auth/login'
  const token = getValidAccessToken()
  if (!isLoginRequest && !token) {
    endExpiredSession()
    throw new ApiError('Login session has expired. Please sign in again.', 401)
  }

  const headers = new Headers(init.headers)
  if (!(init.body instanceof FormData) && init.body !== undefined) headers.set('Content-Type', 'application/json')
  if (token) headers.set('Authorization', `Bearer ${token}`)

  let response: Response
  try {
    response = await fetch(`${API_BASE}${path}`, { ...init, headers })
  } catch {
    throw new ApiError('无法连接后端服务，请确认网关已启动。')
  }
  const body = (await response.json().catch(() => null)) as ApiEnvelope<T> | null
  if (response.status === 401 || body?.code === 401 || body?.code === '401') {
    endExpiredSession()
    throw new ApiError(body?.message || 'Login session has expired. Please sign in again.', response.status)
  }
  if (!response.ok) throw new ApiError(body?.message || `请求失败（${response.status}）`, response.status)
  if (!body || !isSuccess(body.code)) throw new ApiError(body?.message || '请求未成功', response.status)
  return body.data
}

export function get<T>(path: string, query?: Record<string, string | number | undefined>) {
  const params = new URLSearchParams()
  Object.entries(query || {}).forEach(([key, value]) => value !== undefined && value !== '' && params.set(key, String(value)))
  const separator = path.includes('?') ? '&' : '?'
  return request<T>(`${path}${params.size ? `${separator}${params}` : ''}`)
}

export function post<T>(path: string, data?: unknown) {
  return request<T>(path, { method: 'POST', body: data === undefined ? undefined : JSON.stringify(data) })
}

export function put<T>(path: string, data: unknown) {
  return request<T>(path, { method: 'PUT', body: JSON.stringify(data) })
}

export function del<T>(path: string) {
  return request<T>(path, { method: 'DELETE' })
}

/** Sends multipart data without forcing a JSON content type. */
export function upload<T>(path: string, data: FormData) {
  return request<T>(path, { method: 'POST', body: data })
}

/** Reads an authenticated binary response, used by SOP attachments and GLB models. */
export async function getBlob(path: string): Promise<Blob> {
  const token = getValidAccessToken()
  if (!token) {
    endExpiredSession()
    throw new ApiError('Login session has expired. Please sign in again.', 401)
  }
  let response: Response
  try {
    response = await fetch(`${API_BASE}${path}`, { headers: { Authorization: `Bearer ${token}` } })
  } catch {
    throw new ApiError('无法连接后端服务，请确认网关已启动。')
  }
  if (response.status === 401) {
    endExpiredSession()
    throw new ApiError('Login session has expired. Please sign in again.', 401)
  }
  if (!response.ok) throw new ApiError(`请求失败（${response.status}）`, response.status)
  return response.blob()
}
