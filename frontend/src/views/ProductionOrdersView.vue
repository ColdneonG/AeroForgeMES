<script setup lang="ts">
import { onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
import { createWorkOrder, getWorkOrders, postProductionAction, type WorkOrder } from '@/api/production'

const { notify } = usePageInteractions()
const keyword = ref('')
const status = ref('')
const rows = ref<WorkOrder[]>([])
const loading = ref(false)
const error = ref('')
const processing = ref('')
const expandedOrderId = ref<number | null>(null)
const creating = ref(false)
const saving = ref(false)
const form = ref({ workOrderNo: '', productId: '', planQty: '', lineId: '', externalNo: '', plannedStartAt: '', plannedEndAt: '', deliveryDate: '', routeId: '' })

function statusLabel(value?: string) {
  const labels: Record<string, string> = { DRAFT: '草稿', WAIT_ISSUE: '待下发', ISSUED: '已下发', RUNNING: '生产中', PAUSED: '暂停', COMPLETED: '已完成', CLOSED: '已关闭', VOIDED: '作废' }
  return value ? (labels[value] || value) : '-'
}

function statusClass(value?: string) {
  if (value === 'DRAFT' || value === '草稿') return 'badge-status-draft'
  if (value === 'PAUSED' || value === '暂停') return 'badge-status-warn'
  if (value === 'CLOSED' || value === 'COMPLETED' || value === '已关闭' || value === '已完成' || value === '作废') return 'badge-status-info'
  return 'badge-status-ok'
}

type Transition = { action: string; label: string; emphasis?: boolean }
const allTransitions: Transition[] = [
  { action: 'issue', label: '下发' },
  { action: 'confirm-issue', label: '确认下发' },
  { action: 'start', label: '开工 / 继续' },
  { action: 'pause', label: '暂停' },
  { action: 'complete', label: '完工' },
  { action: 'close', label: '关闭工单' },
  { action: 'void', label: '作废' },
]
function transitions(row: WorkOrder): Transition[] {
  const current = row.status || ''
  const map: Record<string, Transition[]> = {
    DRAFT: [{ action: 'issue', label: '下发', emphasis: true }, { action: 'void', label: '作废' }],
    '草稿': [{ action: 'issue', label: '下发', emphasis: true }, { action: 'void', label: '作废' }],
    WAIT_ISSUE: [{ action: 'confirm-issue', label: '确认下发', emphasis: true }, { action: 'void', label: '作废' }],
    '待下发': [{ action: 'confirm-issue', label: '确认下发', emphasis: true }, { action: 'void', label: '作废' }],
    ISSUED: [{ action: 'start', label: '开工', emphasis: true }, { action: 'complete', label: '完工' }],
    '已下发': [{ action: 'start', label: '开工', emphasis: true }, { action: 'complete', label: '完工' }],
    RUNNING: [{ action: 'pause', label: '暂停' }, { action: 'complete', label: '完工', emphasis: true }],
    '生产中': [{ action: 'pause', label: '暂停' }, { action: 'complete', label: '完工', emphasis: true }],
    PAUSED: [{ action: 'start', label: '继续生产', emphasis: true }, { action: 'complete', label: '完工' }],
    '暂停': [{ action: 'start', label: '继续生产', emphasis: true }, { action: 'complete', label: '完工' }],
    COMPLETED: [{ action: 'close', label: '关闭工单', emphasis: true }],
    '已完成': [{ action: 'close', label: '关闭工单', emphasis: true }],
  }
  return map[current] || []
}

function isAllowed(row: WorkOrder, action: string) {
  return transitions(row).some((item) => item.action === action)
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

function closeCreateForm() {
  creating.value = false
  form.value = { workOrderNo: '', productId: '', planQty: '', lineId: '', externalNo: '', plannedStartAt: '', plannedEndAt: '', deliveryDate: '', routeId: '' }
}

async function create() {
  saving.value = true
  error.value = ''
  try {
    await createWorkOrder({
      workOrderNo: form.value.workOrderNo.trim(),
      productId: Number(form.value.productId),
      planQty: Number(form.value.planQty),
      lineId: Number(form.value.lineId),
      externalNo: form.value.externalNo.trim() || undefined,
      plannedStartAt: form.value.plannedStartAt || undefined,
      plannedEndAt: form.value.plannedEndAt || undefined,
      deliveryDate: form.value.deliveryDate || undefined,
      routeId: form.value.routeId ? Number(form.value.routeId) : undefined,
      status: '草稿',
    })
    closeCreateForm()
    notify('生产订单已创建')
    await load()
  } catch (reason) {
    error.value = reason instanceof Error ? reason.message : '创建生产订单失败'
  } finally {
    saving.value = false
  }
}

async function transition(row: WorkOrder, item: Transition) {
  const key = `${row.id}-${item.action}`
  processing.value = key
  expandedOrderId.value = null
  error.value = ''
  try {
    const payload = item.action === 'complete'
      ? { completedQty: Number(row.planQty || row.completedQty || 0), qualifiedQty: Number(row.planQty || row.completedQty || 0), defectiveQty: 0 }
      : {}
    await postProductionAction(`/production/work-orders/${row.id}/${item.action}`, payload)
    notify(`${row.workOrderNo || `工单 #${row.id}`} 已${item.label}`)
    await load()
  } catch (reason) {
    error.value = reason instanceof Error ? reason.message : '工单状态转换失败'
  } finally {
    processing.value = ''
  }
}

function toggleActionPanel(id: number) {
  expandedOrderId.value = expandedOrderId.value === id ? null : id
}

onMounted(load)
</script>

<template>
<MesLayout active="orders">
  <header class="app-header" data-od-id="orders-header">
    <div class="header-breadcrumb"><span>生产管理</span> <span class="bc-sep">/</span> <span class="bc-current">生产订单</span></div>
    <div class="header-actions"><button class="btn btn-primary btn-sm" @click="creating = true">+ 新建订单</button><span class="user-avatar">张</span></div>
  </header>

  <main class="app-main" data-od-id="orders-main">
    <h1 class="page-title">生产订单</h1>
    <div class="card mb-5" data-od-id="orders-filters">
      <div class="search-bar" style="margin-bottom:0;">
        <div class="form-group" style="min-width:180px;"><label class="form-label">订单号</label><input v-model="keyword" class="form-input" type="text" placeholder="搜索订单号..." @keyup.enter="load" /></div>
        <div class="form-group" style="min-width:140px;"><label class="form-label">订单状态</label><select v-model="status" class="form-select"><option value="">全部</option><option value="草稿">草稿</option><option value="待下发">待下发</option><option value="已下发">已下达</option><option value="生产中">生产中</option><option value="已完成">已完成</option><option value="暂停">已暂停</option></select></div>
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
          <template v-for="row in rows" :key="row.id">
          <tr>
            <td class="cell-mono"><RouterLink :to="{ path: '/production-order-detail', query: { id: row.id } }">{{ row.workOrderNo || `#${row.id}` }}</RouterLink></td>
            <td>产品 #{{ row.productId ?? '-' }}</td><td>{{ row.planQty ?? '-' }}</td><td>{{ row.completedQty ?? '-' }}</td><td>{{ formatDate(row.plannedStartAt) }}</td><td>产线 #{{ row.lineId ?? '-' }}</td>
            <td><span class="badge" :class="statusClass(row.status)"><span v-if="row.status !== 'DRAFT' && row.status !== '草稿'" class="badge-dot"></span>{{ statusLabel(row.status) }}</span></td>
            <td class="cell-actions"><button class="action-trigger" :class="{ active: expandedOrderId === row.id }" :aria-expanded="expandedOrderId === row.id" aria-label="展开工单状态操作" @click="toggleActionPanel(row.id)"><span></span><span></span><span></span></button></td>
          </tr>
          <tr v-if="expandedOrderId === row.id" class="action-panel-row"><td colspan="8"><div class="action-panel"><div class="action-panel-heading"><p class="action-panel-title">工单状态转换</p></div><div class="action-panel-buttons"><button v-for="item in allTransitions" :key="item.action" type="button" class="panel-action" :class="{ danger: item.action === 'void', primary: item.emphasis }" :disabled="Boolean(processing) || !isAllowed(row, item.action)" @click="transition(row, item)"><span>{{ processing === `${row.id}-${item.action}` ? '处理中...' : item.label }}</span></button></div></div></td></tr>
          </template>
        </tbody>
      </table>
      <div class="pagination"><span>共 {{ rows.length }} 条记录</span></div>
    </div>
    <div v-if="creating" class="order-mask" @click.self="closeCreateForm">
      <form class="order-dialog" @submit.prevent="create">
        <div class="card-header"><h2 class="card-title">新建生产订单</h2><button type="button" class="btn btn-ghost btn-sm" @click="closeCreateForm">关闭</button></div>
        <div class="order-form">
          <label>订单号<input v-model="form.workOrderNo" class="form-input" maxlength="64" required></label>
          <label>产品 ID<input v-model="form.productId" class="form-input" type="number" min="1" required></label>
          <label>计划数量<input v-model="form.planQty" class="form-input" type="number" min="0.0001" step="any" required></label>
          <label>产线 ID<input v-model="form.lineId" class="form-input" type="number" min="1" required></label>
          <label>外部订单号<input v-model="form.externalNo" class="form-input" maxlength="64"></label>
          <label>工艺路线 ID<input v-model="form.routeId" class="form-input" type="number" min="1"></label>
          <label>计划开始<input v-model="form.plannedStartAt" class="form-input" type="datetime-local"></label>
          <label>计划结束<input v-model="form.plannedEndAt" class="form-input" type="datetime-local"></label>
          <label>交付日期<input v-model="form.deliveryDate" class="form-input" type="date"></label>
        </div>
        <div class="dialog-actions"><button type="button" class="btn btn-secondary" :disabled="saving" @click="closeCreateForm">取消</button><button class="btn btn-primary" :disabled="saving">{{ saving ? '创建中...' : '创建订单' }}</button></div>
      </form>
    </div>
  </main>

  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">生产订单 · {{ rows.length }} 条</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span></footer>
</MesLayout>
</template>

<style>
.action-trigger { display:inline-flex; align-items:center; justify-content:center; width:32px; height:32px; gap:3px; color:var(--color-text-muted); background:transparent; border:1px solid transparent; border-radius:var(--radius-sm); cursor:pointer; }.action-trigger:hover,.action-trigger.active { color:var(--color-primary); background:var(--color-surface-muted); border-color:var(--color-border); }.action-trigger span { display:block; width:3px; height:3px; background:currentColor; border-radius:50%; }
.order-mask{position:fixed;inset:0;z-index:20;background:rgba(15,23,42,.38);display:grid;place-items:center}.order-dialog{width:min(680px,calc(100vw - 32px));background:#fff;border-radius:8px;padding:20px;box-shadow:0 18px 50px rgba(15,23,42,.28)}.order-form{display:grid;grid-template-columns:1fr 1fr;gap:16px}.order-form label{display:grid;gap:7px;font-size:13px;color:var(--color-text-muted,#667085)}.dialog-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:22px}
.action-panel-row td { padding:0 !important; background:#fafbfc; }.action-panel { display:flex; align-items:center; gap:24px; padding:18px 24px; border-top:1px solid var(--color-border); border-bottom:1px solid var(--color-border); }.action-panel-heading { flex:0 0 200px; }.action-panel-title { margin:0; color:#20242c; font-size:14px; font-weight:600; line-height:1.4; }.action-panel-buttons { display:flex; flex:0 1 auto; flex-wrap:wrap; align-items:center; gap:10px 12px; }.panel-action { display:inline-flex; align-items:center; justify-content:center; min-width:108px; height:42px; padding:0 22px; color:#20242c; font:600 var(--text-sm)/1 inherit; white-space:nowrap; background:#f1f3f6; border:1px solid #dbe1e8; border-radius:5px; cursor:pointer; transition:background-color .15s ease, border-color .15s ease, color .15s ease; }.panel-action:hover:not(:disabled) { background:#e7ebf0; border-color:#cbd3dc; }.panel-action.primary:not(:disabled) { color:#fff; background:#2f61ad; border-color:#2f61ad; }.panel-action.primary:hover:not(:disabled) { background:#285695; border-color:#285695; }.panel-action:disabled { color:#b5bdc8; background:#f7f8fa; border-color:#eceff3; cursor:not-allowed; opacity:1; }.panel-action.danger:not(:disabled) { color:#c9362b; background:#fff7f6; border-color:#dc5a52; }.panel-action.danger:hover:not(:disabled) { color:#ad2a22; background:#ffefed; border-color:#c8443c; }
</style>
