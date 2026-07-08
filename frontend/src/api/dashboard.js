import request from '@/services/request'

export function getManufacturingDashboard(params) {
  return request.get('/dashboard/manufacturing', { params })
}

export function getControlCenterBoard(params) {
  return request.get('/boards/control-center', { params })
}

export function getLineBoard(params) {
  return request.get('/boards/line', { params })
}

export function getWorkshopBoard(params) {
  return request.get('/boards/workshop', { params })
}
