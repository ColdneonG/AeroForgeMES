<template>
  <section class="siemens-page quality-monitoring">
    <header class="siemens-page-header">
      <div>
        <h1>Quality Monitoring</h1>
        <p>Inspection tasks, defect categories, quality trend and nonconformance closure</p>
      </div>
      <div class="siemens-actions">
        <button class="siemens-btn">Failure Catalog</button>
        <button class="siemens-btn primary">New Inspection</button>
      </div>
    </header>

    <div class="siemens-content quality-layout">
      <section class="siemens-grid quality-top">
        <aside class="siemens-panel">
          <header>
            <h2>Defect Types</h2>
            <span class="siemens-muted">Today</span>
          </header>
          <div class="siemens-panel-body defect-list">
            <div v-for="defect in defectTypes" :key="defect.name" class="defect-row">
              <div>
                <strong>{{ defect.name }}</strong>
                <span>{{ defect.count }}</span>
              </div>
              <div class="siemens-progress"><span :style="{ width: Math.min(defect.count * 6, 100) + '%' }"></span></div>
            </div>
          </div>
        </aside>

        <main class="siemens-panel">
          <header>
            <h2>Inspection Tasks</h2>
            <span class="siemens-muted">IQC / PQC / FQC</span>
          </header>
          <div class="siemens-panel-body siemens-scroll">
            <table class="siemens-table">
              <thead>
                <tr><th>Task</th><th>Product</th><th>Type</th><th>Result</th><th>Pass Rate</th></tr>
              </thead>
              <tbody>
                <tr v-for="task in qualityRows" :key="task.rowKey">
                  <td><strong class="mono">{{ task.task }}</strong><br><span class="siemens-muted">{{ task.order }}</span></td>
                  <td>{{ task.product }}</td>
                  <td>{{ task.type }}</td>
                  <td><span :class="['siemens-status', statusTone(task.result)]">{{ task.result }}</span></td>
                  <td>{{ task.passRate }}%</td>
                </tr>
              </tbody>
            </table>
          </div>
        </main>

        <aside class="siemens-panel">
          <header>
            <h2>Nonconformance Closure</h2>
            <span class="siemens-status danger">{{ qualityAlerts.length }} open</span>
          </header>
          <div class="siemens-panel-body alert-column siemens-scroll">
            <article v-for="alert in qualityAlerts" :key="alert.code" class="siemens-work-card danger">
              <h3>{{ alert.issue }}</h3>
              <p><span class="mono">{{ alert.code }}</span> / <span class="mono">{{ alert.order }}</span></p>
              <div class="alert-foot">
                <span :class="['siemens-status', statusTone(alert.status)]">{{ alert.status }}</span>
                <span class="siemens-muted">{{ alert.owner }}</span>
              </div>
            </article>
          </div>
        </aside>
      </section>

      <section class="siemens-grid quality-bottom">
        <article class="siemens-panel">
          <header>
            <h2>Pass Rate Trend</h2>
            <span class="siemens-muted">Shift A / %</span>
          </header>
          <div class="siemens-panel-body">
            <div class="siemens-mini-chart">
              <i v-for="(value, index) in passRateTrend" :key="index" :style="{ height: value - 12 + '%' }"></i>
            </div>
          </div>
        </article>

        <article class="siemens-panel">
          <header>
            <h2>Disposition Flow</h2>
          </header>
          <div class="siemens-panel-body disposition-panel">
            <div class="siemens-step-line">
              <span class="siemens-step done">Detected</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step active">Judgement</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step warn">Rework</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step">Recheck</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step">Closed</span>
            </div>
            <div class="disposition-table-wrap">
              <table class="siemens-table">
                <thead>
                  <tr><th>Node</th><th>Owner</th><th>Time</th><th>Status</th></tr>
                </thead>
                <tbody>
                  <tr><td>Register</td><td>Li</td><td>10:18</td><td><span class="siemens-status ok">Done</span></td></tr>
                  <tr><td>Judgement</td><td>Zhou</td><td>10:31</td><td><span class="siemens-status running">Running</span></td></tr>
                  <tr><td>Rework dispatch</td><td>Wang</td><td>Pending</td><td><span class="siemens-status">Pending</span></td></tr>
                </tbody>
              </table>
            </div>
          </div>
        </article>
      </section>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { qualityTasks, defectTypes as mockDefectTypes, qualityAlerts as mockQualityAlerts } from '../../mock/mesData'
import { mesApi } from '../../services/mesApi'

const useMock = import.meta.env.VITE_USE_MOCK === 'true'

const qualityRows = ref([...qualityTasks, ...qualityTasks.slice(0, 3)].map((task, index) => ({
  ...task,
  rowKey: `${task.task}-${index}`
})))
const defectTypes = ref(mockDefectTypes)
const qualityAlerts = ref(mockQualityAlerts)
const passRateTrend = [96, 97, 98, 97, 99, 98, 96, 95, 98, 99, 98, 97]

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || [])

const mapInspection = (row, index) => ({
  rowKey: `${row.inspectionNo || row.inspection_no || row.id}-${index}`,
  task: row.inspectionNo || row.inspection_no || row.id,
  order: row.workOrderNo || row.work_order_no || row.taskId || row.task_id || '-',
  product: row.productName || row.product_name || (row.productId || row.product_id || '-'),
  type: row.inspectionType || row.inspection_type || '-',
  result: row.result || row.status || '-',
  passRate: row.passRate || row.pass_rate || (row.status === 'PASS' ? 100 : 0)
})

const mapAlert = (row) => ({
  code: row.defectNo || row.defect_no || row.id,
  issue: row.defectType || row.defect_type || row.sourceType || row.source_type || 'Defect',
  order: row.workOrderNo || row.work_order_no || row.sourceId || row.source_id || '-',
  status: row.status || '-',
  owner: row.ownerName || row.owner_name || row.handlerId || row.handler_id || '-'
})

const summarizeDefects = (rows) => {
  const counts = rows.reduce((acc, row) => {
    const key = row.defectType || row.defect_type || row.sourceType || row.source_type || 'Unknown'
    acc[key] = (acc[key] || 0) + 1
    return acc
  }, {})
  return Object.entries(counts).map(([name, count]) => ({ name, count }))
}

const loadRows = async () => {
  if (useMock) return
  const [inspections, defects] = await Promise.all([
    mesApi.quality.inspections(),
    mesApi.quality.defects()
  ])
  const inspectionRecords = recordsOf(inspections)
  const defectRecords = recordsOf(defects)
  qualityRows.value = inspectionRecords.map(mapInspection)
  qualityAlerts.value = defectRecords.map(mapAlert)
  defectTypes.value = summarizeDefects(defectRecords)
}

const statusTone = (status) => ({
  合格: 'ok',
  已完成: 'ok',
  PASS: 'ok',
  COMPLETED: 'ok',
  进行中: 'running',
  PROCESSING: 'running',
  返工: 'warn',
  待检: '',
  不合格: 'danger',
  FAIL: 'danger',
  异常: 'danger'
}[status] || '')

onMounted(loadRows)
</script>

<style scoped>
.quality-layout {
  grid-template-rows: minmax(0, 1fr) minmax(250px, 0.58fr);
}

.quality-top {
  grid-template-columns: 360px minmax(0, 1fr) 420px;
}

.quality-bottom {
  grid-template-columns: minmax(0, 1.2fr) minmax(520px, 0.9fr);
}

.defect-list,
.alert-column {
  display: grid;
  align-content: start;
  gap: 12px;
}

.defect-row > div:first-child,
.alert-foot {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.defect-row .siemens-progress {
  margin-top: 8px;
}

.disposition-panel {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  gap: 12px;
  overflow: hidden;
}

.disposition-table-wrap {
  overflow-y: auto;
  min-height: 0;
}
</style>
