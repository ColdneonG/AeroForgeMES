<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="sop-versions">
  
  <header class="app-header"><div class="header-breadcrumb"><span>电子SOP</span> <span class="bc-sep">/</span> <span class="bc-current">版本管理</span></div><div class="header-actions"><span class="user-avatar">张</span></div></header>
  <main class="app-main" data-od-id="sopv-main">
    <h1 class="page-title">SOP版本管理 — FS-40 落地扇总装作业指导书</h1>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>版本号</th><th>变更说明</th><th>作者</th><th>发布日期</th><th>审批人</th><th>附件数</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td class="cell-mono">v3.2</td><td>更新扇叶安装扭矩参数（3.5→4.0 N·m），新增遥控款兼容说明</td><td>张工</td><td>07-10</td><td>陈晓燕</td><td>5</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>当前版本</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">查看</button><button class="btn btn-ghost btn-sm">对比</button></td></tr>
        <tr><td class="cell-mono">v3.1</td><td>修正电源线固定方式图示，增加绝缘测试步骤</td><td>李组长</td><td>06-28</td><td>陈晓燕</td><td>4</td><td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已归档</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">查看</button><button class="btn btn-ghost btn-sm">对比</button></td></tr>
        <tr><td class="cell-mono">v3.0</td><td>新增运转测试步骤，网罩卡扣安装细化</td><td>张工</td><td>06-15</td><td>陈晓燕</td><td>4</td><td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已归档</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">查看</button></td></tr>
        <tr><td class="cell-mono">v2.1</td><td>更新立杆安装高度参考值（1100→1150mm）</td><td>张工</td><td>05-20</td><td>陈晓燕</td><td>3</td><td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已归档</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">查看</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">SOP版本 · SOP-FS40-001 · 4 个版本</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span></footer>
</MesLayout>
</template>
