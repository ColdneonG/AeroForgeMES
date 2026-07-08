<template>
  <section class="board-page workshop-board-page">
    <div class="board-header">
      <div>
        <p>电子看板</p>
        <h1>车间看板</h1>
        <span>按车间区域展示注塑、电机装配、扇叶装配、总装、质检和包装区域状态。</span>
      </div>
      <div class="board-time">车间总览 · {{ activeAreas }}/{{ areas.length }} 区域正常</div>
    </div>

    <div class="workshop-layout">
      <article v-for="area in areas" :key="area.name" class="workshop-area-card" :class="area.tone">
        <div class="area-map-cell">{{ area.short }}</div>
        <div class="area-content">
          <div class="area-title">
            <strong>{{ area.name }}</strong>
            <span>{{ area.status }}</span>
          </div>
          <p>{{ area.description }}</p>
          <div class="area-stats">
            <span>工单 {{ area.orders }}</span>
            <span>设备 {{ area.equipment }}</span>
            <span>异常 {{ area.exceptions }}</span>
          </div>
        </div>
      </article>
    </div>

    <div class="workshop-summary-row">
      <article>
        <span>车间产出</span>
        <strong>1,832 台</strong>
        <p>较计划 +6.2%</p>
      </article>
      <article>
        <span>区域 OEE</span>
        <strong>84.7%</strong>
        <p>老化区拉低 3.1%</p>
      </article>
      <article>
        <span>质量合格率</span>
        <strong>98.1%</strong>
        <p>重点关注电机异响</p>
      </article>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'

const areas = [
  { short: '注塑', name: '注塑区', status: '运行', description: '后壳与网罩注塑稳定供给。', orders: 3, equipment: '8/8', exceptions: 0, tone: 'running' },
  { short: '电机', name: '电机装配区', status: '运行', description: '绕线与电机装配节拍正常。', orders: 4, equipment: '7/8', exceptions: 1, tone: 'warn' },
  { short: '扇叶', name: '扇叶装配区', status: '待料', description: '前网罩短缺影响总装二线。', orders: 2, equipment: '5/5', exceptions: 1, tone: 'warn' },
  { short: '总装', name: '总装区', status: '运行', description: '总装一线满负荷，二线等待补料。', orders: 5, equipment: '10/11', exceptions: 1, tone: 'running' },
  { short: '质检', name: '质检区', status: '运行', description: '首末件与巡检按计划执行。', orders: 6, equipment: '4/4', exceptions: 0, tone: 'running' },
  { short: '包装', name: '包装区', status: '异常', description: '扫码失败率偏高，已通知维护。', orders: 3, equipment: '5/6', exceptions: 2, tone: 'danger' }
]

const activeAreas = computed(() => areas.filter((area) => area.tone === 'running').length)
</script>
