<template>
  <CrudBoard
    :eyebrow="t('equipment.maintenance.eyebrow')"
    :title="t('equipment.maintenance.title')"
    :description="t('equipment.maintenance.description')"
    :list-title="t('equipment.maintenance.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="equipment"
    :row-actions="rowActions"
  />
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getMaintenanceOrders, getMaintenancePlans } from '../../api/equipment'

const { t } = useI18n()
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
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: t('tableColumns.taskNoShort') },
  { key: 'equipment', label: t('tableColumns.equipment') },
  { key: 'type', label: t('tableColumns.type') },
  { key: 'owner', label: t('tableColumns.assignee') },
  { key: 'due', label: t('tableColumns.due') },
  { key: 'status', label: t('tableColumns.status') }
]
const rowActions = [
  { label: t('statusFlow.actions.acceptRepair'), action: 'acceptRepair' },
  { label: t('statusFlow.actions.finishRepair'), action: 'finishRepair' },
  { label: t('statusFlow.actions.close'), action: 'close' },
  { label: t('statusFlow.actions.audit'), action: 'audit' }
]

onMounted(loadRows)
</script>

<style scoped>
.api-state {
  margin: 12px 24px 0;
  color: #52616b;
  font-size: 14px;
}
</style>
