import request from '@/services/request'

export function getWorkOrders(params) {
  return request.get('/production/work-orders', { params })
}

export function getWorkOrderDetail(id) {
  return request.get(`/production/work-orders/${id}`)
}

export function createWorkOrder(data) {
  return request.post('/production/work-orders', data)
}

export function updateWorkOrder(id, data) {
  return request.put(`/production/work-orders/${id}`, data)
}

export function deleteWorkOrder(id) {
  return request.delete(`/production/work-orders/${id}`)
}

export function releaseWorkOrder(id, data = {}) {
  return request.post(`/production/work-orders/${id}/issue`, data)
}

export function confirmWorkOrderIssue(id, data = {}) {
  return request.post(`/production/work-orders/${id}/confirm-issue`, data)
}

export function startWorkOrder(id, data = {}) {
  return request.post(`/production/work-orders/${id}/start`, data)
}

export function pauseWorkOrder(id, data = {}) {
  return request.post(`/production/work-orders/${id}/pause`, data)
}

export function completeWorkOrder(id, data) {
  return request.post(`/production/work-orders/${id}/complete`, data)
}

export function closeWorkOrder(id, data = {}) {
  return request.post(`/production/work-orders/${id}/close`, data)
}

export function voidWorkOrder(id, data = {}) {
  return request.post(`/production/work-orders/${id}/void`, data)
}

export function getDispatchOrders(params) {
  return request.get('/production/dispatch-orders', { params })
}

export function releaseDispatchOrder(id, data = {}) {
  return request.post(`/production/dispatch-orders/${id}/issue`, data)
}

export function confirmDispatchIssue(id, data = {}) {
  return request.post(`/production/dispatch-orders/${id}/confirm-issue`, data)
}

export function startDispatchOrder(id, data = {}) {
  return request.post(`/production/dispatch-orders/${id}/start`, data)
}

export function completeDispatchOrder(id, data = {}) {
  return request.post(`/production/dispatch-orders/${id}/complete`, data)
}

export function closeDispatchOrder(id, data = {}) {
  return request.post(`/production/dispatch-orders/${id}/close`, data)
}

export function voidDispatchOrder(id, data = {}) {
  return request.post(`/production/dispatch-orders/${id}/void`, data)
}

export function getKittingBoard(params) {
  return request.get('/production/kitting-analyses', { params })
}

export function getKittingMissingBoard() {
  return request.get('/production/kitting-missing-board')
}

export function getKittingMissingItems(params) {
  return request.get('/production/kitting-missing-items', { params })
}

export function createTaskFromDispatch(id, data = {}) {
  return request.post(`/production/dispatch-orders/${id}/tasks`, data)
}

export function getShopTasks(params) {
  return request.get('/production/tasks', { params })
}

export function startShopTask(id, data = {}) {
  return request.post(`/production/tasks/${id}/start`, data)
}

export function pauseShopTask(id, data = {}) {
  return request.post(`/production/tasks/${id}/pause`, data)
}

export function resumeShopTask(id, data = {}) {
  return request.post(`/production/tasks/${id}/resume`, data)
}

export function completeShopTask(id, data = {}) {
  return request.post(`/production/tasks/${id}/complete`, data)
}
