<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="quality-plans">
  
  <header class="app-header"><div class="header-breadcrumb"><span>质量与追溯</span> <span class="bc-sep">/</span> <RouterLink to="/quality/plans">检验方案</RouterLink> <span class="bc-sep">/</span> <span class="bc-current">方案明细</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="notify('添加检验项 — PUT /api/quality/plan-items')">+ 添加检验项</button><span class="user-avatar">李</span></div></header>
  <main class="app-main" data-od-id="pldet-main">
    <h1 class="page-title">方案明细 — FS-40 落地扇标准检验方案</h1>
    <div class="card mb-5"><div style="display:grid;grid-template-columns:1fr 1fr 1fr 1fr;gap:var(--space-4);">
      <div><span class="form-label">方案编号</span><br><span class="cell-mono">PL-FS40-001</span></div>
      <div><span class="form-label">检验类型</span><br><span class="badge badge-status-info"><span class="badge-dot"></span>IPQC 过程检验</span></div>
      <div><span class="form-label">抽样标准</span><br>GB/T 2828.1 · AQL=1.0</div>
      <div><span class="form-label">版本</span><br>v2.1 · 2026-06-28</div>
    </div></div>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>序号</th><th>检验项编码</th><th>检验项名称</th><th>规格要求</th><th>量具</th><th>抽样数</th><th>排序</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td>1</td><td class="cell-mono">CI-001</td><td>外观检查</td><td>无划伤、色差、变形</td><td>目视</td><td>50</td><td>1</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">移除</button></td></tr>
        <tr><td>2</td><td class="cell-mono">CI-002</td><td>运转测试</td><td>3 档运转正常，无异响</td><td>通电测试台</td><td>50</td><td>2</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">移除</button></td></tr>
        <tr><td>3</td><td class="cell-mono">CI-003</td><td>风速测试</td><td>高速档风速 ≥4.5 m/s</td><td>风速仪</td><td>10</td><td>3</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">移除</button></td></tr>
        <tr><td>4</td><td class="cell-mono">CI-004</td><td>绝缘耐压</td><td>AC 1500V / 1min 无击穿</td><td>耐压测试仪</td><td>50</td><td>4</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">移除</button></td></tr>
        <tr><td>5</td><td class="cell-mono">CI-005</td><td>功率测试</td><td>额定功率 75W±10%</td><td>功率计</td><td>10</td><td>5</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">移除</button></td></tr>
        <tr><td>6</td><td class="cell-mono">CI-006</td><td>噪音测试</td><td>高速档噪音 ≤55dB(A)</td><td>噪音计</td><td>5</td><td>6</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">移除</button></td></tr>
        <tr><td>7</td><td class="cell-mono">CI-010</td><td>网罩牢固度</td><td>施加 50N 推力不脱落</td><td>推拉力计</td><td>10</td><td>7</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">移除</button></td></tr>
        <tr><td>8</td><td class="cell-mono">CI-012</td><td>摇头机构</td><td>左右摆角 80°±5°</td><td>角度尺</td><td>5</td><td>8</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">移除</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">方案明细 · PL-FS40-001 · 8 个检验项</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 李检验</span></footer>
</MesLayout>
</template>
