<template>
  <CrudBoard
    :eyebrow="eyebrow"
    :title="title"
    :description="description"
    list-title="接口同步记录"
    :rows="integrationRows"
    :columns="columns"
    row-key="id"
    flow-type="integration"
    :row-actions="rowActions"
  />
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'
import { integrationRows } from '../../mock/mesExtendedData'

defineProps({
  eyebrow: { type: String, default: '集成接口' },
  title: { type: String, default: 'ERP 与标准 API 接口' },
  description: { type: String, default: '生产任务读取、工艺数据读取、标准 API 写入、设备计数上报和失败重试。' }
})

const columns = [
  { key: 'id', label: '日志号' },
  { key: 'module', label: '接口' },
  { key: 'direction', label: '方向' },
  { key: 'externalNo', label: '外部单号' },
  { key: 'result', label: '结果' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '重试', action: 'retry' },
  { label: '关闭', action: 'close' },
  { label: '审计', action: 'audit' }
]
</script>
