<template>
  <CrudBoard
    :eyebrow="eyebrow"
    :title="title"
    :description="description"
    :list-title="t('integration.center.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="integration"
    :row-actions="rowActions"
    :handle-actions-externally="true"
  />
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getSyncLogs } from '../../api/integration'

const { t } = useI18n()

defineProps({
  eyebrow: { type: String, default: 'Integration' },
  title: { type: String, default: 'ERP & Standard API' },
  description: { type: String, default: 'Demo data is disabled. Sync records are loaded from real APIs only.' }
})

const rows = ref([])
const loading = ref(false)
const error = ref('')
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapRow = (row) => ({
  id: row.syncNo || row.sync_no || row.requestNo || row.request_no || row.id,
  module: row.module || row.apiName || row.api_name || row.resource || '-',
  direction: row.direction || '-',
  externalNo: row.externalNo || row.external_no || row.bizNo || row.biz_no || '-',
  result: row.result || row.message || '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    rows.value = recordsOf(await getSyncLogs()).map(mapRow)
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: t('tableColumns.logNo') },
  { key: 'module', label: t('tableColumns.api') },
  { key: 'direction', label: t('tableColumns.direction') },
  { key: 'externalNo', label: t('tableColumns.externalId') },
  { key: 'result', label: t('tableColumns.result') },
  { key: 'status', label: t('tableColumns.status') }
]
const rowActions = [
  { label: t('statusFlow.actions.retry'), action: 'retry' },
  { label: t('statusFlow.actions.close'), action: 'close' },
  { label: t('statusFlow.actions.audit'), action: 'audit' }
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
