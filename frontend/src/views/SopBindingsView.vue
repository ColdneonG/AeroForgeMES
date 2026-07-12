<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="sop-bindings">
  
  <header class="app-header"><div class="header-breadcrumb"><span>电子SOP</span> <span class="bc-sep">/</span> <span class="bc-current">绑定配置</span></div><div class="header-actions"><button class="btn btn-primary btn-sm">+ 新建绑定</button><span class="user-avatar">张</span></div></header>
  <main class="app-main" data-od-id="sopbind-main">
    <h1 class="page-title">SOP绑定配置</h1>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>SOP文档</th><th>版本</th><th>产品</th><th>工序</th><th>绑定工位</th><th>生效日期</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td>SOP-FS40-001 · 总装作业指导书</td><td>v3.2</td><td>FS-40-B3</td><td>总装配 OP-30</td><td>A01-A05</td><td>07-01</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>生效中</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td>SOP-FS40-002 · 绕线作业指导书</td><td>v2.1</td><td>FS-40-B3</td><td>定子绕线 OP-10</td><td>WD-01-03</td><td>06-20</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>生效中</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td>SOP-FT30-001 · 台扇总装指导书</td><td>v2.3</td><td>FT-30-A1</td><td>总装配 OP-30</td><td>B01-B05</td><td>07-05</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>生效中</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">SOP绑定 · 3 条</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span></footer>
</MesLayout>
</template>
