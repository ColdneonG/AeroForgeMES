import request from '@/services/request'

export function getAndonExceptions(params) {
  // TODO backend: provide andon exception listing through mes-gateway.
  return request.get('/andon/exceptions', { params })
}
