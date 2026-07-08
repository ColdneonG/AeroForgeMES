import request from '@/services/request'

export function getProductionReport(params) {
  return request.get('/report/production', { params })
}

export function getQualityReport(params) {
  return request.get('/report/quality', { params })
}

export function getEquipmentReport(params) {
  return request.get('/report/equipment', { params })
}

export function getReport(path, params) {
  return request.get(`/report/${path}`, { params })
}
