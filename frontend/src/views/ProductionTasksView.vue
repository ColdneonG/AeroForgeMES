<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="tasks">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>生产管理</span> <span class="bc-sep">/</span> <span class="bc-current">生产任务</span></div>
    <div class="header-actions">
      <span style="font-size:var(--text-caption);opacity:0.5;">今日任务 16 项</span>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="tasks-main">
    <div class="flex justify-between items-center mb-5">
      <h1 class="page-title" style="margin-bottom:0;">生产任务</h1>
      <div class="flex gap-3">
        <button class="btn btn-secondary btn-sm">批量领用</button>
        <button class="btn btn-primary btn-sm">批量报工</button>
      </div>
    </div>

    <div class="card mb-5" data-od-id="tasks-filters">
      <div class="search-bar" style="margin-bottom:0;">
        <div class="form-group" style="min-width:160px;"><label class="form-label">任务编号</label><input class="form-input" type="text" placeholder="搜索..." /></div>
        <div class="form-group" style="min-width:140px;"><label class="form-label">工位</label><select class="form-select"><option>全部</option><option>底座组装</option><option>电机安装</option><option>扇叶安装</option><option>网罩组装</option><option>通电测试</option><option>绕线</option><option>压装</option></select></div>
        <div class="form-group" style="min-width:130px;"><label class="form-label">状态</label><select class="form-select"><option>全部</option><option>待领用</option><option>执行中</option><option>已完工</option><option>已质检</option></select></div>
        <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">筛选</button>
      </div>
    </div>

    <div class="data-table-wrap" data-od-id="tasks-table">
      <table class="data-table">
        <thead><tr><th>任务编号</th><th>关联派工</th><th>产品/工序</th><th>工位</th><th>任务数量</th><th>已完工</th><th>作业员</th><th>开始时间</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr><td class="cell-mono">TK-0711-0182</td><td>DO-0711-0052</td><td>FS-40-B3 / 底座锁付</td><td>底座组装 A01</td><td>200</td><td>160</td><td>陈作业</td><td>08:15</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot"></span>执行中</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">报工</button> <button class="btn btn-ghost btn-sm">暂停</button></td></tr>
          <tr><td class="cell-mono">TK-0711-0183</td><td>DO-0711-0052</td><td>FS-40-B3 / 电机安装</td><td>电机安装 A02</td><td>200</td><td>158</td><td>刘作业</td><td>08:18</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot"></span>执行中</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">报工</button> <button class="btn btn-ghost btn-sm">暂停</button></td></tr>
          <tr><td class="cell-mono">TK-0711-0184</td><td>DO-0711-0052</td><td>FS-40-B3 / 扇叶锁紧</td><td>扇叶安装 A03</td><td>200</td><td>155</td><td>黄作业</td><td>08:20</td>
            <td><span class="badge badge-status-warn"><span class="badge-dot"></span>堆积预警</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">报工</button> <button class="btn btn-primary btn-sm">求助</button></td></tr>
          <tr><td class="cell-mono">TK-0711-0185</td><td>DO-0711-0051</td><td>FT-30-A1 / 电机安装</td><td>电机安装 B02</td><td>300</td><td>160</td><td>赵作业</td><td>08:10</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot"></span>执行中</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">报工</button></td></tr>
          <tr><td class="cell-mono">TK-0710-0178</td><td>DO-0710-0050</td><td>FS-35-W2 / 通电测试</td><td>通电测试 A05</td><td>150</td><td>150</td><td>周作业</td><td>16:30</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已完工</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
          <tr><td class="cell-mono">TK-0711-0186</td><td>DO-0711-0053</td><td>FS-40-R1 / 定子绕线</td><td>绕线工位 #3</td><td>200</td><td>0</td><td>—</td><td>—</td>
            <td><span class="badge badge-status-info"><span class="badge-dot"></span>待领用</span></td>
            <td class="cell-actions"><button class="btn btn-primary btn-sm">领用</button></td></tr>
        </tbody>
      </table>
      <div class="pagination"><span>共 16 条记录</span><div class="page-btns"><span class="page-btn">‹</span><span class="page-btn active">1</span><span class="page-btn">2</span><span class="page-btn">›</span></div></div>
    </div>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">生产任务 · 16 条 · 执行中 10 · 待领用 3 · 已完工 3</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span>
  </footer>
</MesLayout>
</template>
