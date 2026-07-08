<template>
  <section class="mes-workspace">
    <div class="mes-page-heading">
      <div>
        <p>现场管理</p>
        <h1>电子 SOP</h1>
        <span>按任务和工位自动切换 SOP，支持图片、视频、文件与阅读确认提交。</span>
      </div>
      <div class="mes-toolbar">
        <button class="mes-action-button" @click="confirmed = true">阅读确认</button>
      </div>
    </div>
    <div class="sop-layout">
      <section class="mes-panel sop-preview">
        <div class="mes-panel-title">
          <strong>FS-500 总装 SOP</strong>
          <span>V3.2</span>
        </div>
        <div class="sop-canvas">
          <h2>前网罩安装与扭矩采集</h2>
          <p>1. 确认卡扣方向；2. 按对角顺序锁紧；3. 扭矩目标 1.8 N.m；4. 扫码绑定成品 SN。</p>
        </div>
      </section>
      <section class="mes-panel">
        <div class="mes-panel-title">
          <strong>提交记录</strong>
          <span>{{ confirmed ? '已确认' : '待确认' }}</span>
        </div>
        <ul class="simple-list">
          <li>任务：TASK-0707-001</li>
          <li>工位：A-01</li>
          <li>操作员：张工</li>
          <li>确认状态：{{ confirmed ? '已提交阅读确认' : '等待操作员确认' }}</li>
        </ul>
      </section>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'

const confirmed = ref(false)
</script>
