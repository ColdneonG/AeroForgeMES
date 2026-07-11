<template>
  <section class="barcode-page">
    <header class="page-heading">
      <div>
        <p>{{ t('barcode.rules.eyebrow') }}</p>
        <h1>{{ t('barcode.rules.title') }}</h1>
        <span>维护编码表达式，并将规则绑定到产品、条码类型和标签模板。</span>
      </div>
      <div class="heading-actions">
        <button class="secondary-btn" type="button" @click="openApplicationDialog()">新增应用绑定</button>
        <button class="primary-btn" type="button" @click="openRuleDialog()">新增条码规则</button>
      </div>
    </header>

    <div v-if="notice" class="message success">{{ notice }}</div>
    <div v-if="error" class="message error">{{ error }}</div>

    <div class="filter-bar">
      <div class="tabs">
        <button :class="{ active: activeTab === 'rules' }" type="button" @click="activeTab = 'rules'">
          条码规则 <b>{{ rules.length }}</b>
        </button>
        <button :class="{ active: activeTab === 'applications' }" type="button" @click="activeTab = 'applications'">
          应用绑定 <b>{{ applications.length }}</b>
        </button>
      </div>
      <input v-model.trim="filters.keyword" type="search" placeholder="规则编码 / 名称 / 产品" @keyup.enter="loadData" />
      <select v-model="filters.typeId">
        <option value="">全部条码类型</option>
        <option v-for="type in types" :key="type.id" :value="String(type.id)">{{ type.typeName }}</option>
      </select>
      <select v-model="filters.status">
        <option value="">全部状态</option>
        <option value="草稿">草稿</option>
        <option value="启用">启用</option>
        <option value="停用">停用</option>
        <option value="作废">作废</option>
      </select>
      <button class="primary-btn compact" type="button" :disabled="loading" @click="loadData">查询</button>
      <button class="secondary-btn compact" type="button" @click="resetFilters">重置</button>
    </div>

    <main class="content-grid">
      <section class="data-panel">
        <div class="panel-title">
          <strong>{{ activeTab === 'rules' ? '条码规则列表' : '产品应用绑定列表' }}</strong>
          <span>{{ loading ? '加载中...' : `${visibleRows.length} 条` }}</span>
        </div>

        <div class="table-scroll">
          <table v-if="activeTab === 'rules'" class="data-table">
            <thead>
              <tr><th>规则编码</th><th>规则名称</th><th>类型</th><th>表达式</th><th>流水位</th><th>状态</th><th>操作</th></tr>
            </thead>
            <tbody>
              <tr
                v-for="row in visibleRules"
                :key="row.id"
                :class="{ selected: selectedRule?.id === row.id }"
                @click="selectedRule = row"
              >
                <td class="mono">{{ row.ruleCode }}</td>
                <td>{{ row.ruleName }}</td>
                <td>{{ row.typeName || '-' }}</td>
                <td class="mono expression">{{ row.ruleExpression }}</td>
                <td>{{ row.serialLength }}</td>
                <td><span :class="['status-chip', statusTone(row.status)]">{{ row.status }}</span></td>
                <td class="row-actions">
                  <button type="button" @click.stop="openRuleDialog(row)">编辑</button>
                  <button type="button" @click.stop="toggleRule(row)">{{ row.status === '启用' ? '停用' : '启用' }}</button>
                  <button class="danger-link" type="button" @click.stop="removeRule(row)">删除</button>
                </td>
              </tr>
              <tr v-if="!loading && visibleRules.length === 0"><td colspan="7" class="empty">暂无匹配规则</td></tr>
            </tbody>
          </table>

          <table v-else class="data-table">
            <thead>
              <tr><th>应用编码</th><th>产品 / 物料</th><th>条码类型</th><th>编码规则</th><th>标签模板</th><th>模式</th><th>状态</th><th>操作</th></tr>
            </thead>
            <tbody>
              <tr
                v-for="row in visibleApplications"
                :key="row.id"
                :class="{ selected: selectedApplication?.id === row.id }"
                @click="selectedApplication = row"
              >
                <td class="mono">{{ row.applicationRuleCode }}</td>
                <td>{{ row.itemCode }} / {{ row.itemName }}</td>
                <td>{{ row.typeName || '-' }}</td>
                <td>{{ row.ruleName || row.ruleCode }}</td>
                <td>{{ row.templateName || '-' }}</td>
                <td>{{ row.barcodeMode }}</td>
                <td><span :class="['status-chip', statusTone(row.status)]">{{ row.status }}</span></td>
                <td class="row-actions">
                  <button type="button" @click.stop="openApplicationDialog(row)">编辑</button>
                  <button class="danger-link" type="button" @click.stop="removeApplication(row)">删除</button>
                </td>
              </tr>
              <tr v-if="!loading && visibleApplications.length === 0"><td colspan="8" class="empty">暂无应用绑定</td></tr>
            </tbody>
          </table>
        </div>
      </section>

      <aside class="detail-panel">
        <div class="panel-title"><strong>配置详情</strong><span>实时预览</span></div>
        <template v-if="activeTab === 'rules' && selectedRule">
          <dl>
            <dt>规则编码</dt><dd class="mono">{{ selectedRule.ruleCode }}</dd>
            <dt>条码类型</dt><dd>{{ selectedRule.typeName }}</dd>
            <dt>规则表达式</dt><dd class="mono">{{ selectedRule.ruleExpression }}</dd>
            <dt>流水长度</dt><dd>{{ selectedRule.serialLength }} 位</dd>
            <dt>关联产品</dt><dd>{{ selectedRule.itemName || '尚未绑定' }}</dd>
            <dt>标签模板</dt><dd>{{ selectedRule.templateName || '尚未绑定' }}</dd>
          </dl>
          <div class="preview-block">
            <span>编码预览</span>
            <strong class="mono">{{ previewExpression(selectedRule) }}</strong>
          </div>
          <div class="token-help">
            <b>可用变量</b>
            <code>${yyyyMMdd}</code><code>${itemCode}</code><code>${workOrderNo}</code>
            <code>${batchNo}</code><code>${####}</code><code>${serial}</code>
          </div>
        </template>
        <template v-else-if="activeTab === 'applications' && selectedApplication">
          <dl>
            <dt>应用编码</dt><dd class="mono">{{ selectedApplication.applicationRuleCode }}</dd>
            <dt>产品</dt><dd>{{ selectedApplication.itemCode }} / {{ selectedApplication.itemName }}</dd>
            <dt>编码规则</dt><dd>{{ selectedApplication.ruleCode }} / {{ selectedApplication.ruleName }}</dd>
            <dt>表达式</dt><dd class="mono">{{ selectedApplication.ruleExpression }}</dd>
            <dt>标签模板</dt><dd>{{ selectedApplication.templateCode }} / {{ selectedApplication.templateName }}</dd>
            <dt>生成来源</dt><dd>{{ selectedApplication.sourceType }}</dd>
          </dl>
        </template>
        <div v-else class="empty detail-empty">选择一条记录查看配置</div>
      </aside>
    </main>

    <div v-if="ruleDialog.open" class="dialog-backdrop" @mousedown.self="closeDialogs">
      <form class="dialog" @submit.prevent="saveRule">
        <header><div><h2>{{ ruleDialog.form.id ? '编辑条码规则' : '新增条码规则' }}</h2><p>规则表达式决定最终条码内容。</p></div><button type="button" @click="closeDialogs">×</button></header>
        <div class="form-grid">
          <label>规则编码<input v-model.trim="ruleDialog.form.ruleCode" required maxlength="64" placeholder="BCR-PRODUCT-SN" /></label>
          <label>规则名称<input v-model.trim="ruleDialog.form.ruleName" required maxlength="128" placeholder="产品 SN 规则" /></label>
          <label>条码类型<select v-model="ruleDialog.form.typeId" required><option disabled value="">请选择</option><option v-for="type in types" :key="type.id" :value="type.id">{{ type.typeName }}</option></select></label>
          <label>流水号长度<input v-model.number="ruleDialog.form.serialLength" required type="number" min="1" max="32" /></label>
          <label class="full">规则表达式<input v-model.trim="ruleDialog.form.ruleExpression" required maxlength="500" placeholder="SN-${yyyyMMdd}-${####}" /></label>
          <label>状态<select v-model="ruleDialog.form.status"><option>草稿</option><option>启用</option><option>停用</option></select></label>
          <div class="form-preview"><span>预览</span><strong class="mono">{{ previewExpression(ruleDialog.form) }}</strong></div>
        </div>
        <p v-if="dialogError" class="dialog-error">{{ dialogError }}</p>
        <footer><button class="secondary-btn" type="button" @click="closeDialogs">取消</button><button class="primary-btn" type="submit" :disabled="saving">{{ saving ? '保存中...' : '保存规则' }}</button></footer>
      </form>
    </div>

    <div v-if="applicationDialog.open" class="dialog-backdrop" @mousedown.self="closeDialogs">
      <form class="dialog wide" @submit.prevent="saveApplication">
        <header><div><h2>{{ applicationDialog.form.id ? '编辑应用绑定' : '新增应用绑定' }}</h2><p>将产品、编码规则和标签模板组成可执行配置。</p></div><button type="button" @click="closeDialogs">×</button></header>
        <div class="form-grid">
          <label>应用编码<input v-model.trim="applicationDialog.form.applicationRuleCode" required maxlength="64" placeholder="APP-FAN500-SN" /></label>
          <label>产品 / 物料<select v-model="applicationDialog.form.itemId" required><option disabled value="">请选择</option><option v-for="item in items" :key="item.id" :value="item.id">{{ item.itemCode }} / {{ item.itemName }}</option></select></label>
          <label>条码类型<select v-model="applicationDialog.form.typeId" required><option disabled value="">请选择</option><option v-for="type in types" :key="type.id" :value="type.id">{{ type.typeName }}</option></select></label>
          <label>编码规则<select v-model="applicationDialog.form.ruleId" required><option disabled value="">请选择</option><option v-for="rule in matchingRules" :key="rule.id" :value="rule.id">{{ rule.ruleCode }} / {{ rule.ruleName }}</option></select></label>
          <label>标签模板<select v-model="applicationDialog.form.templateId" required><option disabled value="">请选择</option><option v-for="template in matchingTemplates" :key="template.id" :value="template.id">{{ template.templateCode }} / {{ template.templateName }}</option></select></label>
          <label>条码模式<select v-model="applicationDialog.form.barcodeMode"><option>唯一码</option><option>批次码</option><option>箱码</option><option>栈板码</option></select></label>
          <label>生成来源<select v-model="applicationDialog.form.sourceType"><option>规则生成</option><option>传入值生成</option><option>外部导入</option></select></label>
          <label>状态<select v-model="applicationDialog.form.status"><option>启用</option><option>停用</option></select></label>
        </div>
        <p v-if="dialogError" class="dialog-error">{{ dialogError }}</p>
        <footer><button class="secondary-btn" type="button" @click="closeDialogs">取消</button><button class="primary-btn" type="submit" :disabled="saving">{{ saving ? '保存中...' : '保存绑定' }}</button></footer>
      </form>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  createBarcodeApplicationRule,
  createBarcodeRule,
  deleteBarcodeApplicationRule,
  deleteBarcodeRule,
  disableBarcodeRule,
  enableBarcodeRule,
  getBarcodeApplicationRules,
  getBarcodeItems,
  getBarcodeRules,
  getBarcodeTemplates,
  getBarcodeTypes,
  updateBarcodeApplicationRule,
  updateBarcodeRule
} from '../../api/barcode'
import { authState } from '../../stores/auth'

const { t } = useI18n()
const rules = ref([])
const applications = ref([])
const types = ref([])
const templates = ref([])
const items = ref([])
const selectedRule = ref(null)
const selectedApplication = ref(null)
const activeTab = ref('rules')
const loading = ref(false)
const saving = ref(false)
const error = ref('')
const notice = ref('')
const dialogError = ref('')
const filters = reactive({ keyword: '', typeId: '', status: '' })

const blankRule = () => ({ id: null, ruleCode: '', ruleName: '', typeId: '', ruleExpression: 'SN-${yyyyMMdd}-${####}', serialLength: 4, status: '草稿' })
const blankApplication = () => ({ id: null, applicationRuleCode: '', itemId: '', typeId: '', ruleId: '', templateId: '', barcodeMode: '唯一码', sourceType: '规则生成', status: '启用' })
const ruleDialog = reactive({ open: false, form: blankRule() })
const applicationDialog = reactive({ open: false, form: blankApplication() })

const visibleRules = computed(() => rules.value.filter((row) => matchesCommon(row)))
const visibleApplications = computed(() => applications.value.filter((row) => matchesCommon(row)))
const visibleRows = computed(() => activeTab.value === 'rules' ? visibleRules.value : visibleApplications.value)
const matchingRules = computed(() => rules.value.filter((rule) => !applicationDialog.form.typeId || String(rule.typeId) === String(applicationDialog.form.typeId)))
const matchingTemplates = computed(() => templates.value.filter((tpl) => !applicationDialog.form.typeId || String(tpl.typeId) === String(applicationDialog.form.typeId)))

watch(() => applicationDialog.form.typeId, () => {
  if (!matchingRules.value.some((row) => String(row.id) === String(applicationDialog.form.ruleId))) applicationDialog.form.ruleId = ''
  if (!matchingTemplates.value.some((row) => String(row.id) === String(applicationDialog.form.templateId))) applicationDialog.form.templateId = ''
})

const matchesCommon = (row) => {
  const term = filters.keyword.toLowerCase()
  const matchesKeyword = !term || Object.values(row).some((value) => String(value ?? '').toLowerCase().includes(term))
  const matchesType = !filters.typeId || String(row.typeId) === filters.typeId
  const matchesStatus = !filters.status || row.status === filters.status
  return matchesKeyword && matchesType && matchesStatus
}

const loadData = async () => {
  loading.value = true
  error.value = ''
  try {
    const [ruleRows, appRows, typeRows, templateRows, itemRows] = await Promise.all([
      getBarcodeRules(), getBarcodeApplicationRules(), getBarcodeTypes(), getBarcodeTemplates(), getBarcodeItems()
    ])
    rules.value = ruleRows || []
    applications.value = appRows || []
    types.value = typeRows || []
    templates.value = templateRows || []
    items.value = itemRows || []
    selectedRule.value = rules.value.find((row) => row.id === selectedRule.value?.id) || rules.value[0] || null
    selectedApplication.value = applications.value.find((row) => row.id === selectedApplication.value?.id) || applications.value[0] || null
  } catch (e) {
    error.value = apiMessage(e, '条码规则加载失败')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => Object.assign(filters, { keyword: '', typeId: '', status: '' })

const openRuleDialog = (row) => {
  Object.assign(ruleDialog.form, row ? {
    id: row.id, ruleCode: row.ruleCode, ruleName: row.ruleName, typeId: row.typeId,
    ruleExpression: row.ruleExpression, serialLength: row.serialLength, status: row.status
  } : blankRule())
  dialogError.value = ''
  ruleDialog.open = true
}

const openApplicationDialog = (row) => {
  Object.assign(applicationDialog.form, row ? {
    id: row.id, applicationRuleCode: row.applicationRuleCode, itemId: row.itemId,
    typeId: row.typeId, ruleId: row.ruleId, templateId: row.templateId,
    barcodeMode: row.barcodeMode, sourceType: row.sourceType, status: row.status
  } : blankApplication())
  dialogError.value = ''
  applicationDialog.open = true
}

const closeDialogs = () => {
  ruleDialog.open = false
  applicationDialog.open = false
  dialogError.value = ''
}

const saveRule = async () => {
  saving.value = true
  dialogError.value = ''
  try {
    const payload = { ...ruleDialog.form }
    delete payload.id
    ruleDialog.form.id ? await updateBarcodeRule(ruleDialog.form.id, payload) : await createBarcodeRule(payload)
    closeDialogs()
    showNotice('条码规则已保存')
    await loadData()
  } catch (e) {
    dialogError.value = apiMessage(e, '保存失败')
  } finally {
    saving.value = false
  }
}

const saveApplication = async () => {
  saving.value = true
  dialogError.value = ''
  try {
    const payload = { ...applicationDialog.form }
    delete payload.id
    applicationDialog.form.id
      ? await updateBarcodeApplicationRule(applicationDialog.form.id, payload)
      : await createBarcodeApplicationRule(payload)
    closeDialogs()
    showNotice('应用绑定已保存')
    await loadData()
  } catch (e) {
    dialogError.value = apiMessage(e, '保存失败')
  } finally {
    saving.value = false
  }
}

const toggleRule = async (row) => {
  error.value = ''
  try {
    const payload = { operatorId: currentOperatorId(), remark: row.status === '启用' ? '页面停用' : '页面启用' }
    row.status === '启用' ? await disableBarcodeRule(row.id, payload) : await enableBarcodeRule(row.id, payload)
    showNotice(`规则已${row.status === '启用' ? '停用' : '启用'}`)
    await loadData()
  } catch (e) {
    error.value = apiMessage(e, '状态变更失败')
  }
}

const removeRule = async (row) => {
  if (!window.confirm(`确认删除规则 ${row.ruleCode}？已被引用的规则不能删除。`)) return
  try {
    await deleteBarcodeRule(row.id)
    showNotice('规则已删除')
    await loadData()
  } catch (e) {
    error.value = apiMessage(e, '删除失败')
  }
}

const removeApplication = async (row) => {
  if (!window.confirm(`确认删除应用绑定 ${row.applicationRuleCode}？`)) return
  try {
    await deleteBarcodeApplicationRule(row.id)
    showNotice('应用绑定已删除')
    await loadData()
  } catch (e) {
    error.value = apiMessage(e, '删除失败')
  }
}

const previewExpression = (rule) => {
  const now = new Date()
  const yyyy = String(now.getFullYear())
  const MM = String(now.getMonth() + 1).padStart(2, '0')
  const dd = String(now.getDate()).padStart(2, '0')
  const serialLength = Number(rule?.serialLength || 4)
  return String(rule?.ruleExpression || '-')
    .replaceAll('${yyyyMMdd}', `${yyyy}${MM}${dd}`)
    .replaceAll('${yyyyMM}', `${yyyy}${MM}`)
    .replaceAll('${yyyy}', yyyy)
    .replaceAll('${itemCode}', rule?.itemCode || 'ITEM')
    .replaceAll('${workOrderNo}', 'WO001')
    .replaceAll('${batchNo}', 'BATCH01')
    .replaceAll('${serial}', '1'.padStart(serialLength, '0'))
    .replace(/\$\{(#+)\}/g, (_, hashes) => '1'.padStart(hashes.length, '0'))
}

const showNotice = (message) => {
  notice.value = message
  window.setTimeout(() => { if (notice.value === message) notice.value = '' }, 2600)
}

const statusTone = (status) => status === '启用' ? 'ok' : status === '作废' ? 'danger' : status === '草稿' ? 'draft' : 'muted'
const currentOperatorId = () => authState.user?.userId || authState.user?.id || null
const apiMessage = (e, fallback) => e?.payload?.message || e?.response?.data?.message || e?.message || fallback

onMounted(loadData)
</script>

<style scoped>
.barcode-page { height: calc(100vh - var(--topbar-height)); min-height: calc(900px - var(--topbar-height)); padding: 22px 24px; overflow: auto; background: #eef2f4; color: #25323b; }
.page-heading { display: flex; justify-content: space-between; gap: 20px; align-items: flex-start; margin-bottom: 15px; }
.page-heading p { margin: 0 0 5px; color: #00799f; font-size: 12px; font-weight: 700; }
.page-heading h1 { margin: 0; font-size: 28px; }
.page-heading span { display: block; margin-top: 6px; color: #66777f; }
.heading-actions, .row-actions { display: flex; gap: 8px; flex-wrap: wrap; }
.primary-btn, .secondary-btn { min-height: 34px; padding: 0 13px; border: 1px solid #00799f; border-radius: 3px; cursor: pointer; }
.primary-btn { color: white; background: #00799f; }
.secondary-btn { color: #006f8e; background: white; border-color: #b7c8d0; }
.compact { min-height: 32px; }
button:disabled { opacity: .55; cursor: not-allowed; }
.message { margin-bottom: 12px; padding: 9px 12px; border: 1px solid; }
.message.success { color: #246a4d; background: #e7f3ed; border-color: #bfdcca; }
.message.error, .dialog-error { color: #a52f35; background: #fae9ea; border-color: #efc4c7; }
.filter-bar { display: flex; align-items: center; gap: 10px; padding: 10px; margin-bottom: 14px; background: white; border: 1px solid #d7e0e4; }
.filter-bar input, .filter-bar select { height: 32px; padding: 0 9px; border: 1px solid #bdcbd2; background: #fff; }
.filter-bar input { width: 250px; }
.tabs { display: flex; align-self: stretch; margin: -10px 8px -10px -10px; }
.tabs button { min-width: 120px; padding: 0 14px; color: #53666f; background: #f5f8f9; border: 0; border-right: 1px solid #d7e0e4; cursor: pointer; }
.tabs button.active { color: #006f8e; background: white; box-shadow: inset 0 -3px #00799f; }
.tabs b { margin-left: 4px; font-size: 11px; }
.content-grid { display: grid; grid-template-columns: minmax(900px, 1fr) 330px; gap: 14px; align-items: start; }
.data-panel, .detail-panel { background: white; border: 1px solid #d7e0e4; }
.detail-panel { position: sticky; top: 0; }
.panel-title { display: flex; min-height: 44px; padding: 0 13px; align-items: center; justify-content: space-between; border-bottom: 1px solid #e1e8eb; }
.panel-title span { color: #72838b; font-size: 12px; }
.table-scroll { overflow: auto; }
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.data-table th, .data-table td { min-height: 40px; padding: 9px 10px; text-align: left; border-bottom: 1px solid #e6ecef; }
.data-table th { color: #53666f; font-size: 12px; background: #f3f7f8; white-space: nowrap; }
.data-table tbody tr { cursor: pointer; }
.data-table tbody tr:hover td, .data-table tbody tr.selected td { background: #edf8fb; }
.expression { max-width: 270px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.mono { font-family: Consolas, 'Courier New', monospace; }
.row-actions { white-space: nowrap; }
.row-actions button { padding: 3px 5px; color: #00799f; background: transparent; border: 0; cursor: pointer; }
.row-actions .danger-link { color: #b23a3a; }
.status-chip { display: inline-block; min-width: 48px; padding: 3px 8px; text-align: center; border-radius: 10px; font-size: 12px; }
.status-chip.ok { color: #287252; background: #e4f3eb; }
.status-chip.danger { color: #a52f35; background: #fae9ea; }
.status-chip.draft { color: #896414; background: #faf1d9; }
.status-chip.muted { color: #687981; background: #edf1f3; }
.empty { padding: 32px !important; color: #7c8b92; text-align: center !important; }
.detail-panel dl { display: grid; grid-template-columns: 86px minmax(0, 1fr); gap: 9px 10px; margin: 0; padding: 14px; }
.detail-panel dt { color: #6c7e86; }
.detail-panel dd { margin: 0; overflow-wrap: anywhere; }
.preview-block { margin: 0 14px 14px; padding: 12px; background: #eef8fa; border: 1px solid #cce5eb; }
.preview-block span { display: block; margin-bottom: 8px; color: #62757d; font-size: 12px; }
.preview-block strong { display: block; color: #005b73; overflow-wrap: anywhere; }
.token-help { display: flex; flex-wrap: wrap; gap: 6px; padding: 14px; border-top: 1px solid #e1e8eb; }
.token-help b { width: 100%; margin-bottom: 4px; }
.token-help code { padding: 3px 5px; color: #385661; background: #f0f4f5; }
.detail-empty { min-height: 220px; display: grid; place-items: center; }
.dialog-backdrop { position: fixed; z-index: 80; inset: 0; display: grid; place-items: center; padding: 30px; background: rgba(0, 31, 40, .45); }
.dialog { width: min(620px, 92vw); background: white; border: 1px solid #b7c7ce; box-shadow: 0 20px 60px rgba(0, 30, 40, .3); }
.dialog.wide { width: min(780px, 92vw); }
.dialog header { display: flex; align-items: flex-start; justify-content: space-between; padding: 16px 18px; border-bottom: 1px solid #dfe7ea; }
.dialog h2, .dialog p { margin: 0; }
.dialog header p { margin-top: 4px; color: #71818b; font-size: 12px; }
.dialog header > button { color: #5c6d75; font-size: 24px; background: transparent; border: 0; cursor: pointer; }
.form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 13px 15px; padding: 18px; }
.form-grid label { display: grid; gap: 6px; color: #52656e; font-size: 12px; }
.form-grid input, .form-grid select { width: 100%; height: 36px; padding: 0 9px; border: 1px solid #bdcbd2; }
.form-grid .full { grid-column: 1 / -1; }
.form-preview { align-self: end; min-height: 50px; padding: 7px 10px; background: #eef8fa; border: 1px solid #cce5eb; }
.form-preview span { display: block; color: #61747c; font-size: 11px; }
.form-preview strong { display: block; margin-top: 4px; overflow-wrap: anywhere; }
.dialog-error { margin: 0 18px 12px !important; padding: 8px 10px; border: 1px solid; }
.dialog footer { display: flex; justify-content: flex-end; gap: 9px; padding: 13px 18px; background: #f5f8f9; border-top: 1px solid #dfe7ea; }
</style>
