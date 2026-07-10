<template>
  <CrudBoard
    :eyebrow="eyebrow"
    :title="title"
    :description="description"
    :list-title="listTitle"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="inspectionOrder"
    :primary-actions="primaryActions"
    :row-actions="rowActions"
    :handle-actions-externally="true"
    @row-action="handleRowAction"
    @primary-action="handlePrimaryAction"
  />
  <p v-if="loading" class="api-state">{{ t('quality.loading') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import {
  getQualityInspections,
  startInspection,
  passInspection,
  failInspection,
  closeInspection,
  voidInspection
} from '../../api/quality'
import { authState } from '../../stores/auth'

const { t } = useI18n()

const props = defineProps({
  eyebrow: { type: String, default: '' },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  listTitle: { type: String, default: '' },
  inspectionType: { type: String, default: '' }
})

const rows = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapInspectionOrder = (row) => ({
  id: row.inspectionNo || row.inspection_no || row.id,
  apiId: row.id,
  orderNo: row.workOrderNo || row.work_order_no || '-',
  product: row.productName || row.product_name || (row.productId ? `${t('tableColumns.product')} ${row.productId}` : '-'),
  type: row.inspectionType || row.inspection_type || '-',
  status: row.status || '-',
  inspector: row.inspectorName || row.inspector_name || '-',
  result: row.finalResult || row.final_result || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    const types = props.inspectionType
      ? props.inspectionType.split(',').map((t) => t.trim()).filter(Boolean)
      : []
    let allData = []
    if (types.length > 0) {
      const results = await Promise.all(
        types.map((type) => getQualityInspections({ inspectionType: type }))
      )
      allData = results.flatMap((resp) => recordsOf(resp))
    } else {
      allData = recordsOf(await getQualityInspections())
    }
    rows.value = allData.map(mapInspectionOrder)
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('quality.apiError')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: t('tableColumns.id') },
  { key: 'orderNo', label: t('tableColumns.workOrderNo') },
  { key: 'product', label: t('tableColumns.product') },
  { key: 'type', label: t('quality.type') },
  { key: 'status', label: t('tableColumns.status') },
  { key: 'inspector', label: t('tableColumns.owner') }
]

const primaryActions = [
  { label: t('common.actions.create'), action: 'create' },
  { label: t('common.actions.import'), action: 'import' },
  { label: t('common.actions.export'), action: 'export' }
]

const rowActions = [
  { label: t('quality.actions.start'), action: 'start' },
  { label: t('quality.actions.pass'), action: 'pass' },
  { label: t('quality.actions.fail'), action: 'fail' },
  { label: t('common.actions.close'), action: 'close' },
  { label: t('common.actions.void'), action: 'void' },
  { label: t('common.actions.audit'), action: 'audit' }
]

const getApiId = (row) => row?.apiId || row?.raw?.id
const actionPayload = (remark) => ({
  operatorId: authState.user?.userId || authState.user?.id || null,
  remark
})

const rowActionHandlers = {
  start: (row) => startInspection(getApiId(row), actionPayload('frontend inspection start')),
  pass: (row) => passInspection(getApiId(row), actionPayload('frontend inspection pass')),
  fail: (row) => failInspection(getApiId(row), actionPayload('frontend inspection fail')),
  close: (row) => closeInspection(getApiId(row), actionPayload('frontend inspection close')),
  void: (row) => voidInspection(getApiId(row), actionPayload('frontend inspection void'))
}

const handleRowAction = async ({ action, row }) => {
  const handler = rowActionHandlers[action]
  if (!handler) return

  loading.value = true
  error.value = ''

  try {
    await handler(row)
    await loadRows()
  } catch (e) {
    error.value = e?.message || t('quality.apiError')
  } finally {
    loading.value = false
  }
}

const handlePrimaryAction = ({ action }) => {
  if (['create', 'import', 'export'].includes(action)) {
    error.value = t('common.error.notImplemented')
  }
}

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
