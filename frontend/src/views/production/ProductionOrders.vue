<template>
  <CrudBoard
    eyebrow="生产订单"
    title="生产工单闭环"
    description="覆盖工单新增、编辑、导入同步、下发、关闭、作废、详情与状态日志。"
    list-title="生产工单"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="workOrder"
    :primary-actions="primaryActions"
    :row-actions="rowActions"
    :handle-actions-externally="true"
    @row-action="handleRowAction"
    @primary-action="handlePrimaryAction"
  />
  <p v-if="loading" class="api-state">正在加载生产工单...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import {
  closeWorkOrder,
  completeWorkOrder,
  confirmWorkOrderIssue,
  getWorkOrders,
  releaseWorkOrder,
  startWorkOrder,
  voidWorkOrder
} from '../../api/production'
import { authState } from '../../stores/auth'

const rows = ref([])
const loading = ref(false)
const error = ref('')

const displayQty = (value) => (value === null || value === undefined ? '-' : value)
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapWorkOrder = (row) => ({
  id: row.workOrderNo || row.work_order_no || row.id,
  apiId: row.id,
  product: row.productName || row.product_name || (row.productId ? `Product ${row.productId}` : '-'),
  line: row.lineName || row.line_name || (row.lineId ? `Line ${row.lineId}` : '-'),
  plan: displayQty(row.planQty ?? row.plan_qty),
  done: displayQty(row.completedQty ?? row.completed_qty),
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    rows.value = recordsOf(await getWorkOrders()).map(mapWorkOrder)
  } catch (e) {
    rows.value = []
    error.value = e?.message || '数据加载失败，请检查后端接口或网关转发配置'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: '工单号' },
  { key: 'product', label: '产品' },
  { key: 'line', label: '产线' },
  { key: 'plan', label: '计划' },
  { key: 'done', label: '完成' },
  { key: 'status', label: '状态' }
]
const primaryActions = [
  { label: '新增', action: 'create' },
  { label: '导入/同步', action: 'import' },
  { label: '导出', action: 'export' }
]
const rowActions = [
  { label: '编辑', action: 'edit' },
  { label: '下发', action: 'release' },
  { label: '开工', action: 'start' },
  { label: '完工', action: 'complete' },
  { label: '关闭', action: 'close' },
  { label: '作废', action: 'void' },
  { label: '审计', action: 'audit' }
]

const getApiId = (row) => row?.apiId || row?.raw?.id
const actionPayload = (remark) => ({
  operatorId: authState.user?.userId || authState.user?.id || null,
  remark
})

const toNumber = (value) => {
  const number = Number(value)
  return Number.isFinite(number) ? number : 0
}

const completePayload = (row) => {
  const completedQty = toNumber(row.done) || toNumber(row.plan)
  return {
    ...actionPayload('frontend complete'),
    completedQty,
    qualifiedQty: completedQty,
    defectiveQty: 0
  }
}

const rowActionHandlers = {
  release: (row) =>
    row.status === '待下发'
      ? confirmWorkOrderIssue(getApiId(row), actionPayload('frontend confirm issue'))
      : releaseWorkOrder(getApiId(row), actionPayload('frontend issue')),
  start: (row) => startWorkOrder(getApiId(row), actionPayload('frontend start')),
  complete: (row) => completeWorkOrder(getApiId(row), completePayload(row)),
  close: (row) => closeWorkOrder(getApiId(row), actionPayload('frontend close')),
  void: (row) => voidWorkOrder(getApiId(row), actionPayload('frontend void'))
}

const handleRowAction = async ({ action, row }) => {
  const handler = rowActionHandlers[action]
  if (!handler) return

  loading.value = true
  error.value = ''

  try {
    await handler(row)
    await loadRows()
  } catch (e) {
    error.value = e?.message || '工单操作失败，请检查后端接口或网关转发配置'
  } finally {
    loading.value = false
  }
}

const handlePrimaryAction = ({ action }) => {
  if (['create', 'import', 'export'].includes(action)) {
    error.value = '该操作尚未接入表单流程，本页当前仅接入列表查询和工单状态动作'
  }
}

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
