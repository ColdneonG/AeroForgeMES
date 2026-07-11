import request from '@/services/request'

export const getBarcodeTypes = (params) => request.get('/production/barcodes/types', { params })
export const getBarcodeItems = (params) => request.get('/production/barcodes/items', { params })
export const getBarcodeTemplates = (params) => request.get('/production/barcodes/templates', { params })

export const getBarcodeRules = (params) => request.get('/production/barcodes/rules', { params })
export const getBarcodeRule = (id) => request.get(`/production/barcodes/rules/${id}`)
export const createBarcodeRule = (data) => request.post('/production/barcodes/rules', data)
export const updateBarcodeRule = (id, data) => request.put(`/production/barcodes/rules/${id}`, data)
export const deleteBarcodeRule = (id) => request.delete(`/production/barcodes/rules/${id}`)
export const enableBarcodeRule = (id, data = {}) => request.post(`/production/barcodes/rules/${id}/enable`, data)
export const disableBarcodeRule = (id, data = {}) => request.post(`/production/barcodes/rules/${id}/disable`, data)

export const getBarcodeApplicationRules = (params) => request.get('/production/barcodes/application-rules', { params })
export const createBarcodeApplicationRule = (data) => request.post('/production/barcodes/application-rules', data)
export const updateBarcodeApplicationRule = (id, data) => request.put(`/production/barcodes/application-rules/${id}`, data)
export const deleteBarcodeApplicationRule = (id) => request.delete(`/production/barcodes/application-rules/${id}`)

export const getBarcodeRecords = (params) => request.get('/production/barcodes', { params })
export const getBarcodeRecord = (id) => request.get(`/production/barcodes/${id}`)
export const generateBarcode = (data) => request.post('/production/barcodes/generate', data)
export const printBarcode = (id, data) => request.post(`/production/barcodes/${id}/print`, data)
export const batchPrintBarcodes = (data) => request.post('/production/barcodes/print', data)
export const getBarcodePrintRecords = (id) => request.get(`/production/barcodes/${id}/print-records`)
export const getBarcodeEvents = (id) => request.get(`/production/barcodes/${id}/events`)
export const scanBarcode = (data) => request.post('/production/barcodes/scan', data)
export const bindBarcodeParent = (id, data) => request.post(`/production/barcodes/${id}/parent`, data)
export const closeBarcode = (id, data = {}) => request.post(`/production/barcodes/${id}/close`, data)
export const voidBarcode = (id, data = {}) => request.post(`/production/barcodes/${id}/void`, data)

export const traceBarcode = (keyword, mode = 'SN') => request.get(`/production/trace/${encodeURIComponent(keyword)}`, {
  params: { mode }
})
