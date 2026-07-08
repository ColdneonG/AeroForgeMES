import request from '@/services/request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function getUserInfo() {
  return request.get('/auth/me')
}

export function getPermissions() {
  return request.get('/auth/me/permissions')
}

export function logout() {
  return request.post('/auth/logout')
}
