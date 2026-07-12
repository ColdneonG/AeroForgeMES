<script setup lang="ts">
import { ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
const activeTab = ref('defs')
</script>

<template>
<MesLayout active="metrics">
    

  <header class="app-header">
    <div class="header-breadcrumb"><span>数据与系统</span> <span class="bc-sep">/</span> <span class="bc-current">指标管理</span></div>
    <div class="header-actions">
      <button class="btn btn-primary btn-sm" @click="notify('新建指标 — POST /api/report/metrics')">+ 新建指标</button>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="metrics-main">
    <h1 class="page-title">指标管理</h1>

    <div class="tabs" data-od-id="metrics-tabs">
      <span class="tab-item" @click="activeTab = 'defs'" :class="{ active: activeTab === 'defs' }">指标定义</span>
      <span class="tab-item" @click="activeTab = 'snapshots'" :class="{ active: activeTab === 'snapshots' }">指标快照</span>
      <span class="tab-item" @click="activeTab = 'detail'" :class="{ active: activeTab === 'detail' }">指标详情</span>
    </div>

    <!-- Defs -->
    <div id="mtab-defs" v-show="activeTab === 'defs'">
      <div class="card mb-5">
        <div class="search-bar" style="margin-bottom:0;">
          <div class="form-group" style="min-width:160px;"><label class="form-label">指标类型</label><select class="form-select"><option>全部</option><option>产量指标</option><option>质量指标</option><option>设备指标</option><option>效率指标</option></select></div>
          <div class="form-group" style="min-width:140px;"><label class="form-label">状态</label><select class="form-select"><option>全部</option><option>启用</option><option>停用</option></select></div>
          <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">筛选</button>
        </div>
      </div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>指标编码</th><th>指标名称</th><th>类型</th><th>单位</th><th>采集频率</th><th>目标值</th><th>当前值</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">OUTPUT_DAILY</td><td>日产量</td><td>产量指标</td><td>台</td><td>每小时</td><td>≥1,500</td><td>1,286</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td>
              <td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-ghost btn-sm" @click="notify('GET /api/report/metric-data/OUTPUT_DAILY')">数据</button></td></tr>
            <tr><td class="cell-mono">QUALITY_FPY</td><td>一次合格率</td><td>质量指标</td><td>%</td><td>每天</td><td>≥98.0%</td><td>97.6%</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td>
              <td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-ghost btn-sm" @click="notify('GET /api/report/metric-data/QUALITY_FPY')">数据</button></td></tr>
            <tr><td class="cell-mono">OEE</td><td>设备综合效率 OEE</td><td>设备指标</td><td>%</td><td>每班次</td><td>≥85.0%</td><td>84.3%</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td>
              <td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-ghost btn-sm" @click="notify('GET /api/report/metric-data/OEE')">数据</button></td></tr>
            <tr><td class="cell-mono">DEFECT_RATE</td><td>不良率</td><td>质量指标</td><td>%</td><td>每班次</td><td>≤1.5%</td><td>1.40%</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td>
              <td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-ghost btn-sm" @click="notify('GET /api/report/metric-data/DEFECT_RATE')">数据</button></td></tr>
            <tr><td class="cell-mono">PERCAPITA_OUTPUT</td><td>人均产量</td><td>效率指标</td><td>台/人/天</td><td>每天</td><td>≥16</td><td>15.0</td>
              <td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td>
              <td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button> <button class="btn btn-ghost btn-sm" @click="notify('GET /api/report/metric-data/PERCAPITA_OUTPUT')">数据</button></td></tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Snapshots -->
    <div id="mtab-snapshots"  v-show="activeTab === 'snapshots'">
      <div class="card mb-5">
        <div class="search-bar" style="margin-bottom:0;">
          <div class="form-group" style="min-width:160px;"><label class="form-label">指标</label><select class="form-select"><option>全部</option><option>日产量</option><option>一次合格率</option><option>OEE</option><option>不良率</option></select></div>
          <div class="form-group" style="min-width:140px;"><label class="form-label">时间范围</label><input class="form-input" type="date" value="2026-07-11" /></div>
          <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">查询</button>
        </div>
      </div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>快照时间</th><th>指标</th><th>值</th><th>目标</th><th>达成率</th><th>采集源</th></tr></thead>
          <tbody>
            <tr><td>07-11 09:00</td><td>日产量</td><td>1,286 台</td><td>1,500</td><td>85.7%</td><td>报工数据</td></tr>
            <tr><td>07-11 09:00</td><td>一次合格率</td><td>97.6%</td><td>98.0%</td><td>99.6%</td><td>检验系统</td></tr>
            <tr><td>07-11 09:00</td><td>OEE</td><td>84.3%</td><td>85.0%</td><td>99.2%</td><td>设备采集</td></tr>
            <tr><td>07-11 08:00</td><td>不良率</td><td>1.40%</td><td>≤1.5%</td><td>✓达标</td><td>检验系统</td></tr>
            <tr><td>07-10 17:00</td><td>日产量</td><td>1,492 台</td><td>1,500</td><td>99.5%</td><td>报工数据</td></tr>
            <tr><td>07-10 17:00</td><td>一次合格率</td><td>97.9%</td><td>98.0%</td><td>99.9%</td><td>检验系统</td></tr>
          </tbody>
        </table>
        <div class="pagination"><span>共 42 条快照</span><div class="page-btns"><span class="page-btn">‹</span><span class="page-btn active">1</span><span class="page-btn">2</span><span class="page-btn">3</span><span class="page-btn">›</span></div></div>
      </div>
    </div>

    <!-- Detail -->
    <div id="mtab-detail"  v-show="activeTab === 'detail'">
      <div class="card mb-5">
        <div class="search-bar" style="margin-bottom:0;">
          <div class="form-group" style="min-width:200px;"><label class="form-label">指标编码</label><input class="form-input" type="text" placeholder="如 OUTPUT_DAILY..." /></div>
          <button class="btn btn-primary btn-sm" style="align-self:flex-end;">查询数据</button>
        </div>
      </div>
      <div class="alert alert-info"><span class="alert-icon">i</span> 输入指标编码后查询该指标的历史数据趋势 — 对接 GET /api/report/metric-data/{metricCode}</div>
    </div>
  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">指标管理 · 5 个启用指标 · 42 条快照</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span>
  </footer>
</MesLayout>
</template>
