<template>
  <CrudBoard
    :eyebrow="t('process.eyebrow')"
    :title="t('process.title')"
    :description="t('process.description')"
    :list-title="t('process.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="code"
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
import { getProcessRows } from '../../api/process'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapRow = (row) => ({
  code: row.processCode || row.process_code || row.routeCode || row.route_code || row.code || row.id,
  name: row.processName || row.process_name || row.routeName || row.route_name || row.name || '-',
  type: row.type || row.processType || row.process_type || '-',
  sop: row.sopName || row.sop_name || row.sopCode || row.sop_code || '-',
  defects: row.defects || row.defectNames || row.defect_names || '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    rows.value = recordsOf(await getProcessRows()).map(mapRow)
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'code', label: t('tableColumns.code') },
  { key: 'name', label: t('tableColumns.name') },
  { key: 'type', label: t('tableColumns.type') },
  { key: 'sop', label: t('tableColumns.sop') },
  { key: 'defects', label: t('tableColumns.defects') },
  { key: 'status', label: t('tableColumns.status') }
]
const rowActions = [
  { label: t('common.actions.edit'), action: 'edit' },
  { label: t('common.actions.close'), action: 'close' },
  { label: t('common.actions.audit'), action: 'audit' }
]

onMounted(loadRows)
</script>
