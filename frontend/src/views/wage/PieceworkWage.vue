<template>
  <CrudBoard
    eyebrow="计件工资"
    title="计件工资核算"
    description="按工序报工、良品数量、不良扣减和人员维度形成工资核算原型。"
    list-title="计件记录"
    :rows="rows"
    :columns="columns"
    row-key="id"
  />
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'

const rows = [
  { id: 'WAGE-0707-001', operator: '张工', process: '总装', good: 326, bad: 3, amount: '489.00', status: '待审核' },
  { id: 'WAGE-0707-002', operator: '王工', process: '扇叶装配', good: 188, bad: 5, amount: '244.40', status: '待审核' },
  { id: 'WAGE-0707-003', operator: '赵工', process: '包装', good: 246, bad: 1, amount: '196.80', status: '已完成' }
]
const columns = [
  { key: 'id', label: '核算单' },
  { key: 'operator', label: '人员' },
  { key: 'process', label: '工序' },
  { key: 'good', label: '良品' },
  { key: 'bad', label: '不良' },
  { key: 'amount', label: '金额' },
  { key: 'status', label: '状态' }
]
</script>
