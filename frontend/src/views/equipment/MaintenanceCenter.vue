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
  >
    <template #detail="{ selected, columns }">
      <dl v-if="selected" class="detail-list">
        <template v-for="column in columns" :key="column.key">
          <dt>{{ column.label }}</dt>
          <dd>{{ selected[column.key] }}</dd>
        </template>
        <dt>{{ t('tableColumns.result') }}</dt>
        <dd>{{ selected.result || '-' }}</dd>
        <dt>{{ t('tableColumns.faultReason') }}</dt>
        <dd>{{ selected.faultReason || '-' }}</dd>
        <dt>{{ t('tableColumns.abnormalDesc') }}</dt>
        <dd>{{ selected.abnormalDesc || '-' }}</dd>
      </dl>
      <div v-else class="empty-detail">{{ t('common.detail.emptyHint') }}</div>
    </template>
  </CrudBoard>
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getMaintenanceOrders, getMaintenancePlans, getRepairOrders, getEquipmentLedgers, getFaultReasons } from '../../api/equipment'
import { getUsers } from '../../api/auth'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')
const userMap = ref({})
const equipmentMap = ref({})
const faultReasonMap = ref({})

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const statusLabel = (status) => {
  if (!status) return '-'
  const key = String(status).toLowerCase()
  const translated = t(`status.${key}`)
  return translated === `status.${key}` ? status : translated
}

const resolveOwnerId = (row) => row.assigned_to || row.assignedTo || row.inspector_id || row.inspectorId || row.reported_by || row.reportedBy

const resolveDisplayName = (userId) => {
  if (!userId) return '-'
  return userMap.value[userId] || userId
}

const resolveEquipmentCode = (equipmentId) => {
  if (!equipmentId) return '-'
  return equipmentMap.value[equipmentId] || equipmentId
}

const resolveFaultReason = (faultReasonId) => {
  if (!faultReasonId) return '-'
  return faultReasonMap.value[faultReasonId] || faultReasonId
}

const mapMaintenance = (row, type) => ({
  id: row.maintenanceNo || row.maintenance_no || row.inspectionNo || row.inspection_no || row.repairNo || row.repair_no || row.id,
  equipment: resolveEquipmentCode(row.equipment_id || row.equipmentId),
  type,
  owner: resolveDisplayName(resolveOwnerId(row)),
  due: row.planAt || row.plan_at || row.inspectionAt || row.inspection_at || row.reportedAt || row.reported_at || '-',
  status: statusLabel(row.status),
  result: row.result || '-',
  abnormalDesc: row.abnormal_desc || row.abnormalDesc || row.repair_result || row.repairResult || '-',
  faultReason: resolveFaultReason(row.fault_reason_id || row.faultReasonId),
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    const [usersPayload, equipmentPayload, faultReasonsPayload, tasks, plans, repairs] = await Promise.all([
      getUsers().catch(() => ({ data: [] })),
      getEquipmentLedgers().catch(() => ({ data: [] })),
      getFaultReasons().catch(() => ({ data: [] })),
      getMaintenanceOrders(),
      getMaintenancePlans(),
      getRepairOrders()
    ])
    const users = Array.isArray(usersPayload) ? usersPayload : usersPayload?.data || []
    userMap.value = {}
    users.forEach(u => {
      if (u.id != null) userMap.value[u.id] = u.displayName || u.display_name || String(u.id)
    })
    const equipments = recordsOf(equipmentPayload)
    equipmentMap.value = {}
    equipments.forEach(e => {
      if (e.id != null) equipmentMap.value[e.id] = e.equipment_code || e.equipmentCode || String(e.id)
    })
    const faultReasons = recordsOf(faultReasonsPayload)
    faultReasonMap.value = {}
    faultReasons.forEach(f => {
      if (f.id != null) faultReasonMap.value[f.id] = f.reason_name || f.reasonName || String(f.id)
    })
    rows.value = [
      ...recordsOf(tasks).map((row) => mapMaintenance(row, t('equipment.maintenance.typeMaintenance'))),
      ...recordsOf(plans).map((row) => mapMaintenance(row, t('equipment.maintenance.typeInspection'))),
      ...recordsOf(repairs).map((row) => mapMaintenance(row, t('equipment.maintenance.typeRepair')))
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
