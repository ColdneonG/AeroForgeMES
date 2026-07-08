<template>
  <CrudBoard
    eyebrow="Shopfloor"
    title="Production Tasks"
    description="Demo data is disabled. Rows and actions must come from backend task APIs."
    list-title="Tasks"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="task"
    :row-actions="rowActions"
    :handle-actions-externally="true"
    @row-action="handleRowAction"
  />
  <p v-if="loading" class="api-state">Loading shop-floor tasks...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import {
  completeShopTask,
  getShopTasks,
  pauseShopTask,
  resumeShopTask,
  startShopTask
} from '../../api/production'

const rows = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapTask = (row) => ({
  id: row.taskNo || row.task_no || row.id,
  apiId: row.id,
  order: row.workOrderNo || row.work_order_no || row.workOrderId || row.work_order_id || '-',
  product: row.productName || row.product_name || row.productId || row.product_id || '-',
  process: row.processName || row.process_name || row.routeId || row.route_id || '-',
  line: row.lineName || row.line_name || row.lineId || row.line_id || '-',
  good: row.goodQty ?? row.good_qty ?? row.completedQty ?? row.completed_qty ?? '-',
  bad: row.badQty ?? row.bad_qty ?? row.defectiveQty ?? row.defective_qty ?? '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    rows.value = recordsOf(await getShopTasks()).map(mapTask)
  } catch (e) {
    rows.value = []
    error.value = e?.message || 'Task API is not connected yet.'
  } finally {
    loading.value = false
  }
}

const actionHandlers = {
  start: (row) => startShopTask(row.apiId || row.raw?.id || row.id),
  pause: (row) => pauseShopTask(row.apiId || row.raw?.id || row.id),
  resume: (row) => resumeShopTask(row.apiId || row.raw?.id || row.id),
  complete: (row) => completeShopTask(row.apiId || row.raw?.id || row.id)
}

const handleRowAction = async ({ action, row }) => {
  const handler = actionHandlers[action]
  if (!handler) {
    error.value = 'This action is not connected to a real API yet.'
    return
  }

  loading.value = true
  error.value = ''
  try {
    await handler(row)
    await loadRows()
  } catch (e) {
    error.value = e?.message || 'Task action failed.'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: 'Task' },
  { key: 'order', label: 'Work Order' },
  { key: 'product', label: 'Product' },
  { key: 'process', label: 'Process' },
  { key: 'line', label: 'Line' },
  { key: 'good', label: 'Good' },
  { key: 'bad', label: 'Bad' },
  { key: 'status', label: 'Status' }
]

const rowActions = [
  { label: 'Start', action: 'start' },
  { label: 'Pause', action: 'pause' },
  { label: 'Resume', action: 'resume' },
  { label: 'Report', action: 'report' },
  { label: 'Complete', action: 'complete' },
  { label: 'Repair', action: 'repair' }
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
