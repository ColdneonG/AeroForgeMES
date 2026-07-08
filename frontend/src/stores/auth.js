import { reactive, computed } from 'vue'

const TOKEN_KEY = 'fan-mes-token'
const USER_KEY = 'fan-mes-user'
const PERMISSIONS_KEY = 'fan-mes-permissions'

const readJson = (key, fallback) => {
  if (typeof window === 'undefined') return fallback

  try {
    const raw = window.localStorage.getItem(key)
    return raw ? JSON.parse(raw) : fallback
  } catch {
    return fallback
  }
}

export const authState = reactive({
  token: typeof window !== 'undefined' ? window.localStorage.getItem(TOKEN_KEY) || '' : '',
  user: readJson(USER_KEY, null),
  permissions: readJson(PERMISSIONS_KEY, {
    menus: [],
    buttons: [],
    apis: [],
    dataScopes: []
  }),
  lastError: ''
})

export const isAuthenticated = computed(() => Boolean(authState.token && authState.user))

export const setAuthSession = ({ token, user, permissions }) => {
  authState.token = token
  authState.user = user
  authState.permissions = permissions
  authState.lastError = ''

  if (typeof window !== 'undefined') {
    window.localStorage.setItem(TOKEN_KEY, token)
    window.localStorage.setItem(USER_KEY, JSON.stringify(user))
    window.localStorage.setItem(PERMISSIONS_KEY, JSON.stringify(permissions))
  }
}

export const clearAuthSession = () => {
  authState.token = ''
  authState.user = null
  authState.permissions = { menus: [], buttons: [], apis: [], dataScopes: [] }

  if (typeof window !== 'undefined') {
    window.localStorage.removeItem(TOKEN_KEY)
    window.localStorage.removeItem(USER_KEY)
    window.localStorage.removeItem(PERMISSIONS_KEY)
  }
}

export const hasPermission = (permission) => {
  if (!permission) return true
  if (authState.user?.superAdmin) return true
  return authState.permissions.menus.includes(permission)
}

export const hasButtonPermission = (permission) => {
  if (!permission) return true
  if (authState.user?.superAdmin) return true
  return authState.permissions.buttons.includes(permission)
}

export const hasApiPermission = (permission) => {
  if (!permission) return true
  if (authState.user?.superAdmin) return true
  return authState.permissions.apis.includes(permission)
}
