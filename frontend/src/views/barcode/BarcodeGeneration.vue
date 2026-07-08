<template>
  <CrudBoard
    eyebrow="条码应用"
    title="条码生成与打印"
    description="演示数据已关闭，生成记录只从真实接口加载。"
    list-title="生成任务"
    :rows="rows"
    :columns="columns"
    row-key="id"
    :row-actions="rowActions"
    :handle-actions-externally="true"
  />
  <p v-if="loading" class="api-state">Loading barcode records...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { getBarcodeRecords } from '../../api/barcode'

const rows = ref([])
const loading = ref(false)
const error = ref('')
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapRow = (row) => ({
  id: row.jobNo || row.job_no || row.barcode || row.id,
  rule: row.ruleCode || row.rule_code || row.ruleId || row.rule_id || '-',
  product: row.productName || row.product_name || row.productId || row.product_id || '-',
  qty: row.qty ?? row.quantity ?? '-',
  printed: row.printedQty ?? row.printed_qty ?? '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    rows.value = recordsOf(await getBarcodeRecords()).map(mapRow)
  } catch (e) {
    rows.value = []
    error.value = e?.message || 'Barcode record API is not connected yet.'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: '任务号' },
  { key: 'rule', label: '规则' },
  { key: 'product', label: '产品' },
  { key: 'qty', label: '数量' },
  { key: 'printed', label: '已打印' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '生成', action: 'create' },
  { label: '打印', action: 'print' },
  { label: '补打', action: 'print' },
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
