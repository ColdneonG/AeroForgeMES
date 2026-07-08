<template>
  <CrudBoard
    eyebrow="条码应用"
    title="条码规则、模板与应用规则"
    description="维护产品码、箱码、栈板码、材料码规则，以及标签模板和应用范围。"
    list-title="条码规则"
    :rows="rows"
    :columns="columns"
    row-key="code"
    :row-actions="rowActions"
  />
  <p v-if="loading" class="api-state">Loading barcode rules...</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { getBarcodeRules } from '../../api/barcode'

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
    error.value = e?.message || 'Data loading failed. Please check backend API or gateway configuration.'
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'code', label: '规则编码' },
  { key: 'type', label: '条码类型' },
  { key: 'pattern', label: '规则' },
  { key: 'template', label: '模板' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '编辑', action: 'edit' },
  { label: '打印', action: 'print' },
  { label: '关闭', action: 'close' },
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
