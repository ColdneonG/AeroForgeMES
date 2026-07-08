<template>
  <section class="mes-workspace">
    <div class="mes-page-heading">
      <div>
        <p>现场管理</p>
        <h1>电子 SOP</h1>
        <span>演示数据已关闭，等待真实任务与 SOP 接口接入。</span>
      </div>
      <div class="mes-toolbar">
        <button class="mes-action-button" @click="confirmed = true">阅读确认</button>
      </div>
    </div>
    <div class="sop-layout">
      <section class="mes-panel sop-preview">
        <div class="mes-panel-title">
          <strong>暂无 SOP 数据</strong>
          <span>disabled</span>
        </div>
        <div class="sop-canvas">
          <h2>电子 SOP 接口暂未接入</h2>
          <p>演示数据已关闭，请接入真实任务与 SOP 接口后展示作业指导内容。</p>
        </div>
      </section>
      <section class="mes-panel">
        <div class="mes-panel-title">
          <strong>提交记录</strong>
          <span>{{ confirmed ? '已确认' : '待确认' }}</span>
        </div>
        <ul class="simple-list">
          <li>任务：-</li>
          <li>工位：-</li>
          <li>操作员：-</li>
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
