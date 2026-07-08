<template>
  <section class="mes-workspace">
    <div class="mes-page-heading">
      <div>
        <p>现场管理</p>
        <h1>工序作业台</h1>
        <span>演示数据已关闭，待处理工序只从真实任务接口加载。</span>
      </div>
    </div>
    <p v-if="loading" class="api-state">Loading operation tasks...</p>
    <p v-if="error" class="api-state error">{{ error }}</p>
    <CrudBoard
      eyebrow="现场管理"
      title="报工与完工"
      description="普通报工、不良报工、检测报工、装箱和完工提交。"
      list-title="待处理工序"
      :rows="rows"
      :columns="columns"
      row-key="id"
      flow-type="task"
      :row-actions="rowActions"
      :handle-actions-externally="true"
    />
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { getShopTasks } from '../../api/production'

const rows = ref([])
const loading = ref(false)
const error = ref('')
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapTask = (row) => ({
  id: row.taskNo || row.task_no || row.id,
  process: row.processName || row.process_name || row.routeId || row.route_id || '-',
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
    error.value = e?.message || 'Operation task API is not connected yet.'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: '任务' },
  { key: 'process', label: '工序' },
  { key: 'line', label: '产线' },
  { key: 'good', label: '良品' },
  { key: 'bad', label: '不良' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '扫码', action: 'report' },
  { label: '报工', action: 'report' },
  { label: '完工', action: 'complete' },
  { label: '安灯', action: 'andon' }
]

onMounted(loadRows)
</script>

<style scoped>
.api-state {
  margin: 12px 24px;
  color: #52616b;
  font-size: 14px;
}

.api-state.error {
  color: #b42318;
}
</style>
