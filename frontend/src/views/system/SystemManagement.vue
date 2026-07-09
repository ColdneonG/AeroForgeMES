<template>
  <CrudBoard
    :eyebrow="t('system.eyebrow')"
    :title="t('system.title')"
    :description="t('system.description')"
    :list-title="t('system.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="account"
    :primary-actions="primaryActions"
    :row-actions="rowActions"
  />
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getMenus, getPermissions, getRoles, getUsers } from '../../api/system'

const { t } = useI18n()
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
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'account', label: t('tableColumns.account') },
  { key: 'name', label: t('tableColumns.name') },
  { key: 'roles', label: t('tableColumns.roles') },
  { key: 'scope', label: t('tableColumns.dataScope') },
  { key: 'status', label: t('tableColumns.status') }
]
const primaryActions = [
  { label: t('common.actions.create'), action: 'create' },
  { label: t('common.actions.edit'), action: 'edit' },
  { label: t('common.actions.export'), action: 'export' }
]
const rowActions = [
  { label: t('common.actions.edit'), action: 'edit' },
  { label: t('common.actions.close'), action: 'close' },
  { label: t('common.actions.audit'), action: 'audit' }
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
