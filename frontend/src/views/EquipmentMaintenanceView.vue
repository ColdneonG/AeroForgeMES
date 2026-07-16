<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { get, post } from '@/api/client'
import { getEquipmentRecords } from '@/api/equipment'

type Row = Record<string, unknown> & { id?: string | number }
type PageResponse = { records?: Row[] }

const activeTab = ref<'plans' | 'records' | 'inspections'>('plans')
const maintenanceRows = ref<Row[]>([])
const inspectionRows = ref<Row[]>([])
const equipmentNames = ref<Record<string, string>>({})
const loading = ref(false)
const error = ref('')
const statusFilter = ref('all')
const page = ref(1)
const pageSize = 10
const savingId = ref<string | number | null>(null)

function camelCase(key: string) {
  return key.replace(/_([a-z])/g, (_, letter: string) => letter.toUpperCase())
}

function normalize(row: Row): Row {
  return Object.entries(row).reduce<Row>((result, [key, value]) => {
    result[key] = value
    result[camelCase(key)] = value
    return result
  }, {})
}

function responseRecords(response: unknown) {
  const rows = Array.isArray(response) ? response : (response as PageResponse)?.records || []
  return rows.map(normalize)
}

function value(row: Row, ...keys: string[]) {
  return String(keys.map((key) => row[key]).find((item) => item !== undefined && item !== null && item !== '') ?? '-')
}

function equipmentName(row: Row) {
  const id = equipmentId(row)
  return equipmentNames.value[id] || id
}

function equipmentId(row: Row) {
  return value(row, 'equipmentId')
}

const maintenanceStatusLabels: Record<string, string> = {
  PENDING: '待执行',
  PROCESSING: '保养中',
  COMPLETED: '已完成',
  CLOSED: '已关闭',
  CANCELLED: '已取消',
}

const inspectionStatusLabels: Record<string, string> = {
  PENDING: '待点检',
  PROCESSING: '点检中',
  COMPLETED: '已完成',
  CLOSED: '已关闭',
  CANCELLED: '已取消',
}

function maintenanceStatus(row: Row) {
  return String(row.status || '').toUpperCase()
}

function maintenanceStatusLabel(row: Row) {
  return maintenanceStatusLabels[maintenanceStatus(row)] || value(row, 'status')
}

function inspectionStatusLabel(row: Row) {
  const status = String(row.status || '').toUpperCase()
  return inspectionStatusLabels[status] || value(row, 'status')
}

const isCompleted = (row: Row) => ['COMPLETED', 'CLOSED'].includes(maintenanceStatus(row))
const isCancelled = (row: Row) => maintenanceStatus(row) === 'CANCELLED'
const planned = computed(() => maintenanceRows.value.filter((row) => !isCompleted(row) && !isCancelled(row)))
const records = computed(() => maintenanceRows.value.filter(isCompleted))
const cancelled = computed(() => maintenanceRows.value.filter(isCancelled))
const sourceRows = computed(() => {
  if (activeTab.value === 'inspections') return inspectionRows.value
  if (activeTab.value === 'records') return records.value
  if (activeTab.value === 'plans' && statusFilter.value === 'CANCELLED') return cancelled.value
  return planned.value
})
const filteredRows = computed(() => statusFilter.value === 'all' || statusFilter.value === 'CANCELLED'
  ? sourceRows.value
  : sourceRows.value.filter((row) => maintenanceStatus(row) === statusFilter.value))
const pageCount = computed(() => Math.max(1, Math.ceil(filteredRows.value.length / pageSize)))
const pageRows = computed(() => filteredRows.value.slice((page.value - 1) * pageSize, page.value * pageSize))

function resetPage() {
  page.value = 1
}

watch([activeTab, statusFilter], resetPage)
watch(activeTab, () => {
  statusFilter.value = 'all'
})
watch(pageCount, (count) => {
  if (page.value > count) page.value = count
})

async function changeStatus(row: Row, action: 'submit' | 'complete' | 'cancel') {
  if (row.id === undefined || savingId.value !== null) return
  savingId.value = row.id
  error.value = ''
  try {
    await post(`/equipment/maintenance-tasks/${row.id}/${action}`)
    await load()
  } catch (reason) {
    error.value = reason instanceof Error ? reason.message : '保养任务状态更新失败'
  } finally {
    savingId.value = null
  }
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [maintenance, inspections, equipments] = await Promise.all([
      get<unknown>('/equipment/maintenance-tasks', { size: 200 }),
      get<unknown>('/equipment/inspections', { size: 200 }),
      getEquipmentRecords(),
    ])
    maintenanceRows.value = responseRecords(maintenance)
    inspectionRows.value = responseRecords(inspections)
    equipmentNames.value = Object.fromEntries(equipments.map((equipment) => [String(equipment.id), value(equipment, 'equipmentName', 'equipmentCode', 'id')]))
  } catch (reason) {
    maintenanceRows.value = []
    inspectionRows.value = []
    equipmentNames.value = {}
    error.value = reason instanceof Error ? reason.message : '点检保养数据加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
<template>
  <MesLayout active="equip-maint">
    <header class="app-header">
      <div class="header-breadcrumb"><span>设备与安灯</span><span class="bc-sep">/</span><span class="bc-current">点检保养</span></div>
      <div class="header-actions"><button class="btn btn-secondary btn-sm" :disabled="loading" @click="load">刷新</button><span class="user-avatar">张</span></div>
    </header>
    <main class="app-main" data-od-id="maint-main">
      <h1 class="page-title">点检保养</h1>
      <div class="maintenance-actions" data-od-id="maint-tabs">
        <button class="btn btn-sm" :class="activeTab === 'plans' ? 'btn-primary' : 'btn-secondary'" @click="activeTab = 'plans'">保养计划</button>
        <button class="btn btn-sm" :class="activeTab === 'records' ? 'btn-primary' : 'btn-secondary'" @click="activeTab = 'records'">执行记录</button>
        <button class="btn btn-sm" :class="activeTab === 'inspections' ? 'btn-primary' : 'btn-secondary'" @click="activeTab = 'inspections'">点检记录</button>
      </div>
      <div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div>
      <div class="maintenance-toolbar">
        <label>状态
          <select v-model="statusFilter" class="form-input">
            <option value="all">全部</option>
            <option v-if="activeTab === 'plans'" value="PENDING">待执行</option>
            <option v-if="activeTab === 'plans'" value="PROCESSING">保养中</option>
            <option v-if="activeTab === 'plans'" value="CANCELLED">已取消</option>
            <option v-if="activeTab === 'records'" value="COMPLETED">已完成</option>
            <option v-if="activeTab === 'records'" value="CLOSED">已关闭</option>
            <option v-if="activeTab === 'inspections'" value="PENDING">待点检</option>
            <option v-if="activeTab === 'inspections'" value="PROCESSING">点检中</option>
            <option v-if="activeTab === 'inspections'" value="COMPLETED">已完成</option>
          </select>
        </label>
        <span class="text-muted">共 {{ filteredRows.length }} 条</span>
      </div>
      <div class="data-table-wrap">
        <table v-if="activeTab === 'plans'" class="data-table">
          <thead><tr><th>计划编号</th><th>设备</th><th>计划日期</th><th>负责人</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-if="loading"><td colspan="6">正在加载...</td></tr>
            <tr v-else-if="!pageRows.length"><td colspan="6" class="text-muted">暂无符合条件的保养任务</td></tr>
            <tr v-for="(row, index) in pageRows" :key="row.id ?? index">
              <td class="cell-mono">{{ value(row, 'maintenanceNo', 'id') }}</td>
              <td><RouterLink v-if="equipmentId(row) !== '-'" :to="{ path: '/equipment/detail', query: { id: equipmentId(row) } }">{{ equipmentName(row) }}</RouterLink><span v-else>-</span></td>
              <td>{{ value(row, 'planAt') }}</td><td>{{ value(row, 'assignedTo') }}</td>
              <td><span class="badge badge-status-info"><span class="badge-dot"></span>{{ maintenanceStatusLabel(row) }}</span></td>
              <td class="cell-actions">
                <button v-if="maintenanceStatus(row) === 'PENDING'" class="btn btn-ghost btn-sm" :disabled="savingId !== null" @click="changeStatus(row, 'submit')">开始保养</button>
                <button v-if="maintenanceStatus(row) === 'PROCESSING'" class="btn btn-primary btn-sm" :disabled="savingId !== null" @click="changeStatus(row, 'complete')">完成保养</button>
                <button v-if="['PENDING', 'PROCESSING'].includes(maintenanceStatus(row))" class="btn btn-ghost btn-sm" :disabled="savingId !== null" @click="changeStatus(row, 'cancel')">取消</button>
              </td>
            </tr>
          </tbody>
        </table>
        <table v-else-if="activeTab === 'records'" class="data-table">
          <thead><tr><th>记录编号</th><th>设备</th><th>完成时间</th><th>负责人</th><th>结果</th><th>状态</th></tr></thead>
          <tbody>
            <tr v-if="loading"><td colspan="6">正在加载...</td></tr>
            <tr v-else-if="!pageRows.length"><td colspan="6" class="text-muted">暂无已完成的保养记录</td></tr>
            <tr v-for="(row, index) in pageRows" :key="row.id ?? index">
              <td class="cell-mono">{{ value(row, 'maintenanceNo', 'id') }}</td>
              <td><RouterLink v-if="equipmentId(row) !== '-'" :to="{ path: '/equipment/detail', query: { id: equipmentId(row) } }">{{ equipmentName(row) }}</RouterLink><span v-else>-</span></td>
              <td>{{ value(row, 'completedAt') }}</td><td>{{ value(row, 'assignedTo') }}</td><td>{{ value(row, 'result') }}</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>{{ maintenanceStatusLabel(row) }}</span></td>
            </tr>
          </tbody>
        </table>
        <table v-else class="data-table">
          <thead><tr><th>点检编号</th><th>设备</th><th>点检时间</th><th>点检人</th><th>结果</th><th>异常说明</th><th>状态</th></tr></thead>
          <tbody>
            <tr v-if="loading"><td colspan="7">正在加载...</td></tr>
            <tr v-else-if="!pageRows.length"><td colspan="7" class="text-muted">暂无符合条件的点检记录</td></tr>
            <tr v-for="(row, index) in pageRows" :key="row.id ?? index">
              <td class="cell-mono">{{ value(row, 'inspectionNo', 'id') }}</td>
              <td><RouterLink v-if="equipmentId(row) !== '-'" :to="{ path: '/equipment/detail', query: { id: equipmentId(row) } }">{{ equipmentName(row) }}</RouterLink><span v-else>-</span></td>
              <td>{{ value(row, 'inspectionAt') }}</td><td>{{ value(row, 'inspectorId') }}</td><td>{{ value(row, 'result') }}</td><td>{{ value(row, 'abnormalDesc') }}</td>
              <td><span class="badge badge-status-info"><span class="badge-dot"></span>{{ inspectionStatusLabel(row) }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="pageCount > 1" class="pagination"><button class="btn btn-ghost btn-sm" :disabled="page === 1" @click="page--">上一页</button><span>第 {{ page }} / {{ pageCount }} 页</span><button class="btn btn-ghost btn-sm" :disabled="page === pageCount" @click="page++">下一页</button></div>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>点检保养数据</span><span class="statusbar-sep">|</span><span class="statusbar-item">待执行 {{ planned.length }} 条 · 已完成 {{ records.length }} 条 · 点检 {{ inspectionRows.length }} 条</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto">AeroForge MES v2.0</span></footer>
  </MesLayout>
</template>

<style scoped>
.maintenance-toolbar { display:flex; align-items:center; justify-content:space-between; gap:var(--space-3); margin:0 0 var(--space-4); }
.maintenance-actions { display:flex; gap:var(--space-2); margin:0 0 var(--space-5); }
.maintenance-toolbar label { display:flex; align-items:center; gap:var(--space-2); color:var(--color-text-muted); font-size:var(--text-sm); }
.maintenance-toolbar select { width:140px; }
</style>
