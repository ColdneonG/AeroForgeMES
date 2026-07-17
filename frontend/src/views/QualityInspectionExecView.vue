<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import MesLayout from '@/layouts/MesLayout.vue'
import { del, get, post, put } from '@/api/client'

type InspectionOrder = { id: number; inspectionNo?: string; planId?: number; status?: string }
type QcItem = { id: number; itemCode: string; itemName: string; standardValue?: string; upperLimit?: number; lowerLimit?: number; unitId?: number }
type PlanItem = { id: number; planId: number; qcItemId: number; standardValue?: string; upperLimit?: number; lowerLimit?: number; requiredFlag?: boolean }
type InspectionResult = { id: number; inspectionId: number; qcItemId: number; measuredValue?: string; result?: string; defectReasonId?: number; remark?: string }
type ResultRow = InspectionResult & { item?: QcItem; planItem?: PlanItem }
type ResultPayload = Omit<InspectionResult, 'id'>

const route = useRoute()
const inspectionId = computed(() => Number(route.query.inspectionId || 0))
const inspection = ref<InspectionOrder | null>(null)
const rows = ref<ResultRow[]>([])
const items = ref<QcItem[]>([])
const error = ref('')
const notice = ref('')
const loading = ref(false)
const saving = ref(false)
const editing = ref(false)
const current = ref<Partial<InspectionResult>>({})

const isFailure = computed(() => current.value.result === '不合格')
const inspectionLabel = computed(() => inspection.value?.inspectionNo || (inspectionId.value ? `#${inspectionId.value}` : '-'))
const itemById = computed(() => new Map(items.value.map((item) => [item.id, item])))
const formatLimit = (value: unknown) => value === undefined || value === null || value === '' ? '-' : String(value)
const resultClass = (result?: string) => result === '不合格' ? 'badge-status-error' : result ? 'badge-status-ok' : 'badge-status-info'

function itemLabel(item?: QcItem) {
  return item ? `${item.itemCode} · ${item.itemName}` : '-'
}

function standard(row: ResultRow) {
  const plan = row.planItem
  const item = row.item
  const target = plan?.standardValue || item?.standardValue
  const lower = plan?.lowerLimit ?? item?.lowerLimit
  const upper = plan?.upperLimit ?? item?.upperLimit
  if (target) return target
  if (lower !== undefined || upper !== undefined) return `${formatLimit(lower)} ~ ${formatLimit(upper)}`
  return '-'
}

function startCreate() {
  current.value = { inspectionId: inspectionId.value, qcItemId: undefined, measuredValue: '', result: '合格', defectReasonId: undefined, remark: '' }
  editing.value = true
}

function startEdit(row: ResultRow) {
  current.value = { ...row }
  editing.value = true
}

async function load() {
  if (!inspectionId.value) { error.value = '缺少检验单编号'; rows.value = []; return }
  loading.value = true; error.value = ''
  try {
    const order = await get<InspectionOrder>(`/quality/inspection-orders/${inspectionId.value}`)
    inspection.value = order
    const [resultRows, itemRows, planItems] = await Promise.all([
      get<InspectionResult[]>('/quality/inspection-results', { inspectionId: inspectionId.value }),
      get<QcItem[]>('/quality/items'),
      order.planId ? get<PlanItem[]>('/quality/plan-items', { planId: order.planId }) : Promise.resolve([]),
    ])
    items.value = itemRows
    const byItem = new Map(resultRows.map((row) => [row.qcItemId, row]))
    const plannedRows = planItems.map((planItem) => ({
      ...(byItem.get(planItem.qcItemId) || { id: 0, inspectionId: inspectionId.value, qcItemId: planItem.qcItemId }),
      item: itemById.value.get(planItem.qcItemId), planItem,
    }))
    const unplannedRows = resultRows.filter((row) => !planItems.some((planItem) => planItem.qcItemId === row.qcItemId))
      .map((row) => ({ ...row, item: itemRows.find((item) => item.id === row.qcItemId) }))
    rows.value = [...plannedRows, ...unplannedRows]
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '检验结果加载失败'
  } finally { loading.value = false }
}

async function save() {
  if (!current.value.qcItemId || !current.value.result) { error.value = '请选择检验项并填写检验结果'; return }
  if (isFailure.value && !current.value.defectReasonId) { error.value = '不合格时必须填写缺陷原因 ID'; return }
  saving.value = true; error.value = ''; notice.value = ''
  const payload: ResultPayload = {
    inspectionId: inspectionId.value,
    qcItemId: Number(current.value.qcItemId),
    measuredValue: current.value.measuredValue?.trim() || undefined,
    result: current.value.result,
    defectReasonId: isFailure.value ? Number(current.value.defectReasonId) : undefined,
    remark: current.value.remark?.trim() || undefined,
  }
  try {
    if (current.value.id) await put(`/quality/inspection-results/${current.value.id}`, payload)
    else await post('/quality/inspection-results', payload)
    editing.value = false
    notice.value = '检验结果已保存。'
    await load()
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '保存检验结果失败'
  } finally { saving.value = false }
}

async function remove(row: ResultRow) {
  if (!row.id || !confirm(`确定删除“${itemLabel(row.item)}”的检验结果吗？`)) return
  try {
    await del(`/quality/inspection-results/${row.id}`)
    notice.value = '检验结果已删除。'
    await load()
  } catch (cause) { error.value = cause instanceof Error ? cause.message : '删除检验结果失败' }
}

onMounted(load)
watch(inspectionId, load)
</script>

<template>
  <MesLayout active="quality-exec">
    <header class="app-header"><div class="header-breadcrumb"><span>质量与追溯</span><span class="bc-sep">/</span><RouterLink to="/quality-inspection">质量检验</RouterLink><span class="bc-sep">/</span><span class="bc-current">检验结果录入</span></div></header>
    <main class="app-main">
      <div class="flex items-center justify-between mb-5"><div><h1 class="page-title" style="margin-bottom:4px">检验结果录入</h1><span class="text-muted">检验单 {{ inspectionLabel }}</span></div><button class="btn btn-primary" :disabled="loading || !inspectionId" @click="startCreate">+ 录入检验结果</button></div>
      <div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div>
      <div v-if="notice" class="alert alert-success mb-5"><span class="alert-icon">✓</span>{{ notice }}</div>
      <div class="data-table-wrap"><table class="data-table"><thead><tr><th>检验项</th><th>标准</th><th>实测值</th><th>检验结果</th><th>缺陷原因</th><th>备注</th><th>操作</th></tr></thead><tbody><tr v-if="loading"><td colspan="7">正在加载...</td></tr><tr v-else-if="!rows.length"><td colspan="7">暂无检验项；可点击“录入检验结果”新增。</td></tr><tr v-for="row in rows" :key="`${row.qcItemId}-${row.id}`"><td>{{ itemLabel(row.item) }}</td><td>{{ standard(row) }}</td><td>{{ row.measuredValue || '-' }}</td><td><span class="badge" :class="resultClass(row.result)"><span class="badge-dot"></span>{{ row.result || '待录入' }}</span></td><td>{{ row.defectReasonId || '-' }}</td><td>{{ row.remark || '-' }}</td><td class="cell-actions"><button class="btn btn-ghost btn-sm" @click="startEdit(row)">{{ row.id ? '编辑' : '录入' }}</button><button v-if="row.id" class="btn btn-ghost btn-sm" @click="remove(row)">删除</button></td></tr></tbody></table></div>
      <div v-if="editing" class="quality-mask" @click.self="editing=false"><form class="quality-dialog" @submit.prevent="save"><div class="card-header"><h2 class="card-title">{{ current.id ? '编辑' : '录入' }}检验结果</h2></div><div class="quality-form"><label>检验项<select v-model.number="current.qcItemId" class="form-select" :disabled="Boolean(current.id)" required><option :value="undefined">请选择检验项</option><option v-for="item in items" :key="item.id" :value="item.id">{{ itemLabel(item) }}</option></select></label><label>实测值<input v-model="current.measuredValue" class="form-input" maxlength="128" placeholder="例如：12.5"></label><label>检验结果<select v-model="current.result" class="form-select" required><option value="合格">合格</option><option value="不合格">不合格</option><option value="让步接收">让步接收</option></select></label><label v-if="isFailure">缺陷原因 ID<input v-model.number="current.defectReasonId" class="form-input" type="number" min="1" required></label><label class="wide">备注<textarea v-model="current.remark" class="form-input" rows="3" maxlength="500"></textarea></label></div><p v-if="isFailure" class="text-muted">不合格记录必须关联已有的缺陷原因 ID。</p><div class="dialog-actions"><button type="button" class="btn btn-secondary" :disabled="saving" @click="editing=false">取消</button><button class="btn btn-primary" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button></div></form></div>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>检验单 {{ inspectionLabel }} · {{ rows.length }} 项</span></footer>
  </MesLayout>
</template>

<style scoped>
.quality-mask { position:fixed; inset:0; z-index:30; display:grid; place-items:center; padding:var(--space-5); background:rgb(15 23 42 / .42); }
.quality-dialog { width:min(640px, 100%); padding:var(--space-5); border-radius:var(--radius-md); background:var(--color-surface, #fff); box-shadow:0 24px 60px rgb(15 23 42 / .25); }
.quality-form { display:grid; grid-template-columns:repeat(2, minmax(0, 1fr)); gap:var(--space-4); }
.quality-form label { display:grid; gap:var(--space-2); font-size:var(--text-sm); font-weight:600; }
.quality-form .wide { grid-column:1 / -1; }
.dialog-actions { display:flex; justify-content:flex-end; gap:var(--space-3); margin-top:var(--space-5); }
@media (max-width:640px) { .quality-form { grid-template-columns:1fr; } }
</style>
