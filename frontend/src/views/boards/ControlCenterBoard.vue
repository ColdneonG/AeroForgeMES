<template>
  <section class="control-center-page">
    <div class="control-header">
      <div>
        <p>电子看板</p>
        <h1>中控看板</h1>
      </div>
      <strong>{{ now }}</strong>
    </div>

    <p v-if="loading" class="board-state">Loading control-center data...</p>
    <p v-if="error" class="board-state error">{{ error }}</p>
    <p v-if="!loading && !error && isEmpty" class="board-state">No board data.</p>

    <div class="control-kpi-strip">
      <article v-for="item in kpis" :key="item.label" :class="item.tone">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <p>{{ item.note }}</p>
      </article>
    </div>

    <div class="control-grid">
      <section class="control-panel production-radar">
        <div class="control-panel-title">实时产量</div>
        <div class="control-big-number">{{ outputTotal }}</div>
        <div class="control-bars">
          <i v-for="(value, index) in trend" :key="index" :style="{ height: `${value}%` }"></i>
        </div>
      </section>

      <section class="control-panel">
        <div class="control-panel-title">产线运行状态</div>
        <div class="control-line-list">
          <article v-for="line in lines" :key="line.name">
            <span>{{ line.name }}</span>
            <b :class="line.tone">{{ line.status }}</b>
            <i><em :style="{ width: `${line.rate}%` }"></em></i>
          </article>
        </div>
      </section>

      <section class="control-panel alert-panel">
        <div class="control-panel-title">报警与异常</div>
        <article v-for="alert in alerts" :key="alert.code" :class="alert.tone">
          <strong>{{ alert.code }}</strong>
          <span>{{ alert.text }}</span>
        </article>
      </section>

      <section class="control-panel">
        <div class="control-panel-title">工单进度 / 库存预警</div>
        <div class="control-work-list">
          <article v-for="work in workOrders" :key="work.id">
            <span>{{ work.id }}</span>
            <strong>{{ work.product }}</strong>
            <i><em :style="{ width: `${work.rate}%` }"></em></i>
            <small>{{ work.warning }}</small>
          </article>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getControlCenterBoard } from '../../api/dashboard'

const now = new Date().toLocaleString('zh-CN', { hour12: false })
const kpis = ref([])
const trend = ref([])
const lines = ref([])
const alerts = ref([])
const workOrders = ref([])
const outputTotal = ref('-')
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])
const numberOrZero = (value) => {
  const number = Number(value)
  return Number.isFinite(number) ? number : 0
}

const toneOf = (status) => {
  const text = String(status || '').toUpperCase()
  if (['ALARM', 'DANGER', 'ERROR', 'FAIL'].includes(text)) return 'danger'
  if (['WARN', 'WARNING', 'WAITING', 'PENDING'].includes(text)) return 'warn'
  return 'ok'
}

const mapKpi = (row) => ({
  label: row.label || row.metricName || row.metric_name || row.metricCode || row.metric_code || '-',
  value: row.value ?? row.metricValue ?? row.metric_value ?? '-',
  note: row.note || row.remark || '',
  tone: row.tone || toneOf(row.status)
})

const mapLine = (row) => ({
  name: row.lineName || row.line_name || row.name || row.id,
  status: row.status || '-',
  rate: numberOrZero(row.rate ?? row.progress ?? row.oee),
  tone: row.tone || toneOf(row.status)
})

const mapAlert = (row) => ({
  code: row.code || row.alertCode || row.alert_code || row.id,
  text: row.text || row.message || row.description || '-',
  tone: row.tone || toneOf(row.level || row.status)
})

const mapWorkOrder = (row) => ({
  id: row.workOrderNo || row.work_order_no || row.id,
  product: row.productName || row.product_name || row.product || '-',
  rate: numberOrZero(row.rate ?? row.progress),
  warning: row.warning || row.remark || row.status || '-'
})

const loadBoard = async () => {
  loading.value = true
  error.value = ''

  try {
    const data = await getControlCenterBoard()
    outputTotal.value = data.outputTotal ?? data.output_total ?? data.realtimeOutput ?? data.realtime_output ?? '-'
    kpis.value = recordsOf(data.kpis || data.metrics || []).map(mapKpi)
    trend.value = recordsOf(data.trend || data.outputTrend || data.output_trend || [])
      .map((item) => numberOrZero(item.value ?? item))
    lines.value = recordsOf(data.lines || data.lineStatus || data.line_status || []).map(mapLine)
    alerts.value = recordsOf(data.alerts || data.exceptions || []).map(mapAlert)
    workOrders.value = recordsOf(data.workOrders || data.work_orders || []).map(mapWorkOrder)
  } catch (e) {
    outputTotal.value = '-'
    kpis.value = []
    trend.value = []
    lines.value = []
    alerts.value = []
    workOrders.value = []
    error.value = e?.message || 'Control-center board API is not connected yet.'
  } finally {
    loading.value = false
  }
}

const isEmpty = computed(
  () => kpis.value.length + trend.value.length + lines.value.length + alerts.value.length + workOrders.value.length === 0
)

onMounted(loadBoard)
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
