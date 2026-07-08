import request from './http'
import { mockLogin, mockPermissions, mockUser } from '../mock/mesExtendedData'

const useMock = import.meta.env.VITE_USE_MOCK === 'true'

const adaptPermissions = (session) => {
  const rawPermissions = Array.isArray(session?.permissions) ? session.permissions : []
  return {
    menus: rawPermissions,
    buttons: rawPermissions,
    apis: rawPermissions.filter((item) => String(item).startsWith('/api/')),
    dataScopes: ['全部数据']
  }
}

const adaptLoginSession = (session) => {
  if (!session) return session

  // 标准 JWT 格式：{ accessToken, username, ... }
  if (session.accessToken) {
    const roles = Array.isArray(session.roles) ? session.roles : []
    return {
      token: session.accessToken,
      user: {
        userId: session.userId,
        username: session.username,
        displayName: session.displayName || session.username,
        roles,
        superAdmin: session.username === 'admin' || roles.includes('admin') || roles.includes('ADMIN')
      },
      permissions: adaptPermissions(session)
    }
  }

  // 扁平格式：{ token, user, permissions }
  // 确保 user 不为空，防止 isAuthenticated 判定失败
  return {
    token: session.token || '',
    user: session.user || { username: 'unknown', displayName: '未知用户', roles: [], superAdmin: false },
    permissions: session.permissions || adaptPermissions(session)
  }
}

export const login = async (credentials) => {
  if (useMock) return mockLogin(credentials)
  return adaptLoginSession(await request.post('/api/auth/login', credentials))
}

export const logout = async () => {
  if (useMock) return { success: true }
  return request.post('/api/auth/logout')
}

export const getCurrentUser = async () => {
  if (useMock) return mockUser
  const session = await request.get('/api/auth/me')
  return adaptLoginSession(session)?.user || session
}

export const getCurrentPermissions = async () => {
  if (useMock) return mockPermissions
  const session = await request.get('/api/auth/me/permissions')
  return adaptPermissions(session)
}
