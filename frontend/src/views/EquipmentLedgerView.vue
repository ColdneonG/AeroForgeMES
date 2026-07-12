<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="equip-ledger">
  
  <header class="app-header">
    <div class="header-breadcrumb"><span>设备与安灯</span> <span class="bc-sep">/</span> <span class="bc-current">设备台账</span></div>
    <div class="header-actions">
      <button class="btn btn-primary btn-sm" @click="notify('新增设备 — 对接设备动态资源 CRUD')">+ 新增设备</button>
      <span class="user-avatar">张</span>
    </div>
  </header>
  <main class="app-main" data-od-id="ledger-main">
    <h1 class="page-title">设备台账</h1>
    <div class="card mb-5">
      <div class="search-bar" style="margin-bottom:0;">
        <div class="form-group" style="min-width:160px;"><label class="form-label">设备名称</label><input class="form-input" type="text" placeholder="搜索..." /></div>
        <div class="form-group" style="min-width:140px;"><label class="form-label">设备类型</label><select class="form-select"><option>全部</option><option>注塑机</option><option>绕线机</option><option>压装机</option><option>动平衡机</option><option>锁螺丝机</option><option>打包机</option><option>测试仪</option></select></div>
        <div class="form-group" style="min-width:130px;"><label class="form-label">状态</label><select class="form-select"><option>全部</option><option>运行中</option><option>空闲</option><option>维修中</option><option>已报废</option></select></div>
        <div class="form-group" style="min-width:140px;"><label class="form-label">所属车间</label><select class="form-select"><option>全部</option><option>注塑车间</option><option>绕线车间</option><option>总装车间</option></select></div>
        <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">查询</button>
      </div>
    </div>
    <div class="data-table-wrap">
      <table class="data-table">
        <thead><tr><th>设备编号</th><th>设备名称</th><th>型号</th><th>类型</th><th>所属车间</th><th>购置日期</th><th>原值（元）</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr><td class="cell-mono">EQ-001</td><td>注塑机 #1</td><td>HTF120W</td><td>注塑机</td><td>注塑车间</td><td>2024-03</td><td>280,000</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>运行中</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button><button class="btn btn-ghost btn-sm">维保记录</button></td></tr>
          <tr><td class="cell-mono">EQ-002</td><td>注塑机 #2</td><td>HTF160W</td><td>注塑机</td><td>注塑车间</td><td>2024-03</td><td>360,000</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>维修中</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button><button class="btn btn-ghost btn-sm">维保记录</button></td></tr>
          <tr><td class="cell-mono">EQ-003</td><td>自动绕线机 #1</td><td>WD-4AX</td><td>绕线机</td><td>绕线车间</td><td>2024-06</td><td>185,000</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>运行中</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button><button class="btn btn-ghost btn-sm">维保记录</button></td></tr>
          <tr><td class="cell-mono">EQ-004</td><td>转子压装机 #1</td><td>RP-PN80</td><td>压装机</td><td>总装车间</td><td>2024-01</td><td>95,000</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>运行中</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
          <tr><td class="cell-mono">EQ-005</td><td>动平衡机 #1</td><td>DB-200</td><td>动平衡机</td><td>总装车间</td><td>2024-02</td><td>72,000</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>运行中</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
          <tr><td class="cell-mono">EQ-008</td><td>自动打包机 #1</td><td>PK-S600</td><td>打包机</td><td>总装车间</td><td>2024-05</td><td>48,000</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>运行中</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
        </tbody>
      </table>
      <div class="pagination"><span>共 24 台设备</span><div class="page-btns"><span class="page-btn active">1</span><span class="page-btn">2</span><span class="page-btn">›</span></div></div>
    </div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">设备台账 · 24 台 · 运行 18 · 维修 2 · 空闲 4</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span></footer>
</MesLayout>
</template>
