<template>
  <CrudBoard
    :eyebrow="t('production.workOrders.eyebrow')"
    :title="t('production.workOrders.title')"
    :description="t('production.workOrders.description')"
    :list-title="t('production.workOrders.listTitle')"
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
  <p v-if="loading" class="api-state">{{ t('production.workOrders.loading') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
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

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')

const displayQty = (value) => (value === null || value === undefined ? '-' : value)
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapWorkOrder = (row) => ({
  id: row.workOrderNo || row.work_order_no || row.id,
  apiId: row.id,
  product: row.productName || row.product_name || (row.productId ? `${t('tableColumns.product')} ${row.productId}` : '-'),
  line: row.lineName || row.line_name || (row.lineId ? `${t('tableColumns.line')} ${row.lineId}` : '-'),
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
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: t('production.workOrders.listTitle') + '#' },
  { key: 'product', label: t('tableColumns.product') },
  { key: 'line', label: t('tableColumns.line') },
  { key: 'plan', label: t('tableColumns.plan') },
  { key: 'done', label: t('tableColumns.done') },
  { key: 'status', label: t('tableColumns.status') }
]
const primaryActions = [
  { label: t('common.actions.create'), action: 'create' },
  { label: t('common.actions.importSync'), action: 'import' },
  { label: t('common.actions.export'), action: 'export' }
]
const rowActions = [
  { label: t('common.actions.edit'), action: 'edit' },
  { label: t('common.actions.release'), action: 'release' },
  { label: t('common.actions.start'), action: 'start' },
  { label: t('common.actions.complete'), action: 'complete' },
  { label: t('common.actions.close'), action: 'close' },
  { label: t('common.actions.void'), action: 'void' },
  { label: t('common.actions.audit'), action: 'audit' }
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
    error.value = e?.message || t('production.workOrders.operationFailed')
  } finally {
    loading.value = false
  }
}

const handlePrimaryAction = ({ action }) => {
  if (['create', 'import', 'export'].includes(action)) {
    error.value = t('common.error.notImplemented')
  }
}

onMounted(loadRows)
</script>
