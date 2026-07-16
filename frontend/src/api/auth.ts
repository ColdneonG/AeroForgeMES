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
let restorePromise: Promise<boolean> | null = null

export async function login(username: string, password: string) {
  const user = await post<LoginUser>('/auth/login', { username, password })
  startSession(user.accessToken)
  activeUser = user
  return user
}

export async function restoreSession() {
  if (restorePromise) return restorePromise
  restorePromise = post<LoginUser>('/auth/refresh')
    .then((user) => {
      startSession(user.accessToken)
      activeUser = user
      return true
    })
    .catch(() => false)
    .finally(() => { restorePromise = null })
  return restorePromise
}

export async function logout() {
  try {
    await post<void>('/auth/logout')
  } finally {
    clearSession()
    activeUser = null
  }
}

export function currentUser(): LoginUser | null {
  return activeUser
}
