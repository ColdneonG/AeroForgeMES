<template>
  <CrudBoard
    eyebrow="生产订单"
    title="齐套分析与缺料看板"
    description="按工单、产线、物料缺口和风险等级展示齐套状态，支持发布与冻结。"
    list-title="齐套记录"
    :rows="rows"
    :columns="columns"
    row-key="order"
    flow-type="workOrder"
    :row-actions="rowActions"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { kittingRows } from '../../mock/mesExtendedData'
import { mesApi } from '../../services/mesApi'

const useMock = import.meta.env.VITE_USE_MOCK === 'true'
const rows = ref(kittingRows)

const mapKitting = (row) => {
  const missing = row.missingCount ?? row.missing_count ?? 0
  return {
    order: row.analysisNo || row.analysis_no || row.id,
    product: row.productName || row.product_name || (row.workOrderId ? `WorkOrder ${row.workOrderId}` : '-'),
    line: row.lineName || row.line_name || '-',
    rate: row.kittingRate || row.kitting_rate || row.kittingStatus || row.kitting_status || '-',
    risk: row.risk || (Number(missing) > 0 ? '缺料风险' : '正常'),
    missing,
    status: row.status || '-',
    raw: row
  }
}

const loadRows = async () => {
  if (useMock) return
  rows.value = (await mesApi.production.listKittingAnalyses()).map(mapKitting)
}

const columns = [
  { key: 'order', label: '工单' },
  { key: 'product', label: '产品' },
  { key: 'line', label: '产线' },
  { key: 'rate', label: '齐套率' },
  { key: 'risk', label: '风险' },
  { key: 'missing', label: '缺料' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '发布', action: 'release' },
  { label: '关闭', action: 'close' },
  { label: '审计', action: 'audit' }
]

onMounted(loadRows)
</script>
