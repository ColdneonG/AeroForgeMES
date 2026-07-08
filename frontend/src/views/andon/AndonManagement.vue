<template>
  <CrudBoard
    eyebrow="安灯管理"
    title="异常安灯闭环"
    description="维护安灯类型、异常配置、异常原因，支持异常发起、通知、处理和关闭。"
    list-title="安灯异常"
    :rows="andonRows"
    :columns="columns"
    row-key="id"
    flow-type="andon"
    :primary-actions="primaryActions"
    :row-actions="rowActions"
  />
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'
import { andonRows } from '../../mock/mesExtendedData'

const columns = [
  { key: 'id', label: '异常单' },
  { key: 'type', label: '类型' },
  { key: 'line', label: '产线' },
  { key: 'reason', label: '原因' },
  { key: 'owner', label: '处理人' },
  { key: 'status', label: '状态' }
]
const primaryActions = [
  { label: '发起安灯', action: 'create' },
  { label: '异常配置', action: 'edit' },
  { label: '导出', action: 'export' }
]
const rowActions = [
  { label: '接收', action: 'accept' },
  { label: '处理', action: 'handle' },
  { label: '关闭', action: 'close' },
  { label: '审计', action: 'audit' }
]
</script>
