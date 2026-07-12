<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="sop-exec">
  
  <header class="app-header">
    <div class="header-breadcrumb"><span>电子SOP</span> <span class="bc-sep">/</span> <span class="bc-current">执行终端</span></div>
    <div class="header-actions">
      <span style="font-size:var(--text-caption);">工单 MO-0032 · FS-40-B3 · 陈作业</span>
      <span class="user-avatar">陈</span>
    </div>
  </header>
  <main class="app-main" data-od-id="sopexec-main">
    <h1 class="page-title">SOP执行终端 — FS-40 落地扇总装</h1>
    <div style="display:grid;grid-template-columns:1fr 340px;gap:var(--space-5);">
      <!-- Step viewer -->
      <section class="card" data-od-id="sopexec-viewer" style="min-height:500px;">
        <div class="flex justify-between items-center mb-4">
          <div>
            <span class="form-label">步骤 3 / 8</span>
            <h2 class="card-title" id="stepTitle" style="margin-top:var(--space-1);">安装扇叶并锁紧螺母</h2>
          </div>
          <span class="badge badge-status-ok"><span class="badge-dot"></span>进行中</span>
        </div>
        <div style="background:var(--surface);border-radius:var(--radius);padding:var(--space-5);margin-bottom:var(--space-4);text-align:center;min-height:200px;display:grid;place-items:center;color:var(--fg);opacity:0.3;font-size:var(--text-h2);border:1px dashed var(--border);">
          图片/3D模型展示区
        </div>
        <div class="mb-4">
          <h3 class="card-title mb-2">操作说明</h3>
          <ol style="padding-left:var(--space-5);line-height:2;font-size:var(--text-body);">
            <li>取 ABS 扇叶（Φ400mm × 3 叶），检查叶片无裂纹、无变形</li>
            <li>将扇叶中心孔对准电机轴，顺时针旋入至止位</li>
            <li>使用专用扳手锁紧螺母，扭矩 3.5–4.0 N·m</li>
            <li>手动转动扇叶 3 圈，确认转动顺畅无卡滞</li>
          </ol>
        </div>
        <div class="mb-4">
          <h3 class="card-title mb-2">注意事项</h3>
          <div class="alert alert-warning"><span class="alert-icon">!</span> 螺母扭矩不可超过 4.5 N·m，否则可能导致扇叶破裂</div>
        </div>
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:var(--space-4);margin-bottom:var(--space-4);">
          <div class="form-group"><label class="form-label">关键参数确认</label><select class="form-select"><option>— 选择 —</option><option>扭矩已确认 3.8 N·m ✓</option><option>扭矩超差，需重新锁紧</option></select></div>
          <div class="form-group"><label class="form-label">转动测试</label><select class="form-select"><option>— 选择 —</option><option>顺畅无卡滞 ✓</option><option>有卡滞，需检查</option></select></div>
        </div>
        <div class="flex gap-3">
          <button class="btn btn-secondary btn-lg">← 上一步</button>
          <button class="btn btn-primary btn-lg" @click="notify('步骤确认 — 对接 POST /api/production/sop/executions')">确认并继续 →</button>
        </div>
      </section>
      <!-- Side panel -->
      <aside data-od-id="sopexec-sidebar" style="display:flex;flex-direction:column;gap:var(--space-4);">
        <div class="card">
          <div class="card-header"><h2 class="card-title">步骤列表</h2></div>
          <div style="display:flex;flex-direction:column;gap:var(--space-2);">
            <div style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3);border-radius:var(--radius);"><span style="width:24px;height:24px;border-radius:50%;background:var(--accent);color:#fff;display:grid;place-items:center;font-size:11px;font-weight:700;">✓</span><span style="opacity:0.6;">1. 安装底座与立杆</span></div>
            <div style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3);border-radius:var(--radius);"><span style="width:24px;height:24px;border-radius:50%;background:var(--accent);color:#fff;display:grid;place-items:center;font-size:11px;font-weight:700;">✓</span><span style="opacity:0.6;">2. 安装电机并接线</span></div>
            <div style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3);border-radius:var(--radius);background:var(--surface);"><span style="width:24px;height:24px;border-radius:50%;background:var(--accent);color:#fff;display:grid;place-items:center;font-size:11px;font-weight:700;box-shadow:0 0 0 4px rgba(62,106,225,0.15);">3</span><span style="font-weight:600;">安装扇叶并锁紧</span></div>
            <div style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3);border-radius:var(--radius);"><span style="width:24px;height:24px;border-radius:50%;background:var(--muted);display:grid;place-items:center;font-size:11px;font-weight:700;">4</span><span style="opacity:0.5;">安装网罩</span></div>
            <div style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3);border-radius:var(--radius);"><span style="width:24px;height:24px;border-radius:50%;background:var(--muted);display:grid;place-items:center;font-size:11px;font-weight:700;">5</span><span style="opacity:0.5;">安装开关面板</span></div>
            <div style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3);border-radius:var(--radius);"><span style="width:24px;height:24px;border-radius:50%;background:var(--muted);display:grid;place-items:center;font-size:11px;font-weight:700;">6</span><span style="opacity:0.5;">电源线固定</span></div>
            <div style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3);border-radius:var(--radius);"><span style="width:24px;height:24px;border-radius:50%;background:var(--muted);display:grid;place-items:center;font-size:11px;font-weight:700;">7</span><span style="opacity:0.5;">通电测试</span></div>
            <div style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3);border-radius:var(--radius);"><span style="width:24px;height:24px;border-radius:50%;background:var(--muted);display:grid;place-items:center;font-size:11px;font-weight:700;">8</span><span style="opacity:0.5;">粘贴铭牌标签</span></div>
          </div>
        </div>
        <div class="card">
          <div class="card-header"><h2 class="card-title">执行信息</h2></div>
          <table class="data-table" style="border:none;">
            <tbody>
              <tr><td style="opacity:0.5;">文档</td><td>SOP-FS40-001 v3.2</td></tr>
              <tr><td style="opacity:0.5;">工位</td><td>总装线 A · 扇叶安装 A03</td></tr>
              <tr><td style="opacity:0.5;">开始时间</td><td>2026-07-11 09:15</td></tr>
              <tr><td style="opacity:0.5;">已完成</td><td>3 / 8 步骤</td></tr>
            </tbody>
          </table>
        </div>
      </aside>
    </div>
  </main>
  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span>
    <span class="statusbar-item">SOP执行 · SOP-FS40-001 v3.2 · 步骤 3/8 · 陈作业</span><span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span>
  </footer>
</MesLayout>
</template>
