<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="defects">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>质量与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">缺陷记录</span></div>
    <div class="header-actions">
      <button class="btn btn-primary btn-sm" @click="notify('登记缺陷 — POST /api/quality/defect-records')">+ 登记缺陷</button>
      <span class="user-avatar">李</span>
    </div>
  </header>

  <main class="app-main" data-od-id="defects-main">
    <h1 class="page-title">缺陷记录管理</h1>

    <div class="card mb-5">
      <div class="search-bar" style="margin-bottom:0;">
        <div class="form-group" style="min-width:160px;"><label class="form-label">缺陷类型</label><select class="form-select"><option>全部</option><option>外观划伤</option><option>运转异响</option><option>色差</option><option>尺寸超差</option><option>电气故障</option></select></div>
        <div class="form-group" style="min-width:130px;"><label class="form-label">严重等级</label><select class="form-select"><option>全部</option><option>轻微</option><option>一般</option><option>严重</option><option>致命</option></select></div>
        <div class="form-group" style="min-width:130px;"><label class="form-label">处理状态</label><select class="form-select"><option>全部</option><option>待处理</option><option>处理中</option><option>已关闭</option></select></div>
        <div class="form-group" style="min-width:130px;"><label class="form-label">关联工单</label><input class="form-input" type="text" placeholder="工单号..." /></div>
        <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">筛选</button>
      </div>
    </div>

    <div class="data-table-wrap" data-od-id="defects-table">
      <table class="data-table">
        <thead><tr><th>缺陷编号</th><th>关联检验</th><th>工单</th><th>缺陷类型</th><th>描述</th><th>严重等级</th><th>处理方式</th><th>责任人</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr><td class="cell-mono">DEF-0042</td><td>QC-0152</td><td>MO-0032</td><td>外观划伤</td><td>底座表面轻微划伤，长度 3mm</td>
            <td><span class="badge badge-status-warn"><span class="badge-dot"></span>轻微</span></td><td>让步接收</td><td>陈作业</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已关闭</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
          <tr><td class="cell-mono">DEF-0041</td><td>QC-0152</td><td>MO-0032</td><td>运转异响</td><td>2 档运转有轻微"嗒嗒"声</td>
            <td><span class="badge badge-status-error"><span class="badge-dot"></span>严重</span></td><td>返工维修</td><td>黄作业</td>
            <td><span class="badge badge-status-warn"><span class="badge-dot"></span>处理中</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button> <button class="btn btn-primary btn-sm">闭环</button></td></tr>
          <tr><td class="cell-mono">DEF-0040</td><td>QC-0151</td><td>—</td><td>色差</td><td>扇叶颜色批次间偏差 ΔE=2.3</td>
            <td><span class="badge badge-status-warn"><span class="badge-dot"></span>轻微</span></td><td>让步接收</td><td>—</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已关闭</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
          <tr><td class="cell-mono">DEF-0039</td><td>QC-0148</td><td>MO-0032</td><td>尺寸超差</td><td>转子轴径 Φ12.05mm（要求 Φ12.00±0.02）</td>
            <td><span class="badge badge-status-error"><span class="badge-dot"></span>严重</span></td><td>报废重做</td><td>刘作业</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已关闭</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
          <tr><td class="cell-mono">DEF-0038</td><td>QC-0145</td><td>MO-0029</td><td>电气故障</td><td>通电无反应，排查为开关触点虚焊</td>
            <td><span class="badge badge-status-error"><span class="badge-dot"></span>严重</span></td><td>返工维修</td><td>周作业</td>
            <td><span class="badge badge-status-warn"><span class="badge-dot"></span>处理中</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button> <button class="btn btn-primary btn-sm">闭环</button></td></tr>
          <tr><td class="cell-mono">DEF-0037</td><td>QC-0142</td><td>MO-0031</td><td>外观划伤</td><td>网罩镀铬层局部脱落 Φ5mm</td>
            <td><span class="badge badge-status-warn"><span class="badge-dot"></span>轻微</span></td><td>让步接收</td><td>林作业</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已关闭</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
        </tbody>
      </table>
      <div class="pagination"><span>共 9 条记录</span><div class="page-btns"><span class="page-btn">‹</span><span class="page-btn active">1</span><span class="page-btn">›</span></div></div>
    </div>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">缺陷记录 · 9 条 · 已关闭 6 · 处理中 3</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 李检验</span>
  </footer>
</MesLayout>
</template>
