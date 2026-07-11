<template>
  <section class="siemens-page operator-station">
    <header class="siemens-page-header">
      <div>
        <h1>{{ $t('shopfloor.operatorTerminal.pageTitle') }}</h1>
        <p>{{ $t('shopfloor.operatorTerminal.pageDesc') }}</p>
      </div>
      <div class="siemens-actions">
        <button class="siemens-btn danger">{{ $t('shopfloor.operatorTerminal.reportIssue') }}</button>
        <button class="siemens-btn primary">{{ $t('shopfloor.operatorTerminal.callLeader') }}</button>
      </div>
    </header>

    <p v-if="loading" class="api-state">{{ $t('shopfloor.operatorTerminal.loading') }}</p>
    <p v-if="error" class="api-state error">{{ error }}</p>

    <div class="siemens-content operator-layout">
      <section class="station-strip">
        <div><span>{{ $t('shopfloor.operatorTerminal.machine') }}</span><strong>{{ station.machine }}</strong></div>
        <div><span>{{ $t('shopfloor.operatorTerminal.machineStatus') }}</span><strong>{{ station.machineStatus }}</strong></div>
        <div><span>{{ $t('shopfloor.operatorTerminal.workOrder') }}</span><strong>{{ station.workOrder }}</strong></div>
        <div><span>{{ $t('shopfloor.operatorTerminal.serialNumber') }}</span><strong>{{ station.serialNo }}</strong></div>
        <div><span>{{ $t('shopfloor.operatorTerminal.activeUser') }}</span><strong>{{ station.activeUser }}</strong></div>
        <div><span>{{ $t('shopfloor.operatorTerminal.remainingTime') }}</span><strong>{{ station.remainingTime }}</strong></div>
      </section>

      <section class="siemens-grid operator-main">
        <aside class="siemens-panel">
          <header>
            <h2>{{ $t('shopfloor.operatorTerminal.currentOrderSteps') }}</h2>
            <span class="siemens-status">{{ selectedTask?.status || $t('shopfloor.operatorTerminal.noData') }}</span>
          </header>
          <div class="siemens-panel-body step-list">
            <article class="siemens-work-card active">
              <h3>{{ selectedTask?.id || '-' }}</h3>
              <p>{{ selectedTask?.product || '-' }} / {{ selectedTask?.line || '-' }} / {{ selectedTask?.process || '-' }}</p>
              <input class="station-input" :placeholder="$t('shopfloor.operatorTerminal.scanPlaceholder')" />
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
              <p v-if="!loading && tasks.length === 0" class="api-state">{{ $t('shopfloor.operatorTerminal.noTaskData') }}</p>
            </div>
          </div>
        </aside>

        <main class="siemens-panel">
          <header>
            <h2>{{ $t('shopfloor.operatorTerminal.workInstruction') }}</h2>
            <span class="siemens-muted">{{ $t('shopfloor.operatorTerminal.mockDisabled') }}</span>
          </header>
          <div class="siemens-panel-body instruction-panel">
            <div class="instruction-visual">
              <div>
                <strong>{{ selectedTask?.process || $t('shopfloor.operatorTerminal.noOperationSelected') }}</strong>
                <span>{{ selectedTask?.id || $t('shopfloor.operatorTerminal.waitingBackend') }}</span>
              </div>
            </div>
          </div>
        </main>

        <aside class="siemens-panel">
          <header>
            <h2>{{ $t('shopfloor.operatorTerminal.productMaterialActions') }}</h2>
          </header>
          <div class="siemens-panel-body action-panel">
            <table class="siemens-table">
              <tbody>
                <tr><td>{{ $t('shopfloor.operatorTerminal.product') }}</td><td><strong>{{ selectedTask?.product || '-' }}</strong></td></tr>
                <tr><td>{{ $t('shopfloor.operatorTerminal.serialNo') }}</td><td><span class="mono">-</span></td></tr>
                <tr><td>{{ $t('shopfloor.operatorTerminal.batch') }}</td><td><span class="mono">-</span></td></tr>
                <tr><td>{{ $t('shopfloor.operatorTerminal.operation') }}</td><td>{{ selectedTask?.process || '-' }}</td></tr>
                <tr><td>{{ $t('shopfloor.operatorTerminal.status') }}</td><td>{{ selectedTask?.status || '-' }}</td></tr>
              </tbody>
            </table>

            <div class="operator-actions">
              <button class="siemens-btn primary">{{ $t('shopfloor.operatorTerminal.start') }}</button>
              <button class="siemens-btn">{{ $t('shopfloor.operatorTerminal.pause') }}</button>
              <button class="siemens-btn primary wide">{{ $t('shopfloor.operatorTerminal.complete') }}</button>
              <button class="siemens-btn danger">{{ $t('shopfloor.operatorTerminal.defect') }}</button>
            </div>
          </div>
        </aside>
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { getShopTasks } from '../../api/production'
import { authState } from '../../stores/auth'

const { t } = useI18n()

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
    error.value = e?.message || t('shopfloor.operatorTerminal.apiError')
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
  background: var(--mes-bg-subtle);
  border: 1px solid var(--mes-border);
}

.station-strip div {
  padding: 8px 12px;
  border-right: 1px solid var(--mes-border-soft);
}

.station-strip div:last-child {
  border-right: 0;
}

.station-strip span,
.station-strip strong {
  display: block;
}

.station-strip span {
  color: var(--mes-text-secondary);
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
  height: var(--mes-control-height);
  margin-top: 12px;
  padding: 0 10px;
  color: var(--mes-text-primary);
  background: var(--mes-bg-input);
  border: 1px solid var(--mes-border-input);
  border-radius: var(--mes-radius-sm);
}

.instruction-visual {
  display: grid;
  min-height: 360px;
  place-items: center;
  background: var(--mes-bg-subtle);
  border: 1px solid var(--mes-border);
}

.instruction-visual strong,
.instruction-visual span {
  display: block;
  text-align: center;
}

.instruction-visual strong {
  color: var(--mes-primary);
  font-size: 24px;
  font-weight: var(--mes-font-semibold);
}

.instruction-visual span {
  margin-top: 8px;
  color: var(--mes-text-secondary);
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

</style>
