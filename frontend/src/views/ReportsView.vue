<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { get } from '@/api/client'
import OutputBarChart from '@/components/OutputBarChart.vue'

type DailyOutput = { statDate?: string; lineCode?: string; lineName?: string; workOrderNo?: string; productCode?: string; productName?: string; plannedQty?: number; reportedQty?: number; qualifiedQty?: number; defectiveQty?: number; reportCount?: number }
const rows = ref<DailyOutput[]>([])
const loading = ref(false)
const error = ref('')
const activeTab = ref<'daily' | 'monthly' | 'wages'>('daily')
const trend = computed(() => {
  const totals = new Map<string, number>()
  rows.value.forEach(row => { const key = row.statDate || '未标注'; totals.set(key, (totals.get(key) || 0) + Number(row.reportedQty || 0)) })
  return [...totals].sort(([left], [right]) => left.localeCompare(right)).map(([label, value]) => ({ label: label.slice(5), value }))
})
const reportedTotal = computed(() => rows.value.reduce((sum, row) => sum + Number(row.reportedQty || 0), 0))
const defectiveTotal = computed(() => rows.value.reduce((sum, row) => sum + Number(row.defectiveQty || 0), 0))
const defectRate = computed(() => reportedTotal.value ? `${(defectiveTotal.value / reportedTotal.value * 100).toFixed(2)}%` : '0.00%')

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
      <div class="tabs"><span class="tab-item" :class="{active:activeTab==='daily'}" @click="activeTab='daily'">生产日报</span><span class="tab-item" :class="{active:activeTab==='monthly'}" @click="activeTab='monthly'">月度汇总</span><span class="tab-item" :class="{active:activeTab==='wages'}" @click="activeTab='wages'">计件工资</span></div>
      <div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div>
      <template v-if="activeTab==='daily'"><div class="kpi-grid mb-5"><div class="kpi-card accent-border"><span class="kpi-label">今日产量</span><strong class="kpi-value">{{ reportedTotal.toLocaleString() }}</strong><span class="kpi-change">台 · 实时汇总</span></div><div class="kpi-card"><span class="kpi-label">今日不良</span><strong class="kpi-value">{{ defectiveTotal }}</strong><span class="kpi-change">不良率 {{ defectRate }}</span></div><div class="kpi-card"><span class="kpi-label">今日产值</span><strong class="kpi-value">¥{{ (reportedTotal*100).toLocaleString() }}</strong><span class="kpi-change">按出厂价 ¥100/台</span></div><div class="kpi-card"><span class="kpi-label">报工记录</span><strong class="kpi-value">{{ rows.length }}</strong><span class="kpi-change">条</span></div></div><section class="card mb-5"><div class="card-header"><h2 class="card-title">本日产量趋势（实时汇总）</h2></div><OutputBarChart :points="trend" /></section></template>
      <template v-else-if="activeTab==='monthly'"><div class="kpi-grid mb-5"><div class="kpi-card accent-border"><span class="kpi-label">本月累计产量</span><strong class="kpi-value">{{reportedTotal.toLocaleString()}}</strong><span class="kpi-change">台</span></div><div class="kpi-card"><span class="kpi-label">本月不良率</span><strong class="kpi-value">{{defectRate}}</strong><span class="kpi-change">实时统计</span></div><div class="kpi-card"><span class="kpi-label">本月产值</span><strong class="kpi-value">¥{{(reportedTotal*100).toLocaleString()}}</strong><span class="kpi-change">估算值</span></div></div></template>
      <div class="data-table-wrap"><table class="data-table"><thead><tr><th>日期</th><th>产线</th><th>工单</th><th>产品</th><th>计划数</th><th>报工数</th><th>合格数</th><th>不良数</th><th>报工次数</th></tr></thead><tbody><tr v-if="loading"><td colspan="9">正在加载...</td></tr><tr v-else-if="!rows.length"><td colspan="9">暂无实时生产报表数据</td></tr><tr v-for="(row, index) in rows" :key="`${row.workOrderNo || index}-${row.statDate || ''}`"><td>{{ row.statDate || '-' }}</td><td>{{ row.lineName || row.lineCode || '-' }}</td><td class="cell-mono">{{ row.workOrderNo || '-' }}</td><td>{{ row.productName || row.productCode || '-' }}</td><td>{{ row.plannedQty ?? '-' }}</td><td>{{ row.reportedQty ?? '-' }}</td><td>{{ row.qualifiedQty ?? '-' }}</td><td>{{ row.defectiveQty ?? '-' }}</td><td>{{ row.reportCount ?? '-' }}</td></tr></tbody></table></div>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>数据库实时数据</span><span class="statusbar-sep">|</span><span class="statusbar-item">生产报表 · {{ rows.length }} 条</span></footer>
  </MesLayout>
</template>
