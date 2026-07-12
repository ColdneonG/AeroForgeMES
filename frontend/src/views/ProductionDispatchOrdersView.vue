<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="dispatch">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>生产管理</span> <span class="bc-sep">/</span> <span class="bc-current">派工单</span></div>
    <div class="header-actions">
      <button class="btn btn-primary btn-sm" @click="notify('创建派工单 — 对接 POST /api/production/dispatch-orders')">+ 新建派工</button>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="dispatch-main">
    <h1 class="page-title">派工单管理</h1>

    <div class="card mb-5" data-od-id="dispatch-filters">
      <div class="search-bar" style="margin-bottom:0;">
        <div class="form-group" style="min-width:180px;">
          <label class="form-label">派工单号</label>
          <input class="form-input" type="text" placeholder="搜索派工单号..." />
        </div>
        <div class="form-group" style="min-width:160px;">
          <label class="form-label">关联工单</label>
          <input class="form-input" type="text" placeholder="工单号..." />
        </div>
        <div class="form-group" style="min-width:140px;">
          <label class="form-label">产线</label>
          <select class="form-select"><option>全部</option><option>总装线 A</option><option>总装线 B</option><option>总装线 C</option><option>绕线车间</option></select>
        </div>
        <div class="form-group" style="min-width:130px;">
          <label class="form-label">状态</label>
          <select class="form-select"><option>全部</option><option>待确认</option><option>已确认</option><option>执行中</option><option>已完成</option></select>
        </div>
        <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">查询</button>
      </div>
    </div>

    <div class="data-table-wrap" data-od-id="dispatch-table">
      <table class="data-table">
        <thead><tr><th>派工单号</th><th>关联工单</th><th>产品</th><th>派工数量</th><th>已完成</th><th>产线/工位</th><th>派工日期</th><th>班次</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr>
            <td class="cell-mono">DO-0711-0052</td><td>MO-20260711-0032</td><td>FS-40-B3 落地扇</td><td>200</td><td>160</td><td>总装线 A / 底座-网罩</td><td>07-11</td><td>白班</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot"></span>执行中</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button> <button class="btn btn-ghost btn-sm">报工</button></td>
          </tr>
          <tr>
            <td class="cell-mono">DO-0711-0051</td><td>MO-20260711-0033</td><td>FT-30-A1 台扇</td><td>300</td><td>160</td><td>总装线 B / 电机-测试</td><td>07-11</td><td>白班</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot"></span>执行中</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button> <button class="btn btn-ghost btn-sm">报工</button></td>
          </tr>
          <tr>
            <td class="cell-mono">DO-0711-0050</td><td>MO-20260710-0031</td><td>FS-35-W2 壁扇</td><td>150</td><td>150</td><td>总装线 A / 全工位</td><td>07-10</td><td>白班</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已完成</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td>
          </tr>
          <tr>
            <td class="cell-mono">DO-0711-0053</td><td>MO-20260710-0030</td><td>FS-40-R1 落地扇（遥控）</td><td>200</td><td>0</td><td>绕线车间 / 定子绕线</td><td>07-11</td><td>白班</td>
            <td><span class="badge badge-status-info"><span class="badge-dot"></span>待确认</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button> <button class="btn btn-primary btn-sm">确认接收</button></td>
          </tr>
          <tr>
            <td class="cell-mono">DO-0710-0049</td><td>MO-20260709-0029</td><td>FH-18-C2 夹扇</td><td>400</td><td>400</td><td>总装线 C / 全工位</td><td>07-09</td><td>白班</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已完成</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td>
          </tr>
          <tr>
            <td class="cell-mono">DO-0711-0054</td><td>MO-20260711-0033</td><td>FT-30-A1 台扇</td><td>300</td><td>0</td><td>总装线 B / 网罩-包装</td><td>07-11</td><td>晚班</td>
            <td><span class="badge badge-status-draft">待派工</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-primary btn-sm">下达</button></td>
          </tr>
        </tbody>
      </table>
      <div class="pagination"><span>共 8 条记录</span><div class="page-btns"><span class="page-btn">‹</span><span class="page-btn active">1</span><span class="page-btn">›</span></div></div>
    </div>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">派工单 · 8 条 · 执行中 3 · 已完成 2 · 待确认 2 · 待派工 1</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span>
  </footer>
</MesLayout>
</template>
