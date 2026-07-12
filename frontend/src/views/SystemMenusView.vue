<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="sys-menus">
  
  <header class="app-header"><div class="header-breadcrumb"><span>数据与系统</span> <span class="bc-sep">/</span> <span class="bc-current">菜单配置</span></div><div class="header-actions"><button class="btn btn-primary btn-sm">+ 新建菜单</button><span class="user-avatar">陈</span></div></header>
  <main class="app-main" data-od-id="menus-main">
    <h1 class="page-title">菜单配置</h1>
    <div class="alert alert-info mb-5"><span class="alert-icon">i</span> 对接 GET /api/system/menus/tree — 菜单树由后端返回，前端动态渲染侧边栏导航。</div>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>菜单名称</th><th>图标</th><th>路由路径</th><th>排序</th><th>类型</th><th>权限标识</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td style="padding-left:8px;">📁 生产管理</td><td>—</td><td>/production</td><td>1</td><td>目录</td><td>production:view</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td style="padding-left:32px;">└ 生产订单</td><td>📋</td><td>/production/orders</td><td>1.1</td><td>菜单</td><td>order:view</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td style="padding-left:32px;">└ 车间看板</td><td>🏭</td><td>/production/shopfloor</td><td>1.5</td><td>菜单</td><td>shopfloor:view</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td style="padding-left:8px;">📁 条码与追溯</td><td>—</td><td>/barcode</td><td>2</td><td>目录</td><td>barcode:view</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td style="padding-left:32px;">└ 条码管理</td><td>—</td><td>/barcode/list</td><td>2.1</td><td>菜单</td><td>barcode:view</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">菜单配置 · 对接 /api/system/menus/tree</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 陈管理员</span></footer>
</MesLayout>
</template>
