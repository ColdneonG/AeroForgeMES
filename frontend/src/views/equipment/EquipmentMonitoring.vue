<template>
  <section class="siemens-page equipment-monitoring">
    <header class="siemens-page-header">
      <div>
        <h1>Equipment Monitoring</h1>
        <p>Machine status, OEE, downtime distribution and maintenance reminders</p>
      </div>
      <div class="siemens-actions">
        <button class="siemens-btn">Inspection Records</button>
        <button class="siemens-btn primary">Create Work Order</button>
      </div>
    </header>

    <div class="siemens-content equipment-layout">
      <section class="siemens-grid equipment-card-grid">
        <article
          v-for="item in equipments"
          :key="item.id"
          class="siemens-work-card equipment-card"
          :class="{ active: item.status === '运行', warn: item.status === '维护中' || item.status === '待机', danger: item.status === '故障' }"
        >
          <div class="equipment-head">
            <h3>{{ item.name }}</h3>
            <span :class="['siemens-status', statusTone(item.status)]">{{ item.status }}</span>
          </div>
          <p><span class="mono">{{ item.id }}</span> / {{ item.area }} / Updated 14:30</p>
          <div class="equipment-oee">
            <span>OEE</span>
            <strong>{{ item.oee }}%</strong>
          </div>
          <div class="siemens-progress"><span :style="{ width: item.oee + '%' }"></span></div>
        </article>
      </section>

      <section class="siemens-grid equipment-main">
        <article class="siemens-panel">
          <header>
            <h2>Industrial Metrics</h2>
            <span class="siemens-muted">OEE / Availability / Performance</span>
          </header>
          <div class="siemens-panel-body metric-board">
            <div v-for="metric in metrics" :key="metric.label" class="metric-tile">
              <span>{{ metric.label }}</span>
              <strong>{{ metric.value }}</strong>
              <div class="siemens-progress"><span :style="{ width: metric.bar }"></span></div>
            </div>
            <div class="siemens-mini-chart">
              <i v-for="(value, index) in [74, 82, 86, 79, 88, 91, 84, 86, 89, 92, 87, 90]" :key="index" :style="{ height: value + '%' }"></i>
            </div>
          </div>
        </article>

        <aside class="siemens-panel">
          <header>
            <h2>Downtime Reasons</h2>
            <span class="siemens-status warn">125 min</span>
          </header>
          <div class="siemens-panel-body downtime-list">
            <div v-for="item in downtimeReasons" :key="item.reason" class="downtime-row">
              <div>
                <strong>{{ item.reason }}</strong>
                <span>{{ item.minutes }} min</span>
              </div>
              <div class="siemens-progress"><span :style="{ width: item.minutes * 1.4 + '%' }"></span></div>
            </div>
          </div>
        </aside>

        <aside class="siemens-panel">
          <header>
            <h2>Maintenance / Events</h2>
          </header>
          <div class="siemens-panel-body siemens-scroll">
            <table class="siemens-table">
              <thead>
                <tr><th>Machine</th><th>ID</th><th>Reminder</th><th>Status</th></tr>
              </thead>
              <tbody>
                <tr v-for="item in eventRows" :key="item.rowKey">
                  <td>{{ item.name }}</td>
                  <td><span class="mono">{{ item.id }}</span></td>
                  <td>{{ item.next }}</td>
                  <td><span :class="['siemens-status', statusTone(item.status)]">{{ item.status }}</span></td>
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
import { equipments, downtimeReasons } from '../../mock/mesData'

const eventRows = [...equipments, ...equipments.slice(0, 4)].map((item, index) => ({
  ...item,
  rowKey: `${item.id}-${index}`
}))

const metrics = [
  { label: 'OEE', value: '86%', bar: '86%' },
  { label: 'Availability', value: '92%', bar: '92%' },
  { label: 'Performance', value: '88%', bar: '88%' },
  { label: 'Quality', value: '97%', bar: '97%' }
]

const statusTone = (status) => ({
  运行: 'running',
  合格: 'ok',
  待机: 'warn',
  维护中: 'warn',
  故障: 'danger',
  异常: 'danger'
}[status] || '')
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
  grid-template-rows: 104px minmax(0, 1fr);
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

.metric-board .siemens-mini-chart {
  grid-column: 1 / -1;
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
</style>
