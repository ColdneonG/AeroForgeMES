import request from '@/services/request'

export function getManufacturingDashboard(params) {
  return request.get('/report/dashboard/manufacturing', { params })
}

export function getControlCenterBoard(params) {
  return request.get('/report/boards/control-center', { params })
}

export function getLineBoard(params) {
  return request.get('/report/boards/line', { params })
}

export function getWorkshopBoard(params) {
  return request.get('/report/boards/workshop', { params })
}
