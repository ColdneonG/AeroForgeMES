<template>
  <CrudBoard
    eyebrow="计件工资"
    title="计件工资核算"
    description="按工序报工、良品数量、不良扣减和人员维度形成工资核算原型。"
    list-title="计件记录"
    :rows="rows"
    :columns="columns"
    row-key="id"
  />
  <p v-if="loading" class="api-state">Loading piecework wages...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { getPieceworkWages } from '../../api/wage'

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
    error.value = e?.message || 'Data loading failed. Please check backend API or gateway configuration.'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: '核算单' },
  { key: 'operator', label: '人员' },
  { key: 'process', label: '工序' },
  { key: 'good', label: '良品' },
  { key: 'bad', label: '不良' },
  { key: 'amount', label: '金额' },
  { key: 'status', label: '状态' }
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
