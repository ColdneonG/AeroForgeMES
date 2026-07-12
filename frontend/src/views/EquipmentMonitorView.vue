<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="equipment">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>设备与安灯</span> <span class="bc-sep">/</span> <span class="bc-current">设备监控</span></div>
    <div class="header-actions">
      <span style="font-size:var(--text-caption);opacity:0.5;">总 OEE 84.3%</span>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="equipment-main">
    <h1 class="page-title">设备状态监控</h1>

    <div class="kpi-grid" style="grid-template-columns:repeat(4,1fr);">
      <div class="kpi-card accent-border"><span class="kpi-label">运行中</span><span class="kpi-value" v-count-up style="color:var(--accent);">18</span></div>
      <div class="kpi-card"><span class="kpi-label">空闲</span><span class="kpi-value" v-count-up>4</span></div>
      <div class="kpi-card"><span class="kpi-label">报警</span><span class="kpi-value" v-count-up style="color:var(--accent);">2</span><span class="kpi-change">绕线机 #3 · 注塑机 #2</span></div>
      <div class="kpi-card"><span class="kpi-label">停机维保</span><span class="kpi-value" v-count-up>0</span></div>
    </div>

    <section class="card mb-5" data-od-id="equipment-grid">
      <div class="card-header"><h2 class="card-title">全部设备 · 24 台</h2></div>
      <div class="status-grid">
        <div class="status-cell" style="border-left:3px solid var(--accent);">
          <span class="sc-name">注塑机 #1</span>
          <span class="sc-code">IM-01 · 锁模力 120T</span>
          <span class="sc-status"><span class="sc-dot running"></span>运行中 · 稼动 92%</span>
        </div>
        <div class="status-cell">
          <span class="sc-name">注塑机 #2</span>
          <span class="sc-code">IM-02 · 锁模力 160T</span>
          <span class="sc-status"><span class="sc-dot alarm"></span>报警 — 模温过高</span>
        </div>
        <div class="status-cell" style="border-left:3px solid var(--accent);">
          <span class="sc-name">自动绕线机 #1</span>
          <span class="sc-code">WD-01 · 4 轴</span>
          <span class="sc-status"><span class="sc-dot running"></span>运行中 · 稼动 88%</span>
        </div>
        <div class="status-cell" style="border-left:3px solid var(--accent);">
          <span class="sc-name">自动绕线机 #2</span>
          <span class="sc-code">WD-02 · 4 轴</span>
          <span class="sc-status"><span class="sc-dot running"></span>运行中 · 稼动 91%</span>
        </div>
        <div class="status-cell">
          <span class="sc-name">自动绕线机 #3</span>
          <span class="sc-code">WD-03 · 6 轴</span>
          <span class="sc-status"><span class="sc-dot alarm"></span>报警 — 张力异常</span>
        </div>
        <div class="status-cell">
          <span class="sc-name">转子压装机 #1</span>
          <span class="sc-code">RP-01 · 气动</span>
          <span class="sc-status"><span class="sc-dot idle"></span>空闲 — 待料</span>
        </div>
        <div class="status-cell" style="border-left:3px solid var(--accent);">
          <span class="sc-name">动平衡机 #1</span>
          <span class="sc-code">DB-01</span>
          <span class="sc-status"><span class="sc-dot running"></span>运行中 · 稼动 85%</span>
        </div>
        <div class="status-cell" style="border-left:3px solid var(--accent);">
          <span class="sc-name">自动锁螺丝机 #1</span>
          <span class="sc-code">AS-01 · 双工位</span>
          <span class="sc-status"><span class="sc-dot running"></span>运行中 · 稼动 94%</span>
        </div>
        <div class="status-cell">
          <span class="sc-name">自动锁螺丝机 #2</span>
          <span class="sc-code">AS-02 · 双工位</span>
          <span class="sc-status"><span class="sc-dot idle"></span>空闲 — 换模中</span>
        </div>
        <div class="status-cell" style="border-left:3px solid var(--accent);">
          <span class="sc-name">自动打包机 #1</span>
          <span class="sc-code">PK-01</span>
          <span class="sc-status"><span class="sc-dot running"></span>运行中 · 稼动 78%</span>
        </div>
        <div class="status-cell" style="border-left:3px solid var(--accent);">
          <span class="sc-name">耐压测试仪 #1</span>
          <span class="sc-code">HT-01 · 5kV</span>
          <span class="sc-status"><span class="sc-dot running"></span>运行中 · 稼动 63%</span>
        </div>
        <div class="status-cell">
          <span class="sc-name">激光打标机 #1</span>
          <span class="sc-code">LM-01 · 光纤</span>
          <span class="sc-status"><span class="sc-dot idle"></span>空闲</span>
        </div>
      </div>
    </section>

    <section class="card" data-od-id="equipment-alerts">
      <div class="card-header"><h2 class="card-title">设备报警记录</h2></div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>设备</th><th>报警类型</th><th>描述</th><th>级别</th><th>触发时间</th><th>状态</th></tr></thead>
          <tbody>
            <tr><td>注塑机 #2</td><td>温度异常</td><td>料筒温度超过设定上限 15°C</td><td><span class="badge badge-status-error"><span class="badge-dot"></span>严重</span></td><td>09:20</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>处理中</span></td></tr>
            <tr><td>绕线机 #3</td><td>张力偏离</td><td>漆包线张力波动超过 ±8% 持续 30s</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>警告</span></td><td>08:42</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>处理中</span></td></tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot warn"></span>设备报警 2</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">运行 18 · 空闲 4 · 停机 0 · 总 OEE 84.3%</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span>
  </footer>
</MesLayout>
</template>
