import { post } from './client'

export interface LoginUser {
  accessToken: string
  tokenType: string
  userId: number
  username: string
  displayName: string
  roles: string[]
  permissions: string[]
}

const USER_KEY = 'fanmes.user'

export async function login(username: string, password: string) {
  const user = await post<LoginUser>('/auth/login', { username, password })
  localStorage.setItem('fanmes.accessToken', user.accessToken)
  localStorage.setItem(USER_KEY, JSON.stringify(user))
  return user
}

export function logout() {
  localStorage.removeItem('fanmes.accessToken')
  localStorage.removeItem(USER_KEY)
}

export function currentUser(): LoginUser | null {
  try { return JSON.parse(localStorage.getItem(USER_KEY) || 'null') as LoginUser | null } catch { return null }
}
