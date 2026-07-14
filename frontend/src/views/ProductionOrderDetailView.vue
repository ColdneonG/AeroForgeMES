<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import MesLayout from '@/layouts/MesLayout.vue'
import { get } from '@/api/client'
import { getWorkOrder, type WorkOrder } from '@/api/production'

type OperationProgress = { operationTaskId: number; operationTaskNo: string; taskNo?: string; stepNo?: number; processCode?: string; processName?: string; planQty?: number; reportedQty?: number; qualifiedQty?: number; defectiveQty?: number; status?: string }
type InspectionOrder = { id: number; inspectionNo?: string; inspectionType?: string; operationTaskId?: number; finalResult?: string; status?: string; inspectionAt?: string }

const activeTab = ref('overview')
const route = useRoute()
const order = ref<WorkOrder | null>(null)
const operations = ref<OperationProgress[]>([])
const inspections = ref<InspectionOrder[]>([])
const error = ref('')
const loadingDetails = ref(false)
const orderNo = computed(() => order.value?.workOrderNo || (route.query.id ? `#${route.query.id}` : '-'))
const progress = computed(() => {
  const planned = Number(order.value?.planQty || 0)
  return planned ? Math.min(100, Math.round(Number(order.value?.completedQty || 0) / planned * 100)) : 0
})
function operationProgress(row: OperationProgress) {
  const planned = Number(row.planQty || 0)
  return planned ? Math.min(100, Math.round(Number(row.reportedQty || 0) / planned * 100)) : 0
}

async function load() {
  const id = String(route.query.id || '')
  order.value = null; operations.value = []; inspections.value = []; error.value = ''
  if (!id) { error.value = '缺少生产工单编号'; return }
  loadingDetails.value = true
  const [workOrder, operationResult, inspectionResult] = await Promise.allSettled([
    getWorkOrder(id),
    get<OperationProgress[]>(`/production/work-orders/${id}/operation-progress`),
    get<InspectionOrder[]>('/quality/inspection-orders', { workOrderId: id }),
  ])
  if (workOrder.status === 'fulfilled') order.value = workOrder.value
  else error.value = workOrder.reason instanceof Error ? workOrder.reason.message : '生产工单加载失败'
  if (operationResult.status === 'fulfilled') operations.value = operationResult.value
  if (inspectionResult.status === 'fulfilled') inspections.value = inspectionResult.value
  loadingDetails.value = false
}
onMounted(load)
watch(() => route.query.id, load)
</script>

<template>
  <MesLayout active="order-detail">
    <header class="app-header"><div class="header-breadcrumb"><span>生产管理</span><span class="bc-sep">/</span><RouterLink to="/production-orders">生产订单</RouterLink><span class="bc-sep">/</span><span class="bc-current">{{ orderNo }}</span></div></header>
    <main class="app-main" data-od-id="detail-main">
      <div class="flex items-center justify-between mb-5"><div><h1 class="page-title" style="margin-bottom:4px">{{ orderNo }}</h1><span class="badge badge-status-ok"><span class="badge-dot"></span>{{ order?.status || '-' }}</span></div></div>
      <div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div>
      <div class="tabs" data-od-id="detail-tabs"><span class="tab-item" :class="{ active: activeTab === 'overview' }" @click="activeTab = 'overview'">概览</span><span class="tab-item" :class="{ active: activeTab === 'progress' }" @click="activeTab = 'progress'">工序进度</span><span class="tab-item" :class="{ active: activeTab === 'quality' }" @click="activeTab = 'quality'">质量记录</span></div>

      <div v-show="activeTab === 'overview'" style="display:grid;grid-template-columns:1fr 1fr;gap:var(--space-5)">
        <section class="card"><h2 class="section-title">基本信息</h2><table class="data-table" style="border:none"><tbody><tr><td>订单号</td><td class="cell-mono">{{ orderNo }}</td></tr><tr><td>产品</td><td>#{{ order?.productId ?? '-' }}</td></tr><tr><td>计划数量</td><td>{{ order?.planQty ?? '-' }}</td></tr><tr><td>已完工</td><td>{{ order?.completedQty ?? '-' }}</td></tr><tr><td>计划开始</td><td>{{ order?.plannedStartAt?.slice(0, 10) || '-' }}</td></tr></tbody></table></section>
        <section class="card"><h2 class="section-title">生产进度</h2><div class="flex justify-between mb-2"><span class="text-muted">完工进度</span><span>{{ progress }}%</span></div><div class="progress-bar" style="height:10px"><div class="progress-fill" :style="{ width: `${progress}%` }"></div></div><p class="text-muted" style="margin-top:var(--space-4)">已接入 {{ operations.length }} 道工序、{{ inspections.length }} 条质量记录。</p></section>
      </div>

      <section v-show="activeTab === 'progress'" class="data-table-wrap"><table class="data-table"><thead><tr><th>工序号</th><th>工序</th><th>任务号</th><th>计划数</th><th>报工数</th><th>合格数</th><th>不良数</th><th>进度</th><th>状态</th></tr></thead><tbody><tr v-if="loadingDetails"><td colspan="9">正在加载...</td></tr><tr v-else-if="!operations.length"><td colspan="9">暂无工序进度数据</td></tr><tr v-for="row in operations" :key="row.operationTaskId"><td class="cell-mono">{{ row.processCode || `OP-${row.stepNo || '-'}` }}</td><td>{{ row.processName || '-' }}</td><td class="cell-mono">{{ row.taskNo || row.operationTaskNo }}</td><td>{{ row.planQty ?? '-' }}</td><td>{{ row.reportedQty ?? '-' }}</td><td>{{ row.qualifiedQty ?? '-' }}</td><td>{{ row.defectiveQty ?? '-' }}</td><td class="cell-progress"><div class="progress-bar"><div class="progress-fill" :style="{ width: `${operationProgress(row)}%` }"></div></div><span class="progress-label">{{ operationProgress(row) }}%</span></td><td>{{ row.status || '-' }}</td></tr></tbody></table></section>

      <section v-show="activeTab === 'quality'" class="data-table-wrap"><table class="data-table"><thead><tr><th>检验单号</th><th>检验类型</th><th>工序任务</th><th>最终结果</th><th>状态</th><th>检验时间</th></tr></thead><tbody><tr v-if="loadingDetails"><td colspan="6">正在加载...</td></tr><tr v-else-if="!inspections.length"><td colspan="6">暂无该工单的质量记录</td></tr><tr v-for="row in inspections" :key="row.id"><td class="cell-mono">{{ row.inspectionNo || `#${row.id}` }}</td><td>{{ row.inspectionType || '-' }}</td><td>{{ row.operationTaskId ?? '-' }}</td><td>{{ row.finalResult || '-' }}</td><td>{{ row.status || '-' }}</td><td>{{ row.inspectionAt?.replace('T', ' ') || '-' }}</td></tr></tbody></table></section>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>数据库实时数据</span><span class="statusbar-sep">|</span><span class="statusbar-item">{{ orderNo }}</span></footer>
  </MesLayout>
</template>
