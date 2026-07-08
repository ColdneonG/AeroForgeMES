<template>
  <CrudBoard
    eyebrow="安灯管理"
    title="异常安灯闭环"
    description="演示数据已关闭，异常数据只从真实接口加载。"
    list-title="安灯异常"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="andon"
    :primary-actions="primaryActions"
    :row-actions="rowActions"
    :handle-actions-externally="true"
  />
  <p v-if="loading" class="api-state">Loading andon exceptions...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { getAndonExceptions } from '../../api/andon'

const rows = ref([])
const loading = ref(false)
const error = ref('')
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapRow = (row) => ({
  id: row.exceptionNo || row.exception_no || row.andonNo || row.andon_no || row.id,
  type: row.typeName || row.type_name || row.type || '-',
  line: row.lineName || row.line_name || row.lineId || row.line_id || '-',
  reason: row.reasonName || row.reason_name || row.reason || row.description || '-',
  owner: row.ownerName || row.owner_name || row.handlerId || row.handler_id || '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    rows.value = recordsOf(await getAndonExceptions()).map(mapRow)
  } catch (e) {
    rows.value = []
    error.value = e?.message || 'Andon API is not connected yet.'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: '异常单' },
  { key: 'type', label: '类型' },
  { key: 'line', label: '产线' },
  { key: 'reason', label: '原因' },
  { key: 'owner', label: '处理人' },
  { key: 'status', label: '状态' }
]
const primaryActions = [
  { label: '发起安灯', action: 'create' },
  { label: '异常配置', action: 'edit' },
  { label: '导出', action: 'export' }
]
const rowActions = [
  { label: '接收', action: 'accept' },
  { label: '处理', action: 'handle' },
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
