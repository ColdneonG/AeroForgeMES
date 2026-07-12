<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="dashboard">

  <!-- Sidebar -->
    

  <!-- Header -->
  <header class="app-header" data-od-id="dashboard-header">
    <div class="header-breadcrumb">
      <span class="bc-current">工作台</span>
    </div>
    <div class="header-actions">
      <span style="font-size:var(--text-caption);opacity:0.5;">2026-07-11 09:32</span>
      <span class="user-avatar" title="张工 — 生产主管">张</span>
    </div>
  </header>

  <!-- Main Content -->
  <main class="app-main" data-od-id="dashboard-main">
    <h1 class="page-title" data-od-id="dashboard-title">生产工作台</h1>

    <!-- KPI Row -->
    <section class="kpi-grid" data-od-id="dashboard-kpi">
      <div class="kpi-card accent-border" data-od-id="kpi-output">
        <span class="kpi-label">今日产量（台）</span>
        <span class="kpi-value" v-count-up>1,286</span>
        <span class="kpi-change up">↑ 8.2% 较昨日同期</span>
      </div>
      <div class="kpi-card" data-od-id="kpi-order-done">
        <span class="kpi-label">今日完工订单</span>
        <span class="kpi-value" v-count-up>14</span>
        <span class="kpi-change up">目标 18 单</span>
      </div>
      <div class="kpi-card" data-od-id="kpi-quality">
        <span class="kpi-label">一次合格率</span>
        <span class="kpi-value" v-count-up>97.6<span class="kpi-unit">%</span></span>
        <span class="kpi-change down">↓ 0.3% 较上周</span>
      </div>
      <div class="kpi-card" data-od-id="kpi-oee">
        <span class="kpi-label">设备综合效率 OEE</span>
        <span class="kpi-value" v-count-up>84.3<span class="kpi-unit">%</span></span>
        <span class="kpi-change up">↑ 2.1%</span>
      </div>
      <div class="kpi-card accent-border" data-od-id="kpi-andon">
        <span class="kpi-label">安灯异常（未关闭）</span>
        <span class="kpi-value" v-count-up>3</span>
        <span class="kpi-change">总装线 ×2 · 绕线 ×1</span>
      </div>
    </section>

    <!-- Two-column layout -->
    <div style="display:grid; grid-template-columns:1fr 1fr; gap:var(--space-5);">
      <!-- Left: Current Orders -->
      <section class="card" data-od-id="dashboard-orders">
        <div class="card-header">
          <h2 class="card-title">进行中的生产订单</h2>
          <RouterLink to="/production-orders" class="btn btn-ghost btn-sm">查看全部 →</RouterLink>
        </div>
        <table class="data-table" style="border:none;">
          <thead>
            <tr>
              <th>订单号</th><th>产品</th><th>数量</th><th>进度</th><th>状态</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="cell-mono"><RouterLink to="/production-order-detail">MO-20260711-0032</RouterLink></td>
              <td>FS-40 落地扇</td>
              <td>500</td>
              <td class="cell-progress"><div class="progress-bar"><div class="progress-fill" style="width:72%;"></div></div><span class="progress-label">72%</span></td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>生产中</span></td>
            </tr>
            <tr>
              <td class="cell-mono"><RouterLink to="/production-order-detail">MO-20260711-0033</RouterLink></td>
              <td>FT-30 台扇</td>
              <td>800</td>
              <td class="cell-progress"><div class="progress-bar"><div class="progress-fill" style="width:45%;"></div></div><span class="progress-label">45%</span></td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>生产中</span></td>
            </tr>
            <tr>
              <td class="cell-mono"><RouterLink to="/production-order-detail">MO-20260710-0031</RouterLink></td>
              <td>FS-35 壁扇</td>
              <td>300</td>
              <td class="cell-progress"><div class="progress-bar"><div class="progress-fill" style="width:94%;"></div></div><span class="progress-label">94%</span></td>
              <td><span class="badge badge-status-warn"><span class="badge-dot"></span>待质检</span></td>
            </tr>
            <tr>
              <td class="cell-mono"><RouterLink to="/production-order-detail">MO-20260710-0030</RouterLink></td>
              <td>FS-40 落地扇（遥控款）</td>
              <td>400</td>
              <td class="cell-progress"><div class="progress-bar"><div class="progress-fill" style="width:28%;"></div></div><span class="progress-label">28%</span></td>
              <td><span class="badge badge-status-info"><span class="badge-dot"></span>物料等待</span></td>
            </tr>
          </tbody>
        </table>
      </section>

      <!-- Right: Alerts & Recent Events -->
      <section data-od-id="dashboard-alerts">
        <div class="card" style="margin-bottom:var(--space-5);">
          <div class="card-header">
            <h2 class="card-title">安灯异常</h2>
            <span class="badge badge-status-error"><span class="badge-dot"></span>3 条未关闭</span>
          </div>
          <div class="alert alert-error" data-od-id="alert-andon-1">
            <span class="alert-icon">!</span>
            <div>
              <strong>总装线 A — 风叶动平衡超差</strong><br>
              <span class="text-subtle">报工时间 09:15 · 班组长已响应 · 预计恢复 10:00</span>
            </div>
          </div>
          <div class="alert alert-warning" data-od-id="alert-andon-2">
            <span class="alert-icon">!</span>
            <div>
              <strong>绕线工位 #3 — 漆包线张力异常</strong><br>
              <span class="text-subtle">报工时间 08:42 · 设备技术员到场处理中</span>
            </div>
          </div>
          <div class="alert alert-warning" data-od-id="alert-andon-3">
            <span class="alert-icon">!</span>
            <div>
              <strong>总装线 B — 网罩卡扣缺料预警</strong><br>
              <span class="text-subtle">报工时间 09:28 · 仓库已确认补料，预计 09:50 送达</span>
            </div>
          </div>
        </div>

        <div class="card" data-od-id="dashboard-recent">
          <div class="card-header">
            <h2 class="card-title">近期事件</h2>
          </div>
          <table class="data-table" style="border:none;">
            <tbody>
              <tr><td style="width:60px;" class="text-muted">09:28</td><td>MO-0032 完成绕线工序 420/500 台</td></tr>
              <tr><td style="width:60px;" class="text-muted">09:15</td><td>质量巡检 — 总装线 A 抽检 20 台，全部合格</td></tr>
              <tr><td style="width:60px;" class="text-muted">08:50</td><td>设备点检完成 — 注塑机 #2 更换模具（FS-40 底座）</td></tr>
              <tr><td style="width:60px;" class="text-muted">08:30</td><td>早会 — 今日排产 5 张工单，目标产出 1,500 台</td></tr>
              <tr><td style="width:60px;" class="text-muted">08:00</td><td>开班 — 到岗 86/88 人（请假 2 人）</td></tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </main>

  <!-- Status Bar -->
  <footer class="app-statusbar" data-od-id="dashboard-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">在线设备 22/24</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item"><span class="dot warn"></span>安灯活跃 3</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工 (生产主管)</span>
  </footer>
</MesLayout>
</template>
