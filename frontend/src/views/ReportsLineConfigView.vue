<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="ccenter">
  
  <header class="app-header"><div class="header-breadcrumb"><span>数据与系统</span> <span class="bc-sep">/</span> <span class="bc-current">产线看板配置</span></div><div class="header-actions"><button class="btn btn-primary btn-sm">+ 新建配置</button><span class="user-avatar">张</span></div></header>
  <main class="app-main" data-od-id="lconf-main">
    <h1 class="page-title">产线看板配置</h1>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>配置名称</th><th>产线</th><th>显示指标</th><th>刷新频率</th><th>更新时间</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td>总装线 A 看板</td><td>总装线 A</td><td>产量/节拍/稼动/不良</td><td>30 秒</td><td>07-01</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button><button class="btn btn-ghost btn-sm">预览</button></td></tr>
        <tr><td>总装线 B 看板</td><td>总装线 B</td><td>产量/节拍/稼动</td><td>60 秒</td><td>06-25</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button><button class="btn btn-ghost btn-sm">预览</button></td></tr>
        <tr><td>绕线车间看板</td><td>绕线车间</td><td>产量/稼动/异常</td><td>60 秒</td><td>06-20</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">产线看板配置 · 3 条</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span></footer>
</MesLayout>
</template>
