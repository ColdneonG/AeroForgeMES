<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="barcode-tpl">
  
  <header class="app-header"><div class="header-breadcrumb"><span>条码与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">打印模板</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="notify('新建模板 — POST /api/production/barcodes/templates')">+ 新建模板</button><span class="user-avatar">张</span></div></header>
  <main class="app-main" data-od-id="btpl-main">
    <h1 class="page-title">打印模板管理</h1>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>模板编码</th><th>模板名称</th><th>条码类型</th><th>纸张宽度(mm)</th><th>纸张高度(mm)</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td class="cell-mono">TPL-PROD-01</td><td>产品条码标准模板</td><td>产品条码</td><td>50</td><td>30</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">预览</button><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">TPL-MATL-01</td><td>物料条码标准模板</td><td>物料条码</td><td>40</td><td>25</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">预览</button><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">TPL-BOX-01</td><td>外箱标签模板</td><td>箱码</td><td>100</td><td>70</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">预览</button><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">TPL-BAT-01</td><td>批次标签模板</td><td>批次条码</td><td>60</td><td>40</td><td><span class="badge badge-status-info"><span class="badge-dot"></span>草稿</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button><button class="btn btn-primary btn-sm">启用</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">打印模板 · 4 个 · 启用 3 · 草稿 1</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span></footer>
</MesLayout>
</template>
