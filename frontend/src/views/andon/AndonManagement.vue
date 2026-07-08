<template>
  <CrudBoard
    :eyebrow="t('andon.eyebrow')"
    :title="t('andon.title')"
    :description="t('andon.description')"
    :list-title="t('andon.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="andon"
    :primary-actions="primaryActions"
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
import { getAndonExceptions } from '../../api/andon'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapRow = (row) => ({
  id: row.exceptionNo || row.exception_no || row.andonNo || row.andon_no || row.id,
  type: row.typeName || row.type_name || row.type || '-',
  line: row.lineName || row.line_name || row.lineId || row.line_id || '-',
  reason: row.reasonName || row.reason_name || row.reason || row.description || '-',
  owner: row.ownerName || row.owner_name || row.handlerId || row.handler_id || '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    rows.value = recordsOf(await getAndonExceptions()).map(mapRow)
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: 'Exception#' },
  { key: 'type', label: 'Type' },
  { key: 'line', label: 'Line' },
  { key: 'reason', label: 'Reason' },
  { key: 'owner', label: 'Handler' },
  { key: 'status', label: 'Status' }
]
const primaryActions = [
  { label: t('common.actions.create'), action: 'create' },
  { label: t('common.actions.edit'), action: 'edit' },
  { label: t('common.actions.export'), action: 'export' }
]
const rowActions = [
  { label: t('statusFlow.actions.accept'), action: 'accept' },
  { label: t('statusFlow.actions.handle'), action: 'handle' },
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
</style>
