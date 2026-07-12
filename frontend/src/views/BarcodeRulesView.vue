<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="barcode-rules">
  
  <header class="app-header"><div class="header-breadcrumb"><span>条码与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">条码规则</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="notify('新建规则 — POST /api/production/barcodes/rules')">+ 新建规则</button><span class="user-avatar">张</span></div></header>
  <main class="app-main" data-od-id="brules-main">
    <h1 class="page-title">条码规则管理</h1>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>规则编码</th><th>规则名称</th><th>条码类型</th><th>编码表达式</th><th>流水号长度</th><th>条码模式</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td class="cell-mono">BR-FS40-01</td><td>FS-40 落地扇产品条码</td><td>产品条码</td><td>FS40{type}{date8}{seq:5}</td><td>5</td><td>自动生成</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button><button class="btn btn-ghost btn-sm">停用</button></td></tr>
        <tr><td class="cell-mono">BR-FT30-01</td><td>FT-30 台扇产品条码</td><td>产品条码</td><td>FT30{type}{date8}{seq:5}</td><td>5</td><td>自动生成</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button><button class="btn btn-ghost btn-sm">停用</button></td></tr>
        <tr><td class="cell-mono">BR-MTR-01</td><td>异步电动机物料条码</td><td>物料条码</td><td>MTR{type}{date8}{seq:4}</td><td>4</td><td>自动生成</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">BR-BLD-01</td><td>扇叶物料条码</td><td>物料条码</td><td>BLD{type}{date8}{seq:4}</td><td>4</td><td>自动生成</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">BR-BOX-01</td><td>箱码规则</td><td>箱码</td><td>BOX{type}{date6}{seq:6}</td><td>6</td><td>手动触发</td><td><span class="badge badge-status-info"><span class="badge-dot"></span>草稿</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button><button class="btn btn-primary btn-sm">启用</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">条码规则 · 5 条 · 启用 4 · 草稿 1</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span></footer>
</MesLayout>
</template>
