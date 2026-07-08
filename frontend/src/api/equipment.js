import request from '@/services/request'

export function getEquipmentLedgers(params) {
  return request.get('/equipment/equipments', { params })
}

export function getEquipmentDetail(id) {
  return request.get(`/equipment/equipments/${id}`)
}

export function getMaintenanceOrders(params) {
  return request.get('/equipment/maintenance-tasks', { params })
}

export function getMaintenancePlans(params) {
  return request.get('/equipment/inspections', { params })
}
