import request from './http'

export const mesApi = {
  production: {
    listWorkOrders: (params) => request.get('/api/production/work-orders', { params }),
    createWorkOrder: (data) => request.post('/api/production/work-orders', data),
    releaseWorkOrder: (id, data) => request.post(`/api/production/work-orders/${id}/issue`, data),
    listKittingAnalyses: (params) => request.get('/api/production/kitting-analyses', { params }),
    listKittingMissingItems: (params) => request.get('/api/production/kitting-missing-items', { params }),
    listKittingMissingBoard: () => request.get('/api/production/kitting-missing-board'),
    completeKittingAnalysis: (id, data) => request.post(`/api/production/kitting-analyses/${id}/complete`, data),
    closeKittingAnalysis: (id, data) => request.post(`/api/production/kitting-analyses/${id}/close`, data),
    voidKittingAnalysis: (id, data) => request.post(`/api/production/kitting-analyses/${id}/void`, data),
    listDispatchOrders: (params) => request.get('/api/production/dispatch-orders', { params }),
    createDispatchOrder: (data) => request.post('/api/production/dispatch-orders', data),
    releaseDispatchOrder: (id, data) => request.post(`/api/production/dispatch-orders/${id}/issue`, data),
    closeDispatchOrder: (id, data) => request.post(`/api/production/dispatch-orders/${id}/close`, data),
    voidDispatchOrder: (id, data) => request.post(`/api/production/dispatch-orders/${id}/void`, data),
    createTaskFromDispatch: (id, data) => request.post(`/api/production/dispatch-orders/${id}/tasks`, data),
    listTasks: (params) => request.get('/api/production/tasks', { params }),
    startTask: (id, data) => request.post(`/api/production/tasks/${id}/start`, data),
    pauseTask: (id, data) => request.post(`/api/production/tasks/${id}/pause`, data),
    report: (data) => request.post('/api/production/reports', data),
    complete: (data) => request.post('/api/production/completions', data),
    generateBarcodes: (data) => request.post('/api/production/barcodes/generate', data),
    trace: (barcode) => request.get(`/api/production/trace/${barcode}`)
  },
  system: {
    users: (params) => request.get('/api/system/users', { params }),
    roles: (params) => request.get('/api/system/roles', { params }),
    menus: () => request.get('/api/system/menus/tree'),
    permissions: () => request.get('/api/system/permissions'),
    dataScopes: () => request.get('/api/system/data-scopes')
  },
  equipment: {
    equipments: (params) => request.get('/api/equipment/equipments', { params }),
    inspections: (params) => request.get('/api/equipment/inspections', { params }),
    maintenances: (params) => request.get('/api/equipment/maintenances', { params }),
    repairs: (data) => request.post('/api/equipment/repairs', data),
    finishRepair: (id, data) => request.post(`/api/equipment/repairs/${id}/complete`, data),
    andonExceptions: (params) => request.get('/api/equipment/andon/exceptions', { params }),
    createAndon: (data) => request.post('/api/equipment/andon/exceptions', data),
    closeAndon: (id, data) => request.post(`/api/equipment/andon/exceptions/${id}/close`, data)
  },
  quality: {
    inspections: (params) => request.get('/api/quality/inspection-orders', { params }),
    inspectionResults: (params) => request.get('/api/quality/inspection-results', { params }),
    defects: (params) => request.get('/api/quality/defect-records', { params })
  },
  report: {
    get: (path, params) => request.get(`/api/report/${path}`, { params })
  },
  integration: {
    logs: (params) => request.get('/api/integration/sync-logs', { params })
  }
}
