<script setup lang="ts">
import { ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
const activeTab = ref('tasks')
</script>

<template>
<MesLayout active="quality">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>质量与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">质量检验</span></div>
    <div class="header-actions">
      <button class="btn btn-primary btn-sm" @click="notify('新建检验任务 — 功能开发中')">+ 新建检验</button>
      <span class="user-avatar">李</span>
    </div>
  </header>

  <main class="app-main" data-od-id="quality-main">
    <h1 class="page-title">质量检验</h1>

    <div class="tabs" data-od-id="quality-tabs">
      <span class="tab-item" @click="activeTab = 'tasks'" :class="{ active: activeTab === 'tasks' }">检验任务</span>
      <span class="tab-item" @click="activeTab = 'defects'" :class="{ active: activeTab === 'defects' }">缺陷记录</span>
      <span class="tab-item" @click="activeTab = 'stats'" :class="{ active: activeTab === 'stats' }">质量统计</span>
    </div>

    <!-- Tab: Tasks -->
    <div id="qtab-tasks" v-show="activeTab === 'tasks'">
      <div class="card mb-4" style="padding:var(--space-4);">
        <div class="search-bar" style="margin-bottom:0;">
          <div class="form-group" style="min-width:160px;"><label class="form-label">检验类型</label><select class="form-select"><option>全部</option><option>来料检验 IQC</option><option>过程检验 IPQC</option><option>成品检验 OQC</option></select></div>
          <div class="form-group" style="min-width:140px;"><label class="form-label">结果</label><select class="form-select"><option>全部</option><option>合格</option><option>让步接收</option><option>不合格</option></select></div>
          <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">筛选</button>
        </div>
      </div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>检验编号</th><th>类型</th><th>关联订单</th><th>产品/物料</th><th>检验数</th><th>合格</th><th>不良</th><th>结果</th><th>检验人</th><th>时间</th><th>操作</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">QC-0711-0152</td><td>IPQC</td><td>MO-0032</td><td>FS-40-B3 落地扇</td><td>50</td><td>48</td><td>2</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>让步接收</span></td><td>李检验</td><td>09:15</td><td><button class="btn btn-ghost btn-sm" @click="notify('检验详情 — 功能开发中')">查看</button></td></tr>
            <tr><td class="cell-mono">QC-0711-0151</td><td>IQC</td><td>—</td><td>ABS 扇叶 · 来料批 L24071103</td><td>200</td><td>196</td><td>4</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>让步接收</span></td><td>王质检</td><td>08:40</td><td><button class="btn btn-ghost btn-sm" @click="notify('检验详情 — 功能开发中')">查看</button></td></tr>
            <tr><td class="cell-mono">QC-0710-0148</td><td>IPQC</td><td>MO-0032</td><td>转子压装 OP-20</td><td>30</td><td>30</td><td>0</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>合格</span></td><td>李检验</td><td>07-10</td><td><button class="btn btn-ghost btn-sm" @click="notify('检验详情 — 功能开发中')">查看</button></td></tr>
            <tr><td class="cell-mono">QC-0710-0145</td><td>OQC</td><td>MO-0029</td><td>FH-18-C2 夹扇</td><td>80</td><td>80</td><td>0</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>合格</span></td><td>王质检</td><td>07-10</td><td><button class="btn btn-ghost btn-sm" @click="notify('检验详情 — 功能开发中')">查看</button></td></tr>
          </tbody>
        </table>
        <div class="pagination"><span>共 28 条记录</span><div class="page-btns"><span class="page-btn">‹</span><span class="page-btn active">1</span><span class="page-btn">2</span><span class="page-btn">3</span><span class="page-btn">›</span></div></div>
      </div>
    </div>

    <!-- Tab: Defects -->
    <div id="qtab-defects"  v-show="activeTab === 'defects'">
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>缺陷编号</th><th>关联检验</th><th>缺陷类型</th><th>描述</th><th>严重等级</th><th>处理方式</th><th>责任人</th><th>状态</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">DEF-0711-0042</td><td>QC-0711-0152</td><td>外观划伤</td><td>底座表面轻微划伤，长度 3mm</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>轻微</span></td><td>让步接收</td><td>陈作业</td><td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已关闭</span></td></tr>
            <tr><td class="cell-mono">DEF-0711-0041</td><td>QC-0711-0152</td><td>运转异响</td><td>2 档运转有轻微"嗒嗒"声</td><td><span class="badge badge-status-error"><span class="badge-dot"></span>严重</span></td><td>返工维修</td><td>黄作业</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>维修中</span></td></tr>
            <tr><td class="cell-mono">DEF-0711-0038</td><td>QC-0711-0151</td><td>色差</td><td>扇叶颜色批次间偏差 ΔE>2</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>轻微</span></td><td>让步接收</td><td>—</td><td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已关闭</span></td></tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Tab: Stats -->
    <div id="qtab-stats"  v-show="activeTab === 'stats'">
      <div style="display:grid; grid-template-columns:repeat(3,1fr); gap:var(--space-4); margin-bottom:var(--space-5);">
        <div class="kpi-card accent-border"><span class="kpi-label">本月一次合格率</span><span class="kpi-value" v-count-up>97.6%</span><span class="kpi-change down">↓ 0.3% 较上月</span></div>
        <div class="kpi-card"><span class="kpi-label">本月检验批次</span><span class="kpi-value" v-count-up>28</span><span class="kpi-change">IQC 6 · IPQC 18 · OQC 4</span></div>
        <div class="kpi-card"><span class="kpi-label">TOP 缺陷</span><span class="kpi-value" v-count-up style="font-size:1rem;">外观划伤</span><span class="kpi-change">占比 38% · 12 次</span></div>
      </div>
      <div class="card">
        <div class="card-header"><h2 class="card-title">不良率趋势（本周）</h2></div>
        <svg viewBox="0 0 700 160" preserveAspectRatio="none" style="width:100%;height:160px;">
          <rect x="10" y="112" width="80" height="48" fill="var(--fg)" opacity="0.45" rx="4"/>
          <rect x="110" y="88" width="80" height="72" fill="var(--fg)" opacity="0.45" rx="4"/>
          <rect x="210" y="120" width="80" height="40" fill="var(--fg)" opacity="0.45" rx="4"/>
          <rect x="310" y="64" width="80" height="96" fill="var(--accent)" opacity="0.6" rx="4"/>
          <rect x="410" y="104" width="80" height="56" fill="var(--fg)" opacity="0.45" rx="4"/>
          <rect x="510" y="96" width="80" height="64" fill="var(--fg)" opacity="0.45" rx="4"/>
          <rect x="610" y="128" width="80" height="32" fill="var(--fg)" opacity="0.45" rx="4"/>
        </svg>
        <div style="display:flex;justify-content:space-between;font-size:var(--text-caption);opacity:0.5;padding:0 var(--space-2);">
          <span>周一</span><span>周二</span><span>周三</span><span>周四</span><span>周五</span><span>周六</span><span>周日</span>
        </div>
      </div>
    </div>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">质量检验 · 28 条记录 · 一次合格率 97.6%</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 李检验</span>
  </footer>
</MesLayout>
</template>
