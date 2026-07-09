<template>
  <CrudBoard
    :eyebrow="t('barcode.generate.eyebrow')"
    :title="t('barcode.generate.title')"
    :description="t('barcode.generate.description')"
    :list-title="t('barcode.generate.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="id"
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
import { getBarcodeRecords } from '../../api/barcode'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapRow = (row) => ({
  id: row.jobNo || row.job_no || row.barcode || row.id,
  rule: row.ruleCode || row.rule_code || row.ruleId || row.rule_id || '-',
  product: row.productName || row.product_name || row.productId || row.product_id || '-',
  qty: row.qty ?? row.quantity ?? '-',
  printed: row.printedQty ?? row.printed_qty ?? '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    rows.value = recordsOf(await getBarcodeRecords()).map(mapRow)
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: t('tableColumns.taskNo') },
  { key: 'rule', label: t('tableColumns.rule') },
  { key: 'product', label: t('tableColumns.product') },
  { key: 'qty', label: t('tableColumns.qty') },
  { key: 'printed', label: t('tableColumns.printed') },
  { key: 'status', label: t('tableColumns.status') }
]
const rowActions = [
  { label: t('tableColumns.generate'), action: 'create' },
  { label: t('tableColumns.print'), action: 'print' },
  { label: t('tableColumns.reprint'), action: 'print' },
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
