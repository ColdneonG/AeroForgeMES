<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import MesLayout from '@/layouts/MesLayout.vue'
import { get } from '@/api/client'
import { getQualityRecords, type QualityRecord } from '@/api/quality'

type InspectionOrder = { id: number; inspectionNo?: string; inspectionType?: string; planId?: number; workOrderId?: number; workOrderNo?: string; taskId?: number; operationTaskId?: number; productId?: number; productName?: string; inspectorId?: number; inspectionAt?: string; finalResult?: string; status?: string }

const route = useRoute()
const activeTab = ref('overview')
const inspection = ref<InspectionOrder | null>(null)
const results = ref<QualityRecord[]>([])
const error = ref('')
const loading = ref(false)
const inspectionId = computed(() => String(route.query.id || ''))
const inspectionNo = computed(() => inspection.value?.inspectionNo || (inspectionId.value ? `#${inspectionId.value}` : '-'))
const value = (item: unknown) => item === undefined || item === null || item === '' ? '-' : String(item)
const isFailed = (item: unknown) => ['FAIL', '不合格'].includes(String(item || '').toUpperCase()) || String(item) === '不合格'
const passCount = computed(() => results.value.filter((row) => !isFailed(row.result) && ['PASS', '合格'].includes(String(row.result || '').toUpperCase())).length)
const failedCount = computed(() => results.value.filter((row) => isFailed(row.result)).length)
function badgeClass(item: unknown) { return isFailed(item) ? 'badge-status-error' : 'badge-status-ok' }
function displayDate(item: string | undefined) { return item ? item.replace('T', ' ') : '-' }

async function load() {
  inspection.value = null; results.value = []; error.value = ''
  if (!inspectionId.value) { error.value = '缺少检验单编号'; return }
  loading.value = true
  const [orderResult, resultRows] = await Promise.allSettled([
    get<InspectionOrder>(`/quality/inspection-orders/${inspectionId.value}`),
    getQualityRecords('/quality/inspection-results', { inspectionId: inspectionId.value }),
  ])
  if (orderResult.status === 'fulfilled') inspection.value = orderResult.value
  else error.value = orderResult.reason instanceof Error ? orderResult.reason.message : '检验单加载失败'
  if (resultRows.status === 'fulfilled') results.value = resultRows.value
  loading.value = false
}
onMounted(load)
watch(inspectionId, load)
</script>

<template>
  <MesLayout active="quality">
    <header class="app-header"><div class="header-breadcrumb"><span>质量与追溯</span><span class="bc-sep">/</span><RouterLink to="/quality-inspection">质量检验</RouterLink><span class="bc-sep">/</span><span class="bc-current">{{ inspectionNo }}</span></div></header>
    <main class="app-main">
      <div class="flex items-center justify-between mb-5"><div><h1 class="page-title" style="margin-bottom:4px">{{ inspectionNo }}</h1><span class="badge" :class="badgeClass(inspection?.status)"><span class="badge-dot"></span>{{ inspection?.status || '-' }}</span></div><RouterLink v-if="inspectionId" :to="{ path: '/quality/inspection-exec', query: { inspectionId } }" class="btn btn-primary">录入检验结果</RouterLink></div>
      <div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div>
      <div class="tabs"><span class="tab-item" :class="{ active: activeTab === 'overview' }" @click="activeTab = 'overview'">概览</span><span class="tab-item" :class="{ active: activeTab === 'results' }" @click="activeTab = 'results'">检验结果</span></div>
      <div v-show="activeTab === 'overview'" class="detail-grid">
        <section class="card"><h2 class="section-title">基本信息</h2><table class="data-table" style="border:none"><tbody><tr><td>检验单号</td><td class="cell-mono">{{ inspectionNo }}</td></tr><tr><td>检验类型</td><td>{{ value(inspection?.inspectionType) }}</td></tr><tr><td>生产工单</td><td class="cell-mono">{{ inspection?.workOrderNo || value(inspection?.workOrderId) }}</td></tr><tr><td>产品</td><td>{{ inspection?.productName || value(inspection?.productId) }}</td></tr><tr><td>检验时间</td><td>{{ displayDate(inspection?.inspectionAt) }}</td></tr><tr><td>最终结果</td><td><span class="badge" :class="badgeClass(inspection?.finalResult)"><span class="badge-dot"></span>{{ value(inspection?.finalResult) }}</span></td></tr></tbody></table></section>
        <section class="card"><h2 class="section-title">检验汇总</h2><div class="inspection-summary"><div><span>检验项目</span><strong>{{ results.length }}</strong></div><div><span>合格项</span><strong class="summary-ok">{{ passCount }}</strong></div><div><span>不合格项</span><strong class="summary-fail">{{ failedCount }}</strong></div></div><table class="data-table" style="border:none;margin-top:var(--space-4)"><tbody><tr><td>检验方案</td><td>{{ value(inspection?.planId) }}</td></tr><tr><td>生产任务</td><td>{{ value(inspection?.taskId) }}</td></tr><tr><td>工序任务</td><td>{{ value(inspection?.operationTaskId) }}</td></tr><tr><td>检验员</td><td>{{ value(inspection?.inspectorId) }}</td></tr></tbody></table></section>
      </div>
      <section v-show="activeTab === 'results'" class="data-table-wrap"><table class="data-table"><thead><tr><th>结果 ID</th><th>检验项</th><th>实测值</th><th>检验结果</th><th>缺陷原因</th><th>备注</th></tr></thead><tbody><tr v-if="loading"><td colspan="6">正在加载...</td></tr><tr v-else-if="!results.length"><td colspan="6">暂无检验结果</td></tr><tr v-for="row in results" :key="row.id"><td class="cell-mono">{{ value(row.id) }}</td><td>{{ value(row.qcItemId) }}</td><td>{{ value(row.measuredValue) }}</td><td><span class="badge" :class="badgeClass(row.result)"><span class="badge-dot"></span>{{ value(row.result) }}</span></td><td>{{ value(row.defectReasonId) }}</td><td>{{ value(row.remark) }}</td></tr></tbody></table></section>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>质量检验单 {{ inspectionNo }} · {{ results.length }} 项检验结果</span></footer>
  </MesLayout>
</template>

<style scoped>
.detail-grid { display:grid; grid-template-columns:1fr 1fr; gap:var(--space-5); }
.inspection-summary { display:grid; grid-template-columns:repeat(3, 1fr); gap:var(--space-3); }
.inspection-summary > div { padding:var(--space-4); border:1px solid var(--color-border); border-radius:var(--radius-md); background:var(--color-surface-muted); }
.inspection-summary span { display:block; color:var(--color-text-muted); font-size:var(--text-sm); }
.inspection-summary strong { display:block; margin-top:var(--space-2); font-size:var(--text-2xl); }
.summary-ok { color:var(--color-success); }.summary-fail { color:var(--color-danger); }
</style>
