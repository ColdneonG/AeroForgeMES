import request from '@/services/request'

export function getSyncLogs(params) {
  return request.get('/integration/sync-logs', { params })
}
