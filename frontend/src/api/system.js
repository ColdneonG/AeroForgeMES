import request from '@/services/request'

export function getUsers(params) {
  // TODO backend: provide system user listing through mes-gateway.
  return request.get('/system/users', { params })
}

export function getRoles(params) {
  // TODO backend: provide system role listing through mes-gateway.
  return request.get('/system/roles', { params })
}

export function getMenus(params) {
  return request.get('/system/menus/tree', { params })
}

export function getPermissions(params) {
  // TODO backend: provide system permission listing through mes-gateway.
  return request.get('/system/permissions', { params })
}
