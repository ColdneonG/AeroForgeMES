<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="shopfloor">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>生产管理</span> <span class="bc-sep">/</span> <span class="bc-current">车间看板</span></div>
    <div class="header-actions">
      <span style="font-size:var(--text-caption);opacity:0.5;">班次: 白班 08:00–18:00</span>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="shopfloor-main">
    <h1 class="page-title">车间作业看板</h1>

    <div style="display:grid; grid-template-columns:1fr 1fr; gap:var(--space-5); margin-bottom:var(--space-5);">
      <div class="kpi-card accent-border"><span class="kpi-label">在产工单</span><span class="kpi-value" v-count-up>4</span><span class="kpi-change">总装线 A×2 · B×1 · C×1</span></div>
      <div class="kpi-card"><span class="kpi-label">今日报工人次</span><span class="kpi-value" v-count-up>86</span><span class="kpi-change">出勤率 97.7%</span></div>
    </div>

    <section class="card mb-5" data-od-id="shopfloor-lines">
      <div class="card-header"><h2 class="card-title">产线状态</h2></div>
      <div style="display:grid; grid-template-columns:repeat(3,1fr); gap:var(--space-4);">
        <div style="background:var(--surface);border-radius:var(--radius);padding:var(--space-4);">
          <div class="flex justify-between items-center mb-3">
            <span class="card-title">总装线 A</span>
            <span class="badge badge-status-ok"><span class="badge-dot"></span>运行中</span>
          </div>
          <div class="text-muted" style="font-size:var(--text-small);margin-bottom:var(--space-3);">FS-40-B3 落地扇 · MO-0032</div>
          <div class="mb-2"><span class="text-muted">目标 500 · 完成 360</span></div>
          <div class="progress-bar" style="height:8px;margin-bottom:var(--space-2);"><div class="progress-fill" style="width:72%;"></div></div>
          <div class="text-muted" style="font-size:var(--text-caption);">节拍 28s/台 · 在岗 18 人</div>
        </div>
        <div style="background:var(--surface);border-radius:var(--radius);padding:var(--space-4);">
          <div class="flex justify-between items-center mb-3">
            <span class="card-title">总装线 B</span>
            <span class="badge badge-status-ok"><span class="badge-dot"></span>运行中</span>
          </div>
          <div class="text-muted" style="font-size:var(--text-small);margin-bottom:var(--space-3);">FT-30-A1 台扇 · MO-0033</div>
          <div class="mb-2"><span class="text-muted">目标 800 · 完成 360</span></div>
          <div class="progress-bar" style="height:8px;margin-bottom:var(--space-2);"><div class="progress-fill" style="width:45%;"></div></div>
          <div class="text-muted" style="font-size:var(--text-caption);">节拍 22s/台 · 在岗 22 人</div>
        </div>
        <div style="background:var(--surface);border-radius:var(--radius);padding:var(--space-4);">
          <div class="flex justify-between items-center mb-3">
            <span class="card-title">总装线 C</span>
            <span class="badge badge-status-info"><span class="badge-dot"></span>换模中</span>
          </div>
          <div class="text-muted" style="font-size:var(--text-small);margin-bottom:var(--space-3);">切换至 FH-18-C2 夹扇 · 预计 10:15 恢复</div>
          <div class="mb-2"><span class="text-muted">下一工单 MO-0035 待下达</span></div>
          <div class="progress-bar" style="height:8px;margin-bottom:var(--space-2);"><div class="progress-fill" style="width:0%;background:var(--muted);"></div></div>
          <div class="text-muted" style="font-size:var(--text-caption);">换模人员 3 人 · 在岗待命 12 人</div>
        </div>
      </div>
    </section>

    <section class="card" data-od-id="shopfloor-stations">
      <div class="card-header"><h2 class="card-title">关键工位状态</h2></div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>工位编号</th><th>工位名称</th><th>当前任务</th><th>作业员</th><th>完工数</th><th>节拍</th><th>状态</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">WS-A01</td><td>底座组装</td><td>FS-40-B3 底座锁付</td><td>陈作业</td><td>360</td><td>6s</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>正常</span></td></tr>
            <tr><td class="cell-mono">WS-A02</td><td>电机安装</td><td>FS-40-B3 电机固定</td><td>刘作业</td><td>358</td><td>7s</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>正常</span></td></tr>
            <tr><td class="cell-mono">WS-A03</td><td>扇叶安装</td><td>FS-40-B3 扇叶锁紧</td><td>黄作业</td><td>355</td><td>5s</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>堆积 5 件</span></td></tr>
            <tr><td class="cell-mono">WS-A04</td><td>网罩组装</td><td>FS-40-B3 网罩卡扣</td><td>林作业</td><td>362</td><td>8s</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>正常</span></td></tr>
            <tr><td class="cell-mono">WS-A05</td><td>通电测试</td><td>FS-40-B3 运转测试</td><td>周作业</td><td>360</td><td>12s</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>正常</span></td></tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">白班 · 在岗 86/88 · 产线 3 条运行</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span>
  </footer>
</MesLayout>
</template>
