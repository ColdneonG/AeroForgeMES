<template>
  <CrudBoard
    :eyebrow="t('report.eyebrow')"
    :title="t('report.title')"
    :description="t('report.description')"
    :list-title="t('report.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="report"
    :row-actions="rowActions"
  />
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getBoardConfigs, getMetricDefs } from '../../api/report'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapMetric = (row) => ({
  report: row.metricName || row.metric_name || row.metricCode || row.metric_code || row.id,
  dimension: row.metricType || row.metric_type || row.dimension || '-',
  api: '/api/report/metric-snapshots',
  status: row.status || 'ENABLED',
  raw: row
})

const mapBoard = (row) => ({
  report: row.boardName || row.board_name || row.boardCode || row.board_code || row.id,
  dimension: row.boardType || row.board_type || '-',
  api: '/api/report/boards',
  status: row.status || 'ENABLED',
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
  { label: t('common.actions.export'), action: 'export' },
  { label: t('common.actions.audit'), action: 'audit' }
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
