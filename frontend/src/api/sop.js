import request from '@/services/request'

export function listSopDocuments(params) {
  return request.get('/production/sop/documents', { params })
}

export function getSopDocument(id) {
  return request.get(`/production/sop/documents/${id}`)
}

export function createSopDocument(data) {
  return request.post('/production/sop/documents', data)
}

export function updateSopDocument(id, data) {
  return request.put(`/production/sop/documents/${id}`, data)
}

export function listSopVersions(sopId) {
  return request.get(`/production/sop/documents/${sopId}/versions`)
}

export function createSopVersion(sopId, data) {
  return request.post(`/production/sop/documents/${sopId}/versions`, data)
}

export function copySopVersion(versionId, data) {
  return request.post(`/production/sop/versions/${versionId}/copy`, data)
}

export function submitSopVersion(versionId, data = {}) {
  return request.post(`/production/sop/versions/${versionId}/submit`, data)
}

export function rejectSopVersion(versionId, data = {}) {
  return request.post(`/production/sop/versions/${versionId}/reject`, data)
}

export function approveSopVersion(versionId, data = {}) {
  return request.post(`/production/sop/versions/${versionId}/approve`, data)
}

export function publishSopVersion(versionId, data = {}) {
  return request.post(`/production/sop/versions/${versionId}/publish`, data)
}

export function disableSopVersion(versionId, data = {}) {
  return request.post(`/production/sop/versions/${versionId}/disable`, data)
}

export function listSopSteps(versionId) {
  return request.get(`/production/sop/versions/${versionId}/steps`)
}

export function createSopStep(versionId, data) {
  return request.post(`/production/sop/versions/${versionId}/steps`, data)
}

export function updateSopStep(stepId, data) {
  return request.put(`/production/sop/steps/${stepId}`, data)
}

export function deleteSopStep(stepId) {
  return request.delete(`/production/sop/steps/${stepId}`)
}

export function listSopAttachments(versionId) {
  return request.get(`/production/sop/versions/${versionId}/attachments`)
}

export function uploadSopAttachment(versionId, formData) {
  return request.post(`/production/sop/versions/${versionId}/attachments`, formData)
}

export function uploadSopModel(versionId, formData) {
  return request.post(`/production/sop/versions/${versionId}/models`, formData)
}

export function listSopBindings(versionId) {
  return request.get(`/production/sop/versions/${versionId}/bindings`)
}

export function createSopBinding(versionId, data) {
  return request.post(`/production/sop/versions/${versionId}/bindings`, data)
}

export function deleteSopBinding(bindingId) {
  return request.delete(`/production/sop/bindings/${bindingId}`)
}

export function lockTaskSop(taskId, params) {
  return request.post(`/production/sop/tasks/${taskId}/lock`, null, { params })
}

export function openTaskSop(taskId, params) {
  return request.get(`/production/sop/tasks/${taskId}`, { params })
}

export function viewSopStep(snapshotId, stepId, params) {
  return request.post(`/production/sop/snapshots/${snapshotId}/steps/${stepId}/view`, null, { params })
}

export function confirmSopStep(snapshotId, stepId, data) {
  return request.post(`/production/sop/snapshots/${snapshotId}/steps/${stepId}/confirm`, data)
}

export function validateSopBeforeReport(taskId) {
  return request.get(`/production/sop/tasks/${taskId}/report-validation`)
}
