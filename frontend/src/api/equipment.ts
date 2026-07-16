import { get } from './client'

export type EquipmentRecord = Record<string, unknown> & { id: number }
type EquipmentPage = { records?: EquipmentRecord[] }

const legacyRuntimeStatuses: Record<string, string> = {
  '正常': 'IDLE',
  '待保养': 'IDLE',
  '运行中': 'RUNNING',
  '待机': 'IDLE',
  '停机': 'IDLE',
  '故障': 'FAULT',
  '报警': 'FAULT',
}

export const runtimeStatusLabels: Record<string, string> = {
  RUNNING: '运行中',
  IDLE: '待机 / 停机',
  FAULT: '故障 / 报警',
}

export const maintenanceStatusLabels: Record<string, string> = {
  NORMAL: '正常',
  DUE: '待保养',
  IN_PROGRESS: '保养中',
}

function camelCase(key: string) {
  return key.replace(/_([a-z])/g, (_, letter: string) => letter.toUpperCase())
}

export function normalizeEquipmentRecord(row: EquipmentRecord): EquipmentRecord {
  const normalized = Object.entries(row).reduce<EquipmentRecord>((result, [key, value]) => {
    result[key] = value
    result[camelCase(key)] = value
    return result
  }, { id: row.id })
  const runtimeStatus = String(normalized.equipmentStatus || '').toUpperCase()
  normalized.equipmentStatus = legacyRuntimeStatuses[runtimeStatus] || runtimeStatus || 'IDLE'
  normalized.maintenanceStatus = String(normalized.maintenanceStatus || 'NORMAL').toUpperCase()
  return normalized
}

export function runtimeStatusLabel(value: unknown) {
  return runtimeStatusLabels[String(value || '').toUpperCase()] || '状态异常'
}

export function maintenanceStatusLabel(value: unknown) {
  return maintenanceStatusLabels[String(value || '').toUpperCase()] || '状态异常'
}

export async function getEquipmentRecords() {
  const response = await get<EquipmentPage | EquipmentRecord[]>('/equipment/equipments', { size: 200 })
  const records = Array.isArray(response) ? response : response.records || []
  return records.map(normalizeEquipmentRecord)
}
