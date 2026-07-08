import request from '@/services/request'

export function getProcessRows(params) {
  // TODO backend: provide process/routing master data through mes-gateway.
  return request.get('/process/routes', { params })
}
