<template>
  <section class="siemens-page traceability-page">
    <header class="siemens-page-header">
      <div>
        <h1>Traceability / Genealogy</h1>
        <p>Material, process, equipment, operator and quality records from serial number genealogy</p>
      </div>
      <div class="siemens-actions">
        <button class="siemens-btn">Export Report</button>
        <button class="siemens-btn primary">Refresh Query</button>
      </div>
    </header>

    <div class="siemens-content trace-layout">
      <div class="siemens-toolbar">
        <input value="SN20260706000158" />
        <select><option>By SN</option><option>By work order</option><option>By batch</option></select>
        <select><option>Complete genealogy</option><option>Exceptions only</option></select>
        <button class="siemens-btn primary">Search</button>
        <span class="siemens-muted">Complete chain / last acquisition 14:30 / 7 process nodes</span>
      </div>

      <section class="siemens-grid trace-main">
        <article class="siemens-panel">
          <header>
            <h2>Genealogy Chain / Timeline</h2>
            <span class="siemens-status ok">Complete</span>
          </header>
          <div class="siemens-panel-body siemens-scroll trace-tree-panel">
            <h3><span class="mono">{{ traceTree.root }}</span></h3>
            <ul class="genealogy-tree">
              <li>
                <span class="mono">{{ traceTree.order }}</span> / <span class="mono">{{ traceTree.batch }}</span>
                <ul>
                  <li v-for="child in traceTree.nodes[0].children" :key="child.title">{{ child.title }}</li>
                </ul>
              </li>
            </ul>
          </div>
        </article>

        <aside class="siemens-panel">
          <header>
            <h2>Object Details</h2>
          </header>
          <div class="siemens-panel-body object-panel">
            <table class="siemens-table">
              <tbody>
                <tr v-for="row in traceTree.details" :key="row[0]">
                  <td>{{ row[0] }}</td>
                  <td><strong>{{ row[1] }}</strong></td>
                </tr>
              </tbody>
            </table>
            <div class="siemens-step-line">
              <span class="siemens-step done">Material</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step done">Process</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step done">Equipment</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step done">Operator</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step active">Quality</span>
            </div>
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
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { traceTree } from '../../mock/mesData'

const events = [
  { time: '08:21', object: 'SN20260706000158', action: 'Scan online', equipment: 'Scanner 2#', owner: 'Wang', status: '合格' },
  { time: '09:12', object: 'BATCH-MTR-0706A', action: 'Motor assembly', equipment: 'Assembly Line 1#', owner: 'Liu', status: '已完成' },
  { time: '09:48', object: 'BATCH-BLD-0706C', action: 'Blade assembly', equipment: 'Assembly Line 01', owner: 'Wang', status: '已完成' },
  { time: '10:06', object: 'FQC-0706-021', action: 'Final inspection', equipment: 'Inspection Bench 1#', owner: 'Li', status: '合格' },
  { time: '10:22', object: 'BATCH-PKG-0706B', action: 'Package batch binding', equipment: 'Packaging Line 1#', owner: 'Zhao', status: '已完成' },
  { time: '10:44', object: 'WAREHOUSE-IN', action: 'Inbound pending', equipment: 'WMS Interface', owner: 'System', status: '进行中' }
]

const statusTone = (status) => ({
  合格: 'ok',
  已完成: 'ok',
  进行中: 'running',
  异常: 'danger'
}[status] || '')
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

.genealogy-tree,
.genealogy-tree ul {
  margin: 0;
  padding-left: 22px;
  list-style: none;
}

.genealogy-tree li {
  position: relative;
  margin: 10px 0;
  padding-left: 18px;
  line-height: 1.6;
}

.genealogy-tree li::before {
  content: "";
  position: absolute;
  top: 8px;
  left: 0;
  width: 8px;
  height: 8px;
  background: #00799f;
}

.object-panel {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  gap: 18px;
}
</style>
