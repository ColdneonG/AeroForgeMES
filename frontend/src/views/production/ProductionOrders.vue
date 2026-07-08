<template>
  <CrudBoard
    eyebrow="生产订单"
    title="生产工单闭环"
    description="覆盖工单新增、编辑、导入同步、下发、关闭、作废、详情与状态日志。"
    list-title="生产工单"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="workOrder"
    :primary-actions="primaryActions"
    :row-actions="rowActions"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { workOrders } from '../../mock/mesData'
import { mesApi } from '../../services/mesApi'

const useMock = import.meta.env.VITE_USE_MOCK !== 'false'

const mockRows = workOrders.map((row) => ({
  ...row,
  status: row.status === '待开始' ? '待下发' : row.status === '进行中' ? '生产中' : row.status
}))

const rows = ref(mockRows)

const displayQty = (value) => (value === null || value === undefined ? '-' : value)

const mapWorkOrder = (row) => ({
  id: row.workOrderNo || row.work_order_no || row.id,
  product: row.productName || row.product_name || (row.productId ? `Product ${row.productId}` : '-'),
  line: row.lineName || row.line_name || (row.lineId ? `Line ${row.lineId}` : '-'),
  plan: displayQty(row.planQty ?? row.plan_qty),
  done: displayQty(row.completedQty ?? row.completed_qty),
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  if (useMock) return
  rows.value = (await mesApi.production.listWorkOrders()).map(mapWorkOrder)
}

const columns = [
  { key: 'id', label: '工单号' },
  { key: 'product', label: '产品' },
  { key: 'line', label: '产线' },
  { key: 'plan', label: '计划' },
  { key: 'done', label: '完成' },
  { key: 'status', label: '状态' }
]
const primaryActions = [
  { label: '新增', action: 'create' },
  { label: '导入/同步', action: 'import' },
  { label: '导出', action: 'export' }
]
const rowActions = [
  { label: '编辑', action: 'edit' },
  { label: '下发', action: 'release' },
  { label: '派工', action: 'dispatch' },
  { label: '关闭', action: 'close' },
  { label: '作废', action: 'void' },
  { label: '审计', action: 'audit' }
]

onMounted(loadRows)
</script>
