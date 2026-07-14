import { get } from './client'

export type EquipmentRecord = Record<string, unknown> & { id: number }
type EquipmentPage = { records?: EquipmentRecord[] }

function camelCase(key: string) {
  return key.replace(/_([a-z])/g, (_, letter: string) => letter.toUpperCase())
}

function normalize(row: EquipmentRecord): EquipmentRecord {
  return Object.entries(row).reduce<EquipmentRecord>((result, [key, value]) => {
    result[key] = value
    result[camelCase(key)] = value
    return result
  }, { id: row.id })
}

export async function getEquipmentRecords() {
  const response = await get<EquipmentPage | EquipmentRecord[]>('/equipment/equipments', { size: 200 })
  const records = Array.isArray(response) ? response : response.records || []
  return records.map(normalize)
}
