<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="orders">

  <!-- Sidebar -->
    

  <!-- Header -->
  <header class="app-header" data-od-id="orders-header">
    <div class="header-breadcrumb">
      <span>生产管理</span> <span class="bc-sep">/</span> <span class="bc-current">生产订单</span>
    </div>
    <div class="header-actions">
      <button class="btn btn-primary btn-sm" @click="notify('新建订单 — 功能开发中')">+ 新建订单</button>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <!-- Main Content -->
  <main class="app-main" data-od-id="orders-main">
    <h1 class="page-title">生产订单</h1>

    <!-- Filters -->
    <div class="card mb-5" data-od-id="orders-filters">
      <div class="search-bar" style="margin-bottom:0;">
        <div class="form-group" style="min-width:180px;">
          <label class="form-label">订单号</label>
          <input class="form-input" type="text" placeholder="搜索订单号..." />
        </div>
        <div class="form-group" style="min-width:160px;">
          <label class="form-label">产品类型</label>
          <select class="form-select"><option>全部</option><option>FS-40 落地扇</option><option>FT-30 台扇</option><option>FS-35 壁扇</option><option>FH-18 夹扇</option><option>FC-56 吊扇</option></select>
        </div>
        <div class="form-group" style="min-width:140px;">
          <label class="form-label">订单状态</label>
          <select class="form-select"><option>全部</option><option>待排产</option><option>生产中</option><option>待质检</option><option>已完成</option><option>已暂停</option></select>
        </div>
        <div class="form-group" style="min-width:130px;">
          <label class="form-label">计划日期</label>
          <input class="form-input" type="date" value="2026-07-11" />
        </div>
        <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">查询</button>
        <button class="btn btn-ghost btn-sm" style="align-self:flex-end;">重置</button>
      </div>
    </div>

    <!-- Table -->
    <div class="data-table-wrap" data-od-id="orders-table">
      <table class="data-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>产品名称</th>
            <th>规格型号</th>
            <th>计划数量</th>
            <th>已完工</th>
            <th>计划日期</th>
            <th>产线</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="cell-mono"><RouterLink to="/production-order-detail">MO-20260711-0032</RouterLink></td>
            <td>落地扇</td><td>FS-40-B3（机械款）</td><td>500</td><td>360</td>
            <td>2026-07-11</td><td>总装线 A</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot"></span>生产中</span></td>
            <td class="cell-actions">
              <RouterLink to="/production-order-detail" class="btn btn-ghost btn-sm">详情</RouterLink>
            </td>
          </tr>
          <tr>
            <td class="cell-mono"><RouterLink to="/production-order-detail">MO-20260711-0033</RouterLink></td>
            <td>台扇</td><td>FT-30-A1（标准款）</td><td>800</td><td>360</td>
            <td>2026-07-11</td><td>总装线 B</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot"></span>生产中</span></td>
            <td class="cell-actions">
              <RouterLink to="/production-order-detail" class="btn btn-ghost btn-sm">详情</RouterLink>
            </td>
          </tr>
          <tr>
            <td class="cell-mono"><RouterLink to="/production-order-detail">MO-20260710-0031</RouterLink></td>
            <td>壁扇</td><td>FS-35-W2（遥控款）</td><td>300</td><td>282</td>
            <td>2026-07-10</td><td>总装线 A</td>
            <td><span class="badge badge-status-warn"><span class="badge-dot"></span>待质检</span></td>
            <td class="cell-actions">
              <RouterLink to="/production-order-detail" class="btn btn-ghost btn-sm">详情</RouterLink>
            </td>
          </tr>
          <tr>
            <td class="cell-mono"><RouterLink to="/production-order-detail">MO-20260710-0030</RouterLink></td>
            <td>落地扇</td><td>FS-40-R1（遥控款）</td><td>400</td><td>112</td>
            <td>2026-07-10</td><td>总装线 A</td>
            <td><span class="badge badge-status-info"><span class="badge-dot"></span>物料等待</span></td>
            <td class="cell-actions">
              <RouterLink to="/production-order-detail" class="btn btn-ghost btn-sm">详情</RouterLink>
            </td>
          </tr>
          <tr>
            <td class="cell-mono">MO-20260709-0029</td>
            <td>夹扇</td><td>FH-18-C2（USB 款）</td><td>1,200</td><td>1,200</td>
            <td>2026-07-09</td><td>总装线 C</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已完成</span></td>
            <td class="cell-actions">
              <span class="btn btn-ghost btn-sm" style="opacity:0.4;">详情</span>
            </td>
          </tr>
          <tr>
            <td class="cell-mono">MO-20260708-0028</td>
            <td>吊扇</td><td>FC-56-D1（铁叶标准款）</td><td>200</td><td>200</td>
            <td>2026-07-08</td><td>总装线 C</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已完成</span></td>
            <td class="cell-actions">
              <span class="btn btn-ghost btn-sm" style="opacity:0.4;">详情</span>
            </td>
          </tr>
          <tr>
            <td class="cell-mono">MO-20260711-0034</td>
            <td>落地扇</td><td>FS-40-B3（机械款）</td><td>600</td><td>0</td>
            <td>2026-07-12</td><td>总装线 B</td>
            <td><span class="badge badge-status-draft">待排产</span></td>
            <td class="cell-actions">
              <span class="btn btn-ghost btn-sm" style="opacity:0.4;">详情</span>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <span>共 12 条记录</span>
        <div class="page-btns">
          <span class="page-btn">‹</span>
          <span class="page-btn active">1</span>
          <span class="page-btn">2</span>
          <span class="page-btn">›</span>
        </div>
      </div>
    </div>
  </main>

  <!-- Status Bar -->
  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">在线设备 22/24</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">生产订单 · 12 条记录</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span>
  </footer>
</MesLayout>
</template>
