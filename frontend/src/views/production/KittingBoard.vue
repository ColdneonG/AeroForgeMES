<template>
  <CrudBoard
    eyebrow="生产订单"
    title="齐套分析与缺料看板"
    description="按工单、产线、物料缺口和风险等级展示齐套状态，支持发布与冻结。"
    list-title="齐套记录"
    :rows="rows"
    :columns="columns"
    row-key="order"
    flow-type="workOrder"
    :row-actions="rowActions"
  />
  <p v-if="loading" class="api-state">Loading kitting records...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { getKittingBoard } from '../../api/production'

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
    error.value = e?.message || 'Data loading failed. Please check backend API or gateway configuration.'
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
