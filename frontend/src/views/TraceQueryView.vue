<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { traceVisible } = usePageInteractions()
</script>

<template>
<MesLayout active="trace-query">
  
  <header class="app-header">
    <div class="header-breadcrumb"><span>条码与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">追溯查询</span></div>
    <div class="header-actions"><span class="user-avatar">张</span></div>
  </header>
  <main class="app-main" data-od-id="trace-main">
    <h1 class="page-title">追溯查询</h1>
    <div class="card mb-5" style="max-width:700px;">
      <div class="card-header"><h2 class="card-title">查询条件</h2></div>
      <div class="search-bar" style="margin-bottom:0;flex-wrap:wrap;">
        <div class="form-group" style="min-width:200px;"><label class="form-label">条码值</label><input class="form-input" type="text" placeholder="扫描或输入条码..." id="traceBarcode" /></div>
        <div class="form-group" style="min-width:160px;"><label class="form-label">工单号</label><input class="form-input" type="text" placeholder="如 MO-20260711-0032" /></div>
        <div class="form-group" style="min-width:160px;"><label class="form-label">批次号</label><input class="form-input" type="text" placeholder="如 BAT-0711A" /></div>
        <button class="btn btn-primary btn-sm" style="align-self:flex-end;" @click="traceVisible = true">查询追溯</button>
      </div>
    </div>
    <div id="traceResult" v-show="traceVisible">
      <h2 class="section-title">追溯链路 — 正向追溯</h2>
      <div class="card mb-5" data-od-id="trace-chain">
        <div class="timeline">
          <div class="timeline-step done"><span class="step-dot">1</span><span class="step-label">来料入库</span><span class="text-muted">07-10 14:00</span></div>
          <div class="timeline-step done"><span class="step-dot">2</span><span class="step-label">绕线</span><span class="text-muted">07-11 08:15</span></div>
          <div class="timeline-step done"><span class="step-dot">3</span><span class="step-label">压装</span><span class="text-muted">07-11 08:45</span></div>
          <div class="timeline-step active"><span class="step-dot">4</span><span class="step-label">总装</span><span class="text-muted">09:15</span></div>
          <div class="timeline-step"><span class="step-dot">5</span><span class="step-label">质检</span><span class="text-muted">—</span></div>
          <div class="timeline-step"><span class="step-dot">6</span><span class="step-label">包装入库</span><span class="text-muted">—</span></div>
        </div>
      </div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>事件时间</th><th>事件类型</th><th>工序/工位</th><th>条码</th><th>关联工单</th><th>作业员</th><th>设备</th><th>结果</th></tr></thead>
          <tbody>
            <tr><td>09:15</td><td>工序开始</td><td>总装配 OP-30</td><td class="cell-mono">FS40B3071100001</td><td>MO-0032</td><td>陈作业</td><td>总装线 A-01</td><td>—</td></tr>
            <tr><td>08:45</td><td>工序完成</td><td>压装 OP-20</td><td class="cell-mono">FS40B3071100001</td><td>MO-0032</td><td>刘作业</td><td>压装区 #2</td><td>合格</td></tr>
            <tr><td>08:15</td><td>工序开始</td><td>绕线 OP-10</td><td class="cell-mono">FS40B3071100001</td><td>MO-0032</td><td>孙作业</td><td>绕线 #2</td><td>—</td></tr>
            <tr><td>07-10 14:00</td><td>来料入库</td><td>—</td><td class="cell-mono">MTR4000750001</td><td>—</td><td>赵仓管</td><td>—</td><td>合格</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  </main>
  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span>
    <span class="statusbar-item">追溯查询 · 对接 GET /api/production/trace</span><span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span>
  </footer>
</MesLayout>
</template>
