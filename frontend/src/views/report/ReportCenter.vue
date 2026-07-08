<template>
  <CrudBoard
    eyebrow="报表分析"
    title="生产、质量、物料与车间报表"
    description="按产量、不良、关键物料追溯和车间时段查询统计数据，支撑管理分析。"
    list-title="报表目录"
    :rows="reportRows"
    :columns="columns"
    row-key="report"
    :row-actions="rowActions"
  />
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'
import { reportRows } from '../../mock/mesExtendedData'

const columns = [
  { key: 'report', label: '报表' },
  { key: 'dimension', label: '维度' },
  { key: 'api', label: '接口路径' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '查询', action: 'audit' },
  { label: '导出', action: 'export' },
  { label: '审计', action: 'audit' }
]
</script>
