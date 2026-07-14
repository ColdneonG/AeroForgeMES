import { post } from './client'
import { clearSession, startSession } from './session'

export interface LoginUser {
  accessToken: string
  tokenType: string
  userId: number
  username: string
  displayName: string
  roles: string[]
  permissions: string[]
}

let activeUser: LoginUser | null = null

export async function login(username: string, password: string) {
  const user = await post<LoginUser>('/auth/login', { username, password })
  startSession(user.accessToken)
  activeUser = user
  return user
}

export function logout() {
  clearSession()
  activeUser = null
}

export function currentUser(): LoginUser | null {
  return activeUser
}
