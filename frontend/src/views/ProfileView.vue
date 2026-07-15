<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { getProfile, updatePassword, updateProfile, type UserProfile } from '@/api/profile'

type ProfileTab = 'info' | 'security' | 'activity'

const activeTab = ref<ProfileTab>('info')
const profile = ref<UserProfile | null>(null)
const loading = ref(false)
const error = ref('')
const editOpen = ref(false)
const savingProfile = ref(false)
const changingPassword = ref(false)
const profileMessage = ref('')
const passwordMessage = ref('')
const passwordError = ref('')
const editForm = ref({ displayName: '', mobile: '' })
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const displayName = computed(() => profile.value?.displayName || profile.value?.username || '—')
const initial = computed(() => displayName.value.trim().slice(0, 1).toUpperCase() || '人')
const roles = computed(() => profile.value?.roles?.join('、') || '未分配角色')
const position = computed(() => [roles.value, profile.value?.orgName].filter(Boolean).join(' · '))

function value(input: string | number | null | undefined) {
  return input === null || input === undefined || input === '' ? '—' : String(input)
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    profile.value = await getProfile()
  } catch (reason) {
    error.value = reason instanceof Error ? reason.message : '个人资料加载失败'
  } finally {
    loading.value = false
  }
}

function openEdit() {
  if (!profile.value) return
  profileMessage.value = ''
  editForm.value = { displayName: profile.value.displayName || '', mobile: profile.value.mobile || '' }
  editOpen.value = true
}

async function saveProfile() {
  if (!editForm.value.displayName.trim()) {
    profileMessage.value = '显示名称不能为空'
    return
  }
  savingProfile.value = true
  profileMessage.value = ''
  try {
    profile.value = await updateProfile({ displayName: editForm.value.displayName.trim(), mobile: editForm.value.mobile.trim() })
    editOpen.value = false
  } catch (reason) {
    profileMessage.value = reason instanceof Error ? reason.message : '资料保存失败'
  } finally {
    savingProfile.value = false
  }
}

async function savePassword() {
  passwordError.value = ''
  passwordMessage.value = ''
  const { oldPassword, newPassword, confirmPassword } = passwordForm.value
  if (!oldPassword || !newPassword || !confirmPassword) {
    passwordError.value = '请填写所有密码字段'
    return
  }
  if (newPassword !== confirmPassword) {
    passwordError.value = '两次输入的新密码不一致'
    return
  }
  changingPassword.value = true
  try {
    await updatePassword({ oldPassword, newPassword, confirmPassword })
    passwordMessage.value = '密码修改成功'
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (reason) {
    passwordError.value = reason instanceof Error ? reason.message : '密码修改失败'
  } finally {
    changingPassword.value = false
  }
}

onMounted(load)
</script>

<template>
  <MesLayout active="profile">
    <header class="app-header">
      <div class="header-breadcrumb"><span>系统</span><span class="bc-sep">/</span><span class="bc-current">个人资料</span></div>
      <div class="header-actions"><button class="btn btn-secondary btn-sm" :disabled="loading" @click="load">刷新</button><span class="user-avatar">{{ initial }}</span></div>
    </header>

    <main class="app-main">
      <h1 class="page-title">个人资料</h1>
      <div v-if="error" class="alert alert-error"><span class="alert-icon">!</span>{{ error }}</div>
      <div v-else-if="loading && !profile" class="card text-muted">正在加载个人资料...</div>
      <div v-else-if="profile" class="profile-grid">
        <aside class="card profile-hero">
          <div class="profile-avatar-lg">{{ initial }}</div>
          <div class="profile-name">{{ displayName }}</div>
          <div class="profile-role">{{ position }}</div>
          <div class="profile-status">{{ value(profile.status) }}</div>
          <div class="profile-meta-list">
            <div class="profile-meta-item"><span class="meta-label">工号</span><span class="meta-value mono">{{ value(profile.employeeNo) }}</span></div>
            <div class="profile-meta-item"><span class="meta-label">用户名</span><span class="meta-value mono">@{{ profile.username }}</span></div>
            <div class="profile-meta-item"><span class="meta-label">组织</span><span class="meta-value">{{ value(profile.orgName) }}</span></div>
            <div class="profile-meta-item"><span class="meta-label">班组</span><span class="meta-value">{{ value(profile.teamName) }}</span></div>
            <div class="profile-meta-item"><span class="meta-label">角色</span><span class="meta-value">{{ roles }}</span></div>
          </div>
        </aside>

        <section>
          <div class="profile-nav card mb-5">
            <button class="profile-nav-item" :class="{ active: activeTab === 'info' }" @click="activeTab = 'info'">基本信息</button>
            <button class="profile-nav-item" :class="{ active: activeTab === 'security' }" @click="activeTab = 'security'">账户安全</button>
            <button class="profile-nav-item" :class="{ active: activeTab === 'activity' }" @click="activeTab = 'activity'">近期操作</button>
          </div>

          <section v-if="activeTab === 'info'" class="card">
            <h2 class="section-title">基本信息</h2>
            <div class="info-grid">
              <div class="info-item"><span class="info-label">显示名称</span><span class="info-value">{{ displayName }}</span></div>
              <div class="info-item"><span class="info-label">手机号码</span><span class="info-value">{{ value(profile.mobile) }}</span></div>
              <div class="info-item readonly"><span class="info-label">工号</span><span class="info-value mono">{{ value(profile.employeeNo) }}</span></div>
              <div class="info-item readonly"><span class="info-label">用户名</span><span class="info-value mono">@{{ profile.username }}</span></div>
              <div class="info-item readonly"><span class="info-label">组织</span><span class="info-value">{{ value(profile.orgName) }}</span></div>
              <div class="info-item readonly"><span class="info-label">班组</span><span class="info-value">{{ value(profile.teamName) }}</span></div>
              <div class="info-item readonly"><span class="info-label">角色</span><span class="info-value">{{ roles }}</span></div>
              <div class="info-item readonly"><span class="info-label">账户状态</span><span class="info-value">{{ value(profile.status) }}</span></div>
            </div>
            <p class="text-muted mt-5 hint">工号、组织、班组、角色和账户状态由管理员在用户管理中维护。</p>
            <button class="btn btn-primary btn-sm mt-4" @click="openEdit">编辑资料</button>
          </section>

          <section v-else-if="activeTab === 'security'" class="card">
            <h2 class="section-title">修改密码</h2>
            <form class="password-form" @submit.prevent="savePassword">
              <div class="form-group mb-4"><label class="form-label" for="old-password">当前密码</label><input id="old-password" v-model="passwordForm.oldPassword" class="form-input" type="password" autocomplete="current-password" placeholder="输入当前密码" /></div>
              <div class="form-group mb-4"><label class="form-label" for="new-password">新密码</label><input id="new-password" v-model="passwordForm.newPassword" class="form-input" type="password" minlength="6" autocomplete="new-password" placeholder="至少 6 位" /></div>
              <div class="form-group mb-4"><label class="form-label" for="confirm-password">确认新密码</label><input id="confirm-password" v-model="passwordForm.confirmPassword" class="form-input" type="password" minlength="6" autocomplete="new-password" placeholder="再次输入新密码" /></div>
              <div v-if="passwordError" class="alert alert-error"><span class="alert-icon">!</span>{{ passwordError }}</div>
              <div v-if="passwordMessage" class="alert alert-info"><span class="alert-icon">✓</span>{{ passwordMessage }}</div>
              <button class="btn btn-primary" :disabled="changingPassword">{{ changingPassword ? '提交中...' : '修改密码' }}</button>
            </form>
          </section>

          <section v-else class="card">
            <div class="card-header"><h2 class="card-title">近期操作记录</h2></div>
            <div class="empty-activity">当前后端尚未提供“我的操作记录”查询接口；接入后可在此展示最近登录及操作日志。</div>
          </section>
        </section>
      </div>
    </main>

    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">个人资料</span><span class="statusbar-item profile-statusbar-name">{{ displayName }}</span></footer>
  </MesLayout>

  <div v-if="editOpen" class="modal-overlay" @click.self="editOpen = false">
    <form class="modal" @submit.prevent="saveProfile">
      <div class="modal-header"><span class="card-title">编辑个人资料</span><button type="button" class="btn btn-ghost btn-sm" aria-label="关闭" @click="editOpen = false">×</button></div>
      <div class="modal-body">
        <div v-if="profileMessage" class="alert alert-error"><span class="alert-icon">!</span>{{ profileMessage }}</div>
        <div class="form-group mb-4"><label class="form-label" for="display-name">显示名称</label><input id="display-name" v-model="editForm.displayName" class="form-input" maxlength="64" required autofocus /></div>
        <div class="form-group"><label class="form-label" for="mobile">手机号码</label><input id="mobile" v-model="editForm.mobile" class="form-input" maxlength="32" inputmode="tel" /></div>
      </div>
      <div class="modal-footer"><button type="button" class="btn btn-secondary" @click="editOpen = false">取消</button><button class="btn btn-primary" :disabled="savingProfile">{{ savingProfile ? '保存中...' : '保存' }}</button></div>
    </form>
  </div>
</template>

<style scoped>
.profile-grid { display:grid; grid-template-columns:320px minmax(0,1fr); gap:var(--space-5); align-items:start; }
.profile-hero { text-align:center; padding:var(--space-6) var(--space-5); }
.profile-avatar-lg { width:80px; height:80px; margin:0 auto var(--space-4); border:3px solid var(--bg); border-radius:50%; box-shadow:0 0 0 2px var(--border); display:grid; place-items:center; background:var(--fg); color:#fff; font-size:2rem; font-weight:700; }
.profile-name { font-size:var(--text-h2); font-weight:700; margin-bottom:var(--space-1); }
.profile-role, .hint { font-size:var(--text-small); }
.profile-role, .meta-label, .empty-activity { color:var(--fg); opacity:.55; }
.profile-status { display:inline-block; margin-top:var(--space-2); padding:2px 10px; border-radius:999px; background:var(--surface); color:var(--accent); font-size:var(--text-caption); font-weight:600; }
.profile-meta-list { margin-top:var(--space-5); padding-top:var(--space-4); border-top:1px solid var(--border); }
.profile-meta-item { display:flex; justify-content:space-between; gap:var(--space-3); padding:var(--space-3) 0; font-size:var(--text-small); text-align:right; }
.profile-meta-item + .profile-meta-item { border-top:1px solid var(--muted); }
.meta-value { font-weight:600; overflow-wrap:anywhere; }.mono { font-family:var(--font-mono); font-size:var(--text-caption); }
.profile-nav { display:flex; flex-direction:row; gap:0; overflow:hidden; padding:0; }.profile-nav-item { padding:var(--space-3) var(--space-4); border:0; border-bottom:2px solid transparent; background:transparent; color:var(--fg); opacity:.55; cursor:pointer; font:600 var(--text-small) var(--font-body); }.profile-nav-item:hover,.profile-nav-item.active { background:var(--surface); opacity:1; }.profile-nav-item.active { color:var(--accent); border-bottom-color:var(--accent); }
.info-grid { display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:var(--space-4); }.info-item { display:flex; flex-direction:column; gap:var(--space-1); }.info-label { font-size:var(--text-caption); font-weight:600; letter-spacing:.06em; opacity:.5; text-transform:uppercase; }.info-value { font-weight:600; overflow-wrap:anywhere; }.readonly .info-value { font-weight:400; opacity:.55; }.readonly .info-label::after { content:'（管理员维护）'; font-weight:400; letter-spacing:0; text-transform:none; }
.password-form { max-width:420px; }.empty-activity { padding:var(--space-5) 0; font-size:var(--text-small); }.profile-statusbar-name { margin-left:auto; }
@media (max-width:960px) { .profile-grid { grid-template-columns:1fr; } }
@media (max-width:600px) { .info-grid { grid-template-columns:1fr; }.profile-nav-item { flex:1; padding-inline:var(--space-2); } }
</style>
