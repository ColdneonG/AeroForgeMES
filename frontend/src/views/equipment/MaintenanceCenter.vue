<template>
  <CrudBoard
    eyebrow="设备管理"
    title="点检、保养、报修与维修"
    description="覆盖点检提交、保养执行、异常转报修、维修接单和维修完成。"
    list-title="维护任务"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="equipment"
    :row-actions="rowActions"
  />
  <p v-if="loading" class="api-state">Loading maintenance tasks...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { getMaintenanceOrders, getMaintenancePlans } from '../../api/equipment'

const rows = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapMaintenance = (row, type) => ({
  id: row.maintenanceNo || row.maintenance_no || row.inspectionNo || row.inspection_no || row.repairNo || row.repair_no || row.id,
  equipment: row.equipmentName || row.equipment_name || row.equipmentId || row.equipment_id || '-',
  type,
  owner: row.assignedTo || row.assigned_to || row.inspectorId || row.inspector_id || row.reportedBy || row.reported_by || '-',
  due: row.planAt || row.plan_at || row.inspectionAt || row.inspection_at || row.reportedAt || row.reported_at || '-',
  status: row.status || row.result || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    const [tasks, plans] = await Promise.all([getMaintenanceOrders(), getMaintenancePlans()])
    rows.value = [
      ...recordsOf(tasks).map((row) => mapMaintenance(row, 'Maintenance')),
      ...recordsOf(plans).map((row) => mapMaintenance(row, 'Inspection'))
    ]
  } catch (e) {
    rows.value = []
    error.value = e?.message || 'Data loading failed. Please check backend API or gateway configuration.'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: '任务号' },
  { key: 'equipment', label: '设备' },
  { key: 'type', label: '类型' },
  { key: 'owner', label: '处理人' },
  { key: 'due', label: '计划时间' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '接单', action: 'acceptRepair' },
  { label: '完成维修', action: 'finishRepair' },
  { label: '关闭', action: 'close' },
  { label: '审计', action: 'audit' }
]

onMounted(loadRows)
</script>

<style scoped>
.api-state {
  margin: 12px 24px 0;
  color: #52616b;
  font-size: 14px;
}

.api-state.error {
  color: #b42318;
}
</style>
