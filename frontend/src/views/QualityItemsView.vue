<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="quality-items">
  
  <header class="app-header"><div class="header-breadcrumb"><span>质量与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">检验项管理</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="notify('新建检验项 — POST /api/quality/items')">+ 新建检验项</button><span class="user-avatar">李</span></div></header>
  <main class="app-main" data-od-id="items-main">
    <h1 class="page-title">检验项管理</h1>
    <div class="card mb-5"><div class="search-bar" style="margin-bottom:0;">
      <div class="form-group" style="min-width:160px;"><label class="form-label">检验项名称</label><input class="form-input" type="text" placeholder="搜索..." /></div>
      <div class="form-group" style="min-width:140px;"><label class="form-label">分类</label><select class="form-select"><option>全部</option><option>外观检查</option><option>尺寸测量</option><option>电气性能</option><option>功能测试</option><option>安全测试</option></select></div>
      <div class="form-group" style="min-width:130px;"><label class="form-label">状态</label><select class="form-select"><option>全部</option><option>启用</option><option>停用</option></select></div>
      <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">筛选</button>
    </div></div>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>检验项编码</th><th>检验项名称</th><th>分类</th><th>规格要求</th><th>量具</th><th>单位</th><th>公差范围</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td class="cell-mono">CI-001</td><td>外观检查</td><td>外观检查</td><td>无划伤、色差、变形</td><td>目视</td><td>—</td><td>—</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">CI-002</td><td>运转测试</td><td>功能测试</td><td>3 档运转正常，无异响</td><td>通电测试台</td><td>—</td><td>—</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">CI-003</td><td>风速测试</td><td>功能测试</td><td>高速档风速 ≥4.5 m/s</td><td>风速仪</td><td>m/s</td><td>≥4.5</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">CI-004</td><td>绝缘耐压</td><td>安全测试</td><td>AC 1500V / 1min 无击穿</td><td>耐压测试仪</td><td>V</td><td>≥1500</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">CI-005</td><td>功率测试</td><td>电气性能</td><td>额定功率 75W±10%</td><td>功率计</td><td>W</td><td>67.5–82.5</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">CI-006</td><td>噪音测试</td><td>功能测试</td><td>高速档噪音 ≤55dB(A)</td><td>噪音计</td><td>dB(A)</td><td>≤55</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">检验项管理 · 12 个检验项</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 李检验</span></footer>
</MesLayout>
</template>
