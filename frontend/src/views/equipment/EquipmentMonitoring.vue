<template>
  <section class="siemens-page equipment-monitoring">
    <header class="siemens-page-header">
      <div>
        <h1>{{ $t('equipment.monitoring.pageTitle') }}</h1>
        <p>{{ $t('equipment.monitoring.pageDesc') }}</p>
      </div>
      <div class="siemens-actions">
        <button class="siemens-btn">{{ $t('equipment.monitoring.inspectionRecords') }}</button>
        <button class="siemens-btn primary">{{ $t('equipment.monitoring.createWorkOrder') }}</button>
      </div>
    </header>

    <p v-if="loading" class="api-state">{{ $t('equipment.monitoring.loading') }}</p>
    <p v-if="error" class="api-state error">{{ error }}</p>

    <div class="siemens-content equipment-layout">
      <section class="siemens-grid equipment-card-grid">
        <article
          v-for="item in equipments"
          :key="item.id"
          class="siemens-work-card equipment-card"
          :class="{ active: item.tone === 'running', warn: item.tone === 'warn', danger: item.tone === 'danger' }"
        >
          <div class="equipment-head">
            <h3>{{ item.name }}</h3>
            <span :class="['siemens-status', item.tone]">{{ item.status }}</span>
          </div>
          <p><span class="mono">{{ item.id }}</span> / {{ item.area }}</p>
          <div class="equipment-oee">
            <span>{{ $t('equipment.monitoring.oee') }}</span>
            <strong>{{ item.oee }}%</strong>
          </div>
          <div class="siemens-progress"><span :style="{ width: item.oee + '%' }"></span></div>
        </article>
      </section>

      <section class="siemens-grid equipment-main">
        <article class="siemens-panel">
          <header>
            <h2>{{ $t('equipment.monitoring.industrialMetrics') }}</h2>
            <span class="siemens-muted">{{ $t('equipment.monitoring.metricsDesc') }}</span>
          </header>
          <div class="siemens-panel-body metric-board">
            <div v-for="metric in metrics" :key="metric.label" class="metric-tile">
              <span>{{ metric.label }}</span>
              <strong>{{ metric.value }}</strong>
              <div class="siemens-progress"><span :style="{ width: metric.bar }"></span></div>
            </div>
            <p v-if="!loading && metrics.length === 0" class="api-state">{{ $t('equipment.monitoring.noMetricData') }}</p>
          </div>
        </article>

        <aside class="siemens-panel">
          <header>
            <h2>{{ $t('equipment.monitoring.downtimeReasons') }}</h2>
          </header>
          <div class="siemens-panel-body downtime-list">
            <div v-for="item in downtimeReasons" :key="item.reason" class="downtime-row">
              <div>
                <strong>{{ item.reason }}</strong>
                <span>{{ item.minutes }} min</span>
              </div>
              <div class="siemens-progress"><span :style="{ width: item.minutes * 1.4 + '%' }"></span></div>
            </div>
            <p v-if="!loading && downtimeReasons.length === 0" class="api-state">{{ $t('equipment.monitoring.noDowntimeData') }}</p>
          </div>
        </aside>

        <aside class="siemens-panel">
          <header>
            <h2>{{ $t('equipment.monitoring.maintenanceEvents') }}</h2>
          </header>
          <div class="siemens-panel-body siemens-scroll">
            <table class="siemens-table">
              <thead>
                <tr><th>{{ $t('equipment.monitoring.machine') }}</th><th>{{ $t('equipment.monitoring.id') }}</th><th>{{ $t('equipment.monitoring.reminder') }}</th><th>{{ $t('equipment.monitoring.status') }}</th></tr>
              </thead>
              <tbody>
                <tr v-for="item in eventRows" :key="item.rowKey">
                  <td>{{ item.name }}</td>
                  <td><span class="mono">{{ item.id }}</span></td>
                  <td>{{ item.next }}</td>
                  <td><span :class="['siemens-status', item.tone]">{{ item.status }}</span></td>
                </tr>
                <tr v-if="!loading && eventRows.length === 0">
                  <td colspan="4">{{ $t('equipment.monitoring.noEventData') }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </aside>
      </section>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { getEquipmentLedgers, getFaultReasons, getMaintenanceOrders, getOeeSnapshots } from '../../api/equipment'

const { t } = useI18n()

const equipments = ref([])
const metrics = ref([])
const downtimeReasons = ref([])
const eventRows = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])
const numberOrZero = (value) => {
  const number = Number(value)
  return Number.isFinite(number) ? number : 0
}
const statusLabel = (status) => {
  if (!status) return '-'
  const key = String(status).toLowerCase()
  const translated = t(`status.${key}`)
  return translated === `status.${key}` ? status : translated
}
const toneOf = (status) => {
  const text = String(status || '').toUpperCase()
  if (['FAULT', 'ERROR', 'FAILED', 'DANGER'].includes(text)) return 'danger'
  if (['MAINTENANCE', 'WAITING', 'PENDING', 'WARN'].includes(text)) return 'warn'
  return 'running'
}

const mapEquipment = (row) => ({
  id: row.equipmentCode || row.equipment_code || row.id,
  name: row.equipmentName || row.equipment_name || row.name || '-',
  area: row.lineName || row.line_name || row.lineId || row.line_id || '-',
  status: statusLabel(row.equipmentStatus || row.equipment_status || row.status),
  oee: numberOrZero(row.oee),
  tone: toneOf(row.equipmentStatus || row.equipment_status || row.status)
})

const mapMetric = (row) => ({
  label: row.metricName || row.metric_name || row.pointName || row.point_name || row.statDate || row.stat_date || row.id,
  value: `${numberOrZero(row.oee ?? row.availability ?? row.performance ?? row.quality_rate).toFixed(2)}%`,
  bar: `${numberOrZero(row.oee ?? row.availability ?? row.performance ?? row.quality_rate).toFixed(2)}%`
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    const [equipmentPayload, oeePayload, faultPayload, maintenancePayload] = await Promise.all([
      getEquipmentLedgers(),
      getOeeSnapshots().catch(() => []),
      getFaultReasons().catch(() => []),
      getMaintenanceOrders().catch(() => [])
    ])
    equipments.value = recordsOf(equipmentPayload).map(mapEquipment)
    metrics.value = recordsOf(oeePayload).map(mapMetric)
    downtimeReasons.value = recordsOf(faultPayload).map((row) => ({
      reason: row.reasonName || row.reason_name || row.reasonCode || row.reason_code || row.id,
      minutes: numberOrZero(row.minutes ?? row.duration)
    }))
    eventRows.value = recordsOf(maintenancePayload).map((row, index) => ({
      rowKey: `${row.id}-${index}`,
      id: row.maintenanceNo || row.maintenance_no || row.id,
      name: row.equipmentName || row.equipment_name || row.equipmentId || row.equipment_id || '-',
      next: row.planAt || row.plan_at || row.completedAt || row.completed_at || '-',
      status: statusLabel(row.status),
      tone: toneOf(row.status)
    }))
  } catch (e) {
    equipments.value = []
    metrics.value = []
    downtimeReasons.value = []
    eventRows.value = []
    error.value = e?.message || t('equipment.monitoring.apiError')
  } finally {
    loading.value = false
  }
}

onMounted(loadRows)
</script>

<style scoped>
.equipment-layout {
  grid-template-rows: 180px minmax(0, 1fr);
}

.equipment-card-grid {
  grid-template-columns: repeat(6, minmax(0, 1fr));
}

.equipment-card {
  min-height: 170px;
}

.equipment-head,
.equipment-oee,
.downtime-row > div:first-child {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.equipment-oee {
  align-items: baseline;
  margin: 14px 0 8px;
}

.equipment-oee strong {
  font-size: 28px;
}

.equipment-main {
  grid-template-columns: minmax(0, 1.35fr) 360px 500px;
}

.metric-board {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.metric-tile {
  padding: 14px;
  background: #f7f9fa;
  border: 1px solid #d7dde1;
}

.metric-tile span,
.metric-tile strong {
  display: block;
}

.metric-tile span {
  color: #71818b;
}

.metric-tile strong {
  margin: 8px 0 10px;
  font-size: 30px;
}

.downtime-list {
  display: grid;
  align-content: start;
  gap: 18px;
}

.downtime-row span {
  color: #71818b;
}

.downtime-row .siemens-progress {
  margin-top: 8px;
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
