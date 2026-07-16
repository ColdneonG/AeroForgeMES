import { get, post } from './client'

export interface WorkOrder {
  id: number
  workOrderNo?: string
  productId?: number
  planQty?: number
  completedQty?: number
  plannedStartAt?: string
  lineId?: number
  status?: string
}

export interface CreateWorkOrderRequest {
  workOrderNo: string
  productId: number
  planQty: number
  lineId: number
  externalNo?: string
  sourceType?: string
  plannedStartAt?: string
  plannedEndAt?: string
  deliveryDate?: string
  routeId?: number
  status?: string
}

export function getWorkOrders(query?: Record<string, string | number | undefined>) {
  return get<WorkOrder[]>('/production/work-orders', query)
}

export function createWorkOrder(data: CreateWorkOrderRequest) {
  return post<WorkOrder>('/production/work-orders', data)
}

export function getWorkOrder(id: string | number) {
  return get<WorkOrder>(`/production/work-orders/${id}`)
}

export function getAndonExceptions() {
  return get<Record<string, unknown>[]>('/andon/exceptions')
}

export interface DashboardMetric { metricKey: string; value: number }
export interface ManufacturingDashboard { gauges: DashboardMetric[] }

export function getManufacturingDashboard() {
  return get<ManufacturingDashboard>('/report/dashboard/manufacturing')
}

export type ResourceRecord = Record<string, unknown> & { id?: number | string }

export function getProductionRecords(path: string, query?: Record<string, string | number | undefined>) {
  return get<ResourceRecord[]>(path, query)
}

export function postProductionAction(path: string, payload: Record<string, unknown> = {}) {
  return post<ResourceRecord>(path, payload)
}
