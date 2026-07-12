<script setup lang="ts">
import { ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
const activeTab = ref('overview')
</script>

<template>
<MesLayout active="order-detail">
    

  <header class="app-header">
    <div class="header-breadcrumb">
      <span>生产管理</span> <span class="bc-sep">/</span>
      <RouterLink to="/production-orders">生产订单</RouterLink> <span class="bc-sep">/</span>
      <span class="bc-current">MO-20260711-0032</span>
    </div>
    <div class="header-actions">
      <button class="btn btn-secondary btn-sm">打印工单</button>
      <button class="btn btn-primary btn-sm">报工</button>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="detail-main">
    <div class="flex items-center justify-between mb-5">
      <div>
        <h1 class="page-title" style="margin-bottom:4px;">MO-20260711-0032</h1>
        <span class="badge badge-status-ok"><span class="badge-dot"></span>生产中</span>
        <span style="font-size:var(--text-small);opacity:0.5;margin-left:var(--space-3);">FS-40-B3 落地扇（机械款）· 计划 500 台</span>
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabs" data-od-id="detail-tabs">
      <span class="tab-item" @click="activeTab = 'overview'" :class="{ active: activeTab === 'overview' }">概览</span>
      <span class="tab-item" @click="activeTab = 'bom'" :class="{ active: activeTab === 'bom' }">物料清单 BOM</span>
      <span class="tab-item" @click="activeTab = 'progress'" :class="{ active: activeTab === 'progress' }">工序进度</span>
      <span class="tab-item" @click="activeTab = 'quality'" :class="{ active: activeTab === 'quality' }">质量记录</span>
    </div>

    <!-- Tab: Overview -->
    <div id="tab-overview" class="tab-content" style="display:grid; grid-template-columns:1fr 1fr; gap:var(--space-5);" v-show="activeTab === 'overview'">
      <section class="card" data-od-id="detail-info">
        <h2 class="section-title">基本信息</h2>
        <table class="data-table" style="border:none;">
          <tbody>
            <tr><td style="opacity:0.5;width:120px;">订单号</td><td class="cell-mono">MO-20260711-0032</td></tr>
            <tr><td style="opacity:0.5;">产品名称</td><td>FS-40 落地扇（机械款）</td></tr>
            <tr><td style="opacity:0.5;">规格型号</td><td>FS-40-B3 · 400mm 扇叶 · 3 档机械调速</td></tr>
            <tr><td style="opacity:0.5;">计划数量</td><td>500 台</td></tr>
            <tr><td style="opacity:0.5;">已完工</td><td>360 台（72%）</td></tr>
            <tr><td style="opacity:0.5;">产线</td><td>总装线 A</td></tr>
            <tr><td style="opacity:0.5;">计划日期</td><td>2026-07-11</td></tr>
            <tr><td style="opacity:0.5;">客户</td><td>粤风电器批发 · PO-20260705-0042</td></tr>
          </tbody>
        </table>
      </section>
      <section class="card" data-od-id="detail-progress">
        <h2 class="section-title">生产进度</h2>
        <div style="margin-bottom:var(--space-4);">
          <div class="flex justify-between mb-2"><span class="text-muted">完工进度</span><span style="font-weight:600;">360 / 500 (72%)</span></div>
          <div class="progress-bar" style="height:10px;"><div class="progress-fill" style="width:72%;"></div></div>
        </div>
        <div class="timeline" data-od-id="detail-timeline">
          <div class="timeline-step done"><span class="step-dot">1</span><span class="step-label">定子绕线</span><span class="text-muted">100%</span></div>
          <div class="timeline-step done"><span class="step-dot">2</span><span class="step-label">转子装配</span><span class="text-muted">100%</span></div>
          <div class="timeline-step active"><span class="step-dot">3</span><span class="step-label">总装</span><span class="text-muted">72%</span></div>
          <div class="timeline-step"><span class="step-dot">4</span><span class="step-label">质检</span><span class="text-muted">0%</span></div>
          <div class="timeline-step"><span class="step-dot">5</span><span class="step-label">包装入库</span><span class="text-muted">0%</span></div>
        </div>
      </section>
    </div>

    <!-- Tab: BOM -->
    <div id="tab-bom" class="tab-content"  v-show="activeTab === 'bom'">
      <div class="data-table-wrap" data-od-id="detail-bom">
        <table class="data-table">
          <thead><tr><th>物料编码</th><th>物料名称</th><th>规格</th><th>单位用量</th><th>需求数</th><th>已发料</th><th>齐套</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">MTR-400-075</td><td>异步电动机</td><td>75W / 220V / 50Hz</td><td>1</td><td>500</td><td>500</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>齐套</span></td></tr>
            <tr><td class="cell-mono">BLD-400-03</td><td>ABS 扇叶</td><td>400mm · 3 叶 · 透明蓝</td><td>1</td><td>500</td><td>500</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>齐套</span></td></tr>
            <tr><td class="cell-mono">GRD-400-01</td><td>金属网罩</td><td>Φ430mm · 镀铬钢丝</td><td>1</td><td>500</td><td>500</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>齐套</span></td></tr>
            <tr><td class="cell-mono">BAS-040-01</td><td>圆形底座</td><td>Φ320mm · PP 注塑</td><td>1</td><td>500</td><td>500</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>齐套</span></td></tr>
            <tr><td class="cell-mono">STD-032-02</td><td>可调立杆</td><td>Φ32mm · 镀锌钢管</td><td>1</td><td>500</td><td>500</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>齐套</span></td></tr>
            <tr><td class="cell-mono">SWT-003-M</td><td>三档琴键开关</td><td>机械式 · 250V/3A</td><td>1</td><td>500</td><td>500</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>齐套</span></td></tr>
            <tr><td class="cell-mono">CBL-220-15</td><td>电源线</td><td>2×0.75mm² · 1.5m</td><td>1</td><td>500</td><td>500</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>齐套</span></td></tr>
            <tr><td class="cell-mono">PKG-FS40</td><td>彩色包装箱</td><td>520×230×490mm</td><td>1</td><td>500</td><td>300</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>缺 200</span></td></tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Tab: Progress -->
    <div id="tab-progress" class="tab-content"  v-show="activeTab === 'progress'">
      <div class="data-table-wrap" data-od-id="detail-operations">
        <table class="data-table">
          <thead><tr><th>工序号</th><th>工序名称</th><th>工作中心</th><th>计划数量</th><th>完工数量</th><th>不良数</th><th>进度</th><th>状态</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">OP-10</td><td>定子绕线</td><td>绕线车间 #1</td><td>500</td><td>500</td><td>3</td><td><div class="progress-bar" style="width:80px;"><div class="progress-fill" style="width:100%;"></div></div>100%</td><td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已完成</span></td></tr>
            <tr><td class="cell-mono">OP-20</td><td>转子压装</td><td>压装区 #2</td><td>500</td><td>500</td><td>0</td><td><div class="progress-bar" style="width:80px;"><div class="progress-fill" style="width:100%;"></div></div>100%</td><td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已完成</span></td></tr>
            <tr><td class="cell-mono">OP-30</td><td>总装配</td><td>总装线 A</td><td>500</td><td>360</td><td>2</td><td><div class="progress-bar" style="width:80px;"><div class="progress-fill" style="width:72%;"></div></div>72%</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>生产中</span></td></tr>
            <tr><td class="cell-mono">OP-40</td><td>出厂检验</td><td>质检区 QA</td><td>500</td><td>0</td><td>—</td><td><div class="progress-bar" style="width:80px;"><div class="progress-fill" style="width:0%;"></div></div>0%</td><td><span class="badge badge-status-draft">待开始</span></td></tr>
            <tr><td class="cell-mono">OP-50</td><td>包装入库</td><td>包装区</td><td>500</td><td>0</td><td>—</td><td><div class="progress-bar" style="width:80px;"><div class="progress-fill" style="width:0%;"></div></div>0%</td><td><span class="badge badge-status-draft">待开始</span></td></tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Tab: Quality -->
    <div id="tab-quality" class="tab-content"  v-show="activeTab === 'quality'">
      <div class="data-table-wrap" data-od-id="detail-quality-records">
        <table class="data-table">
          <thead><tr><th>检验编号</th><th>检验类型</th><th>工序</th><th>检验数</th><th>合格数</th><th>不良数</th><th>结果</th><th>检验人</th><th>时间</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">QC-0711-0152</td><td>过程检验</td><td>总装配 OP-30</td><td>50</td><td>48</td><td>2</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>让步接收</span></td><td>李检验</td><td>09:15</td></tr>
            <tr><td class="cell-mono">QC-0710-0148</td><td>过程检验</td><td>转子压装 OP-20</td><td>30</td><td>30</td><td>0</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>合格</span></td><td>李检验</td><td>07-10 16:30</td></tr>
            <tr><td class="cell-mono">QC-0710-0142</td><td>过程检验</td><td>定子绕线 OP-10</td><td>30</td><td>29</td><td>1</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>合格</span></td><td>王质检</td><td>07-10 14:00</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">MO-20260711-0032 · FS-40-B3 · 360/500</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span>
  </footer>
</MesLayout>
</template>
