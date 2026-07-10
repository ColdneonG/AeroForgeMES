<template>
  <CrudBoard
    :eyebrow="t('equipment.ledger.eyebrow')"
    :title="t('equipment.ledger.title')"
    :description="t('equipment.ledger.description')"
    :list-title="t('equipment.ledger.listTitle')"
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
import { getEquipmentLedgers } from '../../api/equipment'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapEquipment = (row) => ({
  id: row.equipmentCode || row.equipment_code || row.id,
  type: row.categoryName || row.category_name || (row.categoryId || row.category_id || '-'),
  maker: row.manufacturerName || row.manufacturer_name || (row.manufacturerId || row.manufacturer_id || '-'),
  dept: row.departmentName || row.department_name || row.line_name || row.lineName || (row.lineId || row.line_id || '-'),
  manager: row.managerName || row.manager_name || row.station_name || row.stationName || (row.stationId || row.station_id || '-'),
  status: row.equipmentStatus || row.equipment_status || row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    rows.value = recordsOf(await getEquipmentLedgers()).map(mapEquipment)
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: t('tableColumns.sn') },
  { key: 'type', label: t('tableColumns.type') },
  { key: 'maker', label: t('tableColumns.maker') },
  { key: 'dept', label: t('tableColumns.department') },
  { key: 'manager', label: t('tableColumns.manager') },
  { key: 'status', label: t('tableColumns.status') }
]
const rowActions = [
  { label: t('statusFlow.actions.edit'), action: 'edit' },
  { label: t('statusFlow.actions.inspect'), action: 'inspect' },
  { label: t('statusFlow.actions.repair'), action: 'repair' },
  { label: t('statusFlow.actions.disable'), action: 'disable' },
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

.api-state.error {
  color: #b42318;
}
</style>
