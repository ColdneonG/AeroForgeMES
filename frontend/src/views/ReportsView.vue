<script setup lang="ts">
import { onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { get } from '@/api/client'

type DailyOutput = { statDate?: string; lineCode?: string; lineName?: string; workOrderNo?: string; productCode?: string; productName?: string; plannedQty?: number; reportedQty?: number; qualifiedQty?: number; defectiveQty?: number; reportCount?: number }
const rows = ref<DailyOutput[]>([])
const loading = ref(false)
const error = ref('')

async function load() {
  loading.value = true; error.value = ''
  try { rows.value = await get<DailyOutput[]>('/report/production-output/daily') }
  catch (reason) { error.value = reason instanceof Error ? reason.message : '生产报表加载失败' }
  finally { loading.value = false }
}
onMounted(load)
</script>

<template>
  <MesLayout active="reports-main">
    <header class="app-header"><div class="header-breadcrumb"><span>数据与系统</span><span class="bc-sep">/</span><span class="bc-current">生产报表</span></div><div class="header-actions"><button class="btn btn-secondary btn-sm" :disabled="loading" @click="load">刷新</button></div></header>
    <main class="app-main" data-od-id="reports-main">
      <h1 class="page-title">生产报表</h1>
      <div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div>
      <div class="data-table-wrap"><table class="data-table"><thead><tr><th>日期</th><th>产线</th><th>工单</th><th>产品</th><th>计划数</th><th>报工数</th><th>合格数</th><th>不良数</th><th>报工次数</th></tr></thead><tbody><tr v-if="loading"><td colspan="9">正在加载...</td></tr><tr v-else-if="!rows.length"><td colspan="9">暂无实时生产报表数据</td></tr><tr v-for="(row, index) in rows" :key="`${row.workOrderNo || index}-${row.statDate || ''}`"><td>{{ row.statDate || '-' }}</td><td>{{ row.lineName || row.lineCode || '-' }}</td><td class="cell-mono">{{ row.workOrderNo || '-' }}</td><td>{{ row.productName || row.productCode || '-' }}</td><td>{{ row.plannedQty ?? '-' }}</td><td>{{ row.reportedQty ?? '-' }}</td><td>{{ row.qualifiedQty ?? '-' }}</td><td>{{ row.defectiveQty ?? '-' }}</td><td>{{ row.reportCount ?? '-' }}</td></tr></tbody></table></div>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>数据库实时数据</span><span class="statusbar-sep">|</span><span class="statusbar-item">生产报表 · {{ rows.length }} 条</span></footer>
  </MesLayout>
</template>
