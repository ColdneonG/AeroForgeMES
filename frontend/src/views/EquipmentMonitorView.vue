<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { post } from '@/api/client'
import { getEquipmentRecords } from '@/api/equipment'

type Equipment = Record<string, unknown> & { id: number; equipmentStatus?: string; equipmentCode?: string; equipmentName?: string }
const rows = ref<Equipment[]>([])
const loading = ref(false)
const error = ref('')
const filter = ref('all')
const value = (row: Equipment, key: string) => String(row[key] ?? '-')
const status = (row: Equipment) => String(row.equipmentStatus || 'UNKNOWN').toUpperCase()
const isRunning = (row: Equipment) => ['RUNNING', '运行中'].includes(status(row))
const isFault = (row: Equipment) => ['FAULT', 'ALARM', '故障', '报警'].includes(status(row))
const filteredRows = computed(() => rows.value.filter((row) => filter.value === 'all' || (filter.value === 'running' && isRunning(row)) || (filter.value === 'fault' && isFault(row)) || (filter.value === 'idle' && !isRunning(row) && !isFault(row))))
const runningCount = computed(() => rows.value.filter(isRunning).length)
const faultCount = computed(() => rows.value.filter(isFault).length)
const idleCount = computed(() => rows.value.length - runningCount.value - faultCount.value)
function statusClass(row: Equipment) { return isFault(row) ? 'badge-status-error' : isRunning(row) ? 'badge-status-ok' : 'badge-status-warn' }

async function load() {
  loading.value = true; error.value = ''
  try { rows.value = await getEquipmentRecords() as Equipment[] }
  catch (e) { error.value = e instanceof Error ? e.message : '设备监控数据加载失败' }
  finally { loading.value = false }
}
async function changeStatus(row: Equipment, equipmentStatus: string) {
  try { await post(`/equipment/equipments/${row.id}/running-status`, { equipmentStatus }); await load() }
  catch (e) { error.value = e instanceof Error ? e.message : '运行状态更新失败' }
}
onMounted(load)
</script>

<template>
  <MesLayout active="equip-monitor">
    <header class="app-header"><div class="header-breadcrumb"><span>设备与安灯</span><span class="bc-sep">/</span><span class="bc-current">设备监控</span></div><div class="header-actions"><button class="btn btn-secondary btn-sm" :disabled="loading" @click="load">刷新状态</button><span class="user-avatar">张</span></div></header>
    <main class="app-main">
      <h1 class="page-title">设备监控</h1>
      <p class="page-subtitle">实时关注设备运行状态与异常处置</p>
      <div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div>
      <section class="monitor-summary" aria-label="设备状态概览">
        <button class="summary-card total" :class="{ active: filter === 'all' }" @click="filter = 'all'"><span class="summary-icon">⌘</span><span class="summary-content"><span class="summary-label">纳管设备</span><strong>{{ rows.length }}</strong><small>全部设备资产</small></span><span class="summary-arrow">›</span></button>
        <button class="summary-card running" :class="{ active: filter === 'running' }" @click="filter = 'running'"><span class="summary-icon">▶</span><span class="summary-content"><span class="summary-label">运行中</span><strong>{{ runningCount }}</strong><small>设备当前生产中</small></span><span class="summary-arrow">›</span></button>
        <button class="summary-card idle" :class="{ active: filter === 'idle' }" @click="filter = 'idle'"><span class="summary-icon">Ⅱ</span><span class="summary-content"><span class="summary-label">待机 / 停机</span><strong>{{ idleCount }}</strong><small>等待任务或计划停机</small></span><span class="summary-arrow">›</span></button>
        <button class="summary-card fault" :class="{ active: filter === 'fault' }" @click="filter = 'fault'"><span class="summary-icon">!</span><span class="summary-content"><span class="summary-label">故障 / 报警</span><strong>{{ faultCount }}</strong><small>需要优先关注</small></span><span class="summary-arrow">›</span></button>
      </section>
      <div class="data-table-wrap"><table class="data-table"><thead><tr><th>设备编码</th><th>设备名称</th><th>实时状态</th><th>所在产线</th><th>所在工位</th><th>监控操作</th></tr></thead><tbody><tr v-if="loading"><td colspan="6">正在加载...</td></tr><tr v-else-if="!filteredRows.length"><td colspan="6">当前筛选条件下暂无设备</td></tr><tr v-for="row in filteredRows" :key="row.id"><td class="cell-mono">{{ value(row, 'equipmentCode') }}</td><td>{{ value(row, 'equipmentName') }}</td><td><span class="badge" :class="statusClass(row)"><span class="badge-dot"></span>{{ value(row, 'equipmentStatus') }}</span></td><td>{{ value(row, 'lineName') }}</td><td>{{ value(row, 'stationName') }}</td><td class="cell-actions"><button class="btn btn-ghost btn-sm" @click="changeStatus(row, 'RUNNING')">设为运行</button><button class="btn btn-ghost btn-sm" @click="changeStatus(row, 'IDLE')">设为待机</button><button class="btn btn-ghost btn-sm" @click="changeStatus(row, 'FAULT')">上报故障</button></td></tr></tbody></table></div>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>设备状态实时数据</span><span class="statusbar-sep">|</span><span class="statusbar-item">故障设备 {{ faultCount }} 台</span></footer>
  </MesLayout>
</template>

<style scoped>
.page-subtitle { margin:-12px 0 var(--space-5); color:var(--color-text-muted); }
.monitor-summary { display:grid; grid-template-columns:repeat(4, minmax(0, 1fr)); gap:var(--space-4); margin-bottom:var(--space-5); }
.summary-card { position:relative; display:flex; align-items:center; gap:var(--space-3); min-height:112px; padding:var(--space-4); overflow:hidden; text-align:left; color:var(--color-text); background:linear-gradient(135deg, var(--color-surface), var(--color-surface-muted)); border:1px solid var(--color-border); border-radius:var(--radius-lg); cursor:pointer; transition:transform .18s ease, border-color .18s ease, box-shadow .18s ease; }
.summary-card::before { position:absolute; top:0; left:0; width:4px; height:100%; content:''; background:var(--card-accent); }
.summary-card:hover { transform:translateY(-2px); border-color:var(--card-accent); box-shadow:0 10px 22px rgba(15, 23, 42, .1); }
.summary-card.active { border-color:var(--card-accent); box-shadow:0 0 0 2px color-mix(in srgb, var(--card-accent) 18%, transparent), 0 10px 22px rgba(15, 23, 42, .1); }
.summary-card.total { --card-accent:var(--color-primary); }.summary-card.running { --card-accent:var(--color-success); }.summary-card.idle { --card-accent:var(--color-warning); }.summary-card.fault { --card-accent:var(--color-danger); }
.summary-icon { display:grid; flex:0 0 40px; width:40px; height:40px; place-items:center; color:var(--card-accent); font-size:22px; font-weight:700; background:color-mix(in srgb, var(--card-accent) 12%, transparent); border-radius:12px; }
.summary-content { display:flex; flex:1; flex-direction:column; min-width:0; }.summary-label { color:var(--color-text-muted); font-size:var(--text-sm); }.summary-content strong { margin:2px 0; color:var(--card-accent); font-size:28px; line-height:1.1; }.summary-content small { overflow:hidden; color:var(--color-text-muted); font-size:12px; text-overflow:ellipsis; white-space:nowrap; }.summary-arrow { color:var(--card-accent); font-size:26px; opacity:.65; }
@media (max-width: 960px) { .monitor-summary { grid-template-columns:repeat(2, minmax(0, 1fr)); } }
@media (max-width: 560px) { .monitor-summary { grid-template-columns:1fr; } }
</style>
