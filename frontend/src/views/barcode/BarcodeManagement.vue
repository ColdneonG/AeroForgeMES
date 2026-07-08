<template>
  <CrudBoard
    eyebrow="条码应用"
    title="条码规则、模板与应用规则"
    description="维护产品码、箱码、栈板码、材料码规则，以及标签模板和应用范围。"
    list-title="条码规则"
    :rows="barcodeRules"
    :columns="columns"
    row-key="code"
    :row-actions="rowActions"
  />
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'
import { barcodeRules } from '../../mock/mesExtendedData'

const columns = [
  { key: 'code', label: '规则编码' },
  { key: 'type', label: '条码类型' },
  { key: 'pattern', label: '规则' },
  { key: 'template', label: '模板' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '编辑', action: 'edit' },
  { label: '打印', action: 'print' },
  { label: '关闭', action: 'close' },
  { label: '审计', action: 'audit' }
]
</script>
