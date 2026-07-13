import { get, post } from './client'

export type QualityRecord = Record<string, unknown> & { id?: number | string }

export function getQualityRecords(path: string, query?: Record<string, string | number | undefined>) {
  return get<QualityRecord[]>(path, query)
}

export function postQualityAction(path: string, payload: Record<string, unknown> = {}) {
  return post<QualityRecord>(path, payload)
}
