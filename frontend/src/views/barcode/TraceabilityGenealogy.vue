<template>
  <section class="trace-page">
    <header class="page-heading">
      <div>
        <p>{{ t('barcode.traceability.eyebrow') }}</p>
        <h1>{{ t('barcode.traceability.pageTitle') }}</h1>
        <span>正向查看产品经历，反向定位关键物料、包装层级、工位、设备和操作人。</span>
      </div>
      <div class="heading-actions">
        <button class="secondary-btn" type="button" :disabled="!trace" @click="exportReport">导出追溯报告</button>
        <button class="secondary-btn" type="button" @click="openScanDialog">扫码登记</button>
        <button class="primary-btn" type="button" :disabled="loading" @click="loadTrace">刷新查询</button>
      </div>
    </header>

    <div class="search-band">
      <select v-model="queryMode">
        <option value="SN">按产品 SN / 条码</option>
        <option value="WORK_ORDER">按生产工单</option>
        <option value="BATCH">按生产批次</option>
      </select>
      <div class="scan-input">
        <span>▥</span>
        <input v-model.trim="keyword" autofocus :placeholder="searchPlaceholder" @keyup.enter="loadTrace" />
      </div>
      <button class="primary-btn" type="button" :disabled="loading || !keyword" @click="loadTrace">{{ loading ? '查询中...' : '查询' }}</button>
      <button class="secondary-btn" type="button" @click="clearTrace">清空</button>
      <span v-if="trace" class="result-summary">{{ trace.nodes.length }} 个谱系对象 · {{ trace.events.length }} 条事件</span>
    </div>

    <div v-if="error" class="message error">{{ error }}</div>
    <div v-if="notice" class="message success">{{ notice }}</div>

    <main class="trace-layout">
      <section class="genealogy-panel panel">
        <header><div><h2>条码谱系</h2><p>包装父子关系与生产物料关系</p></div><span v-if="trace" class="root-chip mono">{{ trace.root }}</span></header>
        <div v-if="flatNodes.length" class="genealogy-list">
          <button
            v-for="node in flatNodes"
            :key="node.id"
            :class="['genealogy-node', { selected: selectedNode?.id === node.id }]"
            :style="{ '--depth': node.depth }"
            type="button"
            @click="selectedNode = node"
          >
            <span class="node-line"></span>
            <i :class="nodeIconClass(node)">{{ nodeIcon(node) }}</i>
            <span class="node-main"><b class="mono">{{ node.barcode }}</b><small>{{ node.type || '条码' }} · {{ node.relation }}</small></span>
            <span :class="['status-chip', statusTone(node.status)]">{{ node.status }}</span>
          </button>
        </div>
        <div v-else class="empty-state">
          <strong>▥</strong><b>等待扫码或输入查询条件</b><span>可按条码、工单或批次查询完整谱系。</span>
        </div>
      </section>

      <aside class="object-panel panel">
        <header><div><h2>对象详情</h2><p>{{ selectedNode ? selectedNode.relation : '查询摘要' }}</p></div></header>
        <div v-if="trace" class="object-body">
          <div v-if="selectedNode" class="selected-object">
            <span>{{ selectedNode.type || '条码对象' }}</span>
            <strong class="mono">{{ selectedNode.barcode }}</strong>
            <em :class="statusTone(selectedNode.status)">{{ selectedNode.status }}</em>
          </div>
          <dl>
            <template v-for="row in detailRows" :key="row[0]">
              <dt>{{ detailLabel(row[0]) }}</dt><dd :class="{ mono: ['barcode', 'batchNo', 'workOrder', 'task'].includes(row[0]) }">{{ row[1] || '-' }}</dd>
            </template>
          </dl>
          <div class="quick-actions">
            <button type="button" @click="copyBarcode">复制条码</button>
            <button type="button" @click="openScanDialog(selectedNode?.barcode)">登记新事件</button>
          </div>
        </div>
        <div v-else class="empty-state compact"><span>暂无对象详情</span></div>
      </aside>

      <section class="timeline-panel panel">
        <header>
          <div><h2>全生命周期事件</h2><p>生成、打印、扫码、报工、质检、装箱与维修记录</p></div>
          <select v-model="eventFilter"><option value="">全部事件</option><option v-for="type in eventTypes" :key="type">{{ type }}</option></select>
        </header>
        <div class="timeline-scroll">
          <ol v-if="filteredEvents.length" class="timeline">
            <li v-for="event in filteredEvents" :key="event.id" :class="eventTone(event.eventType)">
              <time>{{ formatTime(event.eventAt) }}</time>
              <i></i>
              <article>
                <div><strong>{{ event.eventType }}</strong><span class="mono">{{ event.barcode }}</span><em>{{ event.result || '成功' }}</em></div>
                <p>
                  <span v-if="event.workOrderNo">工单 {{ event.workOrderNo }}</span>
                  <span v-if="event.taskNo">任务 {{ event.taskNo }}</span>
                  <span v-if="event.processName">工序 {{ event.processName }}</span>
                  <span v-if="event.stationName">工位 {{ event.stationName }}</span>
                  <span v-if="event.equipmentName">设备 {{ event.equipmentName }}</span>
                  <span v-if="event.operatorName">操作人 {{ event.operatorName }}</span>
                  <span v-if="!event.workOrderNo && !event.taskNo && !event.stationName && event.bizType">业务 {{ event.bizType }} #{{ event.bizId || '-' }}</span>
                </p>
              </article>
            </li>
          </ol>
          <div v-else class="empty-state compact"><span>{{ trace ? '当前筛选下没有事件' : '暂无追溯事件' }}</span></div>
        </div>
      </section>
    </main>

    <div v-if="scanDialog.open" class="dialog-backdrop" @mousedown.self="closeDialog">
      <form class="dialog" @submit.prevent="submitScan">
        <header><div><h2>扫码登记追溯事件</h2><p>条码必须已经存在，登记后会更新为“已扫码”。</p></div><button type="button" @click="closeDialog">×</button></header>
        <div class="form-grid">
          <label class="full">条码值<input v-model.trim="scanDialog.form.barcodeValue" required maxlength="128" placeholder="扫描或输入条码" /></label>
          <label>事件类型<select v-model="scanDialog.form.eventType"><option>扫码</option><option>装配扫码</option><option>关键物料</option><option>报工</option><option>质检</option><option>装箱</option><option>维修</option></select></label>
          <label>业务类型<input v-model.trim="scanDialog.form.bizType" maxlength="64" placeholder="SHOP_OPERATION" /></label>
          <label>生产工单 ID<input v-model.number="scanDialog.form.workOrderId" type="number" min="1" placeholder="可选" /></label>
          <label>生产任务 ID<input v-model.number="scanDialog.form.taskId" type="number" min="1" placeholder="可选" /></label>
          <label>工序 ID<input v-model.number="scanDialog.form.processId" type="number" min="1" placeholder="可选" /></label>
          <label>工位 ID<input v-model.number="scanDialog.form.stationId" type="number" min="1" placeholder="可选" /></label>
          <label>设备 ID<input v-model.number="scanDialog.form.deviceId" type="number" min="1" placeholder="可选" /></label>
          <label>上级包装码<input v-model.trim="scanDialog.form.parentBarcodeValue" maxlength="128" placeholder="装箱时填写" /></label>
        </div>
        <p v-if="dialogError" class="dialog-error">{{ dialogError }}</p>
        <footer><button class="secondary-btn" type="button" @click="closeDialog">取消</button><button class="primary-btn" type="submit" :disabled="saving">{{ saving ? '登记中...' : '确认登记' }}</button></footer>
      </form>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { scanBarcode, traceBarcode } from '../../api/barcode'
import { authState } from '../../stores/auth'

const { t } = useI18n()
const keyword = ref('SN202607080001')
const queryMode = ref('SN')
const eventFilter = ref('')
const trace = ref(null)
const selectedNode = ref(null)
const loading = ref(false)
const saving = ref(false)
const error = ref('')
const notice = ref('')
const dialogError = ref('')
const scanDialog = reactive({ open: false, form: blankScanForm() })

const searchPlaceholder = computed(() => ({ SN: '扫描或输入产品 SN / 条码', WORK_ORDER: '输入生产工单号', BATCH: '输入生产批次号' }[queryMode.value]))
const detailRows = computed(() => Object.entries(trace.value?.details || {}))
const eventTypes = computed(() => [...new Set((trace.value?.events || []).map((event) => event.eventType).filter(Boolean))])
const filteredEvents = computed(() => (trace.value?.events || []).filter((event) => !eventFilter.value || event.eventType === eventFilter.value))
const flatNodes = computed(() => flattenNodes(trace.value?.nodes || []))

function blankScanForm(value = '') {
  return { barcodeValue: value, eventType: '扫码', bizType: 'SCAN', workOrderId: null, taskId: null, processId: null, stationId: null, deviceId: null, parentBarcodeValue: '' }
}

const loadTrace = async () => {
  if (!keyword.value) return
  loading.value = true
  error.value = ''
  eventFilter.value = ''
  try {
    trace.value = await traceBarcode(keyword.value, queryMode.value)
    selectedNode.value = trace.value?.nodes?.[0] || null
  } catch (e) {
    trace.value = null
    selectedNode.value = null
    error.value = apiMessage(e, '未查询到追溯数据')
  } finally {
    loading.value = false
  }
}

const clearTrace = () => {
  keyword.value = ''
  trace.value = null
  selectedNode.value = null
  error.value = ''
}

const openScanDialog = (value = '') => {
  Object.assign(scanDialog.form, blankScanForm(value || selectedNode.value?.barcode || (queryMode.value === 'SN' ? keyword.value : '')))
  dialogError.value = ''
  scanDialog.open = true
}

const closeDialog = () => {
  scanDialog.open = false
  dialogError.value = ''
}

const submitScan = async () => {
  saving.value = true
  dialogError.value = ''
  try {
    const payload = Object.fromEntries(Object.entries({ ...scanDialog.form, operatorId: currentOperatorId() }).map(([key, value]) => [key, value === '' ? null : value]))
    await scanBarcode(payload)
    keyword.value = scanDialog.form.barcodeValue
    queryMode.value = 'SN'
    closeDialog()
    showNotice('扫码事件已登记')
    await loadTrace()
  } catch (e) {
    dialogError.value = apiMessage(e, '扫码登记失败')
  } finally {
    saving.value = false
  }
}

const flattenNodes = (nodes) => {
  const byParent = new Map()
  const ids = new Set(nodes.map((node) => String(node.id)))
  nodes.forEach((node) => {
    const parent = node.parentId && ids.has(String(node.parentId)) ? String(node.parentId) : '__root__'
    byParent.set(parent, [...(byParent.get(parent) || []), node])
  })
  const output = []
  const visited = new Set()
  const walk = (parent, depth) => {
    ;(byParent.get(parent) || []).forEach((node) => {
      if (visited.has(String(node.id))) return
      visited.add(String(node.id))
      output.push({ ...node, depth })
      walk(String(node.id), depth + 1)
    })
  }
  walk('__root__', 0)
  nodes.forEach((node) => { if (!visited.has(String(node.id))) output.push({ ...node, depth: 0 }) })
  return output
}

const exportReport = () => {
  if (!trace.value) return
  const rows = [
    ['查询值', trace.value.root], ['查询方式', trace.value.queryMode], [],
    ['谱系对象', '关系', '类型', '状态'],
    ...trace.value.nodes.map((node) => [node.barcode, node.relation, node.type, node.status]),
    [], ['时间', '条码', '事件', '工单', '任务', '工序', '工位', '设备', '操作人', '结果'],
    ...trace.value.events.map((event) => [event.eventAt, event.barcode, event.eventType, event.workOrderNo, event.taskNo, event.processName, event.stationName, event.equipmentName, event.operatorName, event.result])
  ]
  const csv = '\ufeff' + rows.map((row) => row.map(csvCell).join(',')).join('\r\n')
  const link = document.createElement('a')
  link.href = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }))
  link.download = `trace-${trace.value.root}-${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  URL.revokeObjectURL(link.href)
}

const copyBarcode = async () => {
  const value = selectedNode.value?.barcode || trace.value?.root
  if (!value) return
  await navigator.clipboard.writeText(value)
  showNotice('条码已复制')
}

const csvCell = (value) => `"${String(value ?? '').replaceAll('"', '""')}"`
const detailLabel = (key) => ({ barcode: '条码值', type: '条码类型', item: '产品 / 物料', batchNo: '生产批次', workOrder: '生产工单', task: '生产任务', status: '当前状态', printCount: '打印次数', matchedBarcodes: '匹配条码', genealogyNodes: '谱系对象', events: '追溯事件' }[key] || key)
const nodeIcon = (node) => String(node.type || '').includes('箱') ? '□' : String(node.type || '').includes('批次') ? 'M' : 'P'
const nodeIconClass = (node) => String(node.type || '').includes('箱') ? 'package' : String(node.type || '').includes('批次') ? 'material' : 'product'
const statusTone = (status) => ({ 已生成: 'draft', 已打印: 'ok', 已扫码: 'running', 已关闭: 'muted', 作废: 'danger' }[status] || 'muted')
const eventTone = (type) => String(type || '').includes('质检') ? 'quality' : String(type || '').includes('打印') ? 'print' : String(type || '').includes('扫码') ? 'scan' : 'normal'
const formatTime = (value) => value ? new Date(value).toLocaleString('zh-CN', { hour12: false }) : '-'
const currentOperatorId = () => authState.user?.userId || authState.user?.id || null
const apiMessage = (e, fallback) => e?.payload?.message || e?.response?.data?.message || e?.message || fallback
const showNotice = (message) => { notice.value = message; window.setTimeout(() => { if (notice.value === message) notice.value = '' }, 2600) }

onMounted(loadTrace)
</script>

<style scoped>
.trace-page { height: calc(100vh - var(--topbar-height)); min-height: calc(900px - var(--topbar-height)); padding: 20px 24px 24px; overflow: auto; background: #eef2f4; color: #25323b; }
.page-heading { display: flex; justify-content: space-between; gap: 20px; align-items: flex-start; margin-bottom: 14px; }
.page-heading p { margin: 0 0 5px; color: #00799f; font-size: 12px; font-weight: 700; }
.page-heading h1 { margin: 0; font-size: 28px; }
.page-heading span { display: block; margin-top: 6px; color: #66777f; }
.heading-actions { display: flex; gap: 8px; }
.primary-btn, .secondary-btn { min-height: 34px; padding: 0 13px; border: 1px solid #00799f; border-radius: 3px; cursor: pointer; }
.primary-btn { color: white; background: #00799f; }
.secondary-btn { color: #006f8e; background: white; border-color: #b7c8d0; }
button:disabled { opacity: .5; cursor: not-allowed; }
.search-band { display: flex; align-items: center; gap: 9px; padding: 11px; margin-bottom: 13px; background: white; border: 1px solid #d6e0e4; }
.search-band select, .search-band input { height: 36px; padding: 0 10px; border: 1px solid #b9c8d0; }
.search-band select { width: 190px; }
.scan-input { position: relative; flex: 1; max-width: 620px; }
.scan-input span { position: absolute; left: 11px; top: 8px; color: #00799f; font-size: 18px; }
.scan-input input { width: 100%; padding-left: 38px; font-family: Consolas, monospace; font-size: 15px; }
.result-summary { margin-left: auto; color: #6e7f87; font-size: 12px; }
.message { margin-bottom: 12px; padding: 9px 12px; border: 1px solid; }
.message.success { color: #246a4d; background: #e7f3ed; border-color: #bfdcca; }
.message.error, .dialog-error { color: #a52f35; background: #fae9ea; border-color: #efc4c7; }
.trace-layout { display: grid; grid-template-columns: minmax(650px, 1.35fr) minmax(330px, .65fr); grid-template-rows: minmax(310px, auto) minmax(300px, auto); gap: 14px; }
.panel { min-width: 0; background: white; border: 1px solid #d6e0e4; }
.panel > header { display: flex; min-height: 48px; padding: 8px 13px; align-items: center; justify-content: space-between; border-bottom: 1px solid #e0e8eb; }
.panel h2, .panel header p { margin: 0; }
.panel h2 { font-size: 16px; }
.panel header p { margin-top: 3px; color: #71828a; font-size: 11px; }
.root-chip { max-width: 250px; padding: 4px 8px; overflow: hidden; color: #006f8e; background: #e6f4f7; text-overflow: ellipsis; white-space: nowrap; }
.genealogy-list { max-height: 440px; padding: 12px; overflow: auto; }
.genealogy-node { --depth: 0; position: relative; display: grid; grid-template-columns: 36px minmax(0, 1fr) auto; gap: 10px; align-items: center; width: calc(100% - var(--depth) * 30px); min-height: 58px; margin: 0 0 8px calc(var(--depth) * 30px); padding: 8px 11px; text-align: left; background: #f8fafb; border: 1px solid #dae3e7; cursor: pointer; }
.genealogy-node:hover, .genealogy-node.selected { background: #eaf7fa; border-color: #8ec7d4; }
.genealogy-node i { display: grid; width: 34px; height: 34px; place-items: center; color: white; font-style: normal; font-weight: 700; background: #00799f; border-radius: 3px; }
.genealogy-node i.material { background: #647a31; }
.genealogy-node i.package { background: #68747b; }
.node-main b, .node-main small { display: block; }
.node-main small { margin-top: 4px; color: #6b7c84; }
.node-line { position: absolute; left: -17px; top: -9px; width: 16px; height: 39px; border-left: 1px solid #a8bbc3; border-bottom: 1px solid #a8bbc3; }
.genealogy-node[style*="--depth: 0"] .node-line { display: none; }
.status-chip { display: inline-block; min-width: 50px; padding: 3px 7px; text-align: center; border-radius: 10px; font-size: 11px; }
.status-chip.ok { color: #287252; background: #e4f3eb; }.status-chip.running { color: #006f8e; background: #e2f3f7; }.status-chip.danger { color: #a52f35; background: #fae9ea; }.status-chip.draft { color: #896414; background: #faf1d9; }.status-chip.muted { color: #687981; background: #edf1f3; }
.object-body { padding: 13px; }
.selected-object { position: relative; padding: 12px; background: #edf8fa; border-left: 4px solid #00799f; }
.selected-object span, .selected-object strong { display: block; }
.selected-object span { color: #60737b; font-size: 11px; }
.selected-object strong { margin-top: 5px; overflow-wrap: anywhere; }
.selected-object em { position: absolute; right: 10px; top: 10px; font-style: normal; color: #006f8e; }
.object-body dl { display: grid; grid-template-columns: 90px minmax(0, 1fr); gap: 8px 10px; margin: 15px 0; }
.object-body dt { color: #6b7c84; }
.object-body dd { margin: 0; overflow-wrap: anywhere; }
.quick-actions { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
.quick-actions button { min-height: 32px; color: #006f8e; background: white; border: 1px solid #b8c9d0; cursor: pointer; }
.timeline-panel { grid-column: 1 / -1; }
.timeline-panel header select { height: 30px; padding: 0 8px; border: 1px solid #bdcbd2; }
.timeline-scroll { max-height: 370px; padding: 16px 18px; overflow: auto; }
.timeline { margin: 0; padding: 0; list-style: none; }
.timeline li { display: grid; grid-template-columns: 145px 18px minmax(0, 1fr); gap: 12px; min-height: 72px; }
.timeline time { padding-top: 2px; color: #64767e; font-family: Consolas, monospace; font-size: 12px; }
.timeline > li > i { position: relative; width: 10px; height: 10px; background: #00799f; border: 2px solid white; border-radius: 50%; box-shadow: 0 0 0 2px #00799f; }
.timeline > li > i::after { content: ''; position: absolute; left: 3px; top: 10px; width: 2px; height: 60px; background: #c8d7dd; }
.timeline li:last-child > i::after { display: none; }
.timeline li.quality > i { background: #8b6e16; box-shadow: 0 0 0 2px #c49c24; }.timeline li.print > i { background: #4a6570; box-shadow: 0 0 0 2px #6b8792; }.timeline li.scan > i { background: #147052; box-shadow: 0 0 0 2px #2c9a72; }
.timeline article { padding: 0 0 13px; border-bottom: 1px solid #e4eaed; }
.timeline article div { display: flex; gap: 10px; align-items: center; }
.timeline article div strong { font-size: 14px; }
.timeline article div span { color: #006f8e; }
.timeline article div em { margin-left: auto; color: #287252; font-style: normal; font-size: 11px; }
.timeline article p { display: flex; flex-wrap: wrap; gap: 5px 12px; margin: 7px 0 0; color: #677981; font-size: 11px; }
.empty-state { display: grid; min-height: 260px; place-items: center; align-content: center; gap: 8px; color: #72838b; text-align: center; }
.empty-state strong { color: #9aadb5; font-size: 38px; }.empty-state.compact { min-height: 170px; }
.mono { font-family: Consolas, 'Courier New', monospace; }
.dialog-backdrop { position: fixed; z-index: 80; inset: 0; display: grid; place-items: center; padding: 30px; background: rgba(0, 31, 40, .45); }
.dialog { width: min(720px, 92vw); background: white; border: 1px solid #b7c7ce; box-shadow: 0 20px 60px rgba(0, 30, 40, .3); }
.dialog header { display: flex; align-items: flex-start; justify-content: space-between; padding: 16px 18px; border-bottom: 1px solid #dfe7ea; }
.dialog h2, .dialog p { margin: 0; }.dialog header p { margin-top: 4px; color: #71818b; font-size: 12px; }.dialog header > button { color: #5c6d75; font-size: 24px; background: transparent; border: 0; cursor: pointer; }
.form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 13px 15px; padding: 18px; }.form-grid label { display: grid; gap: 6px; color: #52656e; font-size: 12px; }.form-grid input, .form-grid select { width: 100%; height: 36px; padding: 0 9px; border: 1px solid #bdcbd2; }.form-grid .full { grid-column: 1 / -1; }
.dialog-error { margin: 0 18px 12px; padding: 8px 10px; border: 1px solid; }.dialog footer { display: flex; justify-content: flex-end; gap: 9px; padding: 13px 18px; background: #f5f8f9; border-top: 1px solid #dfe7ea; }
</style>
