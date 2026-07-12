<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="users">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>数据与系统</span> <span class="bc-sep">/</span> <span class="bc-current">用户管理</span></div>
    <div class="header-actions">
      <button class="btn btn-primary btn-sm" @click="notify('添加用户 — 功能开发中')">+ 添加用户</button>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="users-main">
    <h1 class="page-title">用户与权限管理</h1>

    <div class="card mb-5">
      <div class="search-bar" style="margin-bottom:0;">
        <div class="form-group" style="min-width:200px;">
          <label class="form-label">搜索</label>
          <input class="form-input" type="text" placeholder="工号、姓名或用户名..." />
        </div>
        <div class="form-group" style="min-width:140px;">
          <label class="form-label">角色</label>
          <select class="form-select"><option>全部</option><option>系统管理员</option><option>生产主管</option><option>班组长</option><option>质检员</option><option>作业员</option><option>仓管员</option></select>
        </div>
        <div class="form-group" style="min-width:120px;">
          <label class="form-label">状态</label>
          <select class="form-select"><option>全部</option><option>启用</option><option>停用</option></select>
        </div>
        <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">查询</button>
      </div>
    </div>

    <div class="data-table-wrap" data-od-id="users-table">
      <table class="data-table">
        <thead><tr><th>工号</th><th>姓名</th><th>用户名</th><th>角色</th><th>所属部门</th><th>手机号</th><th>状态</th><th>最后登录</th><th>操作</th></tr></thead>
        <tbody>
          <tr><td class="cell-mono">EMP-0001</td><td>张建国</td><td>zhangjg</td><td>生产主管</td><td>生产部</td><td>138****6201</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="text-muted">今天 08:12</td><td><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-ghost btn-sm" style="color:var(--accent);">停用</button></td></tr>
          <tr><td class="cell-mono">EMP-0005</td><td>李明辉</td><td>limh</td><td>班组长</td><td>总装车间</td><td>139****4532</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="text-muted">今天 07:55</td><td><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-ghost btn-sm" style="color:var(--accent);">停用</button></td></tr>
          <tr><td class="cell-mono">EMP-0012</td><td>王丽芳</td><td>wanglf</td><td>质检员</td><td>质量部</td><td>137****8901</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="text-muted">今天 08:30</td><td><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-ghost btn-sm" style="color:var(--accent);">停用</button></td></tr>
          <tr><td class="cell-mono">EMP-0018</td><td>刘伟强</td><td>liuwq</td><td>作业员</td><td>总装车间</td><td>136****3456</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="text-muted">今天 07:50</td><td><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-ghost btn-sm" style="color:var(--accent);">停用</button></td></tr>
          <tr><td class="cell-mono">EMP-0025</td><td>赵永刚</td><td>zhaoyg</td><td>仓管员</td><td>仓储部</td><td>135****7890</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="text-muted">今天 07:40</td><td><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-ghost btn-sm" style="color:var(--accent);">停用</button></td></tr>
          <tr><td class="cell-mono">EMP-0030</td><td>陈晓燕</td><td>chenxy</td><td>系统管理员</td><td>信息部</td><td>133****2109</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="text-muted">今天 09:05</td><td><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
          <tr><td class="cell-mono">EMP-0076</td><td>孙志明</td><td>sunzm</td><td>作业员</td><td>绕线车间</td><td>132****5678</td><td><span class="badge badge-status-draft">停用</span></td><td class="text-muted">07-05 16:22</td><td><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-ghost btn-sm">启用</button></td></tr>
        </tbody>
      </table>
      <div class="pagination">
        <span>共 88 条记录</span>
        <div class="page-btns">
          <span class="page-btn">‹</span>
          <span class="page-btn active">1</span>
          <span class="page-btn">2</span>
          <span class="page-btn">3</span>
          <span class="page-btn">4</span>
          <span class="page-btn">5</span>
          <span class="page-btn">›</span>
        </div>
      </div>
    </div>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">用户管理 · 88 人 · 启用 86 · 停用 2</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span>
  </footer>
</MesLayout>
</template>
