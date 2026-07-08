<template>
  <CrudBoard
    eyebrow="工艺管理"
    title="工序、工艺路线与电子 SOP"
    description="维护工序类型、路线组合、SOP 文件和不良原因绑定，为派工、报工和质检提供依据。"
    list-title="工艺资料"
    :rows="processRows"
    :columns="columns"
    row-key="code"
    :row-actions="rowActions"
  />
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'
import { processRows } from '../../mock/mesExtendedData'

const columns = [
  { key: 'code', label: '编码' },
  { key: 'name', label: '名称' },
  { key: 'type', label: '类型' },
  { key: 'sop', label: 'SOP' },
  { key: 'defects', label: '不良原因' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '编辑', action: 'edit' },
  { label: '启用/停用', action: 'close' },
  { label: '审计', action: 'audit' }
]
</script>
