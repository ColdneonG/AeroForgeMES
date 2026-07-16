<script setup lang="ts">
import { onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { getProductionRecords, type ResourceRecord } from '@/api/production'
const rows = ref<ResourceRecord[]>([]); const loading = ref(false); const error = ref('')
const value = (row: ResourceRecord, key: string) => String(row[key] ?? '-')
const dispatch = (row: ResourceRecord) => row.dispatchNo ?? (row.dispatchId == null ? '-' : `派工 #${row.dispatchId}`)
const operation = (row: ResourceRecord) => row.operationNames ?? row.routeName ?? (row.routeId == null ? '-' : `路线 #${row.routeId}`)
const completedQty = (row: ResourceRecord) => row.completedQty ?? 0
async function load() { loading.value = true; error.value = ''; try { rows.value = await getProductionRecords('/production/tasks') } catch (e) { error.value = e instanceof Error ? e.message : '生产任务加载失败' } finally { loading.value = false } }
onMounted(load)
</script>
<template><MesLayout active="tasks"><header class="app-header"><div class="header-breadcrumb"><span>生产管理</span><span class="bc-sep">/</span><span class="bc-current">生产任务</span></div><div class="header-actions"><span class="user-avatar">张</span></div></header><main class="app-main"><h1 class="page-title">生产任务</h1><div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div><div class="data-table-wrap"><table class="data-table"><thead><tr><th>任务编号</th><th>关联派工</th><th>工序</th><th>计划数量</th><th>已完工</th><th>状态</th></tr></thead><tbody><tr v-if="loading"><td colspan="6">正在加载...</td></tr><tr v-else-if="!rows.length"><td colspan="6">暂无生产任务</td></tr><tr v-for="row in rows" :key="row.id"><td class="cell-mono">{{ value(row, 'taskNo') }}</td><td class="cell-mono">{{ dispatch(row) }}</td><td>{{ operation(row) }}</td><td>{{ value(row, 'planQty') }}</td><td>{{ completedQty(row) }}</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>{{ value(row, 'status') }}</span></td></tr></tbody></table><div class="pagination">共 {{ rows.length }} 条记录</div></div></main><footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>数据库实时数据</span><span class="statusbar-sep">|</span><span class="statusbar-item">生产任务 · {{ rows.length }} 项</span></footer></MesLayout></template>
