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
