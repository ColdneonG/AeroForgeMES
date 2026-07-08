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

    <div class="siemens-content work-order-layout">
      <div class="siemens-toolbar">
        <input value="WO202607" />
        <select><option>All status</option><option>In progress</option><option>Exception</option></select>
        <select><option>All lines</option><option>Assembly Line 01</option><option>Motor Line</option></select>
        <button class="siemens-btn primary">Search</button>
        <span class="siemens-muted">Updated 14:30 / Shift A</span>
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
              :class="{ active: index === 0, warn: order.status === '暂停', danger: order.status === '异常' }"
            >
              <div class="order-card-head">
                <h3>{{ order.id }}</h3>
                <span :class="['siemens-status', statusTone(order.status)]">{{ order.status }}</span>
              </div>
              <p>{{ order.product }} / {{ order.line }} / {{ order.process }}</p>
              <div class="order-card-progress">
                <strong>{{ order.done }} / {{ order.plan }}</strong>
                <span>{{ order.progress }}%</span>
              </div>
              <div class="siemens-progress"><span :style="{ width: order.progress + '%' }"></span></div>
            </article>
          </div>
        </aside>

        <main class="siemens-panel">
          <header>
            <h2>Routing / Current Operation</h2>
            <span class="siemens-status running">In progress</span>
          </header>
          <div class="siemens-panel-body selected-order">
            <section>
              <h3><span class="mono">WO20260706001</span> / FS-500 Floor Fan</h3>
              <div class="siemens-kv">
                <div><span>Responsible Team</span><strong>Assembly A</strong></div>
                <div><span>Target Completion</span><strong>18:20</strong></div>
                <div><span>Station</span><strong>A03</strong></div>
                <div><span>Current Process</span><strong>Assembly</strong></div>
              </div>
            </section>

            <section class="siemens-step-line">
              <template v-for="(step, index) in productionSteps" :key="step">
                <span class="siemens-step" :class="{ done: index < 5, active: index === 5, warn: index === 7 }">{{ step }}</span>
                <span v-if="index < productionSteps.length - 1" class="siemens-step-join"></span>
              </template>
            </section>

            <section class="route-visual">
              <div class="route-node active">Assembly</div>
              <div class="route-meta">
                <strong>Operation OP-ASM-060</strong>
                <span>Cycle time 42s / operator Liu / scanner gun 2#</span>
              </div>
            </section>
          </div>
        </main>

        <aside class="siemens-panel">
          <header>
            <h2>Execution State</h2>
            <span class="siemens-status warn">1 alert</span>
          </header>
          <div class="siemens-panel-body execution-side">
            <div class="siemens-kv">
              <div><span>Plan Qty.</span><strong>420</strong></div>
              <div><span>Finished</span><strong>326</strong></div>
              <div><span>OEE</span><strong>88%</strong></div>
              <div><span>Defects</span><strong>1</strong></div>
            </div>
            <table class="siemens-table">
              <thead>
                <tr><th>Record</th><th>Owner</th><th>Time</th><th>Result</th></tr>
              </thead>
              <tbody>
                <tr v-for="record in records" :key="record.name">
                  <td>{{ record.name }}</td>
                  <td>{{ record.owner }}</td>
                  <td>{{ record.time }}</td>
                  <td><span :class="['siemens-status', statusTone(record.status)]">{{ record.status }}</span></td>
                </tr>
              </tbody>
            </table>
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
                <td><span :class="['siemens-status', statusTone(order.status)]">{{ order.status }}</span></td>
                <td>{{ order.plan }}</td>
                <td>{{ order.done }}</td>
                <td><div class="siemens-progress"><span :style="{ width: order.progress + '%' }"></span></div></td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { workOrders, productionSteps } from '../../mock/mesData'

const orderRows = [...workOrders, ...workOrders.slice(0, 5)].map((order, index) => ({
  ...order,
  rowKey: `${order.id}-${index}`
}))

const records = [
  { name: 'Start confirmation', owner: 'Wang', time: '08:12', status: '已完成' },
  { name: 'Material check', owner: 'Chen', time: '08:18', status: '合格' },
  { name: 'Assembly execution', owner: 'Liu', time: 'Running', status: '进行中' },
  { name: 'Torque capture', owner: 'Liu', time: 'Pending', status: '待开始' }
]

const statusTone = (status) => ({
  运行: 'running',
  进行中: 'running',
  合格: 'ok',
  已完成: 'ok',
  暂停: 'warn',
  待开始: '',
  待检: '',
  异常: 'danger',
  不合格: 'danger'
}[status] || '')
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
  grid-template-rows: auto auto minmax(0, 1fr);
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
</style>
