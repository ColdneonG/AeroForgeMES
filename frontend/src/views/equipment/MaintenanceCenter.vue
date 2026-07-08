<template>
  <CrudBoard
    eyebrow="设备管理"
    title="点检、保养、报修与维修"
    description="覆盖点检提交、保养执行、异常转报修、维修接单和维修完成。"
    list-title="维护任务"
    :rows="maintenanceRows"
    :columns="columns"
    row-key="id"
    flow-type="equipment"
    :row-actions="rowActions"
  />
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'
import { maintenanceRows } from '../../mock/mesExtendedData'

const columns = [
  { key: 'id', label: '任务号' },
  { key: 'equipment', label: '设备' },
  { key: 'type', label: '类型' },
  { key: 'owner', label: '处理人' },
  { key: 'due', label: '计划时间' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '接单', action: 'acceptRepair' },
  { label: '完成维修', action: 'finishRepair' },
  { label: '关闭', action: 'close' },
  { label: '审计', action: 'audit' }
]
</script>
