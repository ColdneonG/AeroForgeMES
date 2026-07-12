<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="erp">
  
  <header class="app-header"><div class="header-breadcrumb"><span>集成管理</span> <span class="bc-sep">/</span> <span class="bc-current">ERP集成</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="notify('手动触发同步 — 对接 /api/integration/erp')">手动同步</button><span class="user-avatar">陈</span></div></header>
  <main class="app-main" data-od-id="erp-main">
    <h1 class="page-title">ERP集成管理</h1>
    <div class="kpi-grid" style="grid-template-columns:repeat(3,1fr);">
      <div class="kpi-card accent-border"><span class="kpi-label">同步状态</span><span class="kpi-value" v-count-up style="font-size:1.2rem;">运行中</span><span class="kpi-change up">最后同步 09:00</span></div>
      <div class="kpi-card"><span class="kpi-label">今日同步次数</span><span class="kpi-value" v-count-up>6</span><span class="kpi-change">成功 6 · 失败 0</span></div>
      <div class="kpi-card"><span class="kpi-label">累计同步记录</span><span class="kpi-value" v-count-up>1,258</span><span class="kpi-change">自 2026-01 起</span></div>
    </div>
    <section class="card mb-5"><div class="card-header"><h2 class="card-title">同步配置</h2></div>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:var(--space-4);">
        <div><span class="form-label">ERP 系统</span><br><span>用友 U8+ v16.0</span></div>
        <div><span class="form-label">同步方向</span><br><span class="badge badge-status-info"><span class="badge-dot"></span>双向同步</span></div>
        <div><span class="form-label">同步频率</span><br><span>每 15 分钟</span></div>
        <div><span class="form-label">认证方式</span><br><span>OAuth 2.0 · Token 有效期 2h</span></div>
      </div>
    </section>
    <section class="card"><div class="card-header"><h2 class="card-title">同步日志</h2></div>
      <div class="data-table-wrap"><table class="data-table">
        <thead><tr><th>同步编号</th><th>同步类型</th><th>方向</th><th>记录数</th><th>开始时间</th><th>耗时</th><th>结果</th></tr></thead>
        <tbody>
          <tr><td class="cell-mono">SYNC-0711-0006</td><td>生产订单</td><td>ERP → MES</td><td>3</td><td>09:00</td><td>1.2s</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>成功</span></td></tr>
          <tr><td class="cell-mono">SYNC-0711-0005</td><td>完工汇报</td><td>MES → ERP</td><td>12</td><td>08:45</td><td>0.8s</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>成功</span></td></tr>
          <tr><td class="cell-mono">SYNC-0711-0004</td><td>物料主数据</td><td>ERP → MES</td><td>5</td><td>08:30</td><td>2.1s</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>成功</span></td></tr>
          <tr><td class="cell-mono">SYNC-0711-0003</td><td>库存同步</td><td>ERP → MES</td><td>28</td><td>08:15</td><td>3.5s</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>部分成功</span></td></tr>
        </tbody>
      </table></div>
    </section>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">ERP集成 · 运行中 · 下次同步 09:15</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 陈管理员</span></footer>
</MesLayout>
</template>
