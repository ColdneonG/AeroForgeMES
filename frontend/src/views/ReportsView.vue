<script setup lang="ts">
import { ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
const activeTab = ref('daily')
</script>

<template>
<MesLayout active="reports-main">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>数据与系统</span> <span class="bc-sep">/</span> <span class="bc-current">生产报表</span></div>
    <div class="header-actions">
      <span style="font-size:var(--text-caption);opacity:0.5;">2026年7月</span>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="reports-main">
    <h1 class="page-title">生产报表</h1>

    <div class="tabs" data-od-id="reports-tabs">
      <span class="tab-item" @click="activeTab = 'daily'" :class="{ active: activeTab === 'daily' }">生产日报</span>
      <span class="tab-item" @click="activeTab = 'monthly'" :class="{ active: activeTab === 'monthly' }">月度汇总</span>
      <span class="tab-item" @click="activeTab = 'wages'" :class="{ active: activeTab === 'wages' }">计件工资</span>
    </div>

    <!-- Tab: Daily -->
    <div id="rtab-daily" v-show="activeTab === 'daily'">
      <div class="kpi-grid">
        <div class="kpi-card accent-border"><span class="kpi-label">今日产量</span><span class="kpi-value" v-count-up>1,286</span><span class="kpi-change">台 · 目标 1,500</span></div>
        <div class="kpi-card"><span class="kpi-label">今日不良</span><span class="kpi-value" v-count-up>18</span><span class="kpi-change">不良率 1.40%</span></div>
        <div class="kpi-card"><span class="kpi-label">今日产值</span><span class="kpi-value" v-count-up style="font-size:1.3rem;">¥128,600</span><span class="kpi-change">按出厂价 ¥100/台</span></div>
        <div class="kpi-card"><span class="kpi-label">在线人数</span><span class="kpi-value" v-count-up>86</span><span class="kpi-change">人均 15.0 台/天</span></div>
      </div>
      <div class="card mb-5">
        <div class="card-header"><h2 class="card-title">本日产量趋势（每 2 小时）</h2></div>
        <svg viewBox="0 0 560 220" style="width:100%;height:220px;font-family:var(--font-mono);">
          <defs>
            <linearGradient id="rBarGrad" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="var(--fg)" stop-opacity="0.55"/>
              <stop offset="100%" stop-color="var(--fg)" stop-opacity="0.25"/>
            </linearGradient>
            <linearGradient id="rBarAccent" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="var(--accent)" stop-opacity="0.75"/>
              <stop offset="100%" stop-color="var(--accent)" stop-opacity="0.35"/>
            </linearGradient>
            <linearGradient id="rHighlight" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="white" stop-opacity="0.18"/>
              <stop offset="100%" stop-color="white" stop-opacity="0"/>
            </linearGradient>
          </defs>
          <!-- Grid lines -->
          <line x1="0" y1="40" x2="560" y2="40" stroke="var(--border)" stroke-width="0.5" stroke-dasharray="4 4"/>
          <line x1="0" y1="80" x2="560" y2="80" stroke="var(--border)" stroke-width="0.5" stroke-dasharray="4 4"/>
          <line x1="0" y1="120" x2="560" y2="120" stroke="var(--border)" stroke-width="0.5" stroke-dasharray="4 4"/>
          <line x1="0" y1="160" x2="560" y2="160" stroke="var(--border)" stroke-width="0.5" stroke-dasharray="4 4"/>
          <!-- Baseline -->
          <line x1="0" y1="200" x2="560" y2="200" stroke="var(--border)" stroke-width="1"/>
          <!-- Bars with highlight caps -->
          <g class="chart-bar-group">
            <rect class="chart-rect" x="16" y="120" width="88" height="80" fill="url(#rBarGrad)" rx="4"/>
            <rect class="chart-rect" x="16" y="120" width="88" height="80" fill="url(#rHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="60" y="114" text-anchor="middle" fill="var(--fg)" opacity="0.65" font-size="11" font-weight="600">352</text>
          </g>
          <g class="chart-bar-group">
            <rect class="chart-rect" x="124" y="80" width="88" height="120" fill="url(#rBarGrad)" rx="4"/>
            <rect class="chart-rect" x="124" y="80" width="88" height="120" fill="url(#rHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="168" y="74" text-anchor="middle" fill="var(--fg)" opacity="0.65" font-size="11" font-weight="600">512</text>
          </g>
          <g class="chart-bar-group">
            <rect class="chart-rect" x="232" y="48" width="88" height="152" fill="url(#rBarAccent)" rx="4"/>
            <rect class="chart-rect" x="232" y="48" width="88" height="152" fill="url(#rHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="276" y="42" text-anchor="middle" fill="var(--accent)" opacity="0.9" font-size="11" font-weight="700">624</text>
          </g>
          <g class="chart-bar-group">
            <rect class="chart-rect" x="340" y="64" width="88" height="136" fill="url(#rBarGrad)" rx="4"/>
            <rect class="chart-rect" x="340" y="64" width="88" height="136" fill="url(#rHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="384" y="58" text-anchor="middle" fill="var(--fg)" opacity="0.65" font-size="11" font-weight="600">580</text>
          </g>
          <g class="chart-bar-group">
            <rect class="chart-rect" x="448" y="96" width="88" height="104" fill="url(#rBarGrad)" rx="4"/>
            <rect class="chart-rect" x="448" y="96" width="88" height="104" fill="url(#rHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="492" y="90" text-anchor="middle" fill="var(--fg)" opacity="0.65" font-size="11" font-weight="600">448</text>
          </g>
        </svg>
        <div style="display:flex;justify-content:space-between;font-size:var(--text-caption);opacity:0.5;padding:var(--space-2) var(--space-1);margin-top:var(--space-1);">
          <span>08–10</span><span>10–12</span><span>12–14</span><span>14–16</span><span>16–18</span>
        </div>
      </div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>产品</th><th>规格</th><th>计划</th><th>完工</th><th>不良</th><th>不良率</th><th>产线</th><th>完成率</th></tr></thead>
          <tbody>
            <tr><td>落地扇</td><td>FS-40-B3</td><td>500</td><td>360</td><td>5</td><td>1.39%</td><td>总装线 A</td><td><div class="progress-bar" style="width:80px;"><div class="progress-fill" style="width:72%;"></div></div> 72%</td></tr>
            <tr><td>台扇</td><td>FT-30-A1</td><td>800</td><td>360</td><td>8</td><td>2.22%</td><td>总装线 B</td><td><div class="progress-bar" style="width:80px;"><div class="progress-fill" style="width:45%;"></div></div> 45%</td></tr>
            <tr><td>壁扇</td><td>FS-35-W2</td><td>300</td><td>282</td><td>3</td><td>1.06%</td><td>总装线 A</td><td><div class="progress-bar" style="width:80px;"><div class="progress-fill" style="width:94%;"></div></div> 94%</td></tr>
            <tr><td>落地扇</td><td>FS-40-R1</td><td>400</td><td>112</td><td>2</td><td>1.79%</td><td>总装线 A</td><td><div class="progress-bar" style="width:80px;"><div class="progress-fill" style="width:28%;"></div></div> 28%</td></tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Tab: Monthly -->
    <div id="rtab-monthly"  v-show="activeTab === 'monthly'">
      <div class="kpi-grid" style="grid-template-columns:repeat(3,1fr);">
        <div class="kpi-card accent-border"><span class="kpi-label">本月累计产量</span><span class="kpi-value" v-count-up>18,240</span><span class="kpi-change">台 · 目标 45,000</span></div>
        <div class="kpi-card"><span class="kpi-label">本月不良率</span><span class="kpi-value" v-count-up>1.52<span style="font-size:0.5em;">%</span></span><span class="kpi-change">↓ 0.18% 较上月</span></div>
        <div class="kpi-card"><span class="kpi-label">本月产值</span><span class="kpi-value" v-count-up style="font-size:1.3rem;">¥1,824,000</span><span class="kpi-change">目标 ¥4,500,000</span></div>
      </div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>日期</th><th>产量（台）</th><th>不良数</th><th>不良率</th><th>产值（元）</th><th>出勤人数</th></tr></thead>
          <tbody>
            <tr><td>07-11</td><td>1,286</td><td>18</td><td>1.40%</td><td>128,600</td><td>86</td></tr>
            <tr><td>07-10</td><td>1,492</td><td>22</td><td>1.47%</td><td>149,200</td><td>89</td></tr>
            <tr><td>07-09</td><td>1,438</td><td>19</td><td>1.32%</td><td>143,800</td><td>88</td></tr>
            <tr><td>07-08</td><td>1,355</td><td>21</td><td>1.55%</td><td>135,500</td><td>87</td></tr>
            <tr><td>07-07</td><td>1,221</td><td>16</td><td>1.31%</td><td>122,100</td><td>84</td></tr>
          </tbody>
        </table>
        <div class="pagination"><span>本月共 11 天数据</span><div class="page-btns"><span class="page-btn active">1</span><span class="page-btn">2</span><span class="page-btn">›</span></div></div>
      </div>
    </div>

    <!-- Tab: Wages -->
    <div id="rtab-wages"  v-show="activeTab === 'wages'">
      <div class="card mb-5">
        <div class="card-header"><h2 class="card-title">计件工资 — 2026年7月11日</h2></div>
      </div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>工号</th><th>姓名</th><th>工位</th><th>产品</th><th>完工数</th><th>单价（元）</th><th>工资（元）</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">EMP-0042</td><td>陈作业</td><td>底座组装</td><td>FS-40-B3</td><td>360</td><td>1.20</td><td>432.00</td></tr>
            <tr><td class="cell-mono">EMP-0018</td><td>刘作业</td><td>电机安装</td><td>FS-40-B3</td><td>358</td><td>1.50</td><td>537.00</td></tr>
            <tr><td class="cell-mono">EMP-0056</td><td>黄作业</td><td>扇叶安装</td><td>FS-40-B3</td><td>355</td><td>1.00</td><td>355.00</td></tr>
            <tr><td class="cell-mono">EMP-0033</td><td>林作业</td><td>网罩组装</td><td>FS-40-B3</td><td>362</td><td>1.10</td><td>398.20</td></tr>
            <tr><td class="cell-mono">EMP-0071</td><td>周作业</td><td>通电测试</td><td>FS-40-B3</td><td>360</td><td>0.80</td><td>288.00</td></tr>
          </tbody>
        </table>
        <div class="pagination"><span>当日合计 86 人 · 工资总额 ¥18,520.00</span></div>
      </div>
    </div>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">2026-07-11 · 今日产量 1,286 台 · 产值 ¥128,600</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span>
  </footer>
</MesLayout>
</template>

<style>

    .chart-bar-group { cursor: pointer; transition: opacity 180ms ease; }
    .chart-bar-group:hover { opacity: 0.78; }
    .chart-bar-group:hover text { opacity: 1 !important; font-weight: 700 !important; }
  
</style>
