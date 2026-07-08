<template>
  <CrudBoard
    eyebrow="现场管理"
    title="生产任务单与现场执行"
    description="覆盖开工、暂停、恢复、报工、完工、返修和安灯发起。"
    list-title="现场任务"
    :rows="productionTasks"
    :columns="columns"
    row-key="id"
    flow-type="task"
    :row-actions="rowActions"
  />
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'
import { productionTasks } from '../../mock/mesExtendedData'

const columns = [
  { key: 'id', label: '任务单' },
  { key: 'order', label: '工单' },
  { key: 'product', label: '产品' },
  { key: 'process', label: '工序' },
  { key: 'line', label: '产线' },
  { key: 'good', label: '良品' },
  { key: 'bad', label: '不良' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '开工', action: 'start' },
  { label: '暂停', action: 'pause' },
  { label: '恢复', action: 'resume' },
  { label: '报工', action: 'report' },
  { label: '完工', action: 'complete' },
  { label: '返修', action: 'repair' }
]
</script>
