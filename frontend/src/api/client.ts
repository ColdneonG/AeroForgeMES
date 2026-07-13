/** 统一通过网关访问后端；开发环境由 Vite 将 /api 代理到 8080。 */
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

export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('fanmes.accessToken')
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
