import request from '@/services/request'

export function getPieceworkWages(params) {
  // TODO backend: provide the wage service route through mes-gateway.
  return request.get('/wage/piecework', { params })
}
