import request from '@/services/request'

export function getProductionReport(params) {
  return request.get('/report/production-output', { params })
}

export function getDailyOutputReport(params) {
  return request.get('/report/production-output/daily', { params })
}

export function getMetricData(metricCode, params) {
  return request.get(`/report/metric-data/${metricCode}`, { params })
}

export function getQualityReport(params) {
  // TODO backend: provide quality report aggregate through mes-gateway.
  return request.get('/report/quality', { params })
}

export function getEquipmentReport(params) {
  // TODO backend: provide equipment report aggregate through mes-gateway.
  return request.get('/report/equipment', { params })
}

export function getReport(path, params) {
  return request.get(`/report/${path}`, { params })
}

export function getMetricDefs(params) {
  return request.get('/report/metrics', { params })
}

export function getMetricSnapshots(params) {
  return request.get('/report/metric-snapshots', { params })
}

export function getBoardConfigs(params) {
  return request.get('/report/boards', { params })
}
