<template>
  <section class="board-page line-board-page">
    <div class="board-header">
      <div>
        <p>电子看板</p>
        <h1>产线看板</h1>
        <span>按产线展示当前工单、产品、计划与完成进度、设备状态、异常状态和预计完成时间。</span>
      </div>
      <div class="board-time">实时刷新 · {{ now }}</div>
    </div>

    <div class="line-board-grid">
      <article v-for="line in lines" :key="line.name" class="line-board-card" :class="line.tone">
        <div class="line-card-head">
          <div>
            <strong>{{ line.name }}</strong>
            <span>{{ line.product }}</span>
          </div>
          <i>{{ line.status }}</i>
        </div>
        <div class="line-card-order">
          <span>当前工单</span>
          <strong>{{ line.order }}</strong>
        </div>
        <div class="line-card-metrics">
          <div>
            <span>计划</span>
            <strong>{{ line.plan }}</strong>
          </div>
          <div>
            <span>完成</span>
            <strong>{{ line.done }}</strong>
          </div>
          <div>
            <span>完成率</span>
            <strong>{{ line.rate }}%</strong>
          </div>
        </div>
        <div class="line-progress"><span :style="{ width: `${line.rate}%` }"></span></div>
        <div class="line-card-foot">
          <span>设备：{{ line.equipment }}</span>
          <span>异常：{{ line.exception }}</span>
          <span>预计：{{ line.eta }}</span>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
const now = new Date().toLocaleString('zh-CN', { hour12: false })

const lines = [
  { name: '总装一线', product: 'FS-500 落地扇', order: 'WO20260706001', plan: 420, done: 326, rate: 78, status: '运行', equipment: '正常', exception: '无', eta: '18:20', tone: 'running' },
  { name: '总装二线', product: 'TF-230 台扇', order: 'WO20260706002', plan: 280, done: 188, rate: 67, status: '待机', equipment: '等料', exception: '前网罩短缺', eta: '20:10', tone: 'warn' },
  { name: '电机装配线', product: 'MTR-65 电机', order: 'WO20260706003', plan: 760, done: 620, rate: 82, status: '运行', equipment: '正常', exception: '无', eta: '17:45', tone: 'running' },
  { name: '老化测试区', product: 'IF-750 工业风扇', order: 'WO20260706004', plan: 220, done: 154, rate: 70, status: '异常', equipment: '温控报警', exception: '设备安灯', eta: '待评估', tone: 'danger' },
  { name: '包装一线', product: 'FS-500 包装', order: 'WO20260706005', plan: 360, done: 298, rate: 83, status: '运行', equipment: '正常', exception: '无', eta: '17:20', tone: 'running' },
  { name: '包装二线', product: 'TF-230 包装', order: 'WO20260706006', plan: 300, done: 246, rate: 82, status: '运行', equipment: '扫码枪维护', exception: '扫码失败率偏高', eta: '18:00', tone: 'warn' }
]
</script>
