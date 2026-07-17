<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { get, post, put } from '@/api/client'
import { getProductionRecords, postProductionAction, type ResourceRecord } from '@/api/production'

type BarcodeType = { id: number; typeCode: string; typeName: string }
type RuleForm = { id?: number; ruleCode: string; ruleName: string; typeId?: number; ruleExpression: string; serialLength: number; status: string }

const rows = ref<ResourceRecord[]>([])
const types = ref<BarcodeType[]>([])
const error = ref('')
const notice = ref('')
const editing = ref(false)
const saving = ref(false)
const current = ref<RuleForm>({ ruleCode: '', ruleName: '', typeId: undefined, ruleExpression: '', serialLength: 5, status: '启用' })

const usesSequence = computed(() => /\$\{(serial|#+)}/.test(current.value.ruleExpression))
const value = (row: ResourceRecord, key: string) => String(row[key] ?? '-')
const ruleUsesSequence = (row: ResourceRecord) => /\$\{(serial|#+)}/.test(String(row.ruleExpression || ''))

async function load() {
  error.value = ''
  try {
    const [rules, barcodeTypes] = await Promise.all([getProductionRecords('/production/barcodes/rules'), get<BarcodeType[]>('/production/barcodes/types')])
    rows.value = rules; types.value = barcodeTypes
  } catch (cause) { error.value = cause instanceof Error ? cause.message : '条码规则加载失败' }
}

function startCreate() {
  current.value = { ruleCode: '', ruleName: '', typeId: types.value[0]?.id, ruleExpression: '', serialLength: 5, status: '启用' }
  editing.value = true
}

function startEdit(row: ResourceRecord) {
  current.value = { id: Number(row.id), ruleCode: String(row.ruleCode || ''), ruleName: String(row.ruleName || ''), typeId: Number(row.typeId), ruleExpression: String(row.ruleExpression || ''), serialLength: Number(row.serialLength || 5), status: String(row.status || '启用') }
  editing.value = true
}

async function save() {
  if (!current.value.ruleCode || !current.value.ruleName || !current.value.typeId || !current.value.ruleExpression) { error.value = '请完整填写规则编码、名称、条码类型和编码表达式'; return }
  saving.value = true; error.value = ''; notice.value = ''
  try {
    const payload = { ...current.value, id: undefined }
    if (current.value.id) await put(`/production/barcodes/rules/${current.value.id}`, payload)
    else await post('/production/barcodes/rules', payload)
    editing.value = false; notice.value = '条码规则已保存。'; await load()
  } catch (cause) { error.value = cause instanceof Error ? cause.message : '保存条码规则失败' } finally { saving.value = false }
}

async function action(row: ResourceRecord, name: string) {
  if (!row.id) return
  try { await postProductionAction(`/production/barcodes/rules/${row.id}/${name}`, { remark: '页面操作' }); await load() }
  catch (cause) { error.value = cause instanceof Error ? cause.message : '规则操作失败' }
}

onMounted(load)
</script>

<template>
  <MesLayout active="barcode-rules">
    <header class="app-header"><div class="header-breadcrumb"><span>条码与追溯</span><span class="bc-sep">/</span><span class="bc-current">条码规则</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="startCreate">+ 新建规则</button><span class="user-avatar">张</span></div></header>
    <main class="app-main"><h1 class="page-title">条码规则管理</h1><div class="rule-guide"><strong>批量生成规则</strong><span><code>${serial}</code> 使用设置的流水号长度；<code>${####}</code> 使用井号数量作为流水号长度。没有流水号的规则只能生成一个唯一条码，多张同码标签应使用打印份数。</span></div><div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div><div v-if="notice" class="alert alert-success mb-5"><span class="alert-icon">✓</span>{{ notice }}</div><div class="data-table-wrap"><table class="data-table"><thead><tr><th>规则编码</th><th>规则名称</th><th>条码类型</th><th>编码表达式</th><th>批量生成</th><th>流水号长度</th><th>状态</th><th>操作</th></tr></thead><tbody><tr v-if="!rows.length"><td colspan="8">暂无条码规则</td></tr><tr v-for="row in rows" :key="row.id"><td class="cell-mono">{{ value(row, 'ruleCode') }}</td><td>{{ value(row, 'ruleName') }}</td><td>{{ value(row, 'typeName') }}</td><td class="cell-mono">{{ value(row, 'ruleExpression') }}</td><td><span class="badge" :class="ruleUsesSequence(row) ? 'badge-status-ok' : 'badge-status-info'"><span class="badge-dot"></span>{{ ruleUsesSequence(row) ? '可批量' : '单条' }}</span></td><td>{{ value(row, 'serialLength') }}</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>{{ value(row, 'status') }}</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm" @click="startEdit(row)">编辑</button><button class="btn btn-ghost btn-sm" @click="action(row, 'enable')">启用</button><button class="btn btn-ghost btn-sm" @click="action(row, 'disable')">停用</button></td></tr></tbody></table></div><div v-if="editing" class="rule-mask" @click.self="editing=false"><form class="rule-dialog" @submit.prevent="save"><div class="card-header"><h2 class="card-title">{{ current.id ? '编辑' : '新建' }}条码规则</h2></div><div class="rule-form"><label>规则编码<input v-model.trim="current.ruleCode" class="form-input" maxlength="64" required></label><label>规则名称<input v-model.trim="current.ruleName" class="form-input" maxlength="128" required></label><label>条码类型<select v-model.number="current.typeId" class="form-select" required><option :value="undefined">请选择</option><option v-for="type in types" :key="type.id" :value="type.id">{{ type.typeCode }} · {{ type.typeName }}</option></select></label><label>流水号长度<input v-model.number="current.serialLength" class="form-input" type="number" min="1" max="32" required></label><label class="wide">编码表达式<input v-model.trim="current.ruleExpression" class="form-input cell-mono" maxlength="500" placeholder="例如：FS500-${yyyyMMdd}-${serial}" required></label></div><p class="expression-tip" :class="{ warning: !usesSequence }"><template v-if="usesSequence">当前表达式含流水号，可批量生成唯一条码。</template><template v-else>当前表达式不含流水号：仅允许单条生成。若这是产品 SN 规则，请加入 <code>${serial}</code> 或 <code>${####}</code>。</template></p><div class="dialog-actions"><button type="button" class="btn btn-secondary" :disabled="saving" @click="editing=false">取消</button><button class="btn btn-primary" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button></div></form></div></main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>条码规则 · {{ rows.length }} 条</span></footer>
  </MesLayout>
</template>

<style scoped>
.rule-guide,.expression-tip { display:grid; gap:var(--space-2); padding:var(--space-3); margin-bottom:var(--space-5); border-left:3px solid var(--accent); border-radius:var(--radius-sm); background:var(--surface); font-size:var(--text-sm); }
.rule-guide code,.expression-tip code { font-family:var(--font-mono); font-weight:700; }.expression-tip.warning { border-left-color:var(--color-warning,#d97706); }
.rule-mask { position:fixed; inset:0; z-index:30; display:grid; place-items:center; padding:var(--space-5); background:rgb(15 23 42 / .42); }.rule-dialog { width:min(700px,100%); padding:var(--space-5); border-radius:var(--radius-md); background:var(--color-surface,#fff); box-shadow:0 24px 60px rgb(15 23 42 / .25); }.rule-form { display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:var(--space-4); }.rule-form label { display:grid; gap:var(--space-2); font-size:var(--text-sm); font-weight:600; }.rule-form .wide { grid-column:1/-1; }.dialog-actions { display:flex; justify-content:flex-end; gap:var(--space-3); margin-top:var(--space-5); }@media(max-width:640px){.rule-form{grid-template-columns:1fr}}
</style>
