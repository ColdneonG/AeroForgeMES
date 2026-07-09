<template>
  <div class="plant-dashboard">
    <section class="line-panel">
      <h1>{{ $t('dashboard.packagingLines') }}</h1>
      <p v-if="loading" class="dashboard-state">{{ $t('dashboard.loading') }}</p>
      <p v-if="error" class="dashboard-state error">{{ error }}</p>
      <p v-if="!loading && !error && lines.length === 0" class="dashboard-state">{{ $t('dashboard.noData') }}</p>

      <div class="line-grid">
        <article
          v-for="line in lines"
          :key="line.id"
          class="line-card"
          :class="{ active: line.active, collapsed: line.collapsed }"
        >
          <template v-if="!line.collapsed">
            <div class="line-top">
              <div class="line-id">
                <span class="gear-mark">++</span>
                <div>
                  <strong>{{ line.id }}</strong>
                  <span>{{ line.name }}</span>
                </div>
              </div>
              <div class="line-alerts">
                <span>{{ line.counts[0] }}</span><i class="person"></i>
                <span>{{ line.counts[1] }}</span><i class="warn"></i>
                <span>{{ line.counts[2] }}</span><i class="flag"></i>
              </div>
              <button aria-label="collapse">⌃</button>
            </div>

            <div class="card-body">
              <div class="line-copy">
                <span>{{ $t('dashboard.lot') }}</span>
                <strong>{{ line.lot }}</strong>
                <span>{{ $t('dashboard.workOrder') }}</span>
                <strong>{{ line.workOrder }}</strong>
                <span>{{ $t('dashboard.product') }}</span>
                <strong>{{ line.product }}</strong>
                <span>{{ $t('dashboard.itemsProduced') }}</span>
                <strong>{{ line.produced }}</strong>
              </div>

              <div class="donut" :style="{ '--value': line.oee }">
                <span>{{ line.oee }}%</span>
              </div>
            </div>

            <div class="card-footer">
              <div class="micro">
                <div class="tiny-bars">
                  <i v-for="(height, index) in line.forecast" :key="index" :style="{ height: height + '%' }"></i>
                </div>
                <span>{{ $t('dashboard.outputPerformance') }}</span>
                <strong>{{ line.performance }}%</strong>
              </div>
              <div class="forecast">
                <span>{{ $t('dashboard.yearForecast') }}</span>
                <strong>{{ line.forecastText }}</strong>
              </div>
            </div>
          </template>

          <template v-else>
            <div class="collapsed-head">
              <span class="gear-mark">++</span>
              <div>
                <strong>{{ line.id }}</strong>
                <span>{{ line.name }}</span>
              </div>
              <button aria-label="expand">⌄</button>
            </div>
          </template>
        </article>
      </div>
    </section>

    <section class="metric-panel">
      <div class="gauge-grid">
        <article v-for="gauge in gauges" :key="gauge.label" class="gauge-card">
          <h2>{{ gauge.label }}</h2>
          <svg viewBox="0 0 236 142" role="img" :aria-label="`${gauge.label} ${gauge.value}%`">
            <path class="arc red" d="M28 112 A90 90 0 0 1 58 45" />
            <path class="arc yellow" d="M58 45 A90 90 0 0 1 178 45" />
            <path class="arc green" d="M178 45 A90 90 0 0 1 208 112" />
            <g class="ticks">
              <text x="27" y="122">0</text>
              <text x="54" y="57">25</text>
              <text x="116" y="32">50</text>
              <text x="174" y="57">75</text>
              <text x="202" y="122">100</text>
            </g>
            <line
              class="needle"
              x1="118"
              y1="112"
              :x2="needlePoint(gauge.value).x"
              :y2="needlePoint(gauge.value).y"
            />
            <circle cx="118" cy="112" r="5" fill="#73818b" />
          </svg>
          <strong>{{ gauge.value }} %</strong>
        </article>
      </div>

      <article class="stock-card">
        <header>
          <span class="stock-icon">▥</span>
          <div>
            <h2>{{ $t('dashboard.stockLevel') }}</h2>
            <p>{{ $t('dashboard.stockDesc') }}</p>
          </div>
          <button aria-label="collapse stock">⌃</button>
        </header>

        <div class="stock-table-wrap">
          <table>
            <thead>
              <tr>
                <th></th>
                <th>{{ $t('dashboard.material') }}</th>
                <th>{{ $t('dashboard.requiredQty') }}</th>
                <th>{{ $t('dashboard.actualQty') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in stock" :key="item.material">
                <td><span :class="['stock-state', item.state]"></span></td>
                <td>
                  <small>{{ item.code }}</small>
                  <strong>{{ item.material }}</strong>
                </td>
                <td>{{ item.required }}</td>
                <td>{{ item.actual }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { getManufacturingDashboard } from '../../api/dashboard'

const { t } = useI18n()

const lines = ref([])
const gauges = ref([])
const stock = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])
const numberOrZero = (value) => {
  const number = Number(value)
  return Number.isFinite(number) ? number : 0
}

const mapLine = (row) => ({
  id: row.lineCode || row.line_code || row.id,
  name: row.lineName || row.line_name || row.name || '-',
  lot: row.lotNo || row.lot_no || row.lot || '-',
  workOrder: row.workOrderNo || row.work_order_no || row.workOrder || '-',
  product: row.productName || row.product_name || row.product || '-',
  produced: row.producedText || row.produced_text || `${row.completedQty ?? row.completed_qty ?? 0} / ${row.planQty ?? row.plan_qty ?? 0}`,
  forecastText: row.forecastText || row.forecast_text || row.forecast || '-',
  oee: numberOrZero(row.oee),
  performance: numberOrZero(row.performance),
  forecast: Array.isArray(row.forecast) ? row.forecast : [],
  counts: Array.isArray(row.counts) ? row.counts : [0, 0, 0],
  active: Boolean(row.active)
})

const mapGauge = (row) => ({
  label: row.label || row.metricName || row.metric_name || row.metricCode || row.metric_code || '-',
  value: numberOrZero(row.value ?? row.metricValue ?? row.metric_value)
})

const mapStock = (row) => ({
  state: row.state || row.tone || 'info',
  code: row.materialCode || row.material_code || row.code || row.id,
  material: row.materialName || row.material_name || row.material || '-',
  required: row.requiredQty || row.required_qty || row.required || '-',
  actual: row.actualQty || row.actual_qty || row.actual || '-'
})

const loadDashboard = async () => {
  loading.value = true
  error.value = ''

  try {
    const data = await getManufacturingDashboard()
    lines.value = recordsOf(data.lines || data.lineStatus || data.line_status || data).map(mapLine)
    gauges.value = recordsOf(data.gauges || data.metrics || []).map(mapGauge)
    stock.value = recordsOf(data.stock || data.materialStock || data.material_stock || []).map(mapStock)
  } catch (e) {
    lines.value = []
    gauges.value = []
    stock.value = []
    error.value = e?.message || t('dashboard.apiError')
  } finally {
    loading.value = false
  }
}

function needlePoint(value) {
  const angle = Math.PI - (Math.PI * value) / 100
  const radius = 76
  return {
    x: 118 + Math.cos(angle) * radius,
    y: 112 - Math.sin(angle) * radius
  }
}

onMounted(loadDashboard)
</script>

<style scoped>
.dashboard-state {
  margin: 12px 0;
  color: #52616b;
  font-size: 14px;
}

.dashboard-state.error {
  color: #b42318;
}
</style>
