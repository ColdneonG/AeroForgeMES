<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { get, post } from '@/api/client'
import { getProductionRecords, type ResourceRecord } from '@/api/production'

type BarcodeRecord = ResourceRecord & {
  id: number
  barcodeValue: string
  itemCode?: string
  itemName?: string
  batchNo?: string
  applicationRuleId?: number
  printCount?: number
  status?: string
}

type ApplicationRule = {
  id: number
  applicationRuleCode: string
  itemCode?: string
  itemName?: string
  ruleName?: string
  templateId?: number
  templateName?: string
  status?: string
}

type BarcodeTemplate = { id: number; templateName: string; paperWidth?: number; paperHeight?: number; status?: string }
type PrintRecord = { id: number; barcodeValue: string; templateName?: string; printerName?: string; printCount?: number; printAt?: string; printByName?: string }
type PrintResult = { barcode: BarcodeRecord; printRecord: PrintRecord }
type GenerationResult = { quantity: number; barcodes: BarcodeRecord[] }

const applicationRules = ref<ApplicationRule[]>([])
const templates = ref<BarcodeTemplate[]>([])
const queue = ref<BarcodeRecord[]>([])
const selectedId = ref<number | null>(null)
const selectedIds = ref<number[]>([])
const history = ref<PrintRecord[]>([])
const applicationRuleId = ref<number | null>(null)
const quantity = ref(5)
const batchNo = ref('')
const templateId = ref<number | null>(null)
const printerName = ref('BROWSER')
const copies = ref(1)
const reprintReason = ref('')
const loading = ref(false)
const printing = ref(false)
const error = ref('')
const notice = ref('')

const selected = computed(() => queue.value.find((item) => item.id === selectedId.value) || null)
const selectedForPrint = computed(() => queue.value.filter((item) => selectedIds.value.includes(item.id)))
const selectedRule = computed(() => applicationRules.value.find((item) => item.id === applicationRuleId.value) || null)
const selectedIsReprint = computed(() => (selected.value?.printCount || 0) > 0)
const batchNeedsReason = computed(() => selectedForPrint.value.some((item) => (item.printCount || 0) > 0))

const value = (row: BarcodeRecord, key: keyof BarcodeRecord) => String(row[key] ?? '-')
const isChecked = (id: number) => selectedIds.value.includes(id)

function syncTemplateFromRule() {
  if (selectedRule.value?.templateId) templateId.value = selectedRule.value.templateId
}

async function loadReferences() {
  loading.value = true
  error.value = ''
  try {
    const [rules, loadedTemplates] = await Promise.all([
      get<ApplicationRule[]>('/production/barcodes/application-rules', { status: '启用' }),
      getProductionRecords('/production/barcodes/templates', { status: '启用' }) as Promise<BarcodeTemplate[]>,
    ])
    applicationRules.value = rules
    templates.value = loadedTemplates
    applicationRuleId.value = rules[0]?.id ?? null
    syncTemplateFromRule()
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '初始化条码生成与打印页面失败'
  } finally {
    loading.value = false
  }
}

async function generate() {
  if (!applicationRuleId.value) {
    error.value = '请选择应用规则'
    return
  }
  loading.value = true
  error.value = ''
  notice.value = ''
  try {
    const result = await post<GenerationResult>('/production/barcodes/generate', {
      applicationRuleId: applicationRuleId.value,
      quantity: quantity.value,
      batchNo: batchNo.value.trim() || undefined,
    })
    queue.value = [...result.barcodes, ...queue.value]
    selectedIds.value = result.barcodes.map((item) => item.id)
    selectedId.value = result.barcodes[0]?.id ?? selectedId.value
    notice.value = `已生成 ${result.quantity} 条条码，并加入待打印清单。`
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '条码生成失败'
  } finally {
    loading.value = false
  }
}

function toggleSelection(id: number) {
  selectedIds.value = isChecked(id) ? selectedIds.value.filter((value) => value !== id) : [...selectedIds.value, id]
}

function selectAll() {
  selectedIds.value = queue.value.map((item) => item.id)
}

function deselectAll() {
  selectedIds.value = []
}

function clearPrinted() {
  const printedIds = new Set(queue.value.filter((item) => (item.printCount || 0) > 0).map((item) => item.id))
  queue.value = queue.value.filter((item) => !printedIds.has(item.id))
  selectedIds.value = selectedIds.value.filter((id) => !printedIds.has(id))
  if (selectedId.value && printedIds.has(selectedId.value)) selectedId.value = queue.value[0]?.id ?? null
}

function validatePrint(items: BarcodeRecord[]) {
  if (!items.length) {
    error.value = '请先选择要打印的条码'
    return false
  }
  if (!templateId.value) {
    error.value = '请选择打印模板'
    return false
  }
  if (items.some((item) => (item.printCount || 0) > 0) && !reprintReason.value) {
    error.value = '补打条码时必须填写补打原因'
    return false
  }
  return true
}

function updateQueue(results: PrintResult[]) {
  const byId = new Map(results.map((result) => [result.barcode.id, result.barcode]))
  queue.value = queue.value.map((item) => byId.get(item.id) || item)
  history.value = [...results.map((result) => result.printRecord), ...history.value].slice(0, 20)
}

function escapeHtml(value: string) {
  return value.replace(/[&<>'"]/g, (character) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' })[character] || character)
}

function openBrowserPrint(items: BarcodeRecord[]) {
  const labels = items.map((item) => `<article class="label"><strong>${escapeHtml(item.itemName || item.itemCode || '条码标签')}</strong><div class="barcode">${escapeHtml(item.barcodeValue)}</div><code>${escapeHtml(item.barcodeValue)}</code>${item.batchNo ? `<small>批次：${escapeHtml(item.batchNo)}</small>` : ''}</article>`).join('')
  const popup = window.open('', '_blank')
  if (!popup) {
    notice.value = '已登记打印；浏览器拦截了打印预览窗口，请允许弹窗后重试。'
    return
  }
  popup.opener = null
  popup.document.write(`<!doctype html><html lang="zh-CN"><head><meta charset="utf-8"><title>条码标签</title><style>@page{margin:4mm}body{font-family:Arial,sans-serif}.label{width:90mm;min-height:45mm;border:1px solid #222;padding:4mm;margin:0 0 4mm;box-sizing:border-box;display:grid;gap:3mm;align-content:center}.barcode{font-family:monospace;font-size:20pt;letter-spacing:2px;border-top:12px solid #111;border-bottom:12px solid #111;padding:2mm 0;text-align:center}code{font-size:12pt}small{font-size:9pt}@media print{.label{break-inside:avoid}}</style></head><body>${labels}</body></html>`)
  popup.document.close()
  window.setTimeout(() => popup.print(), 150)
}

async function printSingle() {
  if (!selected.value || !validatePrint([selected.value])) return
  printing.value = true
  error.value = ''
  try {
    const result = await post<PrintResult>(`/production/barcodes/${selected.value.id}/print`, {
      templateId: templateId.value,
      copies: copies.value,
      printerName: printerName.value,
      reason: reprintReason.value || undefined,
    })
    updateQueue([result])
    openBrowserPrint([result.barcode])
    reprintReason.value = ''
    notice.value = `已登记 ${result.barcode.barcodeValue} 的打印任务。`
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '打印失败'
  } finally {
    printing.value = false
  }
}

async function printBatch() {
  const items = selectedForPrint.value
  if (!validatePrint(items)) return
  printing.value = true
  error.value = ''
  try {
    const results = await post<PrintResult[]>('/production/barcodes/print', {
      barcodeIds: items.map((item) => item.id),
      templateId: templateId.value,
      copies: copies.value,
      printerName: printerName.value,
      reason: reprintReason.value || undefined,
    })
    updateQueue(results)
    openBrowserPrint(results.map((result) => result.barcode))
    selectedIds.value = []
    reprintReason.value = ''
    notice.value = `已登记 ${results.length} 条条码的批量打印任务。`
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '批量打印失败'
  } finally {
    printing.value = false
  }
}

async function showPrintRecords(item: BarcodeRecord) {
  selectedId.value = item.id
  error.value = ''
  try {
    history.value = await get<PrintRecord[]>(`/production/barcodes/${item.id}/print-records`)
    notice.value = `已加载 ${item.barcodeValue} 的打印记录。`
  } catch (cause) {
    error.value = cause instanceof Error ? cause.message : '加载打印记录失败'
  }
}

onMounted(loadReferences)
</script>

<template>
  <MesLayout active="barcode-generate-print">
    <header class="app-header"><div class="header-breadcrumb"><span>条码与追溯</span><span class="bc-sep">/</span><span class="bc-current">生成与打印</span></div><div class="header-actions"><span class="user-avatar">张</span></div></header>
    <main class="app-main barcode-workbench">
      <h1 class="page-title">条码生成与打印</h1>
      <div v-if="error" class="alert alert-error mb-4"><span class="alert-icon">!</span>{{ error }}</div>
      <div v-if="notice" class="alert alert-success mb-4"><span class="alert-icon">✓</span>{{ notice }}</div>

      <section class="card mb-5"><div class="card-header"><h2 class="card-title">条码生成</h2></div><div class="generate-row"><div class="form-group rule-select"><label class="form-label">应用规则</label><select v-model.number="applicationRuleId" class="form-select" :disabled="loading" @change="syncTemplateFromRule"><option :value="null">请选择应用规则</option><option v-for="rule in applicationRules" :key="rule.id" :value="rule.id">{{ rule.applicationRuleCode }} · {{ rule.itemName || rule.itemCode }} · {{ rule.ruleName }}</option></select></div><div class="form-group"><label class="form-label">关联物料</label><input class="form-input" :value="selectedRule ? `${selectedRule.itemCode || ''} ${selectedRule.itemName || ''}` : ''" readonly></div><div class="form-group batch-field"><label class="form-label">批次号</label><input v-model="batchNo" class="form-input" placeholder="可选"></div><div class="form-group qty-field"><label class="form-label">生成数量</label><input v-model.number="quantity" class="form-input" type="number" min="1" max="500"></div><button class="btn btn-primary" :disabled="loading" @click="generate">{{ loading ? '生成中…' : '生成条码' }}</button></div></section>

      <div class="print-zone">
        <section class="card queue-card"><div class="card-header"><h2 class="card-title">待打印清单</h2><div class="queue-toolbar"><button class="btn btn-ghost btn-sm" @click="selectAll">全选</button><button class="btn btn-ghost btn-sm" @click="deselectAll">取消</button><button class="btn btn-ghost btn-sm" @click="clearPrinted">清除已打印</button></div></div><div class="queue-body"><div v-if="!queue.length" class="empty-state"><div class="empty-icon">—</div><p>待打印清单为空</p><span>生成条码后会自动加入此清单</span></div><button v-for="item in queue" :key="item.id" type="button" class="queue-item" :class="{ selected: item.id === selectedId }" @click="selectedId = item.id"><input type="checkbox" :checked="isChecked(item.id)" :aria-label="`选择 ${item.barcodeValue}`" @click.stop="toggleSelection(item.id)"><span class="q-code">{{ item.barcodeValue }}</span><span class="q-meta">{{ value(item, 'itemName') }}</span><span class="q-status" :class="{ printed: (item.printCount || 0) > 0 }">{{ (item.printCount || 0) > 0 ? `已打印 ${item.printCount} 次` : '待打印' }}</span></button></div><div class="queue-footer"><span>已选 {{ selectedIds.length }} / 共 {{ queue.length }} 条</span><button class="btn btn-primary btn-sm" :disabled="printing || !selectedIds.length" @click="printBatch">{{ printing ? '处理中…' : '批量打印选中' }}</button></div></section>

        <aside class="card preview-card"><div class="card-header"><h2 class="card-title">标签预览与打印设置</h2></div><div class="label-preview" :class="{ empty: !selected }"><template v-if="selected"><span v-if="selectedIsReprint" class="printed-badge">已打印</span><strong>{{ selected.itemName || selected.itemCode || '条码标签' }}</strong><div class="barcode-lines" aria-hidden="true"></div><code>{{ selected.barcodeValue }}</code><small>批次：{{ selected.batchNo || '-' }}</small></template><span v-else>← 选择左侧条码查看预览</span></div><div class="print-settings"><div class="settings-row"><div class="form-group"><label class="form-label">打印模板</label><select v-model.number="templateId" class="form-select"><option :value="null">请选择模板</option><option v-for="template in templates" :key="template.id" :value="template.id">{{ template.templateName }}<template v-if="template.paperWidth && template.paperHeight"> · {{ template.paperWidth }} × {{ template.paperHeight }} mm</template></option></select></div><div class="form-group copies-field"><label class="form-label">份数</label><input v-model.number="copies" class="form-input" type="number" min="1" max="100"></div></div><div class="form-group"><label class="form-label">打印机</label><input v-model="printerName" class="form-input" placeholder="BROWSER"></div><div v-if="selectedIsReprint || batchNeedsReason" class="form-group"><label class="form-label">补打原因 <span class="required">*</span></label><select v-model="reprintReason" class="form-select"><option value="">请选择补打原因</option><option>标签污损</option><option>打印不清</option><option>标签脱落</option><option>信息变更</option><option>标签遗失</option><option>其他</option></select></div><button class="btn btn-primary w-full" :disabled="printing || !selected" @click="printSingle">{{ printing ? '处理中…' : selectedIsReprint ? '补打标签' : '打印标签' }}</button><button v-if="selected" class="btn btn-ghost w-full" @click="showPrintRecords(selected)">查看此条打印记录</button></div></aside>
      </div>

      <section class="card history-card"><div class="card-header"><h2 class="card-title">最近打印记录</h2><span class="text-muted">最近 {{ history.length }} 条</span></div><div class="data-table-wrap"><table class="data-table"><thead><tr><th>打印时间</th><th>条码</th><th>模板</th><th>打印机</th><th>份数</th><th>操作人</th></tr></thead><tbody><tr v-if="!history.length"><td colspan="6">选择条码后可查看其打印记录；新打印的记录会自动显示在此。</td></tr><tr v-for="record in history" :key="record.id"><td>{{ record.printAt?.replace('T', ' ') || '-' }}</td><td class="cell-mono">{{ record.barcodeValue }}</td><td>{{ record.templateName || '-' }}</td><td>{{ record.printerName || '-' }}</td><td>{{ record.printCount || '-' }}</td><td>{{ record.printByName || '-' }}</td></tr></tbody></table></div></section>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>条码生成与打印</span><span class="statusbar-sep">|</span><span class="statusbar-item">待打印 {{ queue.length }} 条</span></footer>
  </MesLayout>
</template>

<style scoped>
.barcode-workbench { min-width: 0; }
.generate-row { display: grid; grid-template-columns: minmax(230px, 1.6fr) minmax(180px, 1fr) minmax(130px, .6fr) 100px auto; gap: var(--space-3); align-items: end; }
.print-zone { display: grid; grid-template-columns: minmax(0, 1.3fr) minmax(290px, .7fr); gap: var(--space-5); margin-bottom: var(--space-5); }
.queue-card, .preview-card { padding: 0; overflow: hidden; }
.queue-card .card-header, .preview-card .card-header { padding: var(--space-4) var(--space-5); margin-bottom: 0; }
.queue-toolbar { display: flex; gap: var(--space-1); flex-wrap: wrap; }
.queue-body { min-height: 330px; max-height: 480px; overflow-y: auto; }
.queue-item { width: 100%; display: grid; grid-template-columns: auto minmax(150px, 1fr) minmax(120px, .8fr) auto; gap: var(--space-3); align-items: center; padding: var(--space-3) var(--space-5); border: 0; border-bottom: 1px solid var(--muted); background: var(--bg); color: var(--fg); text-align: left; cursor: pointer; }
.queue-item:hover, .queue-item.selected { background: var(--surface); }
.q-code { font-family: var(--font-mono); font-size: var(--text-caption); font-weight: 600; }
.q-meta, .q-status { font-size: var(--text-caption); color: var(--fg); opacity: .55; }
.q-status { color: var(--accent); opacity: 1; font-weight: 600; }
.q-status.printed { color: var(--fg); opacity: .5; }
.queue-footer { display: flex; justify-content: space-between; gap: var(--space-3); align-items: center; padding: var(--space-3) var(--space-5); border-top: 1px solid var(--border); font-size: var(--text-caption); color: var(--fg); opacity: .65; }
.empty-state { min-height: 330px; display: grid; place-content: center; justify-items: center; gap: var(--space-2); color: var(--fg); opacity: .45; font-size: var(--text-small); }
.empty-state p { margin: 0; }
.empty-icon { font-size: var(--text-display); }
.preview-card { padding-bottom: var(--space-5); }
.label-preview { position: relative; margin: var(--space-4) var(--space-5); min-height: 190px; display: grid; place-content: center; justify-items: center; gap: var(--space-2); border: 2px dashed var(--border); border-radius: var(--radius); background: var(--bg); text-align: center; }
.label-preview.empty { color: var(--fg); opacity: .4; font-size: var(--text-small); }
.label-preview code { font-family: var(--font-mono); font-size: var(--text-body); font-weight: 700; letter-spacing: .06em; }
.label-preview small { color: var(--fg); opacity: .55; }
.barcode-lines { width: min(240px, 78%); height: 46px; background: repeating-linear-gradient(90deg, var(--fg) 0 2px, transparent 2px 4px, var(--fg) 4px 5px, transparent 5px 8px, var(--fg) 8px 12px, transparent 12px 14px); }
.printed-badge { position: absolute; top: var(--space-2); right: var(--space-2); padding: 1px 7px; border-radius: 999px; background: var(--muted); font-size: var(--text-caption); }
.print-settings { display: grid; gap: var(--space-3); padding: 0 var(--space-5); }
.settings-row { display: grid; grid-template-columns: 1fr 90px; gap: var(--space-2); }
.required { color: var(--accent); }
.history-card { padding: 0; overflow: hidden; }
.history-card .card-header { padding: var(--space-4) var(--space-5); margin-bottom: 0; }
.history-card .data-table-wrap { border: 0; border-radius: 0; }
@media (max-width: 1100px) { .generate-row { grid-template-columns: repeat(2, minmax(0, 1fr)); } .generate-row .btn { width: fit-content; } .print-zone { grid-template-columns: 1fr; } }
@media (max-width: 680px) { .generate-row { grid-template-columns: 1fr; } .generate-row .btn { width: 100%; } .queue-item { grid-template-columns: auto 1fr auto; } .q-meta { display: none; } .queue-footer { align-items: stretch; flex-direction: column; } .queue-footer .btn { width: 100%; } }
</style>
