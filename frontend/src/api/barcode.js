import request from '@/services/request'

export function getBarcodeRules(params) {
  // TODO backend: provide barcode rule listing through mes-gateway.
  return request.get('/production/barcodes/rules', { params })
}

export function getBarcodeRecords(params) {
  // TODO backend: provide barcode record listing through mes-gateway.
  return request.get('/production/barcodes', { params })
}

export function generateBarcode(data) {
  return request.post('/production/barcodes/generate', data)
}

export function traceBarcode(barcode) {
  return request.get(`/production/trace/${barcode}`)
}
