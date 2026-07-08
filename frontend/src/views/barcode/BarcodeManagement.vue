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
