<template>
  <CrudBoard
    eyebrow="生产订单"
    title="生产派工单"
    description="基于交期、产线、工位、人员与产能建议生成派工单并下发现场。"
    list-title="派工建议"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="workOrder"
    :row-actions="rowActions"
    :handle-actions-externally="true"
    @row-action="handleRowAction"
  />
  <p v-if="loading" class="api-state">正在加载派工单...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import {
  closeDispatchOrder,
  confirmDispatchIssue,
  getDispatchOrders,
  releaseDispatchOrder,
  voidDispatchOrder
} from '../../api/production'
import { authState } from '../../stores/auth'

const rows = ref([])
const loading = ref(false)
const error = ref('')

const statusTextMap = {
  DRAFT: '草稿',
  ENABLED: '启用',
  DISABLED: '停用',
  WAIT_ISSUE: '待下发',
  WAIT_RELEASE: '待下发',
  TO_RELEASE: '待下发',
  PENDING: '待下发',
  PENDING_DISPATCH: '待下发',
  ISSUED: '已下发',
  RELEASED: '已下发',
  RUNNING: '生产中',
  PAUSED: '暂停',
  COMPLETED: '已完成',
  CLOSED: '已关闭',
  VOIDED: '作废',
  CANCELLED: '作废'
}

const normalizeStatus = (status) => statusTextMap[String(status || '').trim().toUpperCase()] || status || '-'
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapDispatch = (row) => ({
  id: row.dispatchNo || row.dispatch_no || row.id,
  apiId: row.id,
  order: row.workOrderNo || row.work_order_no || row.workOrderId || row.work_order_id || '-',
  process: row.processName || row.process_name || row.routeId || row.route_id || '-',
  station: row.stationName || row.station_name || row.stationId || row.station_id || '-',
  operator: row.operatorName || row.operator_name || row.teamId || row.team_id || '-',
  suggestion: row.suggestion || row.planQty || row.plan_qty || '-',
  status: normalizeStatus(row.status),
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    rows.value = recordsOf(await getDispatchOrders()).map(mapDispatch)
  } catch (e) {
    rows.value = []
    error.value = e?.message || '数据加载失败，请检查后端接口或网关转发配置'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: '派工单' },
  { key: 'order', label: '工单' },
  { key: 'process', label: '工序' },
  { key: 'station', label: '工位' },
  { key: 'operator', label: '操作员' },
  { key: 'suggestion', label: '建议' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '编辑', action: 'edit' },
  { label: '下发', action: 'release' },
  { label: '关闭', action: 'close' },
  { label: '作废', action: 'void' },
  { label: '审计', action: 'audit' }
]

const getApiId = (row) => row?.apiId || row?.raw?.id
const actionPayload = (remark) => ({
  operatorId: authState.user?.userId || authState.user?.id || null,
  remark
})

const rowActionHandlers = {
  release: (row) =>
    row.status === '待下发'
      ? confirmDispatchIssue(getApiId(row), actionPayload('frontend dispatch confirm issue'))
      : releaseDispatchOrder(getApiId(row), actionPayload('frontend dispatch issue')),
  close: (row) => closeDispatchOrder(getApiId(row), actionPayload('frontend dispatch close')),
  void: (row) => voidDispatchOrder(getApiId(row), actionPayload('frontend dispatch void'))
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
    error.value = e?.message || '派工单操作失败，请检查后端接口或状态流转规则'
  } finally {
    loading.value = false
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
