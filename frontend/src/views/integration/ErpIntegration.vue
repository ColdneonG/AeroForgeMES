<template>
  <CrudBoard
    eyebrow="ERP接口监控"
    title="ERP 接口同步日志"
    description="本页面用于展示 ERP / OpenAPI 接口调用记录、同步状态、失败原因和重试次数。"
    list-title="ERP 接口同步日志"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="integration"
    :row-actions="rowActions"
    :handle-actions-externally="true"
    @row-action="handleRowAction"
  />
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
  <p v-if="!loading && !error && rows.length === 0" class="api-state">暂无 ERP 同步日志</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getErpSyncLogs, retrySyncLog } from '../../api/integration'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])
const statusLabels = { PENDING: 'status.syncPending', FAILED: 'status.syncFailed', SUCCESS: 'status.syncSuccess', CLOSED: 'status.closed' }
const displayStatus = (status) => statusLabels[status] ? t(statusLabels[status]) : status || '-'

const columns = [
  { key: 'id', label: t('tableColumns.logNo') },
  { key: 'api', label: t('tableColumns.api') },
  { key: 'direction', label: t('tableColumns.direction') },
  { key: 'externalNo', label: t('tableColumns.externalId') },
  { key: 'errorMessage', label: '失败原因' },
  { key: 'retryCount', label: '重试次数' },
  { key: 'status', label: t('tableColumns.status') }
]
const rowActions = [
  { label: t('statusFlow.actions.retry'), action: 'retry' },
  { label: t('statusFlow.actions.close'), action: 'close' },
  { label: t('statusFlow.actions.audit'), action: 'audit' }
]

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    rows.value = recordsOf(await getErpSyncLogs()).map((row) => ({
      id: row.id,
      api: row.interfaceCode || row.interface_code || '-',
      direction: row.direction || '-',
      externalNo: row.externalNo || row.external_no || '-',
      errorMessage: row.errorMessage || row.error_message || '-',
      retryCount: row.retryCount ?? row.retry_count ?? 0,
      status: displayStatus(row.syncStatus || row.sync_status || row.status),
      raw: row
    }))
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const handleRowAction = async ({ action, row }) => {
  if (action !== 'retry') return

  loading.value = true
  error.value = ''
  try {
    await retrySyncLog(row.id)
    await loadRows()
  } catch (e) {
    error.value = e?.message || t('common.error.operationFailed')
  } finally {
    loading.value = false
  }
}

onMounted(loadRows)
</script>
