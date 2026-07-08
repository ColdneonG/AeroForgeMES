import request from '@/services/request'

export function getUsers(params) {
  return request.get('/system/users', { params })
}

export function getRoles(params) {
  return request.get('/system/roles', { params })
}

export function getMenus(params) {
  return request.get('/system/menus/tree', { params })
}

export function getPermissions(params) {
  return request.get('/system/permissions', { params })
}
