<script setup lang="ts">
import { ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
const activeTab = ref('daily')
</script>

<template>
<MesLayout active="output">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>数据与系统</span> <span class="bc-sep">/</span> <span class="bc-current">产量报表</span></div>
    <div class="header-actions">
      <button class="btn btn-secondary btn-sm">导出 Excel</button>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="output-main">
    <h1 class="page-title">产量报表</h1>

    <div class="tabs" data-od-id="output-tabs">
      <span class="tab-item" @click="activeTab = 'daily'" :class="{ active: activeTab === 'daily' }">日报</span>
      <span class="tab-item" @click="activeTab = 'weekly'" :class="{ active: activeTab === 'weekly' }">周报</span>
      <span class="tab-item" @click="activeTab = 'monthly'" :class="{ active: activeTab === 'monthly' }">月报</span>
      <span class="tab-item" @click="activeTab = 'product'" :class="{ active: activeTab === 'product' }">按产品</span>
    </div>

    <!-- Daily -->
    <div id="otab-daily" v-show="activeTab === 'daily'">
      <div class="kpi-grid" style="grid-template-columns:repeat(4,1fr);">
        <div class="kpi-card accent-border"><span class="kpi-label">今日产量</span><span class="kpi-value" v-count-up>1,286</span><span class="kpi-change">台</span></div>
        <div class="kpi-card"><span class="kpi-label">日均产量（本月）</span><span class="kpi-value" v-count-up>1,658</span><span class="kpi-change">台/天</span></div>
        <div class="kpi-card"><span class="kpi-label">峰值产能</span><span class="kpi-value" v-count-up>1,892</span><span class="kpi-change">台/天（07-05）</span></div>
        <div class="kpi-card"><span class="kpi-label">本月累计</span><span class="kpi-value" v-count-up>18,240</span><span class="kpi-change">台 · 目标 45,000</span></div>
      </div>

      <div class="card mb-5">
        <div class="card-header"><h2 class="card-title">近 7 天产量趋势</h2></div>
        <svg viewBox="0 0 700 230" style="width:100%;height:230px;font-family:var(--font-mono);">
          <defs>
            <linearGradient id="oBarGrad" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="var(--fg)" stop-opacity="0.55"/>
              <stop offset="100%" stop-color="var(--fg)" stop-opacity="0.22"/>
            </linearGradient>
            <linearGradient id="oBarAccent" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="var(--accent)" stop-opacity="0.75"/>
              <stop offset="100%" stop-color="var(--accent)" stop-opacity="0.3"/>
            </linearGradient>
            <linearGradient id="oHighlight" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="white" stop-opacity="0.18"/>
              <stop offset="100%" stop-color="white" stop-opacity="0"/>
            </linearGradient>
          </defs>
          <!-- Grid lines -->
          <line x1="0" y1="45" x2="700" y2="45" stroke="var(--border)" stroke-width="0.5" stroke-dasharray="4 4"/>
          <line x1="0" y1="90" x2="700" y2="90" stroke="var(--border)" stroke-width="0.5" stroke-dasharray="4 4"/>
          <line x1="0" y1="135" x2="700" y2="135" stroke="var(--border)" stroke-width="0.5" stroke-dasharray="4 4"/>
          <line x1="0" y1="180" x2="700" y2="180" stroke="var(--border)" stroke-width="0.5" stroke-dasharray="4 4"/>
          <!-- Baseline -->
          <line x1="0" y1="210" x2="700" y2="210" stroke="var(--border)" stroke-width="1"/>
          <!-- Bars -->
          <g class="chart-bar-group">
            <rect class="chart-rect" x="10" y="93" width="80" height="117" fill="url(#oBarGrad)" rx="4"/>
            <rect class="chart-rect" x="10" y="93" width="80" height="117" fill="url(#oHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="50" y="87" text-anchor="middle" fill="var(--fg)" opacity="0.6" font-size="11" font-weight="600">1,892</text>
          </g>
          <g class="chart-bar-group">
            <rect class="chart-rect" x="110" y="68" width="80" height="142" fill="url(#oBarGrad)" rx="4"/>
            <rect class="chart-rect" x="110" y="68" width="80" height="142" fill="url(#oHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="150" y="62" text-anchor="middle" fill="var(--fg)" opacity="0.6" font-size="11" font-weight="600">1,756</text>
          </g>
          <g class="chart-bar-group">
            <rect class="chart-rect" x="210" y="80" width="80" height="130" fill="url(#oBarGrad)" rx="4"/>
            <rect class="chart-rect" x="210" y="80" width="80" height="130" fill="url(#oHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="250" y="74" text-anchor="middle" fill="var(--fg)" opacity="0.6" font-size="11" font-weight="600">1,221</text>
          </g>
          <g class="chart-bar-group">
            <rect class="chart-rect" x="310" y="30" width="80" height="180" fill="url(#oBarAccent)" rx="4"/>
            <rect class="chart-rect" x="310" y="30" width="80" height="180" fill="url(#oHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="350" y="24" text-anchor="middle" fill="var(--accent)" opacity="0.9" font-size="11" font-weight="700">1,892</text>
          </g>
          <g class="chart-bar-group">
            <rect class="chart-rect" x="410" y="73" width="80" height="137" fill="url(#oBarGrad)" rx="4"/>
            <rect class="chart-rect" x="410" y="73" width="80" height="137" fill="url(#oHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="450" y="67" text-anchor="middle" fill="var(--fg)" opacity="0.6" font-size="11" font-weight="600">1,438</text>
          </g>
          <g class="chart-bar-group">
            <rect class="chart-rect" x="510" y="88" width="80" height="122" fill="url(#oBarGrad)" rx="4"/>
            <rect class="chart-rect" x="510" y="88" width="80" height="122" fill="url(#oHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="550" y="82" text-anchor="middle" fill="var(--fg)" opacity="0.6" font-size="11" font-weight="600">1,492</text>
          </g>
          <g class="chart-bar-group">
            <rect class="chart-rect" x="610" y="120" width="80" height="90" fill="url(#oBarGrad)" rx="4"/>
            <rect class="chart-rect" x="610" y="120" width="80" height="90" fill="url(#oHighlight)" rx="4" style="pointer-events:none;"/>
            <text x="650" y="114" text-anchor="middle" fill="var(--fg)" opacity="0.6" font-size="11" font-weight="600">1,286</text>
          </g>
        </svg>
        <div style="display:flex;justify-content:space-between;font-size:var(--text-caption);opacity:0.5;padding:var(--space-2) var(--space-1);margin-top:var(--space-1);">
          <span>07-05<br>1,892</span><span>07-06<br>1,756</span><span>07-07<br>1,221</span><span>07-08<br>1,355</span><span>07-09<br>1,438</span><span>07-10<br>1,492</span><span>07-11<br>1,286</span>
        </div>
      </div>

      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>日期</th><th>产品</th><th>规格</th><th>计划数量</th><th>完工数量</th><th>不良数</th><th>不良率</th><th>产线</th><th>完成率</th></tr></thead>
          <tbody>
            <tr><td>07-11</td><td>落地扇</td><td>FS-40-B3</td><td>500</td><td>360</td><td>5</td><td>1.39%</td><td>总装线 A</td><td>72%</td></tr>
            <tr><td>07-11</td><td>台扇</td><td>FT-30-A1</td><td>800</td><td>360</td><td>8</td><td>2.22%</td><td>总装线 B</td><td>45%</td></tr>
            <tr><td>07-11</td><td>壁扇</td><td>FS-35-W2</td><td>300</td><td>282</td><td>3</td><td>1.06%</td><td>总装线 A</td><td>94%</td></tr>
            <tr><td>07-11</td><td>落地扇</td><td>FS-40-R1</td><td>400</td><td>112</td><td>2</td><td>1.79%</td><td>总装线 A</td><td>28%</td></tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Weekly -->
    <div id="otab-weekly"  v-show="activeTab === 'weekly'">
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>周次</th><th>日期范围</th><th>计划产量</th><th>实际产量</th><th>不良数</th><th>不良率</th><th>产值（元）</th><th>达成率</th></tr></thead>
          <tbody>
            <tr><td>W28</td><td>07-06 ~ 07-12</td><td>10,000</td><td>7,950</td><td>118</td><td>1.48%</td><td>795,000</td><td>79.5%</td></tr>
            <tr><td>W27</td><td>06-29 ~ 07-05</td><td>10,500</td><td>10,615</td><td>158</td><td>1.49%</td><td>1,061,500</td><td>101.1%</td></tr>
            <tr><td>W26</td><td>06-22 ~ 06-28</td><td>9,800</td><td>9,654</td><td>155</td><td>1.61%</td><td>965,400</td><td>98.5%</td></tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Monthly -->
    <div id="otab-monthly"  v-show="activeTab === 'monthly'">
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>月份</th><th>计划产量</th><th>实际产量</th><th>不良数</th><th>不良率</th><th>产值（元）</th><th>达成率</th><th>工作日</th></tr></thead>
          <tbody>
            <tr><td>2026-07</td><td>45,000</td><td>18,240</td><td>278</td><td>1.52%</td><td>1,824,000</td><td>40.5%</td><td>11 / 26</td></tr>
            <tr><td>2026-06</td><td>42,000</td><td>40,856</td><td>628</td><td>1.54%</td><td>4,085,600</td><td>97.3%</td><td>25</td></tr>
            <tr><td>2026-05</td><td>38,000</td><td>37,442</td><td>542</td><td>1.45%</td><td>3,744,200</td><td>98.5%</td><td>24</td></tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- By Product -->
    <div id="otab-product"  v-show="activeTab === 'product'">
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>产品名称</th><th>规格</th><th>本月产量</th><th>占比</th><th>不良率</th><th>标准工时</th><th>标准产能/天</th></tr></thead>
          <tbody>
            <tr><td>落地扇</td><td>FS-40 系列</td><td>8,420</td><td>46.2%</td><td>1.48%</td><td>28s</td><td>800</td></tr>
            <tr><td>台扇</td><td>FT-30 系列</td><td>5,680</td><td>31.1%</td><td>1.62%</td><td>22s</td><td>1,000</td></tr>
            <tr><td>壁扇</td><td>FS-35 系列</td><td>2,340</td><td>12.8%</td><td>1.12%</td><td>32s</td><td>700</td></tr>
            <tr><td>夹扇</td><td>FH-18 系列</td><td>1,200</td><td>6.6%</td><td>1.05%</td><td>16s</td><td>1,400</td></tr>
            <tr><td>吊扇</td><td>FC-56 系列</td><td>600</td><td>3.3%</td><td>2.17%</td><td>56s</td><td>400</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">产量报表 · 2026-07 · 本月 18,240 台 · 达成率 40.5%</span>
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
