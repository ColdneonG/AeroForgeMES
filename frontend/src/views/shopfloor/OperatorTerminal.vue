<template>
  <section class="siemens-page operator-station">
    <header class="siemens-page-header">
      <div>
        <h1>Operator Station</h1>
        <p>Station work guidance, material verification, operation execution and completion reporting</p>
      </div>
      <div class="siemens-actions">
        <button class="siemens-btn danger">Report Issue</button>
        <button class="siemens-btn primary">Call Leader</button>
      </div>
    </header>

    <p v-if="loading" class="api-state">Loading operator tasks...</p>
    <p v-if="error" class="api-state error">{{ error }}</p>

    <div class="siemens-content operator-layout">
      <section class="station-strip">
        <div><span>Machine</span><strong>{{ station.machine }}</strong></div>
        <div><span>Machine Status</span><strong>{{ station.machineStatus }}</strong></div>
        <div><span>Work Order</span><strong>{{ station.workOrder }}</strong></div>
        <div><span>Serial Number</span><strong>{{ station.serialNo }}</strong></div>
        <div><span>Active User</span><strong>{{ station.activeUser }}</strong></div>
        <div><span>Remaining Time</span><strong>{{ station.remainingTime }}</strong></div>
      </section>

      <section class="siemens-grid operator-main">
        <aside class="siemens-panel">
          <header>
            <h2>Current Order / Steps</h2>
            <span class="siemens-status">{{ selectedTask?.status || 'No data' }}</span>
          </header>
          <div class="siemens-panel-body step-list">
            <article class="siemens-work-card active">
              <h3>{{ selectedTask?.id || '-' }}</h3>
              <p>{{ selectedTask?.product || '-' }} / {{ selectedTask?.line || '-' }} / {{ selectedTask?.process || '-' }}</p>
              <input class="station-input" placeholder="Scan serial number" />
            </article>

            <div class="siemens-scroll task-list">
              <article
                v-for="task in tasks"
                :key="task.id"
                class="siemens-work-card"
                :class="{ active: task.id === selectedTask?.id }"
              >
                <div class="task-head">
                  <h3>{{ task.process }}</h3>
                  <span :class="['siemens-status', statusTone(task.status)]">{{ task.status }}</span>
                </div>
                <p>{{ task.id }} / {{ task.product }}</p>
              </article>
              <p v-if="!loading && tasks.length === 0" class="api-state">No operator task data.</p>
            </div>
          </div>
        </aside>

        <main class="siemens-panel">
          <header>
            <h2>Work Instruction / Assembly Operation</h2>
            <span class="siemens-muted">Mock instruction disabled</span>
          </header>
          <div class="siemens-panel-body instruction-panel">
            <div class="instruction-visual">
              <div>
                <strong>{{ selectedTask?.process || 'No operation selected' }}</strong>
                <span>{{ selectedTask?.id || 'Waiting for backend task data' }}</span>
              </div>
            </div>
          </div>
        </main>

        <aside class="siemens-panel">
          <header>
            <h2>Product / Material / Actions</h2>
          </header>
          <div class="siemens-panel-body action-panel">
            <table class="siemens-table">
              <tbody>
                <tr><td>Product</td><td><strong>{{ selectedTask?.product || '-' }}</strong></td></tr>
                <tr><td>Serial No.</td><td><span class="mono">-</span></td></tr>
                <tr><td>Batch</td><td><span class="mono">-</span></td></tr>
                <tr><td>Operation</td><td>{{ selectedTask?.process || '-' }}</td></tr>
                <tr><td>Status</td><td>{{ selectedTask?.status || '-' }}</td></tr>
              </tbody>
            </table>

            <div class="operator-actions">
              <button class="siemens-btn primary">Start</button>
              <button class="siemens-btn">Pause</button>
              <button class="siemens-btn primary wide">Complete</button>
              <button class="siemens-btn danger">Defect</button>
            </div>
          </div>
        </aside>
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getShopTasks } from '../../api/production'
import { authState } from '../../stores/auth'

const tasks = ref([])
const loading = ref(false)
const error = ref('')
const station = computed(() => ({
  machine: '-',
  machineStatus: '-',
  workOrder: selectedTask.value?.order || '-',
  serialNo: '-',
  activeUser: authState.user?.displayName || '-',
  remainingTime: '-'
}))
const selectedTask = computed(() => tasks.value[0] || null)

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])
const mapTask = (row) => ({
  id: row.taskNo || row.task_no || row.id,
  order: row.workOrderNo || row.work_order_no || row.workOrderId || row.work_order_id || '-',
  product: row.productName || row.product_name || row.productId || row.product_id || '-',
  process: row.processName || row.process_name || row.routeId || row.route_id || '-',
  line: row.lineName || row.line_name || row.lineId || row.line_id || '-',
  status: row.status || '-'
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    tasks.value = recordsOf(await getShopTasks()).map(mapTask)
  } catch (e) {
    tasks.value = []
    error.value = e?.message || 'Operator task API is not connected yet.'
  } finally {
    loading.value = false
  }
}

const statusTone = (status) => ({
  RUNNING: 'running',
  COMPLETED: 'ok',
  PASS: 'ok',
  PENDING: '',
  FAILED: 'danger',
  ERROR: 'danger'
}[String(status || '').toUpperCase()] || '')

onMounted(loadRows)
</script>

<style scoped>
.operator-layout {
  grid-template-rows: auto minmax(0, 1fr);
}

.station-strip {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  min-height: 58px;
  background: #f7f9fa;
  border: 1px solid #d7dde1;
}

.station-strip div {
  padding: 8px 12px;
  border-right: 1px solid #e2e8ec;
}

.station-strip div:last-child {
  border-right: 0;
}

.station-strip span,
.station-strip strong {
  display: block;
}

.station-strip span {
  color: #71818b;
  font-size: 11px;
}

.station-strip strong {
  margin-top: 4px;
}

.operator-main {
  grid-template-columns: 390px minmax(0, 1fr) 430px;
}

.step-list,
.instruction-panel,
.action-panel {
  display: grid;
  gap: 14px;
  min-height: 0;
}

.task-list {
  display: grid;
  align-content: start;
  gap: 10px;
}

.task-head {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.station-input {
  width: 100%;
  height: 32px;
  margin-top: 12px;
  padding: 0 10px;
  border: 1px solid #b9c8d1;
}

.instruction-visual {
  display: grid;
  min-height: 360px;
  place-items: center;
  background: #f7f9fa;
  border: 1px solid #d7dde1;
}

.instruction-visual strong,
.instruction-visual span {
  display: block;
  text-align: center;
}

.instruction-visual strong {
  color: #00799f;
  font-size: 24px;
  font-weight: 650;
}

.instruction-visual span {
  margin-top: 8px;
  color: #71818b;
}

.operator-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.operator-actions .siemens-btn {
  min-height: 44px;
}

.operator-actions .wide {
  grid-column: 1 / -1;
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
