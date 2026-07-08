<template>
  <section class="board-page line-board-page">
    <div class="board-header">
      <div>
        <p>电子看板</p>
        <h1>产线看板</h1>
        <span>演示数据已关闭，数据只从真实接口加载。</span>
      </div>
      <div class="board-time">实时刷新 · {{ now }}</div>
    </div>

    <p v-if="loading" class="board-state">Loading line board...</p>
    <p v-if="error" class="board-state error">{{ error }}</p>
    <p v-if="!loading && !error && lines.length === 0" class="board-state">暂无产线看板数据</p>

    <div class="line-board-grid">
      <article v-for="line in lines" :key="line.name" class="line-board-card" :class="line.tone">
        <div class="line-card-head">
          <div>
            <strong>{{ line.name }}</strong>
            <span>{{ line.product }}</span>
          </div>
          <i>{{ line.status }}</i>
        </div>
        <div class="line-card-order">
          <span>当前工单</span>
          <strong>{{ line.order }}</strong>
        </div>
        <div class="line-card-metrics">
          <div><span>计划</span><strong>{{ line.plan }}</strong></div>
          <div><span>完成</span><strong>{{ line.done }}</strong></div>
          <div><span>完成率</span><strong>{{ line.rate }}%</strong></div>
        </div>
        <div class="line-progress"><span :style="{ width: `${line.rate}%` }"></span></div>
        <div class="line-card-foot">
          <span>设备：{{ line.equipment }}</span>
          <span>异常：{{ line.exception }}</span>
          <span>预计：{{ line.eta }}</span>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getLineBoard } from '../../api/dashboard'

const now = new Date().toLocaleString('zh-CN', { hour12: false })
const lines = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])
const numberOrZero = (value) => {
  const number = Number(value)
  return Number.isFinite(number) ? number : 0
}
const toneOf = (status) => {
  const text = String(status || '').toUpperCase()
  if (['ALARM', 'ERROR', 'FAILED', 'DANGER'].includes(text)) return 'danger'
  if (['WARN', 'WARNING', 'PENDING', 'WAITING'].includes(text)) return 'warn'
  return 'running'
}

const mapLine = (row) => ({
  name: row.lineName || row.line_name || row.name || row.id,
  product: row.productName || row.product_name || row.product || '-',
  order: row.workOrderNo || row.work_order_no || row.order || '-',
  plan: row.planQty ?? row.plan_qty ?? row.plan ?? '-',
  done: row.completedQty ?? row.completed_qty ?? row.done ?? '-',
  rate: numberOrZero(row.rate ?? row.progress),
  status: row.status || '-',
  equipment: row.equipmentStatus || row.equipment_status || row.equipment || '-',
  exception: row.exception || row.alert || '-',
  eta: row.eta || row.estimatedAt || row.estimated_at || '-',
  tone: row.tone || toneOf(row.status)
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    lines.value = recordsOf(await getLineBoard()).map(mapLine)
  } catch (e) {
    lines.value = []
    error.value = e?.message || 'Line board API is not connected yet.'
  } finally {
    loading.value = false
  }
}

onMounted(loadRows)
</script>

<style scoped>
.board-state {
  margin: 12px 0;
  color: #52616b;
  font-size: 14px;
}

.board-state.error {
  color: #b42318;
}
</style>
