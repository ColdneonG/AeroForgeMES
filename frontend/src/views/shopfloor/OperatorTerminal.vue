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

    <div class="siemens-content operator-layout">
      <section class="station-strip">
        <div><span>Machine</span><strong>ASM-LINE-01 / A03</strong></div>
        <div><span>Machine Status</span><strong>Running</strong></div>
        <div><span>Work Order</span><strong>WO20260706001</strong></div>
        <div><span>Serial Number</span><strong>SN20260706000158</strong></div>
        <div><span>Active User</span><strong>Liu / Assembly A</strong></div>
        <div><span>Remaining Time</span><strong>00:18:42</strong></div>
      </section>

      <section class="siemens-grid operator-main">
        <aside class="siemens-panel">
          <header>
            <h2>Current Order / Steps</h2>
            <span class="siemens-status running">Running</span>
          </header>
          <div class="siemens-panel-body step-list">
            <article class="siemens-work-card active">
              <h3>WO20260706001</h3>
              <p>FS-500 Floor Fan / Assembly Line 01 / Station A03</p>
              <input class="station-input" value="SN20260706000158" />
            </article>

            <div class="siemens-scroll task-list">
              <article
                v-for="task in operatorTasks"
                :key="task.title"
                class="siemens-work-card"
                :class="{ active: task.status === '进行中' }"
              >
                <div class="task-head">
                  <h3>{{ task.title }}</h3>
                  <span :class="['siemens-status', statusTone(task.status)]">{{ task.status }}</span>
                </div>
                <p>{{ task.note }}</p>
              </article>
            </div>
          </div>
        </aside>

        <main class="siemens-panel">
          <header>
            <h2>Work Instruction / Assembly Operation</h2>
            <span class="siemens-muted">Cycle time 42s / Step 3</span>
          </header>
          <div class="siemens-panel-body instruction-panel">
            <div class="instruction-visual">
              <div>
                <strong>SOP: Front Guard Installation</strong>
                <span>SOP-FS500-ASM-06 / Version V2.3 / Effective 2026-06-28</span>
              </div>
            </div>

            <div class="siemens-step-line">
              <span class="siemens-step done">Scan SN</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step done">Material Check</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step active">Install Guard</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step">Torque</span>
              <span class="siemens-step-join"></span>
              <span class="siemens-step">Function Test</span>
            </div>

            <table class="siemens-table">
              <tbody>
                <tr><td>Work points</td><td>Front guard buckle faces upward, all sides pressed tightly, screw torque 1.8 N.m.</td></tr>
                <tr><td>Data collection</td><td>Torque, three-speed airflow, noise and insulation test.</td></tr>
                <tr><td>Quality gate</td><td>Completion is blocked before torque capture and function test are submitted.</td></tr>
              </tbody>
            </table>
          </div>
        </main>

        <aside class="siemens-panel">
          <header>
            <h2>Product / Material / Actions</h2>
          </header>
          <div class="siemens-panel-body action-panel">
            <table class="siemens-table">
              <tbody>
                <tr><td>Product</td><td><strong>FS-500 Floor Fan</strong></td></tr>
                <tr><td>Serial No.</td><td><span class="mono">SN20260706000158</span></td></tr>
                <tr><td>Batch</td><td><span class="mono">BATCH-FS500-0706A</span></td></tr>
                <tr><td>Operation</td><td>Assembly</td></tr>
                <tr><td>Cycle Time</td><td>42 s / pc</td></tr>
              </tbody>
            </table>

            <section class="material-panel">
              <h3>Material Trace</h3>
              <table class="siemens-table">
                <tbody>
                  <tr v-for="item in materialChecks" :key="item.name">
                    <td>{{ item.name }}</td>
                    <td>{{ item.code }}</td>
                    <td><span class="siemens-status ok">OK</span></td>
                  </tr>
                </tbody>
              </table>
            </section>

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
import { operatorTasks, materialChecks } from '../../mock/mesData'

const statusTone = (status) => ({
  进行中: 'running',
  已完成: 'ok',
  合格: 'ok',
  待开始: '',
  异常: 'danger'
}[status] || '')
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

.step-list {
  grid-template-rows: auto minmax(0, 1fr);
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

.instruction-panel {
  grid-template-rows: minmax(0, 1fr) auto auto;
}

.instruction-visual {
  display: grid;
  min-height: 360px;
  place-items: center;
  background:
    linear-gradient(90deg, rgba(0, 121, 159, 0.08) 1px, transparent 1px),
    linear-gradient(0deg, rgba(0, 121, 159, 0.08) 1px, transparent 1px),
    #f7f9fa;
  background-size: 38px 38px;
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

.action-panel {
  grid-template-rows: auto minmax(0, 1fr) auto;
}

.material-panel h3 {
  margin: 0 0 10px;
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
