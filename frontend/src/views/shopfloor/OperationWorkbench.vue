<template>
  <section class="mes-workspace">
    <div class="mes-page-heading">
      <div>
        <p>现场管理</p>
        <h1>工序作业台</h1>
        <span>高密度工位操作界面，支持扫码、物料消耗、设备计数、良品/不良报工。</span>
      </div>
    </div>
    <div class="operation-grid">
      <article v-for="card in cards" :key="card.title" class="operation-card">
        <strong>{{ card.title }}</strong>
        <span>{{ card.value }}</span>
        <p>{{ card.note }}</p>
      </article>
    </div>
    <CrudBoard
      eyebrow="现场管理"
      title="报工与完工"
      description="普通报工、不良报工、检测报工、装箱和完工提交。"
      list-title="待处理工序"
      :rows="productionTasks"
      :columns="columns"
      row-key="id"
      flow-type="task"
      :row-actions="rowActions"
    />
  </section>
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'
import { productionTasks } from '../../mock/mesExtendedData'

const cards = [
  { title: '当前产品 SN', value: 'SN20260706000158', note: '扫码校验通过' },
  { title: '关键物料', value: '4/4', note: '电机、扇叶、网罩、纸箱已绑定' },
  { title: '设备计数', value: '326', note: '来自装配线 1#' },
  { title: 'SOP 确认', value: '已确认', note: '版本 SOP-FS500-V3.2' }
]
const columns = [
  { key: 'id', label: '任务' },
  { key: 'process', label: '工序' },
  { key: 'line', label: '产线' },
  { key: 'good', label: '良品' },
  { key: 'bad', label: '不良' },
  { key: 'status', label: '状态' }
]
const rowActions = [
  { label: '扫码', action: 'report' },
  { label: '报工', action: 'report' },
  { label: '完工', action: 'complete' },
  { label: '安灯', action: 'andon' }
]
</script>
