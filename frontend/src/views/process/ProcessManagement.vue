<template>
  <CrudBoard
    eyebrow="工艺管理"
    title="工序、工艺路线与电子 SOP"
    description="演示数据已关闭，工艺资料只从真实接口加载。"
    list-title="工艺资料"
    :rows="rows"
    :columns="columns"
    row-key="code"
    :row-actions="rowActions"
    :handle-actions-externally="true"
  />
  <p v-if="loading" class="api-state">Loading process data...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { getProcessRows } from '../../api/process'

const rows = ref([])
const loading = ref(false)
const error = ref('')
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapRow = (row) => ({
  code: row.processCode || row.process_code || row.routeCode || row.route_code || row.code || row.id,
  name: row.processName || row.process_name || row.routeName || row.route_name || row.name || '-',
  type: row.type || row.processType || row.process_type || '-',
  sop: row.sopName || row.sop_name || row.sopCode || row.sop_code || '-',
  defects: row.defects || row.defectNames || row.defect_names || '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    rows.value = recordsOf(await getProcessRows()).map(mapRow)
  } catch (e) {
    rows.value = []
    error.value = e?.message || 'Process API is not connected yet.'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'code', label: '编码' },
  { key: 'name', label: '名称' },
  { key: 'type', label: '类型' },
  { key: 'sop', label: 'SOP' },
  { key: 'defects', label: '不良原因' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '编辑', action: 'edit' },
  { label: '启用/停用', action: 'close' },
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
