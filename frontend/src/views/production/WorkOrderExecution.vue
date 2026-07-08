<template>
  <section class="siemens-page work-order-monitoring">
    <header class="siemens-page-header">
      <div>
        <h1>Work Order Monitoring</h1>
        <p>Order execution, routing progress, current operation and shop-floor records</p>
      </div>
      <div class="siemens-actions">
        <button class="siemens-btn">Split Order</button>
        <button class="siemens-btn">Hold / Change</button>
        <button class="siemens-btn primary">Release</button>
      </div>
    </header>

    <p v-if="loading" class="api-state">Loading work orders...</p>
    <p v-if="error" class="api-state error">{{ error }}</p>

    <div class="siemens-content work-order-layout">
      <div class="siemens-toolbar">
        <input v-model="keyword" placeholder="Work order keyword" />
        <button class="siemens-btn primary" @click="loadRows">Search</button>
        <span class="siemens-muted">Mock execution data disabled</span>
      </div>

      <section class="siemens-grid work-order-main">
        <aside class="siemens-panel">
          <header>
            <h2>Work Orders</h2>
            <span class="siemens-muted">{{ orderRows.length }} items</span>
          </header>
          <div class="siemens-panel-body siemens-scroll order-card-list">
            <article
              v-for="(order, index) in orderRows"
              :key="order.rowKey"
              class="siemens-work-card"
              :class="{ active: index === 0, warn: order.tone === 'warn', danger: order.tone === 'danger' }"
            >
              <div class="order-card-head">
                <h3>{{ order.id }}</h3>
                <span :class="['siemens-status', order.tone]">{{ order.status }}</span>
              </div>
              <p>{{ order.product }} / {{ order.line }} / {{ order.process }}</p>
              <div class="order-card-progress">
                <strong>{{ order.done }} / {{ order.plan }}</strong>
                <span>{{ order.progress }}%</span>
              </div>
              <div class="siemens-progress"><span :style="{ width: order.progress + '%' }"></span></div>
            </article>
            <p v-if="!loading && orderRows.length === 0" class="api-state">No work order data.</p>
          </div>
        </aside>

        <main class="siemens-panel">
          <header>
            <h2>Routing / Current Operation</h2>
            <span :class="['siemens-status', selectedOrder?.tone]">{{ selectedOrder?.status || 'No data' }}</span>
          </header>
          <div class="siemens-panel-body selected-order">
            <section>
              <h3><span class="mono">{{ selectedOrder?.id || '-' }}</span> / {{ selectedOrder?.product || '-' }}</h3>
              <div class="siemens-kv">
                <div><span>Responsible Team</span><strong>-</strong></div>
                <div><span>Target Completion</span><strong>-</strong></div>
                <div><span>Station</span><strong>-</strong></div>
                <div><span>Current Process</span><strong>{{ selectedOrder?.process || '-' }}</strong></div>
              </div>
            </section>
            <section class="route-visual">
              <div class="route-node active">{{ selectedOrder?.process || '-' }}</div>
              <div class="route-meta">
                <strong>{{ selectedOrder?.id || 'No operation selected' }}</strong>
                <span>Waiting for backend routing and operation task data.</span>
              </div>
            </section>
          </div>
        </main>

        <aside class="siemens-panel">
          <header>
            <h2>Execution State</h2>
          </header>
          <div class="siemens-panel-body execution-side">
            <div class="siemens-kv">
              <div><span>Plan Qty.</span><strong>{{ selectedOrder?.plan || '-' }}</strong></div>
              <div><span>Finished</span><strong>{{ selectedOrder?.done || '-' }}</strong></div>
              <div><span>Progress</span><strong>{{ selectedOrder?.progress ?? '-' }}%</strong></div>
              <div><span>Status</span><strong>{{ selectedOrder?.status || '-' }}</strong></div>
            </div>
          </div>
        </aside>
      </section>

      <section class="siemens-panel">
        <header>
          <h2>Production Records</h2>
          <span class="siemens-muted">Recent execution details</span>
        </header>
        <div class="siemens-panel-body siemens-scroll">
          <table class="siemens-table">
            <thead>
              <tr>
                <th>Work Order</th><th>Product</th><th>Line</th><th>Process</th><th>Priority</th><th>Status</th><th>Plan</th><th>Done</th><th>Progress</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orderRows" :key="`row-${order.rowKey}`">
                <td><strong class="mono">{{ order.id }}</strong></td>
                <td>{{ order.product }}</td>
                <td>{{ order.line }}</td>
                <td>{{ order.process }}</td>
                <td>{{ order.priority }}</td>
                <td><span :class="['siemens-status', order.tone]">{{ order.status }}</span></td>
                <td>{{ order.plan }}</td>
                <td>{{ order.done }}</td>
                <td><div class="siemens-progress"><span :style="{ width: order.progress + '%' }"></span></div></td>
              </tr>
              <tr v-if="!loading && orderRows.length === 0">
                <td colspan="9">No production records.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getWorkOrders } from '../../api/production'

const keyword = ref('')
const orderRows = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])
const numberOrZero = (value) => {
  const number = Number(value)
  return Number.isFinite(number) ? number : 0
}
const toneOf = (status) => {
  const text = String(status || '').toUpperCase()
  if (['PAUSED', 'PENDING', 'WAIT_ISSUE'].includes(text)) return 'warn'
  if (['VOIDED', 'CANCELLED', 'ERROR'].includes(text)) return 'danger'
  if (['RUNNING', 'ISSUED', 'COMPLETED', 'CLOSED'].includes(text)) return 'running'
  return ''
}

const mapOrder = (row, index) => {
  const plan = numberOrZero(row.planQty ?? row.plan_qty)
  const done = numberOrZero(row.completedQty ?? row.completed_qty)
  const progress = plan > 0 ? Math.round((done / plan) * 100) : 0
  return {
    rowKey: `${row.id}-${index}`,
    id: row.workOrderNo || row.work_order_no || row.id,
    product: row.productName || row.product_name || row.productId || row.product_id || '-',
    line: row.lineName || row.line_name || row.lineId || row.line_id || '-',
    process: row.processName || row.process_name || row.routeId || row.route_id || '-',
    priority: row.priority || '-',
    status: row.status || '-',
    plan,
    done,
    progress,
    tone: toneOf(row.status),
    raw: row
  }
}

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    orderRows.value = recordsOf(await getWorkOrders({ keyword: keyword.value || undefined })).map(mapOrder)
  } catch (e) {
    orderRows.value = []
    error.value = e?.message || 'Work order API failed.'
  } finally {
    loading.value = false
  }
}

const selectedOrder = computed(() => orderRows.value[0] || null)

onMounted(loadRows)
</script>

<style scoped>
.work-order-layout {
  grid-template-rows: auto minmax(0, 1fr) minmax(190px, 0.35fr);
}

.work-order-main {
  grid-template-columns: 420px minmax(0, 1fr) 430px;
}

.order-card-list {
  display: grid;
  align-content: start;
  gap: 10px;
}

.order-card-head,
.order-card-progress {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.order-card-progress {
  align-items: baseline;
  margin: 12px 0 7px;
}

.selected-order,
.execution-side {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  gap: 16px;
  overflow: hidden;
}

.selected-order h3 {
  margin: 0 0 14px;
  font-size: 20px;
}

.route-visual {
  display: grid;
  grid-template-columns: 180px 1fr;
  gap: 18px;
  align-items: center;
  min-height: 0;
  padding: 16px 20px;
  background: #f7f9fa;
  border: 1px solid #d7dde1;
}

.route-node {
  display: grid;
  height: 80px;
  color: #ffffff;
  font-size: 20px;
  font-weight: 650;
  place-items: center;
  background: #00799f;
}

.route-meta strong,
.route-meta span {
  display: block;
}

.route-meta span {
  margin-top: 8px;
  color: #71818b;
}

.api-state {
  margin: 12px 24px;
  color: #52616b;
  font-size: 14px;
}

.api-state.error {
  color: #b42318;
}
</style>
