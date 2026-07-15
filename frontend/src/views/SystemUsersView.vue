<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { get, post, put } from '@/api/client'

type Role = { id: number; roleCode: string; roleName: string; status: string }
type User = {
  id: number; username: string; displayName: string; mobile?: string; employeeNo?: string
  orgId?: number; orgName?: string; teamId?: number; teamName?: string; status: string; roles: Role[]
}
type UserPage = { total: number; records: User[] }
type Org = { id: number; parentId?: number; orgCode: string; orgName: string; children?: Org[] }
type Workshop = { id: number; workshopCode: string; workshopName: string; orgId: number }
type Team = { id: number; teamCode: string; teamName: string; workshopId: number }
type Modal = 'create' | 'detail' | 'edit' | 'roles' | 'resetPassword' | 'status' | null

const rows = ref<User[]>([])
const roles = ref<Role[]>([])
const orgs = ref<Org[]>([])
const workshops = ref<Workshop[]>([])
const teams = ref<Team[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = 20
const loading = ref(false)
const saving = ref(false)
const error = ref('')
const modal = ref<Modal>(null)
const selected = ref<User | null>(null)
const selectedRoleIds = ref<number[]>([])
const passwordConfirmation = ref('')
const pendingStatus = ref<'启用' | '停用'>('停用')

const filters = reactive({ keyword: '', status: '', roleId: '', orgId: '', workshopId: '', teamId: '' })
const createForm = reactive({ username: '', password: '', displayName: '', employeeNo: '', mobile: '', orgId: '', workshopId: '', teamId: '', status: '启用' })
const editForm = reactive({ displayName: '', employeeNo: '', mobile: '', orgId: '', workshopId: '', teamId: '' })
const resetPassword = ref('')

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))
const activeCount = computed(() => rows.value.filter((item) => item.status === '启用' || item.status === 'ENABLED').length)
const selectedRoleNames = computed(() => roles.value.filter((role) => selectedRoleIds.value.includes(role.id)).map((role) => role.roleName))
const flatOrgs = computed(() => {
  const flatten = (items: Org[], depth = 0): Array<Org & { depth: number }> => items.flatMap((org) => [{ ...org, depth }, ...flatten(org.children || [], depth + 1)])
  return flatten(orgs.value)
})
const filterWorkshops = computed(() => workshops.value.filter((item) => !filters.orgId || item.orgId === Number(filters.orgId)))
const filterTeams = computed(() => teams.value.filter((item) => !filters.workshopId || item.workshopId === Number(filters.workshopId)))
const createWorkshops = computed(() => workshops.value.filter((item) => !createForm.orgId || item.orgId === Number(createForm.orgId)))
const createTeams = computed(() => teams.value.filter((item) => !createForm.workshopId || item.workshopId === Number(createForm.workshopId)))
const editWorkshops = computed(() => workshops.value.filter((item) => !editForm.orgId || item.orgId === Number(editForm.orgId)))
const editTeams = computed(() => teams.value.filter((item) => !editForm.workshopId || item.workshopId === Number(editForm.workshopId)))

function fieldNumber(value: string) {
  const number = Number(value)
  return value.trim() && Number.isFinite(number) ? number : undefined
}
function statusLabel(status: string) { return status === 'ENABLED' ? '启用' : status === 'DISABLED' ? '停用' : status || '—' }
function isEnabled(user: User) { return user.status === '启用' || user.status === 'ENABLED' }
function roleText(user: User) { return user.roles?.map((role) => role.roleName).join('、') || '未分配角色' }
function orgTeamText(user: User) { return [user.orgName || (user.orgId ? `组织 #${user.orgId}` : ''), user.teamName || (user.teamId ? `班组 #${user.teamId}` : '')].filter(Boolean).join(' / ') || '—' }

async function loadRoles() {
  try { roles.value = await get<Role[]>('/auth/admin/roles') } catch { roles.value = [] }
}
async function loadMasterData() {
  try {
    const [orgTree, workshopList, teamList] = await Promise.all([
      get<Org[]>('/system/orgs/tree'), get<Workshop[]>('/system/workshops'), get<Team[]>('/system/teams'),
    ])
    orgs.value = orgTree; workshops.value = workshopList; teams.value = teamList
  } catch (cause) { error.value = cause instanceof Error ? cause.message : '组织与班组数据加载失败' }
}
async function loadUsers(page = pageNo.value) {
  loading.value = true; error.value = ''
  try {
    const result = await get<UserPage>('/auth/admin/users', {
      keyword: filters.keyword, status: filters.status, roleId: filters.roleId || undefined,
      orgId: filters.orgId || undefined, teamId: filters.teamId || undefined, pageNo: page, pageSize,
    })
    rows.value = result.records; total.value = result.total; pageNo.value = page
  } catch (cause) {
    rows.value = []; total.value = 0
    error.value = cause instanceof Error ? cause.message : '用户数据加载失败'
  } finally { loading.value = false }
}
function applyFilters() { void loadUsers(1) }
function resetFilters() { Object.assign(filters, { keyword: '', status: '', roleId: '', orgId: '', workshopId: '', teamId: '' }); void loadUsers(1) }
function resetFilterWorkshop() { filters.workshopId = ''; filters.teamId = '' }
function resetFilterTeam() { filters.teamId = '' }
function resetCreateWorkshop() { createForm.workshopId = ''; createForm.teamId = '' }
function resetCreateTeam() { createForm.teamId = '' }
function resetEditWorkshop() { editForm.workshopId = ''; editForm.teamId = '' }
function resetEditTeam() { editForm.teamId = '' }

async function openUserModal(next: Exclude<Modal, 'create' | 'status' | null>, user: User) {
  try {
    selected.value = await get<User>(`/auth/admin/users/${user.id}`)
    selectedRoleIds.value = selected.value.roles.map((role) => role.id)
    if (next === 'edit') {
      Object.assign(editForm, {
        displayName: selected.value.displayName, employeeNo: selected.value.employeeNo || '', mobile: selected.value.mobile || '',
        orgId: selected.value.orgId ? String(selected.value.orgId) : '', teamId: selected.value.teamId ? String(selected.value.teamId) : '',
      })
      editForm.workshopId = selected.value.teamId ? String(teams.value.find((team) => team.id === selected.value?.teamId)?.workshopId || '') : ''
    }
    if (next === 'resetPassword') { resetPassword.value = ''; passwordConfirmation.value = '' }
    modal.value = next
  } catch (cause) { error.value = cause instanceof Error ? cause.message : '用户详情加载失败' }
}
function openCreate() {
  Object.assign(createForm, { username: '', password: '', displayName: '', employeeNo: '', mobile: '', orgId: '', workshopId: '', teamId: '', status: '启用' })
  selectedRoleIds.value = []; modal.value = 'create'
}
function openStatus(user: User) { selected.value = user; pendingStatus.value = isEnabled(user) ? '停用' : '启用'; modal.value = 'status' }
function closeModal() { if (!saving.value) modal.value = null }
function toggleRole(roleId: number) {
  selectedRoleIds.value = selectedRoleIds.value.includes(roleId)
    ? selectedRoleIds.value.filter((id) => id !== roleId) : [...selectedRoleIds.value, roleId]
}
async function createUser() {
  if (!createForm.username.trim() || !createForm.password || !createForm.displayName.trim()) { error.value = '请填写账号、初始密码和姓名'; return }
  saving.value = true; error.value = ''
  try {
    await post<User>('/auth/admin/users', {
      username: createForm.username.trim(), password: createForm.password, displayName: createForm.displayName.trim(),
      employeeNo: createForm.employeeNo.trim() || undefined, mobile: createForm.mobile.trim() || undefined,
      orgId: fieldNumber(createForm.orgId), teamId: fieldNumber(createForm.teamId), status: createForm.status, roleIds: selectedRoleIds.value,
    })
    modal.value = null; await loadUsers(1)
  } catch (cause) { error.value = cause instanceof Error ? cause.message : '账号创建失败' } finally { saving.value = false }
}
async function saveEdit() {
  if (!selected.value || !editForm.displayName.trim()) { error.value = '姓名不能为空'; return }
  saving.value = true; error.value = ''
  try {
    await put<User>(`/auth/admin/users/${selected.value.id}`, {
      displayName: editForm.displayName.trim(), employeeNo: editForm.employeeNo.trim() || undefined, mobile: editForm.mobile.trim() || undefined,
      orgId: fieldNumber(editForm.orgId), teamId: fieldNumber(editForm.teamId),
    })
    modal.value = null; await loadUsers()
  } catch (cause) { error.value = cause instanceof Error ? cause.message : '资料保存失败' } finally { saving.value = false }
}
async function saveRoles() {
  if (!selected.value) return
  saving.value = true; error.value = ''
  try { await put<User>(`/auth/admin/users/${selected.value.id}/roles`, { roleIds: selectedRoleIds.value }); modal.value = null; await loadUsers() }
  catch (cause) { error.value = cause instanceof Error ? cause.message : '角色保存失败' } finally { saving.value = false }
}
async function updateStatus() {
  if (!selected.value) return
  saving.value = true; error.value = ''
  try { await put<User>(`/auth/admin/users/${selected.value.id}/status`, { status: pendingStatus.value }); modal.value = null; await loadUsers() }
  catch (cause) { error.value = cause instanceof Error ? cause.message : '账号状态更新失败' } finally { saving.value = false }
}
async function savePassword() {
  if (!selected.value) return
  if (resetPassword.value.length < 6) { error.value = '新密码至少应为 6 位'; return }
  if (resetPassword.value !== passwordConfirmation.value) { error.value = '两次输入的密码不一致'; return }
  saving.value = true; error.value = ''
  try { await post<void>(`/auth/admin/users/${selected.value.id}/reset-password`, { password: resetPassword.value }); modal.value = null }
  catch (cause) { error.value = cause instanceof Error ? cause.message : '密码重置失败' } finally { saving.value = false }
}

onMounted(async () => { await Promise.all([loadRoles(), loadMasterData(), loadUsers(1)]) })
</script>

<template>
  <MesLayout active="system-users">
    <header class="app-header">
      <div class="header-breadcrumb"><span>数据与系统</span><span class="bc-sep">/</span><span class="bc-current">用户管理</span></div>
      <div class="header-actions"><button class="btn btn-primary btn-sm" type="button" @click="openCreate">+ 创建账号</button><span class="user-avatar">管</span></div>
    </header>
    <main class="app-main">
      <h1 class="page-title">用户管理</h1>
      <div v-if="error" class="alert alert-error mb-4"><span class="alert-icon">!</span>{{ error }}</div>
      <div class="card mb-5"><div class="filter-bar">
        <div class="form-group search"><label class="form-label" for="user-keyword">搜索</label><input id="user-keyword" v-model="filters.keyword" class="form-input" placeholder="账号 / 姓名 / 工号…" @keyup.enter="applyFilters"></div>
        <div class="form-group"><label class="form-label" for="user-org">组织</label><select id="user-org" v-model="filters.orgId" class="form-select" @change="resetFilterWorkshop"><option value="">全部</option><option v-for="org in flatOrgs" :key="org.id" :value="String(org.id)">{{ '　'.repeat(org.depth) }}{{ org.orgName }}</option></select></div>
        <div class="form-group"><label class="form-label" for="user-workshop">车间</label><select id="user-workshop" v-model="filters.workshopId" class="form-select" @change="resetFilterTeam"><option value="">全部</option><option v-for="workshop in filterWorkshops" :key="workshop.id" :value="String(workshop.id)">{{ workshop.workshopName }}</option></select></div>
        <div class="form-group"><label class="form-label" for="user-team">班组</label><select id="user-team" v-model="filters.teamId" class="form-select"><option value="">全部</option><option v-for="team in filterTeams" :key="team.id" :value="String(team.id)">{{ team.teamName }}</option></select></div>
        <div class="form-group"><label class="form-label" for="user-role">角色</label><select id="user-role" v-model="filters.roleId" class="form-select"><option value="">全部</option><option v-for="role in roles" :key="role.id" :value="String(role.id)">{{ role.roleName }}</option></select></div>
        <div class="form-group"><label class="form-label" for="user-status">状态</label><select id="user-status" v-model="filters.status" class="form-select"><option value="">全部</option><option value="启用">启用</option><option value="停用">停用</option></select></div>
        <button class="btn btn-secondary btn-sm" type="button" @click="applyFilters">筛选</button><button class="btn btn-ghost btn-sm" type="button" @click="resetFilters">重置</button>
      </div></div>
      <div class="data-table-wrap"><table class="data-table"><thead><tr><th>工号</th><th>姓名</th><th>用户名</th><th>组织 / 班组</th><th>角色</th><th>状态</th><th>最后登录</th><th>操作</th></tr></thead>
        <tbody><tr v-if="loading"><td colspan="8">正在加载…</td></tr><tr v-else-if="!rows.length"><td colspan="8">暂无用户数据</td></tr><tr v-for="user in rows" :key="user.id"><td class="cell-mono">{{ user.employeeNo || '—' }}</td><td>{{ user.displayName }}</td><td class="cell-mono">{{ user.username }}</td><td>{{ orgTeamText(user) }}</td><td>{{ roleText(user) }}</td><td><span class="badge" :class="isEnabled(user) ? 'badge-status-ok' : 'badge-status-draft'"><span class="badge-dot"></span>{{ statusLabel(user.status) }}</span></td><td>—</td><td class="cell-actions"><button class="btn btn-ghost btn-sm" @click="openUserModal('detail', user)">详情</button><button class="btn btn-ghost btn-sm" @click="openUserModal('edit', user)">编辑</button><button class="btn btn-ghost btn-sm" @click="openUserModal('roles', user)">角色</button><button class="btn btn-ghost btn-sm" @click="openUserModal('resetPassword', user)">重置密码</button><button class="btn btn-ghost btn-sm" @click="openStatus(user)">{{ isEnabled(user) ? '停用' : '启用' }}</button></td></tr></tbody>
      </table><div class="pagination"><span>共 {{ total }} 条记录</span><div class="page-btns"><button class="page-btn" :disabled="pageNo <= 1 || loading" @click="loadUsers(pageNo - 1)">‹</button><span class="page-btn active">{{ pageNo }}</span><button class="page-btn" :disabled="pageNo >= totalPages || loading" @click="loadUsers(pageNo + 1)">›</button></div></div></div>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">用户管理 · {{ total }} 人 · 当前页启用 {{ activeCount }} 人</span><span class="statusbar-sep">|</span><span class="statusbar-item statusbar-end">AeroForge MES</span></footer>
  </MesLayout>

  <div v-if="modal" class="modal-overlay" @click.self="closeModal"><section class="modal users-modal" role="dialog" aria-modal="true"><header class="modal-header"><span class="card-title">{{ modal === 'create' ? '创建账号' : modal === 'detail' ? '账号详情' : modal === 'edit' ? '编辑账号资料' : modal === 'roles' ? '管理角色' : modal === 'resetPassword' ? '重置密码' : `${pendingStatus}账号` }}</span><button class="btn btn-ghost btn-sm" :disabled="saving" @click="closeModal">×</button></header>
    <div class="modal-body"><div v-if="error" class="alert alert-error mb-4"><span class="alert-icon">!</span>{{ error }}</div>
      <template v-if="modal === 'create'"><div class="detail-two-col"><label class="form-group"><span class="form-label">用户名 *</span><input v-model="createForm.username" class="form-input" placeholder="登录账号，创建后不可修改"></label><label class="form-group"><span class="form-label">初始密码 *</span><input v-model="createForm.password" class="form-input" type="password" placeholder="至少 6 位"></label><label class="form-group"><span class="form-label">姓名 *</span><input v-model="createForm.displayName" class="form-input"></label><label class="form-group"><span class="form-label">工号</span><input v-model="createForm.employeeNo" class="form-input"></label><label class="form-group"><span class="form-label">手机号</span><input v-model="createForm.mobile" class="form-input"></label><label class="form-group"><span class="form-label">初始状态</span><select v-model="createForm.status" class="form-select"><option value="启用">启用</option><option value="停用">停用</option></select></label><label class="form-group"><span class="form-label">组织</span><select v-model="createForm.orgId" class="form-select" @change="resetCreateWorkshop"><option value="">—</option><option v-for="org in flatOrgs" :key="org.id" :value="String(org.id)">{{ '　'.repeat(org.depth) }}{{ org.orgName }}</option></select></label><label class="form-group"><span class="form-label">车间</span><select v-model="createForm.workshopId" class="form-select" @change="resetCreateTeam"><option value="">—</option><option v-for="workshop in createWorkshops" :key="workshop.id" :value="String(workshop.id)">{{ workshop.workshopName }}</option></select></label><label class="form-group"><span class="form-label">班组</span><select v-model="createForm.teamId" class="form-select"><option value="">—</option><option v-for="team in createTeams" :key="team.id" :value="String(team.id)">{{ team.teamName }}</option></select></label></div><div class="form-group mt-4"><span class="form-label">分配角色</span><div class="role-chip-list"><button v-for="role in roles" :key="role.id" class="role-chip" :class="{ selected: selectedRoleIds.includes(role.id) }" @click="toggleRole(role.id)" type="button">{{ role.roleName }}</button></div></div></template>
      <template v-else-if="modal === 'detail' && selected"><div class="detail-two-col"><div class="info-item"><span class="info-label">用户名</span><span class="info-value mono">{{ selected.username }}</span></div><div class="info-item"><span class="info-label">姓名</span><span class="info-value">{{ selected.displayName }}</span></div><div class="info-item"><span class="info-label">工号</span><span class="info-value mono">{{ selected.employeeNo || '—' }}</span></div><div class="info-item"><span class="info-label">手机号</span><span class="info-value">{{ selected.mobile || '—' }}</span></div><div class="info-item"><span class="info-label">组织 / 班组</span><span class="info-value">{{ orgTeamText(selected) }}</span></div><div class="info-item"><span class="info-label">状态</span><span class="info-value">{{ statusLabel(selected.status) }}</span></div></div><div class="detail-section"><h3 class="card-title mb-3">已分配角色</h3><div class="role-chip-list"><span v-for="role in selected.roles" :key="role.id" class="role-chip readonly">{{ role.roleName }}</span><span v-if="!selected.roles.length" class="text-muted">未分配角色</span></div></div></template>
      <template v-else-if="modal === 'edit' && selected"><div class="detail-two-col"><label class="form-group"><span class="form-label">用户名</span><input :value="selected.username" class="form-input" disabled></label><label class="form-group"><span class="form-label">姓名 *</span><input v-model="editForm.displayName" class="form-input"></label><label class="form-group"><span class="form-label">工号</span><input v-model="editForm.employeeNo" class="form-input"></label><label class="form-group"><span class="form-label">手机号</span><input v-model="editForm.mobile" class="form-input"></label><label class="form-group"><span class="form-label">组织</span><select v-model="editForm.orgId" class="form-select" @change="resetEditWorkshop"><option value="">—</option><option v-for="org in flatOrgs" :key="org.id" :value="String(org.id)">{{ '　'.repeat(org.depth) }}{{ org.orgName }}</option></select></label><label class="form-group"><span class="form-label">车间</span><select v-model="editForm.workshopId" class="form-select" @change="resetEditTeam"><option value="">—</option><option v-for="workshop in editWorkshops" :key="workshop.id" :value="String(workshop.id)">{{ workshop.workshopName }}</option></select></label><label class="form-group"><span class="form-label">班组</span><select v-model="editForm.teamId" class="form-select"><option value="">—</option><option v-for="team in editTeams" :key="team.id" :value="String(team.id)">{{ team.teamName }}</option></select></label></div></template>
      <template v-else-if="modal === 'roles' && selected"><p class="text-muted mb-4">选择要分配给 <strong>{{ selected.displayName }}</strong> 的角色，保存时将全量更新角色关系。</p><div class="role-chip-list"><button v-for="role in roles" :key="role.id" class="role-chip" :class="{ selected: selectedRoleIds.includes(role.id) }" @click="toggleRole(role.id)">{{ role.roleName }}</button></div><div class="detail-section"><h3 class="card-title mb-3">已选择角色</h3><p class="text-muted">{{ selectedRoleNames.join('、') || '未选择角色' }}</p></div></template>
      <template v-else-if="modal === 'resetPassword' && selected"><p class="text-muted mb-4">为 <strong>{{ selected.displayName }}（{{ selected.username }}）</strong> 设置新密码。</p><label class="form-group mb-4"><span class="form-label">新密码</span><input v-model="resetPassword" class="form-input" type="password" placeholder="至少 6 位"></label><label class="form-group"><span class="form-label">确认新密码</span><input v-model="passwordConfirmation" class="form-input" type="password"></label></template>
      <template v-else-if="modal === 'status' && selected"><p>确认{{ pendingStatus }}账号 <strong>{{ selected.displayName }}（{{ selected.username }}）</strong>？</p><p class="text-muted mt-3">{{ pendingStatus === '停用' ? '停用后该用户将无法再次登录系统。' : '启用后该用户可使用已分配的角色权限登录系统。' }}</p></template>
    </div><footer class="modal-footer"><button class="btn btn-secondary btn-sm" :disabled="saving" @click="closeModal">取消</button><button v-if="modal === 'create'" class="btn btn-primary btn-sm" :class="{ 'btn-loading': saving }" @click="createUser">创建账号</button><button v-else-if="modal === 'edit'" class="btn btn-primary btn-sm" :class="{ 'btn-loading': saving }" @click="saveEdit">保存修改</button><button v-else-if="modal === 'roles'" class="btn btn-primary btn-sm" :class="{ 'btn-loading': saving }" @click="saveRoles">保存角色</button><button v-else-if="modal === 'resetPassword'" class="btn btn-primary btn-sm" :class="{ 'btn-loading': saving }" @click="savePassword">确认重置</button><button v-else-if="modal === 'status'" class="btn btn-primary btn-sm" :class="{ 'btn-loading': saving }" @click="updateStatus">确认{{ pendingStatus }}</button><button v-else class="btn btn-primary btn-sm" @click="closeModal">关闭</button></footer>
  </section></div>
</template>

<style scoped>
.filter-bar { display: flex; gap: var(--space-3); align-items: end; flex-wrap: wrap; }
.filter-bar .search { min-width: 260px; flex: 1; }
.filter-bar .form-group:not(.search) { min-width: 130px; }
.statusbar-end { margin-left: auto; }
.users-modal { width: min(560px, calc(100vw - 32px)); max-height: calc(100vh - 40px); overflow: auto; }
.detail-two-col { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: var(--space-4); }
.info-item { display: flex; flex-direction: column; gap: var(--space-1); }
.info-label { font-size: var(--text-caption); color: var(--fg); opacity: .55; font-weight: 600; letter-spacing: .06em; text-transform: uppercase; }
.info-value { font-weight: 600; }.mono { font-family: var(--font-mono); font-size: var(--text-caption); }
.detail-section { border-top: 1px solid var(--border); margin-top: var(--space-5); padding-top: var(--space-4); }
.role-chip-list { display: flex; flex-wrap: wrap; gap: var(--space-2); }
.role-chip { border: 1px solid var(--border); background: var(--bg); border-radius: 999px; padding: 5px 10px; font: inherit; font-size: var(--text-caption); cursor: pointer; }
.role-chip.selected { background: var(--accent); border-color: var(--accent); color: #fff; }.role-chip.readonly { cursor: default; background: var(--surface); }
.page-btn { border: 1px solid var(--border); background: var(--bg); min-width: 28px; height: 28px; border-radius: 4px; cursor: pointer; }.page-btn.active { background: var(--accent); border-color: var(--accent); color: #fff; display: inline-grid; place-items: center; }.page-btn:disabled { opacity: .4; cursor: not-allowed; }
@media (max-width: 760px) { .detail-two-col { grid-template-columns: 1fr; }.filter-bar .search { min-width: 100%; }.cell-actions { min-width: 350px; } }
</style>
