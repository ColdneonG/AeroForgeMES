<template>
  <CrudBoard
    eyebrow="系统管理"
    title="用户、角色、菜单与权限"
    description="维护登录用户、角色授权、菜单按钮权限、接口权限、数据范围和关键审计记录。"
    list-title="用户列表"
    :rows="rows"
    :columns="columns"
    row-key="account"
    :primary-actions="primaryActions"
    :row-actions="rowActions"
  />
  <p v-if="loading" class="api-state">Loading system users...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { getMenus, getPermissions, getRoles, getUsers } from '../../api/system'

const rows = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapUser = (row) => ({
  account: row.account || row.username || row.userName || row.user_name || row.loginName || row.login_name || row.id,
  name: row.name || row.displayName || row.display_name || row.realName || row.real_name || '-',
  roles: Array.isArray(row.roles) ? row.roles.join(', ') : row.roles || row.roleNames || row.role_names || '-',
  scope: row.scope || row.dataScope || row.data_scope || '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    const [users] = await Promise.all([
      getUsers(),
      getRoles().catch(() => []),
      getMenus().catch(() => []),
      getPermissions().catch(() => [])
    ])
    rows.value = recordsOf(users).map(mapUser)
  } catch (e) {
    rows.value = []
    error.value = e?.message || 'Data loading failed. Please check backend API or gateway configuration.'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'account', label: '账号' },
  { key: 'name', label: '姓名' },
  { key: 'roles', label: '角色' },
  { key: 'scope', label: '数据范围' },
  { key: 'status', label: '状态' }
]
const primaryActions = [
  { label: '新增用户', action: 'create' },
  { label: '角色授权', action: 'edit' },
  { label: '权限导出', action: 'export' }
]
const rowActions = [
  { label: '编辑', action: 'edit' },
  { label: '启停', action: 'close' },
  { label: '审计', action: 'audit' }
]

onMounted(loadRows)
</script>

<style scoped>
.api-state {
  margin: 12px 24px 0;
  color: #52616b;
  font-size: 14px;
}

.api-state.error {
  color: #b42318;
}
</style>
