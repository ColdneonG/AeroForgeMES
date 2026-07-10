import request from '@/services/request'

export function getQualityInspections(params) {
  return request.get('/quality/inspection-orders', { params })
}

export function getInspectionDetail(id) {
  return request.get(`/quality/inspection-orders/${id}`)
}

export function submitInspection(data) {
  return request.post('/quality/inspection-orders', data)
}

export function getDefectRecords(params) {
  return request.get('/quality/defect-records', { params })
}

export function getInspectionResults(params) {
  return request.get('/quality/inspection-results', { params })
}

export function startInspection(id, data = {}) {
  return request.post(`/quality/inspection-orders/${id}/start`, data)
}

export function passInspection(id, data = {}) {
  return request.post(`/quality/inspection-orders/${id}/pass`, data)
}

export function failInspection(id, data = {}) {
  return request.post(`/quality/inspection-orders/${id}/fail`, data)
}

export function closeInspection(id, data = {}) {
  return request.post(`/quality/inspection-orders/${id}/close`, data)
}

export function voidInspection(id, data = {}) {
  return request.post(`/quality/inspection-orders/${id}/void`, data)
}
