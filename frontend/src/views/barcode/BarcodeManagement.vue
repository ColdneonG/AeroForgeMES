<template>
  <CrudBoard
    :eyebrow="t('barcode.rules.eyebrow')"
    :title="t('barcode.rules.title')"
    :description="t('barcode.rules.description')"
    :list-title="t('barcode.rules.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="code"
    :row-actions="rowActions"
  />
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getBarcodeRules } from '../../api/barcode'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapRule = (row) => ({
  code: row.ruleCode || row.rule_code || row.code || row.id,
  type: row.barcodeType || row.barcode_type || row.type || '-',
  pattern: row.pattern || row.rulePattern || row.rule_pattern || '-',
  template: row.templateName || row.template_name || row.template || '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    rows.value = recordsOf(await getBarcodeRules()).map(mapRule)
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'code', label: t('tableColumns.ruleCode') },
  { key: 'type', label: t('tableColumns.barcodeType') },
  { key: 'pattern', label: t('tableColumns.rule') },
  { key: 'template', label: t('tableColumns.template') },
  { key: 'status', label: t('tableColumns.status') }
]
const rowActions = [
  { label: t('common.actions.edit'), action: 'edit' },
  { label: t('tableColumns.print'), action: 'print' },
  { label: t('common.actions.close'), action: 'close' },
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
