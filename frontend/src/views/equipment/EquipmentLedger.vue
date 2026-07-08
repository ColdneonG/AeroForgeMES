<template>
  <CrudBoard
    eyebrow="设备管理"
    title="设备台账"
    description="维护设备类别、制造商、使用部门、管理部门和当前设备状态。"
    list-title="设备台账"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="equipment"
    :row-actions="rowActions"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { equipmentLedger } from '../../mock/mesExtendedData'
import { mesApi } from '../../services/mesApi'

const useMock = import.meta.env.VITE_USE_MOCK === 'true'
const rows = ref(equipmentLedger)

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || [])

const mapEquipment = (row) => ({
  id: row.equipmentCode || row.equipment_code || row.id,
  type: row.categoryName || row.category_name || (row.categoryId || row.category_id || '-'),
  maker: row.manufacturerName || row.manufacturer_name || (row.manufacturerId || row.manufacturer_id || '-'),
  dept: row.departmentName || row.department_name || (row.lineId || row.line_id || '-'),
  manager: row.managerName || row.manager_name || (row.stationId || row.station_id || '-'),
  status: row.equipmentStatus || row.equipment_status || row.status || '-',
  raw: row
})

const loadRows = async () => {
  if (useMock) return
  rows.value = recordsOf(await mesApi.equipment.equipments()).map(mapEquipment)
}

const columns = [
  { key: 'id', label: '设备编号' },
  { key: 'type', label: '类别' },
  { key: 'maker', label: '制造商' },
  { key: 'dept', label: '使用部门' },
  { key: 'manager', label: '管理部门' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '编辑', action: 'edit' },
  { label: '点检', action: 'inspect' },
  { label: '报修', action: 'repair' },
  { label: '停用', action: 'disable' },
  { label: '审计', action: 'audit' }
]

onMounted(loadRows)
</script>
