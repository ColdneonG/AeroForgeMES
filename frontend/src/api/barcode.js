import request from '@/services/request'

export function getBarcodeRules(params) {
  return request.get('/production/barcodes/rules', { params })
}

export function getBarcodeRecords(params) {
  return request.get('/production/barcodes', { params })
}

export function generateBarcode(data) {
  return request.post('/production/barcodes/generate', data)
}

export function traceBarcode(barcode) {
  return request.get(`/production/trace/${barcode}`)
}
