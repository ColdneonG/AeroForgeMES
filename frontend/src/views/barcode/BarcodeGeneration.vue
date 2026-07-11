<template>
  <section class="barcode-page">
    <header class="page-heading">
      <div>
        <p>{{ t('barcode.generate.eyebrow') }}</p>
        <h1>{{ t('barcode.generate.title') }}</h1>
        <span>按应用规则批量生成产品码、物料批次码和包装码，支持标签打印与补打审计。</span>
      </div>
      <div class="heading-actions">
        <button class="secondary-btn" type="button" :disabled="selectedIds.length === 0" @click="openPrintDialog()">
          打印所选（{{ selectedIds.length }}）
        </button>
        <button class="primary-btn" type="button" @click="openGenerateDialog">批量生成条码</button>
      </div>
    </header>

    <div v-if="notice" class="message success">{{ notice }}</div>
    <div v-if="error" class="message error">{{ error }}</div>

    <div class="filter-bar">
      <input v-model.trim="filters.keyword" type="search" placeholder="扫描或输入条码 / 产品 / 工单" @keyup.enter="loadRows" />
      <select v-model="filters.typeId"><option value="">全部类型</option><option v-for="type in types" :key="type.id" :value="type.id">{{ type.typeName }}</option></select>
      <select v-model="filters.status"><option value="">全部状态</option><option>已生成</option><option>已打印</option><option>已扫码</option><option>已关闭</option><option>作废</option></select>
      <select v-model="filters.workOrderId"><option value="">全部工单</option><option v-for="order in workOrders" :key="order.id" :value="order.id">{{ order.workOrderNo }}</option></select>
      <button class="primary-btn compact" type="button" :disabled="loading" @click="loadRows">查询</button>
      <button class="secondary-btn compact" type="button" @click="resetFilters">重置</button>
      <span class="summary">共 {{ rows.length }} 个条码 · 已选 {{ selectedIds.length }} 个</span>
    </div>

    <main class="content-grid">
      <section class="data-panel">
        <div class="panel-title"><strong>条码实例</strong><span>{{ loading ? '加载中...' : '生成、打印、补打全记录' }}</span></div>
        <div class="table-scroll">
          <table class="data-table">
            <thead>
              <tr>
                <th class="check-cell"><input type="checkbox" :checked="allSelected" @change="toggleAll" /></th>
                <th>条码值</th><th>条码类型</th><th>产品 / 物料</th><th>批次</th><th>工单</th><th>打印次数</th><th>状态</th><th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in rows" :key="row.id" :class="{ selected: selected?.id === row.id }" @click="selectRow(row)">
                <td class="check-cell"><input type="checkbox" :checked="selectedIds.includes(row.id)" @click.stop @change="toggleOne(row.id)" /></td>
                <td class="mono barcode-value">{{ row.barcodeValue }}</td>
                <td>{{ row.typeName || '-' }}</td>
                <td>{{ displayItem(row) }}</td>
                <td class="mono">{{ row.batchNo || '-' }}</td>
                <td class="mono">{{ row.workOrderNo || '-' }}</td>
                <td>{{ row.printCount || 0 }}</td>
                <td><span :class="['status-chip', statusTone(row.status)]">{{ row.status }}</span></td>
                <td class="row-actions">
                  <button type="button" :disabled="!isOperable(row)" @click.stop="openPrintDialog([row.id])">{{ row.printCount ? '补打' : '打印' }}</button>
                  <button type="button" :disabled="!isOperable(row)" @click.stop="openBindDialog(row)">绑定包装</button>
                  <button type="button" :disabled="!isOperable(row)" @click.stop="changeStatus(row, 'close')">关闭</button>
                  <button class="danger-link" type="button" :disabled="row.status === '作废'" @click.stop="changeStatus(row, 'void')">作废</button>
                </td>
              </tr>
              <tr v-if="!loading && rows.length === 0"><td colspan="9" class="empty">暂无条码数据</td></tr>
            </tbody>
          </table>
        </div>
      </section>

      <aside class="detail-panel">
        <div class="panel-title"><strong>标签与审计</strong><span>{{ selected?.barcodeValue || '未选择' }}</span></div>
        <template v-if="selected">
          <div class="label-preview">
            <svg ref="detailBarcodeSvg"></svg>
            <strong class="mono">{{ selected.barcodeValue }}</strong>
            <span>{{ displayItem(selected) }} · {{ selected.batchNo || '无批次' }}</span>
          </div>
          <dl>
            <dt>条码类型</dt><dd>{{ selected.typeName }}</dd>
            <dt>应用规则</dt><dd class="mono">{{ selected.applicationRuleCode || '-' }}</dd>
            <dt>生产工单</dt><dd class="mono">{{ selected.workOrderNo || '-' }}</dd>
            <dt>生产任务</dt><dd class="mono">{{ selected.taskNo || '-' }}</dd>
            <dt>上级包装</dt><dd class="mono">{{ selected.parentBarcodeValue || '未绑定' }}</dd>
            <dt>生成时间</dt><dd>{{ formatTime(selected.generatedAt) }}</dd>
          </dl>
          <div class="audit-tabs">
            <button :class="{ active: detailTab === 'print' }" type="button" @click="detailTab = 'print'">打印记录 {{ printRecords.length }}</button>
            <button :class="{ active: detailTab === 'event' }" type="button" @click="detailTab = 'event'">追溯事件 {{ events.length }}</button>
          </div>
          <div class="audit-list">
            <template v-if="detailTab === 'print'">
              <article v-for="record in printRecords" :key="record.id">
                <b>{{ record.printCount }} 份 · {{ record.printerName }}</b>
                <span>{{ formatTime(record.printAt) }} · {{ record.printByName || `用户 ${record.printBy || '-'}` }}</span>
              </article>
              <p v-if="printRecords.length === 0">暂无打印记录</p>
            </template>
            <template v-else>
              <article v-for="event in events" :key="event.id">
                <b>{{ event.eventType }} · {{ event.result }}</b>
                <span>{{ formatTime(event.eventAt) }} · {{ event.stationName || event.bizType || '-' }}</span>
              </article>
              <p v-if="events.length === 0">暂无追溯事件</p>
            </template>
          </div>
        </template>
        <div v-else class="empty detail-empty">选择条码查看标签和审计记录</div>
      </aside>
    </main>

    <div v-if="generateDialog.open" class="dialog-backdrop" @mousedown.self="closeDialogs">
      <form class="dialog" @submit.prevent="submitGenerate">
        <header><div><h2>批量生成条码</h2><p>选择已经启用的应用绑定，系统会原子分配流水号。</p></div><button type="button" @click="closeDialogs">×</button></header>
        <div class="form-grid">
          <label class="full">应用规则<select v-model="generateDialog.form.applicationRuleId" required><option disabled value="">请选择</option><option v-for="rule in enabledApplications" :key="rule.id" :value="rule.id">{{ rule.applicationRuleCode }} · {{ rule.itemCode }} · {{ rule.typeName }} · {{ rule.ruleExpression }}</option></select></label>
          <label>生成数量<input v-model.number="generateDialog.form.quantity" type="number" min="1" max="500" required /></label>
          <label>生产批次<input v-model.trim="generateDialog.form.batchNo" maxlength="64" placeholder="BATCH-20260710-A" /></label>
          <label>生产工单<select v-model="generateDialog.form.workOrderId"><option value="">不关联</option><option v-for="order in workOrders" :key="order.id" :value="order.id">{{ order.workOrderNo }}</option></select></label>
          <label>生产任务<select v-model="generateDialog.form.taskId"><option value="">不关联</option><option v-for="task in matchingTasks" :key="task.id" :value="task.id">{{ task.taskNo }}</option></select></label>
          <label class="full">上级包装码（可选）<input v-model.trim="generateDialog.form.parentBarcodeValue" list="barcode-options" placeholder="输入现有箱码或栈板码" /></label>
          <label class="full">外部条码值（可选，每行一个；填写后忽略生成数量）<textarea v-model="generateDialog.form.externalText" rows="4" placeholder="用于供应商码或外部系统导入"></textarea></label>
          <datalist id="barcode-options"><option v-for="row in rows" :key="row.id" :value="row.barcodeValue" /></datalist>
        </div>
        <div v-if="selectedApplicationPreview" class="rule-preview">
          <span>编码规则</span><strong class="mono">{{ selectedApplicationPreview.ruleExpression }}</strong><span>标签模板：{{ selectedApplicationPreview.templateName }}</span>
        </div>
        <p v-if="dialogError" class="dialog-error">{{ dialogError }}</p>
        <footer><button class="secondary-btn" type="button" @click="closeDialogs">取消</button><button class="primary-btn" type="submit" :disabled="saving">{{ saving ? '生成中...' : '确认生成' }}</button></footer>
      </form>
    </div>

    <div v-if="printDialog.open" class="dialog-backdrop print-backdrop" @mousedown.self="closeDialogs">
      <form class="dialog print-dialog" @submit.prevent="submitPrint">
        <header><div><h2>{{ hasReprint ? '条码补打' : '标签打印' }}</h2><p>已选择 {{ printDialog.ids.length }} 个标签，打印记录会进入追溯链。</p></div><button type="button" @click="closeDialogs">×</button></header>
        <div class="print-settings">
          <label>打印份数<input v-model.number="printDialog.form.copies" type="number" min="1" max="100" required /></label>
          <label>打印机<input v-model.trim="printDialog.form.printerName" maxlength="128" placeholder="BROWSER / LABEL-01" /></label>
          <label v-if="hasReprint" class="full">补打原因<input v-model.trim="printDialog.form.reason" maxlength="500" required placeholder="必填：标签破损、卡纸等" /></label>
        </div>
        <div id="barcode-print-area" class="print-labels">
          <article v-for="row in printRows" :key="row.id" class="print-label">
            <div><b>{{ displayItem(row) }}</b><span>{{ row.typeName }} · {{ row.batchNo || '无批次' }}</span></div>
            <svg class="print-barcode" :data-value="row.barcodeValue"></svg>
            <strong class="mono">{{ row.barcodeValue }}</strong>
            <small>{{ row.workOrderNo || '-' }}</small>
          </article>
        </div>
        <p v-if="dialogError" class="dialog-error">{{ dialogError }}</p>
        <footer><button class="secondary-btn" type="button" @click="closeDialogs">取消</button><button class="primary-btn" type="submit" :disabled="saving">{{ saving ? '登记中...' : '登记并打印' }}</button></footer>
      </form>
    </div>

    <div v-if="bindDialog.open" class="dialog-backdrop" @mousedown.self="closeDialogs">
      <form class="dialog small" @submit.prevent="submitBind">
        <header><div><h2>绑定上级包装码</h2><p class="mono">{{ bindDialog.row?.barcodeValue }}</p></div><button type="button" @click="closeDialogs">×</button></header>
        <div class="form-grid single">
          <label>上级箱码 / 栈板码<input v-model.trim="bindDialog.form.parentBarcodeValue" list="barcode-options" required placeholder="扫描或输入上级码" /></label>
          <label>备注<input v-model.trim="bindDialog.form.remark" maxlength="500" placeholder="装箱、换箱等" /></label>
        </div>
        <p v-if="dialogError" class="dialog-error">{{ dialogError }}</p>
        <footer><button class="secondary-btn" type="button" @click="closeDialogs">取消</button><button class="primary-btn" type="submit" :disabled="saving">确认绑定</button></footer>
      </form>
    </div>
  </section>
</template>

<script setup>
import JsBarcode from 'jsbarcode'
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  batchPrintBarcodes,
  bindBarcodeParent,
  closeBarcode,
  generateBarcode,
  getBarcodeApplicationRules,
  getBarcodeEvents,
  getBarcodePrintRecords,
  getBarcodeRecords,
  getBarcodeTypes,
  voidBarcode
} from '../../api/barcode'
import { getShopTasks, getWorkOrders } from '../../api/production'
import { authState } from '../../stores/auth'

const { t } = useI18n()
const rows = ref([])
const types = ref([])
const applications = ref([])
const workOrders = ref([])
const tasks = ref([])
const selected = ref(null)
const selectedIds = ref([])
const printRecords = ref([])
const events = ref([])
const detailTab = ref('print')
const detailBarcodeSvg = ref(null)
const loading = ref(false)
const saving = ref(false)
const error = ref('')
const notice = ref('')
const dialogError = ref('')
const filters = reactive({ keyword: '', typeId: '', status: '', workOrderId: '' })
const generateDialog = reactive({ open: false, form: { applicationRuleId: '', quantity: 1, batchNo: '', workOrderId: '', taskId: '', parentBarcodeValue: '', externalText: '' } })
const printDialog = reactive({ open: false, ids: [], form: { copies: 1, printerName: 'BROWSER', reason: '' } })
const bindDialog = reactive({ open: false, row: null, form: { parentBarcodeValue: '', remark: '' } })

const enabledApplications = computed(() => applications.value.filter((row) => row.status === '启用'))
const selectedApplicationPreview = computed(() => applications.value.find((row) => String(row.id) === String(generateDialog.form.applicationRuleId)))
const matchingTasks = computed(() => tasks.value.filter((task) => !generateDialog.form.workOrderId || String(task.workOrderId) === String(generateDialog.form.workOrderId)))
const allSelected = computed(() => rows.value.length > 0 && rows.value.every((row) => selectedIds.value.includes(row.id)))
const printRows = computed(() => rows.value.filter((row) => printDialog.ids.includes(row.id)))
const hasReprint = computed(() => printRows.value.some((row) => Number(row.printCount || 0) > 0))

watch(selected, async (row) => {
  if (!row) return
  await nextTick()
  renderBarcode(detailBarcodeSvg.value, row.barcodeValue, { height: 58, width: 1.45, fontSize: 0, displayValue: false })
  try {
    const [prints, traceEvents] = await Promise.all([getBarcodePrintRecords(row.id), getBarcodeEvents(row.id)])
    if (selected.value?.id === row.id) {
      printRecords.value = prints || []
      events.value = traceEvents || []
    }
  } catch {
    printRecords.value = []
    events.value = []
  }
})

watch(() => generateDialog.form.workOrderId, () => {
  if (!matchingTasks.value.some((task) => String(task.id) === String(generateDialog.form.taskId))) generateDialog.form.taskId = ''
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    const params = Object.fromEntries(Object.entries(filters).filter(([, value]) => value !== ''))
    const [barcodeRows, typeRows, appRows, orderRows, taskRows] = await Promise.all([
      getBarcodeRecords(params), getBarcodeTypes(), getBarcodeApplicationRules(), getWorkOrders(), getShopTasks()
    ])
    rows.value = barcodeRows || []
    types.value = typeRows || []
    applications.value = appRows || []
    workOrders.value = orderRows || []
    tasks.value = taskRows || []
    selectedIds.value = selectedIds.value.filter((id) => rows.value.some((row) => row.id === id))
    selected.value = rows.value.find((row) => row.id === selected.value?.id) || rows.value[0] || null
  } catch (e) {
    error.value = apiMessage(e, '条码列表加载失败')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  Object.assign(filters, { keyword: '', typeId: '', status: '', workOrderId: '' })
  loadRows()
}

const selectRow = (row) => { selected.value = row }
const toggleOne = (id) => { selectedIds.value = selectedIds.value.includes(id) ? selectedIds.value.filter((value) => value !== id) : [...selectedIds.value, id] }
const toggleAll = () => { selectedIds.value = allSelected.value ? [] : rows.value.map((row) => row.id) }

const openGenerateDialog = () => {
  Object.assign(generateDialog.form, { applicationRuleId: enabledApplications.value[0]?.id || '', quantity: 1, batchNo: '', workOrderId: '', taskId: '', parentBarcodeValue: '', externalText: '' })
  dialogError.value = ''
  generateDialog.open = true
}

const submitGenerate = async () => {
  saving.value = true
  dialogError.value = ''
  try {
    const parent = rows.value.find((row) => row.barcodeValue === generateDialog.form.parentBarcodeValue)
    if (generateDialog.form.parentBarcodeValue && !parent) throw new Error('上级包装码不存在，请先生成或刷新条码列表')
    const externalValues = generateDialog.form.externalText.split(/\r?\n/).map((value) => value.trim()).filter(Boolean)
    const payload = {
      applicationRuleId: generateDialog.form.applicationRuleId,
      quantity: generateDialog.form.quantity,
      batchNo: generateDialog.form.batchNo || null,
      workOrderId: generateDialog.form.workOrderId || null,
      taskId: generateDialog.form.taskId || null,
      parentBarcodeId: parent?.id || null,
      externalValues,
      operatorId: currentOperatorId()
    }
    const result = await generateBarcode(payload)
    closeDialogs()
    showNotice(`已生成 ${result.quantity} 个条码`)
    await loadRows()
    selectedIds.value = (result.barcodes || []).map((row) => row.id)
    if (selectedIds.value.length) selected.value = rows.value.find((row) => row.id === selectedIds.value[0]) || selected.value
  } catch (e) {
    dialogError.value = apiMessage(e, '条码生成失败')
  } finally {
    saving.value = false
  }
}

const openPrintDialog = async (ids = selectedIds.value) => {
  printDialog.ids = [...ids]
  Object.assign(printDialog.form, { copies: 1, printerName: 'BROWSER', reason: '' })
  dialogError.value = ''
  printDialog.open = true
  await nextTick()
  document.querySelectorAll('.print-barcode').forEach((svg) => renderBarcode(svg, svg.dataset.value, { height: 62, width: 1.6, displayValue: false, margin: 0 }))
}

const submitPrint = async () => {
  saving.value = true
  dialogError.value = ''
  try {
    await batchPrintBarcodes({
      barcodeIds: printDialog.ids,
      copies: printDialog.form.copies,
      printerName: printDialog.form.printerName || 'BROWSER',
      operatorId: currentOperatorId(),
      reason: printDialog.form.reason || null
    })
    await nextTick()
    window.print()
    closeDialogs()
    showNotice(`已登记并发送 ${printDialog.ids.length} 个标签到打印窗口`)
    await loadRows()
  } catch (e) {
    dialogError.value = apiMessage(e, '打印登记失败')
  } finally {
    saving.value = false
  }
}

const openBindDialog = (row) => {
  bindDialog.row = row
  Object.assign(bindDialog.form, { parentBarcodeValue: row.parentBarcodeValue || '', remark: '' })
  dialogError.value = ''
  bindDialog.open = true
}

const submitBind = async () => {
  saving.value = true
  dialogError.value = ''
  try {
    await bindBarcodeParent(bindDialog.row.id, { ...bindDialog.form, operatorId: currentOperatorId() })
    closeDialogs()
    showNotice('包装层级已绑定')
    await loadRows()
  } catch (e) {
    dialogError.value = apiMessage(e, '绑定失败')
  } finally {
    saving.value = false
  }
}

const changeStatus = async (row, action) => {
  const label = action === 'void' ? '作废' : '关闭'
  if (!window.confirm(`确认${label}条码 ${row.barcodeValue}？`)) return
  try {
    const payload = { operatorId: currentOperatorId(), remark: `页面${label}` }
    action === 'void' ? await voidBarcode(row.id, payload) : await closeBarcode(row.id, payload)
    showNotice(`条码已${label}`)
    await loadRows()
  } catch (e) {
    error.value = apiMessage(e, `${label}失败`)
  }
}

const closeDialogs = () => {
  generateDialog.open = false
  printDialog.open = false
  bindDialog.open = false
  dialogError.value = ''
}

const renderBarcode = (element, value, options = {}) => {
  if (!element || !value) return
  try { JsBarcode(element, value, { format: 'CODE128', lineColor: '#132a33', background: '#ffffff', margin: 4, ...options }) } catch { element.innerHTML = '' }
}
const displayItem = (row) => row?.itemCode && row?.itemName ? `${row.itemCode} / ${row.itemName}` : row?.itemName || row?.itemCode || '-'
const isOperable = (row) => !['已关闭', '作废'].includes(row.status)
const statusTone = (status) => ({ 已生成: 'draft', 已打印: 'ok', 已扫码: 'running', 已关闭: 'muted', 作废: 'danger' }[status] || 'muted')
const formatTime = (value) => value ? new Date(value).toLocaleString('zh-CN', { hour12: false }) : '-'
const currentOperatorId = () => authState.user?.userId || authState.user?.id || null
const apiMessage = (e, fallback) => e?.payload?.message || e?.response?.data?.message || e?.message || fallback
const showNotice = (message) => { notice.value = message; window.setTimeout(() => { if (notice.value === message) notice.value = '' }, 3000) }

onMounted(loadRows)
</script>

<style scoped>
.barcode-page { height: calc(100vh - var(--topbar-height)); min-height: calc(900px - var(--topbar-height)); padding: 22px 24px; overflow: auto; background: #eef2f4; color: #25323b; }
.page-heading { display: flex; justify-content: space-between; gap: 20px; align-items: flex-start; margin-bottom: 15px; }
.page-heading p { margin: 0 0 5px; color: #00799f; font-size: 12px; font-weight: 700; }
.page-heading h1 { margin: 0; font-size: 28px; }
.page-heading span { display: block; margin-top: 6px; color: #66777f; }
.heading-actions, .row-actions { display: flex; gap: 7px; flex-wrap: wrap; }
.primary-btn, .secondary-btn { min-height: 34px; padding: 0 13px; border: 1px solid #00799f; border-radius: 3px; cursor: pointer; }
.primary-btn { color: white; background: #00799f; }
.secondary-btn { color: #006f8e; background: white; border-color: #b7c8d0; }
.compact { min-height: 32px; }
button:disabled { opacity: .5; cursor: not-allowed !important; }
.message { margin-bottom: 12px; padding: 9px 12px; border: 1px solid; }
.message.success { color: #246a4d; background: #e7f3ed; border-color: #bfdcca; }
.message.error, .dialog-error { color: #a52f35; background: #fae9ea; border-color: #efc4c7; }
.filter-bar { display: flex; align-items: center; gap: 9px; padding: 10px; margin-bottom: 14px; background: white; border: 1px solid #d7e0e4; }
.filter-bar input, .filter-bar select { height: 32px; padding: 0 9px; border: 1px solid #bdcbd2; }
.filter-bar input { width: 270px; }
.filter-bar select { max-width: 190px; }
.summary { margin-left: auto; color: #6c7d85; font-size: 12px; }
.content-grid { display: grid; grid-template-columns: minmax(980px, 1fr) 350px; gap: 14px; align-items: start; }
.data-panel, .detail-panel { background: white; border: 1px solid #d7e0e4; }
.detail-panel { position: sticky; top: 0; }
.panel-title { display: flex; min-height: 44px; padding: 0 13px; align-items: center; justify-content: space-between; border-bottom: 1px solid #e1e8eb; }
.panel-title span { max-width: 210px; overflow: hidden; color: #72838b; font-size: 12px; text-overflow: ellipsis; white-space: nowrap; }
.table-scroll { overflow: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 12px; }
.data-table th, .data-table td { padding: 9px 9px; text-align: left; border-bottom: 1px solid #e6ecef; white-space: nowrap; }
.data-table th { color: #53666f; background: #f3f7f8; }
.data-table tbody tr { cursor: pointer; }
.data-table tbody tr:hover td, .data-table tbody tr.selected td { background: #edf8fb; }
.check-cell { width: 32px; text-align: center !important; }
.barcode-value { max-width: 220px; overflow: hidden; text-overflow: ellipsis; }
.mono { font-family: Consolas, 'Courier New', monospace; }
.row-actions button { padding: 3px 4px; color: #00799f; background: transparent; border: 0; cursor: pointer; }
.row-actions .danger-link { color: #b23a3a; }
.status-chip { display: inline-block; min-width: 50px; padding: 3px 7px; text-align: center; border-radius: 10px; }
.status-chip.ok { color: #287252; background: #e4f3eb; }
.status-chip.running { color: #006f8e; background: #e2f3f7; }
.status-chip.danger { color: #a52f35; background: #fae9ea; }
.status-chip.draft { color: #896414; background: #faf1d9; }
.status-chip.muted { color: #687981; background: #edf1f3; }
.empty { padding: 32px !important; color: #7c8b92; text-align: center !important; }
.detail-empty { min-height: 300px; display: grid; place-items: center; }
.label-preview { margin: 14px; padding: 12px; text-align: center; border: 1px solid #cbd9de; }
.label-preview svg { display: block; width: 100%; max-height: 70px; }
.label-preview strong, .label-preview span { display: block; }
.label-preview strong { margin-top: 6px; font-size: 14px; }
.label-preview span { margin-top: 4px; color: #687a82; font-size: 11px; }
.detail-panel dl { display: grid; grid-template-columns: 82px minmax(0, 1fr); gap: 8px; margin: 0; padding: 0 14px 14px; }
.detail-panel dt { color: #6d7e86; }
.detail-panel dd { margin: 0; overflow-wrap: anywhere; }
.audit-tabs { display: flex; border-top: 1px solid #e1e8eb; border-bottom: 1px solid #e1e8eb; }
.audit-tabs button { flex: 1; min-height: 35px; color: #60727a; background: #f5f8f9; border: 0; cursor: pointer; }
.audit-tabs button.active { color: #006f8e; background: white; box-shadow: inset 0 -2px #00799f; }
.audit-list { max-height: 220px; padding: 10px 14px; overflow: auto; }
.audit-list article { padding: 8px 0; border-bottom: 1px solid #edf1f3; }
.audit-list b, .audit-list span { display: block; }
.audit-list span, .audit-list p { margin-top: 3px; color: #708189; font-size: 11px; }
.dialog-backdrop { position: fixed; z-index: 80; inset: 0; display: grid; place-items: center; padding: 30px; background: rgba(0, 31, 40, .45); }
.dialog { width: min(740px, 92vw); max-height: 88vh; overflow: auto; background: white; border: 1px solid #b7c7ce; box-shadow: 0 20px 60px rgba(0, 30, 40, .3); }
.dialog.small { width: min(500px, 92vw); }
.dialog header { display: flex; align-items: flex-start; justify-content: space-between; padding: 16px 18px; border-bottom: 1px solid #dfe7ea; }
.dialog h2, .dialog p { margin: 0; }
.dialog header p { margin-top: 4px; color: #71818b; font-size: 12px; }
.dialog header > button { color: #5c6d75; font-size: 24px; background: transparent; border: 0; cursor: pointer; }
.form-grid, .print-settings { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 13px 15px; padding: 18px; }
.form-grid.single { grid-template-columns: 1fr; }
.form-grid label, .print-settings label { display: grid; gap: 6px; color: #52656e; font-size: 12px; }
.form-grid input, .form-grid select, .form-grid textarea, .print-settings input { width: 100%; min-height: 36px; padding: 7px 9px; border: 1px solid #bdcbd2; font: inherit; }
.form-grid .full, .print-settings .full { grid-column: 1 / -1; }
.rule-preview { display: flex; gap: 10px; align-items: center; margin: 0 18px 14px; padding: 10px; background: #eef8fa; border: 1px solid #cce5eb; }
.rule-preview span { color: #61747c; font-size: 11px; }
.rule-preview strong { flex: 1; }
.dialog-error { margin: 0 18px 12px !important; padding: 8px 10px; border: 1px solid; }
.dialog footer { display: flex; justify-content: flex-end; gap: 9px; padding: 13px 18px; background: #f5f8f9; border-top: 1px solid #dfe7ea; }
.print-dialog { width: min(900px, 94vw); }
.print-labels { display: grid; grid-template-columns: repeat(2, minmax(300px, 1fr)); gap: 10px; max-height: 380px; padding: 0 18px 18px; overflow: auto; }
.print-label { display: grid; grid-template-columns: 1fr 1.6fr; gap: 4px 12px; align-items: center; padding: 12px; border: 1px dashed #7b8d95; }
.print-label div span, .print-label strong, .print-label small { display: block; }
.print-label div span, .print-label small { color: #677981; font-size: 10px; }
.print-label svg { grid-row: span 2; width: 100%; max-height: 70px; }
@media print {
  :global(body *) { visibility: hidden !important; }
  #barcode-print-area, #barcode-print-area * { visibility: visible !important; }
  #barcode-print-area { position: fixed; inset: 0; display: block; padding: 0; overflow: visible; background: white; }
  .print-label { width: 60mm; height: 40mm; margin: 0; break-after: page; border: 0; }
}
</style>
