<template>
  <CrudBoard
    :eyebrow="t('production.dispatch.eyebrow')"
    :title="t('production.dispatch.title')"
    :description="t('production.dispatch.description')"
    :list-title="t('production.dispatch.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="workOrder"
    :row-actions="rowActions"
    :handle-actions-externally="true"
    @row-action="handleRowAction"
  />
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import {
  closeDispatchOrder,
  confirmDispatchIssue,
  getDispatchOrders,
  releaseDispatchOrder,
  startDispatchOrder,
  voidDispatchOrder
} from '../../api/production'
import { authState } from '../../stores/auth'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')

const statusTextMap = {
  DRAFT: () => t('status.draft'),
  ENABLED: () => t('status.enabled'),
  DISABLED: () => t('status.disabled'),
  WAIT_ISSUE: () => t('status.pending'),
  WAIT_RELEASE: () => t('status.pending'),
  TO_RELEASE: () => t('status.pending'),
  PENDING: () => t('status.pending'),
  PENDING_DISPATCH: () => t('status.pending'),
  ISSUED: () => t('status.released'),
  RELEASED: () => t('status.released'),
  RUNNING: () => t('status.running'),
  PAUSED: () => t('status.paused'),
  COMPLETED: () => t('status.completed'),
  CLOSED: () => t('status.closed'),
  VOIDED: () => t('status.voided'),
  CANCELLED: () => t('status.voided')
}

const normalizeStatus = (status) => {
  const fn = statusTextMap[String(status || '').trim().toUpperCase()]
  return fn ? fn() : status || '-'
}
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
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: t('production.dispatch.listTitle') },
  { key: 'order', label: t('tableColumns.workOrderNo') },
  { key: 'process', label: t('common.filter.status') },
  { key: 'station', label: t('tableColumns.line') },
  { key: 'operator', label: t('tableColumns.owner') },
  { key: 'suggestion', label: t('tableColumns.plan') },
  { key: 'status', label: t('tableColumns.status') }
]
const rowActions = [
  { label: t('common.actions.edit'), action: 'edit' },
  { label: t('common.actions.release'), action: 'release' },
  { label: t('common.actions.start'), action: 'start' },
  { label: t('common.actions.close'), action: 'close' },
  { label: t('common.actions.void'), action: 'void' },
  { label: t('common.actions.audit'), action: 'audit' }
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
  start: (row) => startDispatchOrder(getApiId(row), actionPayload('frontend dispatch start')),
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
    error.value = e?.message || t('common.error.operationFailed')
  } finally {
    loading.value = false
  }
}

onMounted(loadRows)
</script>
