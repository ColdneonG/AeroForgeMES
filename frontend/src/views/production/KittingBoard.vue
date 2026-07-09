<template>
  <CrudBoard
    :eyebrow="t('production.kitting.eyebrow')"
    :title="t('production.kitting.title')"
    :description="t('production.kitting.description')"
    :list-title="t('production.kitting.listTitle')"
    :rows="rows"
    :columns="columns"
    row-key="order"
    flow-type="workOrder"
    :row-actions="rowActions"
    @select="onSelectRow"
  >
    <template #detail="{ selected, columns: detailColumns }">
      <div v-if="selected">
        <dl class="detail-list">
          <template v-for="column in detailColumns" :key="column.key">
            <dt>{{ column.label }}</dt>
            <dd>{{ selected[column.key] }}</dd>
          </template>
        </dl>
        <div v-if="missingItems.length" class="missing-section">
          <strong class="missing-title">{{ t('production.kitting.shortageDetail') }}</strong>
          <table class="mes-table">
            <thead>
              <tr>
                <th>{{ t('production.kitting.material') }}</th>
                <th>{{ t('production.kitting.requiredQty') }}</th>
                <th>{{ t('production.kitting.availableQty') }}</th>
                <th>{{ t('production.kitting.missingQty') }}</th>
                <th>{{ t('production.kitting.expectedArrival') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in missingItems" :key="item.id || item.materialId">
                <td>{{ item.materialName || item.material_name || item.materialId || item.material_id || '-' }}</td>
                <td>{{ item.requiredQty ?? item.required_qty ?? '-' }}</td>
                <td>{{ item.availableQty ?? item.available_qty ?? '-' }}</td>
                <td>{{ item.missingQty ?? item.missing_qty ?? '-' }}</td>
                <td>{{ formatArrival(item.expectedArrivalAt ?? item.expected_arrival_at) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else-if="missingLoading" class="missing-loading">{{ t('common.loading.generic') }}</div>
      </div>
      <div v-else class="empty-detail">{{ t('common.detail.emptyHint') }}</div>
    </template>
  </CrudBoard>
  <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
  <p v-if="error" class="api-state error">{{ error }}</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getKittingBoard, getKittingMissingItems } from '../../api/production'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')
const missingItems = ref([])
const missingLoading = ref(false)

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapKitting = (row) => {
  const missing = row.missingCount ?? row.missing_count ?? 0
  return {
    order: row.analysisNo || row.analysis_no || row.id,
    product: row.productName || row.product_name || row.workOrderName || row.work_order_name || (row.workOrderId ? `WorkOrder ${row.workOrderId}` : '-'),
    line: row.lineName || row.line_name || '-',
    rate: row.kittingRate || row.kitting_rate || row.kittingStatus || row.kitting_status || '-',
    risk: row.risk || (Number(missing) > 0 ? t('production.kitting.shortageRisk') : t('production.kitting.normal')),
    missing,
    status: row.status || '-',
    raw: row
  }
}

const formatArrival = (val) => {
  if (!val) return '-'
  if (typeof val === 'string') return val
  try {
    return new Date(val).toLocaleString('zh-CN', { hour12: false })
  } catch {
    return val
  }
}

const loadRows = async () => {
  loading.value = true
  error.value = ''

  try {
    rows.value = recordsOf(await getKittingBoard()).map(mapKitting)
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const onSelectRow = async (row) => {
  missingItems.value = []
  if (!row?.raw?.id) return

  missingLoading.value = true
  try {
    missingItems.value = recordsOf(await getKittingMissingItems({ analysisId: row.raw.id }))
  } catch {
    missingItems.value = []
  } finally {
    missingLoading.value = false
  }
}

const columns = [
  { key: 'order', label: t('production.kitting.order') },
  { key: 'product', label: t('production.kitting.product') },
  { key: 'line', label: t('production.kitting.line') },
  { key: 'rate', label: t('production.kitting.kittingRate') },
  { key: 'risk', label: t('production.kitting.risk') },
  { key: 'missing', label: t('production.kitting.shortage') },
  { key: 'status', label: t('production.kitting.status') }
]
const rowActions = [
  { label: t('production.kitting.publish'), action: 'release' },
  { label: t('production.kitting.close'), action: 'close' },
  { label: t('production.kitting.audit'), action: 'audit' }
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

.missing-section {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
}

.missing-title {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  color: #1a202c;
}

.missing-section .mes-table {
  width: 100%;
  font-size: 12px;
}

.missing-section .mes-table th,
.missing-section .mes-table td {
  padding: 4px 6px;
}

.missing-loading {
  margin-top: 12px;
  color: #a0aec0;
  font-size: 13px;
}

.empty-detail {
  color: #a0aec0;
  font-size: 13px;
  padding-top: 12px;
}
</style>
