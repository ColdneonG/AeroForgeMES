<template>
  <CrudBoard
    :eyebrow="t('wage.eyebrow')"
    :title="t('wage.title')"
    :description="t('wage.description')"
    :list-title="t('wage.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="id"
  />
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getPieceworkWages } from '../../api/wage'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapWage = (row) => ({
  id: row.wageNo || row.wage_no || row.settlementNo || row.settlement_no || row.id,
  operator: row.operatorName || row.operator_name || row.operator || row.userId || row.user_id || '-',
  process: row.processName || row.process_name || row.process || '-',
  good: row.goodQty ?? row.good_qty ?? row.qualifiedQty ?? row.qualified_qty ?? '-',
  bad: row.badQty ?? row.bad_qty ?? row.defectiveQty ?? row.defective_qty ?? '-',
  amount: row.amount ?? row.totalAmount ?? row.total_amount ?? '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    rows.value = recordsOf(await getPieceworkWages()).map(mapWage)
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: t('tableColumns.settlementNo') },
  { key: 'operator', label: t('tableColumns.operator') },
  { key: 'process', label: t('tableColumns.process') },
  { key: 'good', label: t('tableColumns.good') },
  { key: 'bad', label: t('tableColumns.bad') },
  { key: 'amount', label: t('tableColumns.amount') },
  { key: 'status', label: t('tableColumns.status') }
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
