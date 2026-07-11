<template>
  <section class="siemens-page quality-monitoring">
    <header class="siemens-page-header">
      <div>
        <h1>{{ $t('quality.pageTitle') }}</h1>
        <p>{{ $t('quality.pageDesc') }}</p>
      </div>
      <div class="siemens-actions">
        <button class="siemens-btn">{{ $t('quality.failureCatalog') }}</button>
        <button class="siemens-btn primary">{{ $t('quality.newInspection') }}</button>
      </div>
    </header>

    <div class="siemens-content quality-layout">
      <section class="siemens-grid quality-top">
        <aside class="siemens-panel">
          <header>
            <h2>{{ $t('quality.defectTypes') }}</h2>
            <span class="siemens-muted">{{ $t('quality.today') }}</span>
          </header>
          <div class="siemens-panel-body defect-list">
            <div v-for="defect in defectTypes" :key="defect.name" class="defect-row">
              <div>
                <strong>{{ defect.name }}</strong>
                <span>{{ defect.count }}</span>
              </div>
              <div class="siemens-progress"><span :style="{ width: Math.min(defect.count * 6, 100) + '%' }"></span></div>
            </div>
            <p v-if="!loading && defectTypes.length === 0" class="api-state">{{ $t('quality.noDefectData') }}</p>
          </div>
        </aside>

        <main class="siemens-panel">
          <header>
            <h2>{{ $t('quality.inspectionTasks') }}</h2>
            <span class="siemens-muted">{{ $t('quality.inspectionTypes') }}</span>
          </header>
          <div class="siemens-panel-body siemens-scroll">
            <!-- Detail View -->
            <template v-if="selectedTaskId">
              <div class="detail-toolbar">
                <button class="siemens-btn" @click="clearSelection">← {{ $t('quality.backToList') }}</button>
              </div>
              <div v-if="detailLoading" class="api-state">{{ $t('quality.loading') }}</div>
              <dl v-else-if="selectedTaskDetail" class="detail-list">
                <template v-for="field in detailFields" :key="field.key">
                  <dt>{{ field.label }}</dt>
                  <dd>{{ selectedTaskDetail[field.key] ?? selectedTaskDetail[field.altKey] ?? '-' }}</dd>
                </template>
              </dl>
              <p v-else class="api-state error">{{ $t('quality.detailLoadError') }}</p>
            </template>
            <!-- Table View -->
            <table v-else class="siemens-table">
              <thead>
                <tr><th>{{ $t('quality.task') }}</th><th>{{ $t('quality.workOrder') }}</th><th>{{ $t('quality.product') }}</th><th>{{ $t('quality.type') }}</th><th>{{ $t('quality.result') }}</th><th>{{ $t('quality.passRate') }}</th></tr>
              </thead>
              <tbody>
                <tr v-for="task in qualityRows" :key="task.rowKey"
                  :class="{ 'row-selected': selectedTaskId === task.task }"
                  @click="selectTask(task)">
                  <td><strong class="mono">{{ task.task }}</strong></td>
                  <td><span class="mono">{{ task.order }}</span></td>
                  <td>{{ task.product }}</td>
                  <td>{{ task.type }}</td>
                  <td><span :class="['siemens-status', statusTone(task.result)]">{{ task.result }}</span></td>
                  <td>{{ task.passRate }}%</td>
                </tr>
                <tr v-if="!loading && qualityRows.length === 0">
                  <td colspan="6">{{ $t('quality.noInspectionData') }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </main>

        <aside class="siemens-panel">
          <header>
            <h2>{{ $t('quality.nonconformanceClosure') }}</h2>
            <span class="siemens-status danger">{{ $t('quality.openCount', { count: qualityAlerts.length }) }}</span>
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
            <p v-if="!loading && qualityAlerts.length === 0" class="api-state">{{ $t('quality.noNonconformanceData') }}</p>
          </div>
        </aside>
      </section>

      <p v-if="loading" class="api-state">{{ $t('quality.loading') }}</p>
      <p v-if="error" class="api-state error">{{ error }}</p>

      <section class="siemens-grid quality-bottom">
        <article class="siemens-panel">
          <header>
            <h2>{{ $t('quality.passRateTrend') }}</h2>
            <span class="siemens-muted">{{ $t('quality.shiftA') }}</span>
          </header>
          <div class="siemens-panel-body">
            <div class="siemens-mini-chart">
              <i v-for="(value, index) in passRateTrend" :key="index" :style="{ height: value - 12 + '%' }"></i>
            </div>
          </div>
        </article>

        <article class="siemens-panel">
          <header>
            <h2>{{ $t('quality.dispositionFlow') }}</h2>
          </header>
          <div class="siemens-panel-body disposition-panel">
            <div class="siemens-step-line">
              <span class="siemens-step done">{{ $t('quality.detected') }}</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step active">{{ $t('quality.judgement') }}</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step warn">{{ $t('quality.rework') }}</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step">{{ $t('quality.recheck') }}</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step">{{ $t('quality.closed') }}</span>
            </div>
            <div class="disposition-table-wrap">
              <table class="siemens-table">
                <thead>
                  <tr><th>{{ $t('quality.node') }}</th><th>{{ $t('quality.owner') }}</th><th>{{ $t('quality.time') }}</th><th>{{ $t('quality.status') }}</th></tr>
                </thead>
                <tbody>
              <tr v-for="item in dispositionRows" :key="item.node">
                <td>{{ item.node }}</td>
                <td>{{ item.owner }}</td>
                <td>{{ item.time }}</td>
                <td><span :class="['siemens-status', statusTone(item.status)]">{{ item.status }}</span></td>
              </tr>
              <tr v-if="!loading && dispositionRows.length === 0">
                <td colspan="4">{{ $t('quality.noDispositionData') }}</td>
              </tr>
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
import { useI18n } from 'vue-i18n'
import { getDefectRecords, getInspectionDetail, getInspectionResults, getQualityInspections } from '../../api/quality'

const { t } = useI18n()

const qualityRows = ref([])
const defectTypes = ref([])
const qualityAlerts = ref([])
const dispositionRows = ref([])
const passRateTrend = ref([])
const loading = ref(false)
const error = ref('')
const selectedTaskId = ref(null)
const selectedTaskDetail = ref(null)
const detailLoading = ref(false)

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapInspection = (row, index) => ({
  rowKey: `${row.inspectionNo || row.inspection_no || row.id}-${index}`,
  task: row.inspectionNo || row.inspection_no || row.id,
  order: row.workOrderNo || row.work_order_no || '-',
  product: row.productName || row.product_name || (row.productId || row.product_id || '-'),
  type: row.inspectionType || row.inspection_type || '-',
  result: row.finalResult || row.final_result || row.status || '-',
  passRate: row.passRate || row.pass_rate || (row.status === 'PASS' ? 100 : 0),
  rawId: row.id
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
    const key = row.defectReasonName || row.defect_reason_name || row.defectReasonId || row.defect_reason_id || 'Unknown'
    acc[key] = (acc[key] || 0) + 1
    return acc
  }, {})
  return Object.entries(counts).map(([name, count]) => ({ name, count }))
}

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    const [inspections, defects, results] = await Promise.all([
      getQualityInspections(),
      getDefectRecords(),
      getInspectionResults()
    ])
    const inspectionRecords = recordsOf(inspections)
    const defectRecords = recordsOf(defects)
    const resultRecords = recordsOf(results)
    qualityRows.value = inspectionRecords.map(mapInspection)
    qualityAlerts.value = defectRecords.map(mapAlert)
    defectTypes.value = summarizeDefects(defectRecords)
    dispositionRows.value = defectRecords.slice(0, 5).map((row) => ({
      node: row.defectNo || row.defect_no || row.id,
      owner: row.ownerName || row.owner_name || row.handlerId || row.handler_id || '-',
      time: row.updatedAt || row.updated_at || row.createdAt || row.created_at || '-',
      status: row.status || '-'
    }))
    passRateTrend.value = resultRecords
      .map((row) => Number(row.passRate ?? row.pass_rate ?? row.qualifiedRate ?? row.qualified_rate))
      .filter((value) => Number.isFinite(value))
  } catch (e) {
    qualityRows.value = []
    qualityAlerts.value = []
    defectTypes.value = []
    dispositionRows.value = []
    passRateTrend.value = []
    error.value = e?.message || t('quality.apiError')
  } finally {
    loading.value = false
  }
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

const selectTask = async (task) => {
  selectedTaskId.value = task.task
  detailLoading.value = true
  selectedTaskDetail.value = null
  try {
    const id = task.rawId || task.task
    const res = await getInspectionDetail(id)
    selectedTaskDetail.value = res?.data ?? res ?? null
  } catch (e) {
    selectedTaskDetail.value = null
    error.value = e?.message || t('quality.apiError')
  } finally {
    detailLoading.value = false
  }
}

const clearSelection = () => {
  selectedTaskId.value = null
  selectedTaskDetail.value = null
}

const detailFields = [
  { key: 'inspectionNo', altKey: 'inspection_no', label: t('quality.task') },
  { key: 'workOrderNo', altKey: 'work_order_no', label: t('quality.workOrder') },
  { key: 'productName', altKey: 'product_name', label: t('quality.product') },
  { key: 'inspectionType', altKey: 'inspection_type', label: t('quality.type') },
  { key: 'finalResult', altKey: 'final_result', label: t('quality.result') },
  { key: 'passRate', altKey: 'pass_rate', label: t('quality.passRate') },
  { key: 'status', altKey: null, label: '状态' },
  { key: 'inspectorName', altKey: 'inspector_name', label: '检验员' },
  { key: 'inspectionDate', altKey: 'inspection_date', label: '检验日期' },
  { key: 'remark', altKey: 'remarks', label: '备注' }
]

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

.siemens-table tbody tr.row-selected {
  background: var(--mes-primary-soft);
  outline: 2px solid var(--mes-primary);
  outline-offset: -2px;
}

.siemens-table tbody tr {
  cursor: pointer;
}

.siemens-table tbody tr:hover {
  background: var(--mes-bg-subtle);
}

.siemens-table tbody tr.row-selected:hover {
  background: var(--mes-primary-soft);
}

.detail-toolbar {
  margin-bottom: 16px;
}

.detail-list {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 8px 16px;
  align-content: start;
}

.detail-list dt {
  color: var(--mes-text-secondary);
  font-size: var(--mes-font-sm);
  font-weight: var(--mes-font-medium);
}

.detail-list dd {
  color: var(--mes-text-primary);
  font-size: var(--mes-font-base);
}
</style>
