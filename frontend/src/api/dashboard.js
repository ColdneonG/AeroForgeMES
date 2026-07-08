import request from '@/services/request'

export function getManufacturingDashboard(params) {
  // TODO backend: provide a dashboard aggregation endpoint through mes-gateway.
  return request.get('/dashboard/manufacturing', { params })
}

export function getControlCenterBoard(params) {
  // TODO backend: provide a control-center board aggregation endpoint through mes-gateway.
  return request.get('/boards/control-center', { params })
}

export function getLineBoard(params) {
  // TODO backend: current report service exposes /report/boards/line, not this dashboard route.
  return request.get('/boards/line', { params })
}

export function getWorkshopBoard(params) {
  // TODO backend: current report service exposes /report/boards/workshop, not this dashboard route.
  return request.get('/boards/workshop', { params })
}
