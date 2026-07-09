<template>
  <CrudBoard
    ref="crudBoard"
    :eyebrow="t('shopfloor.tasks.eyebrow')"
    :title="t('shopfloor.tasks.title')"
    :description="t('shopfloor.tasks.description')"
    :list-title="t('shopfloor.tasks.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="task"
    :row-actions="rowActions"
    :handle-actions-externally="true"
    @row-action="handleRowAction"
  />
  <p v-if="loading" class="api-state">{{ t('shopfloor.tasks.loading') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import {
  completeShopTask,
  getShopTasks,
  pauseShopTask,
  resumeShopTask,
  startShopTask
} from '../../api/production'

const { t } = useI18n()

const rows = ref([])
const loading = ref(false)
const error = ref('')
const crudBoard = ref(null)

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapTask = (row) => ({
  id: row.taskNo || row.task_no || row.id,
  apiId: row.id,
  order: row.workOrderNo || row.work_order_no || row.workOrderId || row.work_order_id || '-',
  product: row.productName || row.product_name || row.productId || row.product_id || '-',
  process: row.processName || row.process_name || row.routeName || row.route_name || row.routeId || row.route_id || '-',
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
    error.value = e?.message || t('shopfloor.tasks.apiError')
  } finally {
    loading.value = false
  }
}

const actionLabels = {
  start: t('statusFlow.actions.start'),
  pause: t('statusFlow.actions.pause'),
  resume: t('statusFlow.actions.resume'),
  complete: t('statusFlow.actions.complete'),
  report: t('statusFlow.actions.report'),
  repair: t('statusFlow.actions.repair'),
  andon: t('statusFlow.actions.andon')
}

const actionStatusMap = {
  start: t('status.running'),
  pause: t('status.paused'),
  resume: t('status.running'),
  complete: t('status.completed'),
  report: t('status.running'),
  repair: t('status.repaired'),
  andon: t('status.running')
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
    error.value = t('shopfloor.tasks.actionNotConnected')
    return
  }

  loading.value = true
  error.value = ''
  try {
    await handler(row)
    await loadRows()
    crudBoard.value?.addAuditEntry({
      action: actionLabels[action] || action,
      from: row.status,
      to: actionStatusMap[action] || row.status,
      remark: `${row.id} ${t('common.auditLog.defaultRemark')}`
    })
  } catch (e) {
    error.value = e?.message || t('shopfloor.tasks.actionFailed')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: t('tableColumns.taskNo') },
  { key: 'order', label: t('tableColumns.workOrderNo') },
  { key: 'product', label: t('tableColumns.product') },
  { key: 'process', label: t('tableColumns.process') },
  { key: 'line', label: t('tableColumns.line') },
  { key: 'good', label: t('tableColumns.good') },
  { key: 'bad', label: t('tableColumns.bad') },
  { key: 'status', label: t('tableColumns.status') }
]

const rowActions = [
  { label: t('statusFlow.actions.start'), action: 'start' },
  { label: t('statusFlow.actions.pause'), action: 'pause' },
  { label: t('statusFlow.actions.resume'), action: 'resume' },
  { label: t('statusFlow.actions.report'), action: 'report' },
  { label: t('statusFlow.actions.complete'), action: 'complete' },
  { label: t('statusFlow.actions.andon'), action: 'andon' },
  { label: t('statusFlow.actions.repair'), action: 'repair' }
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
