<template>
  <CrudBoard
    eyebrow="报表分析"
    title="生产、质量、物料与车间报表"
    description="按产量、不良、关键物料追溯和车间时段查询统计数据，支撑管理分析。"
    list-title="报表目录"
    :rows="rows"
    :columns="columns"
    row-key="report"
    :row-actions="rowActions"
  />
  <p v-if="loading" class="api-state">Loading reports...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { getBoardConfigs, getMetricDefs } from '../../api/report'

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
    error.value = e?.message || 'Data loading failed. Please check backend API or gateway configuration.'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'report', label: '报表' },
  { key: 'dimension', label: '维度' },
  { key: 'api', label: '接口路径' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '查询', action: 'audit' },
  { label: '导出', action: 'export' },
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
