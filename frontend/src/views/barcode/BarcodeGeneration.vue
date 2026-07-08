<template>
  <CrudBoard
    eyebrow="条码应用"
    title="条码生成与打印"
    description="按规则生成、外部导入、批量打印和补打记录追踪。"
    list-title="生成任务"
    :rows="barcodeJobs"
    :columns="columns"
    row-key="id"
    :row-actions="rowActions"
  />
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'
import { barcodeJobs } from '../../mock/mesExtendedData'

const columns = [
  { key: 'id', label: '任务号' },
  { key: 'rule', label: '规则' },
  { key: 'product', label: '产品' },
  { key: 'qty', label: '数量' },
  { key: 'printed', label: '已打印' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '生成', action: 'create' },
  { label: '打印', action: 'print' },
  { label: '补打', action: 'print' },
  { label: '审计', action: 'audit' }
]
</script>
