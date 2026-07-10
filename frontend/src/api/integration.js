import request from '@/services/request'

export function getSyncLogs(params) {
  return request.get('/integration/sync-logs', { params })
}

export function getErpSyncLogs(params) {
  return request.get('/integration/erp/sync-logs', { params })
}

export function retrySyncLog(id) {
  return request.post(`/integration/sync-logs/${id}/retry`)
}
