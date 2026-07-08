import request from '@/services/request'

export function getPieceworkWages(params) {
  return request.get('/wage/piecework', { params })
}
