<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { getProductionRecords, type ResourceRecord } from '@/api/production'
const rows = ref<ResourceRecord[]>([]); const loading = ref(false); const error = ref('')
const value = (row: ResourceRecord, key: string) => String(row[key] ?? '-')
const totalShortage = computed(() => rows.value.reduce((sum, row) => sum + Number(row.missingQty || 0), 0))
const material = (row: ResourceRecord) => row.materialId == null ? '-' : `物料 #${row.materialId}`
const arrivalTime = (row: ResourceRecord) => row.expectedArrivalAt ? String(row.expectedArrivalAt).replace('T', ' ').slice(0, 16) : '待确认'
async function load() { loading.value = true; error.value = ''; try { rows.value = await getProductionRecords('/production/kitting-missing-board') } catch (e) { error.value = e instanceof Error ? e.message : '缺料看板加载失败' } finally { loading.value = false } }
onMounted(load)
</script>
<template><MesLayout active="kboard"><header class="app-header"><div class="header-breadcrumb"><span>生产管理</span><span class="bc-sep">/</span><span class="bc-current">缺料看板</span></div><div class="header-actions"><span class="badge badge-status-error"><span class="badge-dot"></span>{{ rows.length }} 项缺料</span><span class="user-avatar">张</span></div></header><main class="app-main"><h1 class="page-title">齐套缺料看板</h1><section class="kpi-grid kb-kpis"><div class="kpi-card accent-border"><span class="kpi-label">缺料项</span><span class="kpi-value">{{ rows.length }}</span><span class="kpi-change">待处理缺料记录</span></div><div class="kpi-card"><span class="kpi-label">总缺口</span><span class="kpi-value">{{ totalShortage }}</span><span class="kpi-change">物料缺口合计</span></div></section><div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div><div class="data-table-wrap"><table class="data-table"><thead><tr><th>工单</th><th>物料</th><th>需求数量</th><th>现有数量</th><th>缺口</th><th>预计到料时间</th><th>状态</th></tr></thead><tbody><tr v-if="loading"><td colspan="7">正在加载...</td></tr><tr v-else-if="!rows.length"><td colspan="7">暂无缺料记录</td></tr><tr v-for="row in rows" :key="row.id"><td class="cell-mono">{{ value(row, 'workOrderNo') }}</td><td>{{ material(row) }}</td><td>{{ value(row, 'requiredQty') }}</td><td>{{ value(row, 'availableQty') }}</td><td>{{ value(row, 'missingQty') }}</td><td>{{ arrivalTime(row) }}</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>{{ value(row, 'status') }}</span></td></tr></tbody></table></div></main><footer class="app-statusbar"><span class="statusbar-item"><span class="dot warn"></span>缺料看板 · {{ rows.length }} 项</span></footer></MesLayout></template>

<style scoped>
.kb-kpis { grid-template-columns: repeat(2, minmax(0, 1fr)); max-width: 520px; margin-bottom: 20px; }
@media (max-width: 640px) { .kb-kpis { grid-template-columns: 1fr; } }
</style>
