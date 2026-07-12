<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="kitting">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>生产管理</span> <span class="bc-sep">/</span> <span class="bc-current">齐套分析</span></div>
    <div class="header-actions">
      <button class="btn btn-secondary btn-sm">刷新分析</button>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="kitting-main">
    <h1 class="page-title">齐套分析</h1>

    <div class="kpi-grid" style="grid-template-columns:repeat(4,1fr);">
      <div class="kpi-card accent-border"><span class="kpi-label">分析工单数</span><span class="kpi-value" v-count-up>5</span><span class="kpi-change">已分析</span></div>
      <div class="kpi-card"><span class="kpi-label">完全齐套</span><span class="kpi-value" v-count-up>3</span><span class="kpi-change up">可正常开工</span></div>
      <div class="kpi-card"><span class="kpi-label">部分缺料</span><span class="kpi-value" v-count-up style="color:var(--accent);">2</span><span class="kpi-change">缺 2 种物料</span></div>
      <div class="kpi-card"><span class="kpi-label">总缺料项</span><span class="kpi-value" v-count-up style="color:var(--accent);">3</span><span class="kpi-change">涉及 2 张工单</span></div>
    </div>

    <section class="card mb-5" data-od-id="kitting-table">
      <div class="card-header"><h2 class="card-title">齐套分析结果</h2></div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>工单号</th><th>产品</th><th>BOM 物料数</th><th>已齐套</th><th>缺料</th><th>齐套率</th><th>分析时间</th><th>结果</th><th>操作</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">MO-20260711-0032</td><td>FS-40-B3 落地扇</td><td>8</td><td>7</td><td>1</td>
              <td><div class="progress-bar" style="width:80px;display:inline-block;vertical-align:middle;"><div class="progress-fill" style="width:87%;"></div></div> 87%</td>
              <td>09:00</td>
              <td><span class="badge badge-status-warn"><span class="badge-dot"></span>部分缺料</span></td>
              <td><button class="btn btn-ghost btn-sm">查看缺料</button></td></tr>
            <tr><td class="cell-mono">MO-20260711-0033</td><td>FT-30-A1 台扇</td><td>7</td><td>7</td><td>0</td>
              <td><div class="progress-bar" style="width:80px;display:inline-block;vertical-align:middle;"><div class="progress-fill" style="width:100%;"></div></div> 100%</td>
              <td>09:00</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>完全齐套</span></td>
              <td><span class="text-muted">—</span></td></tr>
            <tr><td class="cell-mono">MO-20260710-0031</td><td>FS-35-W2 壁扇</td><td>9</td><td>9</td><td>0</td>
              <td><div class="progress-bar" style="width:80px;display:inline-block;vertical-align:middle;"><div class="progress-fill" style="width:100%;"></div></div> 100%</td>
              <td>07-10 17:00</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>完全齐套</span></td>
              <td><span class="text-muted">—</span></td></tr>
            <tr><td class="cell-mono">MO-20260710-0030</td><td>FS-40-R1 落地扇（遥控款）</td><td>10</td><td>8</td><td>2</td>
              <td><div class="progress-bar" style="width:80px;display:inline-block;vertical-align:middle;"><div class="progress-fill" style="width:80%;"></div></div> 80%</td>
              <td>07-10 17:00</td>
              <td><span class="badge badge-status-warn"><span class="badge-dot"></span>部分缺料</span></td>
              <td><button class="btn btn-ghost btn-sm">查看缺料</button></td></tr>
            <tr><td class="cell-mono">MO-20260709-0029</td><td>FH-18-C2 夹扇</td><td>6</td><td>6</td><td>0</td>
              <td><div class="progress-bar" style="width:80px;display:inline-block;vertical-align:middle;"><div class="progress-fill" style="width:100%;"></div></div> 100%</td>
              <td>07-09 08:30</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>完全齐套</span></td>
              <td><span class="text-muted">—</span></td></tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="card" data-od-id="kitting-missing">
      <div class="card-header"><h2 class="card-title">缺料明细</h2></div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>工单号</th><th>物料编码</th><th>物料名称</th><th>规格</th><th>单位用量</th><th>需求数</th><th>库存</th><th>缺口</th><th>预计到货</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">MO-0032</td><td class="cell-mono">PKG-FS40</td><td>彩色包装箱</td><td>520×230×490mm</td><td>1</td><td>500</td><td>300</td><td style="color:var(--accent);font-weight:600;">200</td><td>07-12</td></tr>
            <tr><td class="cell-mono">MO-0030</td><td class="cell-mono">RCV-FS40</td><td>遥控接收模块</td><td>433MHz · 4 通道</td><td>1</td><td>400</td><td>180</td><td style="color:var(--accent);font-weight:600;">220</td><td>07-13</td></tr>
            <tr><td class="cell-mono">MO-0030</td><td class="cell-mono">RMT-FS40</td><td>遥控器</td><td>CR2025 · 3 键</td><td>1</td><td>400</td><td>250</td><td style="color:var(--accent);font-weight:600;">150</td><td>07-12</td></tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">齐套分析 · 5 张工单 · 完全齐套 3 · 缺料 2</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span>
  </footer>
</MesLayout>
</template>
