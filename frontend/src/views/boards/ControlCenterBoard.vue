<template>
  <section class="control-center-page">
    <div class="control-header">
      <div>
        <p>电子看板</p>
        <h1>中控看板</h1>
      </div>
      <strong>{{ now }}</strong>
    </div>

    <div class="control-kpi-strip">
      <article v-for="item in kpis" :key="item.label" :class="item.tone">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <p>{{ item.note }}</p>
      </article>
    </div>

    <div class="control-grid">
      <section class="control-panel production-radar">
        <div class="control-panel-title">实时产量</div>
        <div class="control-big-number">1,832</div>
        <div class="control-bars">
          <i v-for="value in trend" :key="value" :style="{ height: `${value}%` }"></i>
        </div>
      </section>

      <section class="control-panel">
        <div class="control-panel-title">产线运行状态</div>
        <div class="control-line-list">
          <article v-for="line in lines" :key="line.name">
            <span>{{ line.name }}</span>
            <b :class="line.tone">{{ line.status }}</b>
            <i><em :style="{ width: `${line.rate}%` }"></em></i>
          </article>
        </div>
      </section>

      <section class="control-panel alert-panel">
        <div class="control-panel-title">报警与异常</div>
        <article v-for="alert in alerts" :key="alert.code" :class="alert.tone">
          <strong>{{ alert.code }}</strong>
          <span>{{ alert.text }}</span>
        </article>
      </section>

      <section class="control-panel">
        <div class="control-panel-title">工单进度 / 库存预警</div>
        <div class="control-work-list">
          <article v-for="work in workOrders" :key="work.id">
            <span>{{ work.id }}</span>
            <strong>{{ work.product }}</strong>
            <i><em :style="{ width: `${work.rate}%` }"></em></i>
            <small>{{ work.warning }}</small>
          </article>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
const now = new Date().toLocaleString('zh-CN', { hour12: false })
const kpis = [
  { label: '实时产量', value: '1,832 台', note: '今日计划 2,160', tone: 'ok' },
  { label: '运行产线', value: '5/6', note: '1 条待料', tone: 'warn' },
  { label: '设备报警', value: '3 起', note: '温控/扫码/张力', tone: 'danger' },
  { label: '质量异常', value: '2 起', note: '电机异响重点跟踪', tone: 'warn' },
  { label: '库存预警', value: '4 项', note: '前网罩、温控模块', tone: 'danger' }
]
const trend = [48, 58, 66, 72, 68, 76, 84, 79, 88, 92, 86, 95]
const lines = [
  { name: '总装一线', status: '运行', rate: 78, tone: 'ok' },
  { name: '总装二线', status: '待料', rate: 67, tone: 'warn' },
  { name: '电机装配线', status: '运行', rate: 82, tone: 'ok' },
  { name: '老化测试区', status: '报警', rate: 70, tone: 'danger' },
  { name: '包装一线', status: '运行', rate: 83, tone: 'ok' }
]
const alerts = [
  { code: 'EQ-AGE-04', text: '老化测试台温控模块报警', tone: 'danger' },
  { code: 'QC-0707-02', text: '总装一线电机异响不良上升', tone: 'warn' },
  { code: 'MAT-0707-01', text: '前网罩库存低于安全线', tone: 'danger' }
]
const workOrders = [
  { id: 'WO20260706001', product: 'FS-500 落地扇', rate: 78, warning: '正常' },
  { id: 'WO20260706002', product: 'TF-230 台扇', rate: 67, warning: '前网罩短缺' },
  { id: 'WO20260706004', product: 'IF-750 工业风扇', rate: 70, warning: '设备报警影响 ETA' }
]
</script>
