<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="barcode-types">
  
  <header class="app-header"><div class="header-breadcrumb"><span>条码与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">条码类型</span></div><div class="header-actions"><button class="btn btn-primary btn-sm">+ 新建类型</button><span class="user-avatar">张</span></div></header>
  <main class="app-main" data-od-id="btypes-main">
    <h1 class="page-title">条码类型管理</h1>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>类型编码</th><th>类型名称</th><th>条码标准</th><th>关联规则数</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td class="cell-mono">PRODUCT</td><td>产品条码</td><td>Code128</td><td>3</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">MATERIAL</td><td>物料条码</td><td>Code128</td><td>2</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">BATCH</td><td>批次条码</td><td>QR Code</td><td>1</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">BOX</td><td>箱码</td><td>Code128</td><td>1</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">条码类型 · 4 个</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span></footer>
</MesLayout>
</template>
