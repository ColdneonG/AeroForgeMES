<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="quality-plans">
  
  <header class="app-header"><div class="header-breadcrumb"><span>质量与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">检验方案</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="notify('新建方案 — POST /api/quality/plans')">+ 新建方案</button><span class="user-avatar">李</span></div></header>
  <main class="app-main" data-od-id="plans-main">
    <h1 class="page-title">检验方案管理</h1>
    <div class="card mb-5"><div class="search-bar" style="margin-bottom:0;">
      <div class="form-group" style="min-width:160px;"><label class="form-label">方案名称</label><input class="form-input" type="text" placeholder="搜索..." /></div>
      <div class="form-group" style="min-width:140px;"><label class="form-label">产品</label><select class="form-select"><option>全部</option><option>FS-40 落地扇</option><option>FT-30 台扇</option><option>FS-35 壁扇</option></select></div>
      <div class="form-group" style="min-width:130px;"><label class="form-label">检验类型</label><select class="form-select"><option>全部</option><option>IQC 来料</option><option>IPQC 过程</option><option>OQC 成品</option></select></div>
      <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">筛选</button>
    </div></div>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>方案编号</th><th>方案名称</th><th>产品</th><th>检验类型</th><th>检验项数</th><th>抽样标准</th><th>AQL</th><th>版本</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td class="cell-mono">PL-FS40-001</td><td>FS-40 落地扇标准检验方案</td><td>FS-40-B3</td><td>IPQC</td><td>8</td><td>GB/T 2828.1</td><td>1.0</td><td>v2.1</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">明细</button><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">PL-FT30-001</td><td>FT-30 台扇标准检验方案</td><td>FT-30-A1</td><td>IPQC</td><td>6</td><td>GB/T 2828.1</td><td>1.0</td><td>v1.8</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">明细</button><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">PL-MTR-001</td><td>异步电动机来料检验方案</td><td>75W 电动机</td><td>IQC</td><td>5</td><td>GB/T 2828.1</td><td>0.65</td><td>v1.5</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">明细</button></td></tr>
        <tr><td class="cell-mono">PL-FS40-002</td><td>FS-40 落地扇成品检验方案</td><td>FS-40-B3</td><td>OQC</td><td>5</td><td>GB/T 2828.1</td><td>1.5</td><td>v2.0</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">明细</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">检验方案 · 4 个启用</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 李检验</span></footer>
</MesLayout>
</template>
