<template>
  <CrudBoard
    :eyebrow="eyebrow"
    :title="title"
    :description="description"
    list-title="接口同步记录"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="integration"
    :row-actions="rowActions"
    :handle-actions-externally="true"
  />
  <p v-if="loading" class="api-state">Loading integration logs...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { getSyncLogs } from '../../api/integration'

defineProps({
  eyebrow: { type: String, default: '集成接口' },
  title: { type: String, default: 'ERP 与标准 API 接口' },
  description: { type: String, default: '演示数据已关闭，接口同步记录只从真实接口加载。' }
})

const rows = ref([])
const loading = ref(false)
const error = ref('')
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapRow = (row) => ({
  id: row.syncNo || row.sync_no || row.requestNo || row.request_no || row.id,
  module: row.module || row.apiName || row.api_name || row.resource || '-',
  direction: row.direction || '-',
  externalNo: row.externalNo || row.external_no || row.bizNo || row.biz_no || '-',
  result: row.result || row.message || '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    rows.value = recordsOf(await getSyncLogs()).map(mapRow)
  } catch (e) {
    rows.value = []
    error.value = e?.message || 'Integration API is not connected yet.'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: '日志号' },
  { key: 'module', label: '接口' },
  { key: 'direction', label: '方向' },
  { key: 'externalNo', label: '外部单号' },
  { key: 'result', label: '结果' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '重试', action: 'retry' },
  { label: '关闭', action: 'close' },
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
