<template>
  <section class="siemens-page traceability-page">
    <header class="siemens-page-header">
      <div>
        <h1>Traceability / Genealogy</h1>
        <p>Material, process, equipment, operator and quality records from serial number genealogy</p>
      </div>
      <div class="siemens-actions">
        <button class="siemens-btn">Export Report</button>
        <button class="siemens-btn primary" @click="loadTrace">Refresh Query</button>
      </div>
    </header>

    <p v-if="loading" class="api-state">Loading traceability data...</p>
    <p v-if="error" class="api-state error">{{ error }}</p>

    <div class="siemens-content trace-layout">
      <div class="siemens-toolbar">
        <input v-model="keyword" placeholder="Scan or enter barcode" />
        <select><option>By SN</option><option>By work order</option><option>By batch</option></select>
        <button class="siemens-btn primary" @click="loadTrace">Search</button>
        <span class="siemens-muted">Mock data disabled</span>
      </div>

      <section class="siemens-grid trace-main">
        <article class="siemens-panel">
          <header>
            <h2>Genealogy Chain / Timeline</h2>
            <span class="siemens-status">{{ traceRoot || 'No data' }}</span>
          </header>
          <div class="siemens-panel-body siemens-scroll trace-tree-panel">
            <h3><span class="mono">{{ traceRoot || '-' }}</span></h3>
            <ul class="genealogy-tree">
              <li v-for="node in traceNodes" :key="node.id || node.title">
                {{ node.title || node.name || node.id }}
              </li>
            </ul>
            <p v-if="!loading && traceNodes.length === 0" class="api-state">No genealogy data.</p>
          </div>
        </article>

        <aside class="siemens-panel">
          <header>
            <h2>Object Details</h2>
          </header>
          <div class="siemens-panel-body object-panel">
            <table class="siemens-table">
              <tbody>
                <tr v-for="row in details" :key="row[0]">
                  <td>{{ row[0] }}</td>
                  <td><strong>{{ row[1] }}</strong></td>
                </tr>
                <tr v-if="!loading && details.length === 0">
                  <td colspan="2">No detail data.</td>
                </tr>
              </tbody>
            </table>
          </div>
        </aside>
      </section>

      <section class="siemens-panel">
        <header>
          <h2>Traceability Event Details</h2>
          <span class="siemens-muted">Acquisition records / quality result / equipment and operator binding</span>
        </header>
        <div class="siemens-panel-body siemens-scroll">
          <table class="siemens-table">
            <thead>
              <tr><th>Time</th><th>Object</th><th>Action</th><th>Equipment</th><th>Operator</th><th>Result</th></tr>
            </thead>
            <tbody>
              <tr v-for="event in events" :key="event.time + event.action">
                <td>{{ event.time }}</td>
                <td><span class="mono">{{ event.object }}</span></td>
                <td>{{ event.action }}</td>
                <td>{{ event.equipment }}</td>
                <td>{{ event.owner }}</td>
                <td><span :class="['siemens-status', statusTone(event.status)]">{{ event.status }}</span></td>
              </tr>
              <tr v-if="!loading && events.length === 0">
                <td colspan="6">No event data.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { traceBarcode } from '../../api/barcode'

const keyword = ref('')
const traceRoot = ref('')
const traceNodes = ref([])
const details = ref([])
const events = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const loadTrace = async () => {
  loading.value = true
  error.value = ''
  try {
    const data = await traceBarcode(keyword.value || '-')
    traceRoot.value = data.root || data.barcode || keyword.value || ''
    traceNodes.value = recordsOf(data.nodes || data.children || data.events || [])
    details.value = Object.entries(data.details || {}).map(([key, value]) => [key, value])
    events.value = recordsOf(data.events || []).map((row) => ({
      time: row.time || row.createdAt || row.created_at || '-',
      object: row.object || row.barcode || row.id || '-',
      action: row.action || row.event || '-',
      equipment: row.equipment || row.equipmentName || row.equipment_name || '-',
      owner: row.owner || row.operatorName || row.operator_name || '-',
      status: row.status || row.result || '-'
    }))
  } catch (e) {
    traceRoot.value = ''
    traceNodes.value = []
    details.value = []
    events.value = []
    error.value = e?.message || 'Traceability API is not connected yet.'
  } finally {
    loading.value = false
  }
}

const statusTone = (status) => ({
  PASS: 'ok',
  COMPLETED: 'ok',
  RUNNING: 'running',
  FAILED: 'danger',
  ERROR: 'danger'
}[String(status || '').toUpperCase()] || '')
</script>

<style scoped>
.trace-layout {
  grid-template-rows: auto minmax(0, 1.08fr) minmax(230px, 0.7fr);
}

.trace-main {
  grid-template-columns: minmax(0, 1.55fr) minmax(520px, 0.85fr);
}

.trace-tree-panel h3 {
  margin: 0 0 16px;
  font-size: 20px;
}

.genealogy-tree {
  margin: 0;
  padding-left: 22px;
  list-style: none;
}

.genealogy-tree li {
  margin: 10px 0;
  line-height: 1.6;
}

.object-panel {
  display: grid;
  gap: 18px;
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
