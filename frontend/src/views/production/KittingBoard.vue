<template>
  <CrudBoard
    :eyebrow="t('production.kitting.eyebrow')"
    :title="t('production.kitting.title')"
    :description="t('production.kitting.description')"
    :list-title="t('production.kitting.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="order"
    flow-type="workOrder"
    :row-actions="rowActions"
  />
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getKittingBoard } from '../../api/production'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapKitting = (row) => {
  const missing = row.missingCount ?? row.missing_count ?? 0
  return {
    order: row.analysisNo || row.analysis_no || row.id,
    product: row.productName || row.product_name || (row.workOrderId ? `WorkOrder ${row.workOrderId}` : '-'),
    line: row.lineName || row.line_name || '-',
    rate: row.kittingRate || row.kitting_rate || row.kittingStatus || row.kitting_status || '-',
    risk: row.risk || (Number(missing) > 0 ? '缺料风险' : '正常'),
    missing,
    status: row.status || '-',
    raw: row
  }
}

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    rows.value = recordsOf(await getKittingBoard()).map(mapKitting)
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'order', label: '工单' },
  { key: 'product', label: '产品' },
  { key: 'line', label: '产线' },
  { key: 'rate', label: '齐套率' },
  { key: 'risk', label: '风险' },
  { key: 'missing', label: '缺料' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '发布', action: 'release' },
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
