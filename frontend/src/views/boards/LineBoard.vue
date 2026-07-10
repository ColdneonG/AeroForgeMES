<template>
  <section class="board-page line-board-page">
    <div class="board-header">
      <div>
        <p>{{ $t('boards.line.eyebrow') }}</p>
        <h1>{{ $t('boards.line.title') }}</h1>
      </div>
      <div class="board-time">{{ $t('boards.line.realtimeRefresh') }}{{ now }}</div>
    </div>

    <p v-if="loading" class="board-state">{{ $t('boards.line.loading') }}</p>
    <p v-if="error" class="board-state error">{{ error }}</p>
    <p v-if="!loading && !error && lines.length === 0" class="board-state">{{ $t('boards.line.noData') }}</p>

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
          <span>{{ $t('boards.line.currentOrder') }}</span>
          <strong>{{ line.order }}</strong>
        </div>
        <div class="line-card-metrics">
          <div><span>{{ $t('boards.line.plan') }}</span><strong>{{ line.plan }}</strong></div>
          <div><span>{{ $t('boards.line.done') }}</span><strong>{{ line.done }}</strong></div>
          <div><span>{{ $t('boards.line.completionRate') }}</span><strong>{{ line.rate }}%</strong></div>
        </div>
        <div class="line-progress"><span :style="{ width: `${line.rate}%` }"></span></div>
        <div class="line-card-foot">
          <span>{{ $t('boards.line.equipment') }}{{ line.equipment }}</span>
          <span>{{ $t('boards.line.abnormal') }}{{ line.exception }}</span>
          <span>{{ $t('boards.line.estimated') }}{{ line.eta }}</span>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { getLineBoard } from '../../api/dashboard'

const { t } = useI18n()

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
    error.value = e?.message || t('boards.line.apiError')
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
