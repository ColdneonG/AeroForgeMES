<script setup lang="ts">
import { onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { getProductionRecords, type ResourceRecord } from '@/api/production'
const rows = ref<ResourceRecord[]>([]); const error = ref(''); const value = (row: ResourceRecord, key: string) => String(row[key] ?? '-')
async function load() { try { rows.value = await getProductionRecords('/production/barcodes/types') } catch (e) { error.value = e instanceof Error ? e.message : '条码类型加载失败' } }
onMounted(load)
</script>
<template><MesLayout active="barcode-types"><header class="app-header"><div class="header-breadcrumb"><span>条码与追溯</span><span class="bc-sep">/</span><span class="bc-current">条码类型</span></div><div class="header-actions"><span class="user-avatar">张</span></div></header><main class="app-main"><h1 class="page-title">条码类型管理</h1><div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div><div class="data-table-wrap"><table class="data-table"><thead><tr><th>类型编码</th><th>类型名称</th><th>唯一范围</th><th>状态</th></tr></thead><tbody><tr v-if="!rows.length"><td colspan="4">暂无条码类型</td></tr><tr v-for="row in rows" :key="row.id"><td class="cell-mono">{{ value(row, 'typeCode') }}</td><td>{{ value(row, 'typeName') }}</td><td>{{ value(row, 'uniqueScope') }}</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>{{ value(row, 'status') }}</span></td></tr></tbody></table></div></main><footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>数据库实时数据</span><span class="statusbar-sep">|</span><span class="statusbar-item">条码类型 · {{ rows.length }} 个</span></footer></MesLayout></template>
