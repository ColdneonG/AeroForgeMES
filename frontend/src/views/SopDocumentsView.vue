<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import MesLayout from '@/layouts/MesLayout.vue'
import { get, post, put } from '@/api/client'

type Document = { id: number; sopCode: string; sopName: string; category?: string; status?: string; currentVersionId?: number; updatedAt?: string }
const router = useRouter()
const rows = ref<Document[]>([]), loading = ref(false), error = ref('')
const keyword = ref(''), status = ref('')
const creating = ref(false), editingId = ref<number | null>(null)
const form = ref({ sopCode: '', sopName: '', category: 'STANDARD_OPERATION', ownerId: '', status: 'ENABLED' })
const text = (value?: unknown) => value == null || value === '' ? '-' : String(value)
const date = (value?: string) => value ? value.replace('T', ' ').slice(0, 16) : '-'

async function load() {
  loading.value = true; error.value = ''
  try { rows.value = await get<Document[]>('/production/sop/documents', { keyword: keyword.value, status: status.value }) }
  catch (e) { error.value = e instanceof Error ? e.message : 'SOP 文档加载失败' }
  finally { loading.value = false }
}
async function create() {
  if (!form.value.sopCode.trim() || !form.value.sopName.trim()) { error.value = '请填写 SOP 编码和名称'; return }
  try {
    const payload = { ...form.value, ownerId: form.value.ownerId ? Number(form.value.ownerId) : null }
    if (editingId.value) await put<Document>(`/production/sop/documents/${editingId.value}`, payload)
    else await post<Document>('/production/sop/documents', payload)
    closeForm(); await load()
  } catch (e) { error.value = e instanceof Error ? e.message : '创建失败' }
}
function openVersions(row: Document) { router.push({ path: '/sop/versions', query: { sopId: row.id, name: row.sopName } }) }
function edit(row: Document) { editingId.value = row.id; form.value = { sopCode: row.sopCode, sopName: row.sopName, category: row.category || 'STANDARD_OPERATION', ownerId: '', status: row.status || 'ENABLED' }; creating.value = true }
function closeForm() { creating.value = false; editingId.value = null; form.value = { sopCode: '', sopName: '', category: 'STANDARD_OPERATION', ownerId: '', status: 'ENABLED' } }
onMounted(load)
</script>

<template>
  <MesLayout active="sop-docs">
    <header class="app-header"><div class="header-breadcrumb"><span>电子 SOP</span><span class="bc-sep">/</span><span class="bc-current">SOP 文档</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="creating = true">+ 新建文档</button><span class="user-avatar">张</span></div></header>
    <main class="app-main"><h1 class="page-title">SOP 文档管理</h1>
      <div class="card mb-5"><div class="search-bar" style="margin-bottom:0"><div class="form-group" style="min-width:220px"><label class="form-label">文档名称</label><input v-model="keyword" class="form-input" placeholder="编码或名称" @keyup.enter="load"></div><div class="form-group" style="min-width:140px"><label class="form-label">状态</label><select v-model="status" class="form-select"><option value="">全部</option><option value="ENABLED">启用</option><option value="DISABLED">停用</option></select></div><button class="btn btn-secondary btn-sm" style="align-self:flex-end" :disabled="loading" @click="load">筛选</button></div></div>
      <div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div>
      <div class="data-table-wrap"><table class="data-table"><thead><tr><th>文档编号</th><th>文档名称</th><th>分类</th><th>当前版本</th><th>更新时间</th><th>状态</th><th>操作</th></tr></thead><tbody><tr v-if="loading"><td colspan="7">正在加载...</td></tr><tr v-else-if="!rows.length"><td colspan="7">暂无 SOP 文档</td></tr><tr v-for="row in rows" :key="row.id"><td class="cell-mono">{{ row.sopCode }}</td><td>{{ row.sopName }}</td><td>{{ text(row.category) }}</td><td>{{ text(row.currentVersionId) }}</td><td>{{ date(row.updatedAt) }}</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>{{ text(row.status) }}</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm" @click="openVersions(row)">查看版本</button><button class="btn btn-ghost btn-sm" @click="edit(row)">编辑</button></td></tr></tbody></table><div class="pagination">共 {{ rows.length }} 份文档</div></div>
      <div v-if="creating" class="sop-mask" @click.self="closeForm"><form class="sop-dialog" @submit.prevent="create"><div class="card-header"><h2 class="card-title">{{ editingId ? '编辑' : '新建' }} SOP 文档</h2><button type="button" class="btn btn-ghost btn-sm" @click="closeForm">关闭</button></div><div class="sop-form"><label>文档编码<input v-model="form.sopCode" class="form-input" required></label><label>文档名称<input v-model="form.sopName" class="form-input" required></label><label>分类<select v-model="form.category" class="form-select"><option value="STANDARD_OPERATION">标准作业</option><option value="QUALITY">质量作业</option><option value="SAFETY">安全作业</option></select></label><label>负责人 ID<input v-model="form.ownerId" class="form-input" inputmode="numeric"></label><label>状态<select v-model="form.status" class="form-select"><option value="ENABLED">启用</option><option value="DISABLED">停用</option></select></label></div><div class="dialog-actions"><button type="button" class="btn btn-secondary" @click="closeForm">取消</button><button class="btn btn-primary">保存</button></div></form></div>
    </main><footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>数据库实时数据</span><span class="statusbar-sep">|</span><span class="statusbar-item">SOP 文档 · {{ rows.length }} 份</span></footer>
  </MesLayout>
</template>

<style scoped>
.sop-mask{position:fixed;inset:0;z-index:20;background:rgba(15,23,42,.38);display:grid;place-items:center}.sop-dialog{width:min(560px,calc(100vw - 32px));background:#fff;border-radius:8px;padding:20px;box-shadow:0 18px 50px rgba(15,23,42,.28)}.sop-form{display:grid;grid-template-columns:1fr 1fr;gap:16px}.sop-form label{display:grid;gap:7px;font-size:13px;color:var(--color-text-muted,#667085)}.dialog-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:22px}
</style>
