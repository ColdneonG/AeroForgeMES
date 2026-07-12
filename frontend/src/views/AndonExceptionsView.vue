<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="andon">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>设备与安灯</span> <span class="bc-sep">/</span> <span class="bc-current">安灯异常</span></div>
    <div class="header-actions">
      <button class="btn btn-primary btn-sm" @click="notify('触发安灯 — POST /api/andon/exceptions')">+ 呼叫安灯</button>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="andon-main">
    <div class="flex justify-between items-center mb-5">
      <h1 class="page-title" style="margin-bottom:0;">安灯异常处理</h1>
      <div class="flex gap-3">
        <span class="badge badge-status-error"><span class="badge-dot"></span>3 条未关闭</span>
      </div>
    </div>

    <div class="card mb-5">
      <div class="search-bar" style="margin-bottom:0;">
        <div class="form-group" style="min-width:140px;"><label class="form-label">异常类型</label><select class="form-select"><option>全部</option><option>质量异常</option><option>设备故障</option><option>物料短缺</option><option>人员异常</option><option>安全事故</option></select></div>
        <div class="form-group" style="min-width:130px;"><label class="form-label">产线</label><select class="form-select"><option>全部</option><option>总装线 A</option><option>总装线 B</option><option>绕线车间</option><option>注塑区</option></select></div>
        <div class="form-group" style="min-width:130px;"><label class="form-label">状态</label><select class="form-select"><option>全部</option><option>待响应</option><option>处理中</option><option>已恢复</option><option>已关闭</option></select></div>
        <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">筛选</button>
      </div>
    </div>

    <div data-od-id="andon-active" style="margin-bottom:var(--space-5);">
      <h2 class="section-title"><svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="var(--accent)" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" style="vertical-align:middle;margin-right:6px;display:inline;"><circle cx="8" cy="8" r="5"/><circle cx="8" cy="11" r="0.8" fill="var(--accent)" stroke="none"/><path d="M8 5v3"/></svg>当前活跃异常</h2>
      <div class="alert alert-error" data-od-id="andon-exc-1" style="padding:var(--space-5);">
        <div class="flex justify-between items-start" style="width:100%;">
          <div>
            <div style="font-weight:700;font-size:var(--text-h3);margin-bottom:var(--space-2);">总装线 A — 风叶动平衡超差</div>
            <div style="display:grid;grid-template-columns:auto auto;gap:var(--space-2) var(--space-5);font-size:var(--text-small);opacity:0.9;">
              <span>类型：质量异常</span><span>触发：09:15 · 黄作业</span>
              <span>响应：09:18 · 班组长 李明辉</span><span>预计恢复：10:00</span>
              <span>影响：总装线 A 停产</span><span>涉及工单：MO-0032</span>
            </div>
            <div style="margin-top:var(--space-3);font-size:var(--text-small);opacity:0.8;">处理措施：已调校动平衡机夹具，正在重新检测 50 台首件样本</div>
          </div>
          <div class="flex gap-2"><button class="btn btn-secondary btn-sm">记录进展</button><button class="btn btn-primary btn-sm" style="background:#ffffff;color:var(--accent);">恢复生产</button></div>
        </div>
      </div>
      <div class="alert alert-warning" data-od-id="andon-exc-2" style="padding:var(--space-4);">
        <div class="flex justify-between items-start" style="width:100%;">
          <div>
            <div style="font-weight:700;margin-bottom:var(--space-1);">绕线工位 #3 — 漆包线张力异常</div>
            <div style="font-size:var(--text-small);opacity:0.7;">类型：设备故障 · 触发 08:42 · 设备技术员已到场 · 预计恢复 09:45</div>
          </div>
          <div class="flex gap-2"><button class="btn btn-ghost btn-sm">记录进展</button><button class="btn btn-primary btn-sm">恢复生产</button></div>
        </div>
      </div>
      <div class="alert alert-warning" data-od-id="andon-exc-3" style="padding:var(--space-4);">
        <div class="flex justify-between items-start" style="width:100%;">
          <div>
            <div style="font-weight:700;margin-bottom:var(--space-1);">总装线 B — 网罩卡扣缺料预警</div>
            <div style="font-size:var(--text-small);opacity:0.7;">类型：物料短缺 · 触发 09:28 · 仓库已确认补料 · 预计 09:50 送达</div>
          </div>
          <div class="flex gap-2"><button class="btn btn-ghost btn-sm">确认到料</button><button class="btn btn-primary btn-sm">恢复生产</button></div>
        </div>
      </div>
    </div>

    <section class="card" data-od-id="andon-history">
      <div class="card-header"><h2 class="card-title">历史异常记录</h2></div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>编号</th><th>产线/工位</th><th>异常类型</th><th>描述</th><th>触发人</th><th>触发时间</th><th>恢复时间</th><th>持续</th><th>状态</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">EX-0710-0018</td><td>注塑机 #2</td><td>设备故障</td><td>料筒温度过冲 +20°C</td><td>赵作业</td><td>07-10 14:30</td><td>15:10</td><td>40min</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已关闭</span></td></tr>
            <tr><td class="cell-mono">EX-0710-0017</td><td>总装线 A</td><td>质量异常</td><td>连续 3 台风速不达标</td><td>周作业</td><td>07-10 11:20</td><td>12:00</td><td>40min</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已关闭</span></td></tr>
            <tr><td class="cell-mono">EX-0709-0016</td><td>绕线车间</td><td>物料短缺</td><td>Φ0.27mm 漆包线库存不足</td><td>孙作业</td><td>07-09 09:50</td><td>11:30</td><td>1h40min</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已关闭</span></td></tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot warn"></span>安灯活跃 3</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">今日累计 5 次 · 平均响应 3.2min · 平均恢复 38min</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span>
  </footer>
</MesLayout>
</template>
