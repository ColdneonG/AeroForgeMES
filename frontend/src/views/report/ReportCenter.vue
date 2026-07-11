<template>
  <CrudBoard
    ref="boardRef"
    :eyebrow="t('report.eyebrow')"
    :title="t('report.title')"
    :description="t('report.description')"
    :list-title="t('report.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="report"
    :row-actions="rowActions"
    handle-actions-externally
    @row-action="handleRowAction"
  />
  <p v-if="actionMessage" class="api-state success">{{ actionMessage }}</p>
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getBoardConfigs, getMetricData, getMetricDefs } from '../../api/report'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')
const actionMessage = ref('')
const boardRef = ref(null)

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapMetric = (row) => ({
  report: row.metricName || row.metric_name || row.metricCode || row.metric_code || row.id,
  dimension: row.metricType || row.metric_type || row.dimension || '-',
  api: '/api/report/metric-snapshots',
  status: row.status || 'ENABLED',
  kind: 'metric',
  metricCode: row.metricCode || row.metric_code || '',
  metricType: row.metricType || row.metric_type || '',
  metricId: row.id,
  raw: row
})

const mapBoard = (row) => ({
  report: row.boardName || row.board_name || row.boardCode || row.board_code || row.id,
  dimension: row.boardType || row.board_type || '-',
  api: '/api/report/boards',
  status: row.status || 'ENABLED',
  kind: 'board',
  metricCode: row.boardCode || row.board_code || '',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    const [metrics, boards] = await Promise.all([getMetricDefs(), getBoardConfigs()])
    rows.value = [...recordsOf(metrics).map(mapMetric), ...recordsOf(boards).map(mapBoard)]
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'report', label: t('tableColumns.report') },
  { key: 'dimension', label: t('tableColumns.dimension') },
  { key: 'api', label: t('tableColumns.apiPath') },
  { key: 'status', label: t('tableColumns.status') }
]
const rowActions = [
  { label: t('common.actions.audit'), action: 'audit' },
  { label: t('common.actions.export'), action: 'export' }
]

const addLog = (row, action, remark) => {
  boardRef.value?.addAuditEntry({
    action,
    from: row.status,
    to: row.status,
    remark
  })
}

const escapeCsv = (value) => {
  const text = String(value ?? '')
  return /[",\n\r]/.test(text) ? `"${text.replaceAll('"', '""')}"` : text
}

const downloadCsv = (filename, headers, dataRows) => {
  const csv = [headers, ...dataRows].map((line) => line.map(escapeCsv).join(',')).join('\n')
  const blob = new Blob([`\ufeff${csv}`], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${filename || 'report'}.csv`
  link.click()
  URL.revokeObjectURL(url)
}

const exportReportData = async (row) => {
  if (row.kind === 'metric' || row.kind === 'board') {
    const dataset = await getMetricData(row.metricCode)
    const headers = dataset.columns.map((column) => column.label)
    const records = recordsOf(dataset.rows)
    if (records.length === 0) return false
    downloadCsv(row.report, headers, records.map((record) => dataset.columns.map((column) => record[column.key])))
    return true
  }

  downloadCsv(row.report, columns.map((column) => column.label), [columns.map((column) => row[column.key])])
  return true
}

const handleRowAction = async ({ action, row }) => {
  actionMessage.value = ''
  error.value = ''

  if (action === 'audit') {
    actionMessage.value = t('report.actions.auditOpened', { report: row.report })
    addLog(row, t('common.actions.audit'), t('report.actions.auditRemark', { report: row.report }))
    return
  }

  if (action === 'export') {
    try {
      const exported = await exportReportData(row)
      actionMessage.value = exported
        ? t('report.actions.exported', { report: row.report })
        : t('report.actions.noExportData', { report: row.report })
      addLog(row, t('common.actions.export'), t('report.actions.exportRemark', { report: row.report }))
    } catch (e) {
      error.value = e?.message || t('common.error.operationFailed')
    }
  }
}

onMounted(loadRows)
</script>
