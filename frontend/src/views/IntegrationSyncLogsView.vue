<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="sync-logs">
  
  <header class="app-header"><div class="header-breadcrumb"><span>集成管理</span> <span class="bc-sep">/</span> <span class="bc-current">同步日志</span></div><div class="header-actions"><button class="btn btn-secondary btn-sm">导出日志</button><span class="user-avatar">陈</span></div></header>
  <main class="app-main" data-od-id="synclog-main">
    <h1 class="page-title">集成同步日志</h1>
    <div class="card mb-5"><div class="search-bar" style="margin-bottom:0;">
      <div class="form-group" style="min-width:140px;"><label class="form-label">同步类型</label><select class="form-select"><option>全部</option><option>生产订单</option><option>完工汇报</option><option>物料主数据</option><option>库存同步</option></select></div>
      <div class="form-group" style="min-width:130px;"><label class="form-label">结果</label><select class="form-select"><option>全部</option><option>成功</option><option>部分成功</option><option>失败</option></select></div>
      <div class="form-group" style="min-width:140px;"><label class="form-label">时间范围</label><input class="form-input" type="date" value="2026-07-11" /></div>
      <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">查询</button>
    </div></div>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>同步编号</th><th>类型</th><th>方向</th><th>记录数</th><th>成功</th><th>失败</th><th>耗时</th><th>时间</th><th>详情</th></tr></thead>
      <tbody>
        <tr><td class="cell-mono">SYNC-0711-0006</td><td>生产订单</td><td>ERP→MES</td><td>3</td><td>3</td><td>0</td><td>1.2s</td><td>09:00</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>成功</span></td></tr>
        <tr><td class="cell-mono">SYNC-0711-0005</td><td>完工汇报</td><td>MES→ERP</td><td>12</td><td>12</td><td>0</td><td>0.8s</td><td>08:45</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>成功</span></td></tr>
        <tr><td class="cell-mono">SYNC-0711-0003</td><td>库存同步</td><td>ERP→MES</td><td>28</td><td>25</td><td>3</td><td>3.5s</td><td>08:15</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>部分成功</span></td></tr>
        <tr><td class="cell-mono">SYNC-0710-0028</td><td>物料主数据</td><td>ERP→MES</td><td>15</td><td>0</td><td>15</td><td>12.3s</td><td>22:00</td><td><span class="badge badge-status-error"><span class="badge-dot"></span>失败</span></td></tr>
      </tbody>
    </table><div class="pagination"><span>共 1,258 条</span><div class="page-btns"><span class="page-btn active">1</span><span class="page-btn">2</span><span class="page-btn">3</span><span class="page-btn">›</span></div></div></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">同步日志 · 1,258 条记录</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 陈管理员</span></footer>
</MesLayout>
</template>
