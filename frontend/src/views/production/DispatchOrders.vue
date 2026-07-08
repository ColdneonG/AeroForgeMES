<template>
  <CrudBoard
    eyebrow="生产订单"
    title="生产派工单"
    description="基于交期、产线、工位、人员与产能建议生成派工单并下发现场。"
    list-title="派工建议"
    :rows="rows"
    :columns="columns"
    row-key="id"
    flow-type="workOrder"
    :row-actions="rowActions"
  />
</template>

<script setup>
import { onMounted, ref } from 'vue'
import CrudBoard from '../../components/CrudBoard.vue'
import { dispatchRows } from '../../mock/mesExtendedData'
import { mesApi } from '../../services/mesApi'

const useMock = import.meta.env.VITE_USE_MOCK !== 'false'
const rows = ref(dispatchRows)

const mapDispatch = (row) => ({
  id: row.dispatchNo || row.dispatch_no || row.id,
  order: row.workOrderNo || row.work_order_no || row.workOrderId || row.work_order_id || '-',
  process: row.processName || row.process_name || row.routeId || row.route_id || '-',
  station: row.stationName || row.station_name || row.stationId || row.station_id || '-',
  operator: row.operatorName || row.operator_name || row.teamId || row.team_id || '-',
  suggestion: row.suggestion || row.planQty || row.plan_qty || '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  if (useMock) return
  rows.value = (await mesApi.production.listDispatchOrders()).map(mapDispatch)
}

const columns = [
  { key: 'id', label: '派工单' },
  { key: 'order', label: '工单' },
  { key: 'process', label: '工序' },
  { key: 'station', label: '工位' },
  { key: 'operator', label: '操作员' },
  { key: 'suggestion', label: '建议' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '编辑', action: 'edit' },
  { label: '下发', action: 'release' },
  { label: '关闭', action: 'close' },
  { label: '审计', action: 'audit' }
]

onMounted(loadRows)
</script>
