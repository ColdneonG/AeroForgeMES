<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="inspexec">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>质量与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">检验执行</span></div>
    <div class="header-actions">
      <span style="font-size:var(--text-caption);opacity:0.5;">检验单 QC-0711-0152</span>
      <span class="user-avatar">李</span>
    </div>
  </header>

  <main class="app-main" data-od-id="inspexec-main">
    <h1 class="page-title">检验执行 — QC-0711-0152</h1>

    <div class="card mb-5" data-od-id="inspexec-info">
      <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:var(--space-5);">
        <div><span class="form-label">关联工单</span><br><span class="cell-mono">MO-20260711-0032</span></div>
        <div><span class="form-label">产品</span><br><span>FS-40-B3 落地扇（机械款）</span></div>
        <div><span class="form-label">检验类型</span><br><span class="badge badge-status-info"><span class="badge-dot"></span>过程检验 IPQC</span></div>
        <div><span class="form-label">工序</span><br><span>总装配 OP-30</span></div>
        <div><span class="form-label">检验方案</span><br><span>IPQC-FS40-标准检验方案 v2.1</span></div>
        <div><span class="form-label">抽样方案</span><br><span>GB/T 2828.1 · AQL=1.0 · 样本量 n=50</span></div>
        <div><span class="form-label">检验时间</span><br><span>2026-07-11 09:15</span></div>
        <div><span class="form-label">检验员</span><br><span>李检验</span></div>
        <div><span class="form-label">状态</span><br><span class="badge badge-status-warn"><span class="badge-dot"></span>进行中</span></div>
      </div>
    </div>

    <section class="card mb-5" data-od-id="inspexec-items">
      <div class="card-header"><h2 class="card-title">检验项目</h2></div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>检验项编号</th><th>检验项</th><th>规格要求</th><th>量具</th><th>抽样数</th><th>合格数</th><th>不良数</th><th>结果</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">CI-001</td><td>外观检查</td><td>无划伤、色差、变形</td><td>目视</td><td>50</td>
              <td><input class="form-input" type="number" value="48" style="width:70px;" /></td>
              <td><input class="form-input" type="number" value="2" style="width:70px;" /></td>
              <td><select class="form-select" style="width:120px;"><option>合格</option><option selected>让步接收</option><option>不合格</option></select></td></tr>
            <tr><td class="cell-mono">CI-002</td><td>运转测试</td><td>3 档运转正常，无异响</td><td>通电测试台</td><td>50</td>
              <td><input class="form-input" type="number" value="48" style="width:70px;" /></td>
              <td><input class="form-input" type="number" value="2" style="width:70px;" /></td>
              <td><select class="form-select" style="width:120px;"><option>合格</option><option>让步接收</option><option selected>不合格</option></select></td></tr>
            <tr><td class="cell-mono">CI-003</td><td>风速测试</td><td>高速档风速 ≥4.5 m/s</td><td>风速仪</td><td>10</td>
              <td><input class="form-input" type="number" value="10" style="width:70px;" /></td>
              <td><input class="form-input" type="number" value="0" style="width:70px;" /></td>
              <td><select class="form-select" style="width:120px;"><option selected>合格</option><option>让步接收</option><option>不合格</option></select></td></tr>
            <tr><td class="cell-mono">CI-004</td><td>绝缘耐压</td><td>AC 1500V / 1min 无击穿</td><td>耐压测试仪</td><td>50</td>
              <td><input class="form-input" type="number" value="50" style="width:70px;" /></td>
              <td><input class="form-input" type="number" value="0" style="width:70px;" /></td>
              <td><select class="form-select" style="width:120px;"><option selected>合格</option><option>让步接收</option><option>不合格</option></select></td></tr>
            <tr><td class="cell-mono">CI-005</td><td>功率测试</td><td>额定功率 75W±10%</td><td>功率计</td><td>10</td>
              <td><input class="form-input" type="number" value="10" style="width:70px;" /></td>
              <td><input class="form-input" type="number" value="0" style="width:70px;" /></td>
              <td><select class="form-select" style="width:120px;"><option selected>合格</option><option>让步接收</option><option>不合格</option></select></td></tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="card mb-5" data-od-id="inspexec-remark">
      <div class="form-group">
        <label class="form-label">检验备注</label>
        <textarea class="form-textarea" rows="3" placeholder="检验发现、处理建议...">外观轻微划伤 2 台（让步接收）；运转 2 档有轻微异响 2 台（返工维修）。其余项目合格。</textarea>
      </div>
    </section>

    <div class="flex gap-3">
      <button class="btn btn-primary btn-lg" @click="notify('提交检验结果 — POST /api/quality/inspection-results')">提交检验结果</button>
      <button class="btn btn-secondary btn-lg">暂存草稿</button>
      <button class="btn btn-ghost btn-lg">取消</button>
    </div>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">QC-0711-0152 · IPQC · FS-40-B3 · 5 项检验</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 李检验</span>
  </footer>
</MesLayout>
</template>
