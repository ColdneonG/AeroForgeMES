<script setup lang="ts">
import { onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
import { getWorkOrders, type WorkOrder } from '@/api/production'

const { notify } = usePageInteractions()
const keyword = ref('')
const status = ref('')
const rows = ref<WorkOrder[]>([])
const loading = ref(false)
const error = ref('')

function statusLabel(value?: string) {
  const labels: Record<string, string> = { DRAFT: '待排产', ISSUED: '已下达', CONFIRMED: '已确认', IN_PROGRESS: '生产中', PAUSED: '已暂停', COMPLETED: '已完成', CLOSED: '已关闭' }
  return value ? (labels[value] || value) : '-'
}

function statusClass(value?: string) {
  if (value === 'DRAFT') return 'badge-status-draft'
  if (value === 'PAUSED') return 'badge-status-warn'
  if (value === 'CLOSED' || value === 'COMPLETED') return 'badge-status-info'
  return 'badge-status-ok'
}

function formatDate(value?: string) {
  return value ? value.slice(0, 10) : '-'
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    rows.value = await getWorkOrders({ keyword: keyword.value || undefined, status: status.value || undefined })
  } catch (reason) {
    error.value = reason instanceof Error ? reason.message : '生产订单加载失败'
  } finally {
    loading.value = false
  }
}

function reset() {
  keyword.value = ''
  status.value = ''
  void load()
}

onMounted(load)
</script>

<template>
<MesLayout active="orders">
  <header class="app-header" data-od-id="orders-header">
    <div class="header-breadcrumb"><span>生产管理</span> <span class="bc-sep">/</span> <span class="bc-current">生产订单</span></div>
    <div class="header-actions"><button class="btn btn-primary btn-sm" @click="notify('新建订单将在后续交互波次接入')">+ 新建订单</button><span class="user-avatar">张</span></div>
  </header>

  <main class="app-main" data-od-id="orders-main">
    <h1 class="page-title">生产订单</h1>
    <div class="card mb-5" data-od-id="orders-filters">
      <div class="search-bar" style="margin-bottom:0;">
        <div class="form-group" style="min-width:180px;"><label class="form-label">订单号</label><input v-model="keyword" class="form-input" type="text" placeholder="搜索订单号..." @keyup.enter="load" /></div>
        <div class="form-group" style="min-width:140px;"><label class="form-label">订单状态</label><select v-model="status" class="form-select"><option value="">全部</option><option value="DRAFT">待排产</option><option value="ISSUED">已下达</option><option value="IN_PROGRESS">生产中</option><option value="COMPLETED">已完成</option><option value="PAUSED">已暂停</option></select></div>
        <button class="btn btn-secondary btn-sm" style="align-self:flex-end;" :disabled="loading" @click="load">{{ loading ? '加载中...' : '查询' }}</button>
        <button class="btn btn-ghost btn-sm" style="align-self:flex-end;" @click="reset">重置</button>
      </div>
    </div>

    <div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div>
    <div class="data-table-wrap" data-od-id="orders-table">
      <table class="data-table">
        <thead><tr><th>订单号</th><th>产品</th><th>计划数量</th><th>已完工</th><th>计划日期</th><th>产线</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-if="loading"><td colspan="8" class="text-muted">正在加载生产订单...</td></tr>
          <tr v-else-if="!rows.length"><td colspan="8" class="text-muted">暂无生产订单</td></tr>
          <tr v-for="row in rows" :key="row.id">
            <td class="cell-mono"><RouterLink :to="{ path: '/production-order-detail', query: { id: row.id } }">{{ row.workOrderNo || `#${row.id}` }}</RouterLink></td>
            <td>产品 #{{ row.productId ?? '-' }}</td><td>{{ row.planQty ?? '-' }}</td><td>{{ row.completedQty ?? '-' }}</td><td>{{ formatDate(row.plannedStartAt) }}</td><td>产线 #{{ row.lineId ?? '-' }}</td>
            <td><span class="badge" :class="statusClass(row.status)"><span v-if="row.status !== 'DRAFT'" class="badge-dot"></span>{{ statusLabel(row.status) }}</span></td>
            <td class="cell-actions"><RouterLink :to="{ path: '/production-order-detail', query: { id: row.id } }" class="btn btn-ghost btn-sm">详情</RouterLink></td>
          </tr>
        </tbody>
      </table>
      <div class="pagination"><span>共 {{ rows.length }} 条记录</span></div>
    </div>
  </main>

  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">生产订单 · {{ rows.length }} 条</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span></footer>
</MesLayout>
</template>
