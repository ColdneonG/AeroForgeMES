<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="quality-categories">
  
  <header class="app-header"><div class="header-breadcrumb"><span>质量与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">检验项分类</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="notify('新建分类 — POST /api/quality/item-categories')">+ 新建分类</button><span class="user-avatar">李</span></div></header>
  <main class="app-main" data-od-id="qccat-main">
    <h1 class="page-title">检验项分类管理</h1>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>分类编码</th><th>分类名称</th><th>检验项数</th><th>排序</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td class="cell-mono">QCAT-01</td><td>外观检查</td><td>2</td><td>1</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">QCAT-02</td><td>尺寸测量</td><td>3</td><td>2</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">QCAT-03</td><td>电气性能</td><td>2</td><td>3</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">QCAT-04</td><td>功能测试</td><td>3</td><td>4</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">QCAT-05</td><td>安全测试</td><td>2</td><td>5</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">检验项分类 · 5 个</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 李检验</span></footer>
</MesLayout>
</template>
