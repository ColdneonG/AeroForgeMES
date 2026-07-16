<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { getAndonExceptions, getManufacturingDashboard, getWorkOrders, type ManufacturingLine, type WorkOrder } from '@/api/production'
import { formatDisplayNumber, formatPercent } from '@/utils/number'

const orders = ref<WorkOrder[]>([])
const andonCount = ref(0)
const andonExceptions = ref<Record<string, unknown>[]>([])
const gauges = ref<Record<string, number>>({})
const manufacturingLines = ref<ManufacturingLine[]>([])
const activeOrders = computed(() => orders.value.filter((order) => !['COMPLETED', 'CLOSED', 'VOID'].includes(String(order.status))).slice(0, 4))
const closedAndonStatuses = new Set(['CLOSED', 'VOID', '关闭', '已关闭', '作废', '已作废'])
const activeAndonExceptions = computed(() => andonExceptions.value
  .filter((item) => !closedAndonStatuses.has(String(item.status || '').trim().toUpperCase()))
  .slice(0, 3))

function metric(keys: string[], fallback = '-', fixedFraction = false) {
  const value = keys.map((key) => gauges.value[key]).find((item) => item !== undefined)
  return value === undefined ? fallback : fixedFraction ? formatPercent(value).slice(0, -1) : formatDisplayNumber(value)
}

function progress(order: WorkOrder) {
  const planned = Number(order.planQty || 0)
  const completed = Number(order.completedQty || 0)
  return planned ? Math.min(100, (completed / planned) * 100) : 0
}

onMounted(async () => {
  const [workOrders, dashboard, andon] = await Promise.allSettled([getWorkOrders(), getManufacturingDashboard(), getAndonExceptions()])
  if (workOrders.status === 'fulfilled') orders.value = workOrders.value
  if (dashboard.status === 'fulfilled') {
    gauges.value = Object.fromEntries(dashboard.value.gauges.map((item) => [item.metricKey, Number(item.value)]))
    manufacturingLines.value = dashboard.value.lines || []
  }
  if (andon.status === 'fulfilled') {
    andonExceptions.value = andon.value
    andonCount.value = andon.value.filter((item) => !closedAndonStatuses.has(String(item.status || '').trim().toUpperCase())).length
  }
})
</script>

<template>
<MesLayout active="dashboard">

  <!-- Sidebar -->
    

  <!-- Header -->
  <header class="app-header" data-od-id="dashboard-header">
    <div class="header-breadcrumb">
      <span class="bc-current">工作台</span>
    </div>
    <div class="header-actions">
      <span style="font-size:var(--text-caption);opacity:0.5;">2026-07-11 09:32</span>
      <span class="user-avatar" title="张工 — 生产主管">张</span>
    </div>
  </header>

  <!-- Main Content -->
  <main class="app-main" data-od-id="dashboard-main">
    <h1 class="page-title" data-od-id="dashboard-title">生产工作台</h1>

    <!-- KPI Row -->
    <section class="kpi-grid" data-od-id="dashboard-kpi">
      <div class="kpi-card accent-border" data-od-id="kpi-output">
        <span class="kpi-label">今日产量（台）</span>
        <span class="kpi-value" v-count-up>{{ metric(['dailyOutput', 'TODAY_OUTPUT']) }}</span>
        <span class="kpi-change up">↑ 8.2% 较昨日同期</span>
      </div>
      <div class="kpi-card" data-od-id="kpi-order-done">
        <span class="kpi-label">今日完工订单</span>
        <span class="kpi-value" v-count-up>{{ metric(['completedOrders', 'COMPLETED_ORDERS'], String(orders.filter((order) => order.status === 'COMPLETED').length)) }}</span>
        <span class="kpi-change up">目标 18 单</span>
      </div>
      <div class="kpi-card" data-od-id="kpi-quality">
        <span class="kpi-label">一次合格率</span>
        <span class="kpi-value" v-count-up>{{ metric(['firstPassRate', 'FIRST_PASS_RATE'], '-', true) }}<span class="kpi-unit">%</span></span>
        <span class="kpi-change down">↓ 0.3% 较上周</span>
      </div>
      <div class="kpi-card" data-od-id="kpi-oee">
        <span class="kpi-label">设备综合效率 OEE</span>
        <span class="kpi-value" v-count-up>{{ metric(['oee', 'OEE'], '-', true) }}<span class="kpi-unit">%</span></span>
        <span class="kpi-change up">↑ 2.1%</span>
      </div>
      <div class="kpi-card accent-border" data-od-id="kpi-andon">
        <span class="kpi-label">安灯异常（未关闭）</span>
        <span class="kpi-value" v-count-up>{{ andonCount }}</span>
        <span class="kpi-change">总装线 ×2 · 绕线 ×1</span>
      </div>
    </section>

    <!-- Two-column layout -->
    <div style="display:grid; grid-template-columns:1fr 1fr; gap:var(--space-5);">
      <!-- Left: Current Orders -->
      <section class="card" data-od-id="dashboard-orders">
        <div class="card-header">
          <h2 class="card-title">进行中的生产订单</h2>
          <RouterLink to="/production-orders" class="btn btn-ghost btn-sm">查看全部 →</RouterLink>
        </div>
        <table class="data-table" style="border:none;">
          <thead>
            <tr>
              <th>订单号</th><th>产品</th><th>数量</th><th>进度</th><th>状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="!activeOrders.length"><td colspan="5" class="text-muted">暂无进行中的生产订单</td></tr>
            <tr v-for="order in activeOrders" :key="order.id">
              <td class="cell-mono"><RouterLink :to="{ path: '/production-order-detail', query: { id: order.id } }">{{ order.workOrderNo || `#${order.id}` }}</RouterLink></td>
              <td>产品 #{{ order.productId ?? '-' }}</td>
              <td>{{ order.planQty ?? '-' }}</td>
              <td class="cell-progress"><div class="progress-bar"><div class="progress-fill" :style="{ width: `${progress(order)}%` }"></div></div><span class="progress-label">{{ formatPercent(progress(order)) }}</span></td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>{{ order.status || '-' }}</span></td>
            </tr>
          </tbody>
        </table>
      </section>

      <!-- Right: Alerts & Recent Events -->
      <section data-od-id="dashboard-alerts">
        <div v-if="false" class="card" style="margin-bottom:var(--space-5);">
          <div class="card-header">
            <h2 class="card-title">安灯异常</h2>
            <span class="badge badge-status-error"><span class="badge-dot"></span>{{ andonCount }} 条未关闭</span>
          </div>
          <div class="alert alert-error" data-od-id="alert-andon-1">
            <span class="alert-icon">!</span>
            <div>
              <strong>总装线 A — 风叶动平衡超差</strong><br>
              <span class="text-subtle">报工时间 09:15 · 班组长已响应 · 预计恢复 10:00</span>
            </div>
          </div>
          <div class="alert alert-warning" data-od-id="alert-andon-2">
            <span class="alert-icon">!</span>
            <div>
              <strong>绕线工位 #3 — 漆包线张力异常</strong><br>
              <span class="text-subtle">报工时间 08:42 · 设备技术员到场处理中</span>
            </div>
          </div>
          <div class="alert alert-warning" data-od-id="alert-andon-3">
            <span class="alert-icon">!</span>
            <div>
              <strong>总装线 B — 网罩卡扣缺料预警</strong><br>
              <span class="text-subtle">报工时间 09:28 · 仓库已确认补料，预计 09:50 送达</span>
            </div>
          </div>
        </div>

        <div v-else class="card" style="margin-bottom:var(--space-5);">
          <div class="card-header"><h2 class="card-title">安灯异常</h2><span class="badge badge-status-error"><span class="badge-dot"></span>{{ andonCount }} 条未关闭</span></div>
          <div v-if="activeAndonExceptions.length">
            <div v-for="(exception, index) in activeAndonExceptions" :key="String(exception.id || index)" class="alert alert-error dashboard-andon-exception">
              <span class="alert-icon">!</span>
              <div>
                <strong>{{ exception.exceptionType || exception.reasonName || '安灯异常' }}</strong><br>
                <span class="text-subtle">产线 {{ exception.lineId || '-' }} · 上报时间 {{ exception.triggeredAt || '-' }} · {{ exception.status || '-' }}</span>
              </div>
            </div>
          </div>
          <p v-else class="text-muted">暂无未关闭安灯异常。</p>
        </div>

        <div v-if="false" class="card" data-od-id="dashboard-recent">
          <div class="card-header">
            <h2 class="card-title">近期事件</h2>
          </div>
          <table class="data-table" style="border:none;">
            <tbody>
              <tr><td style="width:60px;" class="text-muted">09:28</td><td>MO-0032 完成绕线工序 420/500 台</td></tr>
              <tr><td style="width:60px;" class="text-muted">09:15</td><td>质量巡检 — 总装线 A 抽检 20 台，全部合格</td></tr>
              <tr><td style="width:60px;" class="text-muted">08:50</td><td>设备点检完成 — 注塑机 #2 更换模具（FS-40 底座）</td></tr>
              <tr><td style="width:60px;" class="text-muted">08:30</td><td>早会 — 今日排产 5 张工单，目标产出 1,500 台</td></tr>
              <tr><td style="width:60px;" class="text-muted">08:00</td><td>开班 — 到岗 86/88 人（请假 2 人）</td></tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>

    <section class="card dashboard-line-overview" data-od-id="dashboard-line-overview">
      <div class="card-header">
        <h2 class="card-title">产线运行概览</h2>
        <span class="text-subtle">{{ manufacturingLines.filter((line) => line.active).length }} 条运行中</span>
      </div>
      <div class="data-table-wrap">
        <table class="data-table" style="border:none;">
          <thead>
            <tr><th>产线</th><th>当前工单</th><th>产品</th><th>生产进度</th><th>OEE</th><th>不良数</th></tr>
          </thead>
          <tbody>
            <tr v-if="!manufacturingLines.length"><td colspan="6" class="text-muted">暂无产线运行数据</td></tr>
            <tr v-for="line in manufacturingLines" :key="line.lineId || line.lineCode || line.lineName">
              <td><strong>{{ line.lineName || line.lineCode || '-' }}</strong></td>
              <td class="cell-mono">{{ line.workOrderNo || '-' }}</td>
              <td>{{ line.productName || '-' }}</td>
              <td class="cell-progress"><div class="progress-bar"><div class="progress-fill" :style="{ width: `${Math.min(100, Number(line.completionRate || 0))}%` }"></div></div><span class="progress-label">{{ formatPercent(line.completionRate || 0) }}</span></td>
              <td>{{ formatPercent(line.oee || 0) }}</td>
              <td>{{ formatDisplayNumber(line.defectQty || 0) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>

  <!-- Status Bar -->
  <footer class="app-statusbar" data-od-id="dashboard-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">在线设备 22/24</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item"><span class="dot warn"></span>安灯活跃 {{ andonCount }}</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工 (生产主管)</span>
  </footer>
</MesLayout>
</template>

<style scoped>
.dashboard-andon-exception {
  background: #fff;
  border: 1px solid #2563eb;
  color: #000;
}

.dashboard-andon-exception .text-subtle {
  color: #000;
}

.dashboard-line-overview {
  margin-top: var(--space-5);
}
</style>
