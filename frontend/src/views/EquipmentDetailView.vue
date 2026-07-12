<script setup lang="ts">
import { ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
const activeTab = ref('info')
</script>

<template>
<MesLayout active="equip-detail">
  
  <header class="app-header"><div class="header-breadcrumb"><span>设备与安灯</span> <span class="bc-sep">/</span> <RouterLink to="/equipment/ledger">设备台账</RouterLink> <span class="bc-sep">/</span> <span class="bc-current">EQ-001 注塑机 #1</span></div><div class="header-actions"><button class="btn btn-primary btn-sm">编辑</button><span class="user-avatar">张</span></div></header>
  <main class="app-main" data-od-id="eqdet-main">
    <h1 class="page-title">设备详情 — EQ-001 注塑机 #1</h1>
    <div class="tabs"><span class="tab-item" @click="activeTab = 'info'" :class="{ active: activeTab === 'info' }">基本信息</span><span class="tab-item" @click="activeTab = 'maint'" :class="{ active: activeTab === 'maint' }">维保记录</span><span class="tab-item" @click="activeTab = 'oee'" :class="{ active: activeTab === 'oee' }">OEE趋势</span></div>
    <div id="tab-info" v-show="activeTab === 'info'">
      <div class="card" style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:var(--space-4);">
        <div><span class="form-label">设备编号</span><br><span class="cell-mono">EQ-001</span></div>
        <div><span class="form-label">设备名称</span><br>注塑机 #1</div>
        <div><span class="form-label">型号</span><br>HTF120W</div>
        <div><span class="form-label">类型</span><br>注塑机 · 锁模力 120T</div>
        <div><span class="form-label">所属车间</span><br>注塑车间</div>
        <div><span class="form-label">购置日期</span><br>2024-03-15</div>
        <div><span class="form-label">原值</span><br>¥280,000</div>
        <div><span class="form-label">当前状态</span><br><span class="badge badge-status-ok"><span class="badge-dot"></span>运行中</span></div>
        <div><span class="form-label">稼动率</span><br>92%</div>
      </div>
    </div>
    <div id="tab-maint"  v-show="activeTab === 'maint'">
      <div class="data-table-wrap"><table class="data-table">
        <thead><tr><th>日期</th><th>保养类型</th><th>执行人</th><th>耗时</th><th>内容</th><th>结果</th></tr></thead>
        <tbody>
          <tr><td>07-10</td><td>周保养</td><td>赵技术员</td><td>45min</td><td>模具清洁、润滑油加注</td><td>正常</td></tr>
          <tr><td>07-03</td><td>周保养</td><td>赵技术员</td><td>50min</td><td>温控校准、安全门检查</td><td>正常</td></tr>
          <tr><td>06-28</td><td>月度保养</td><td>赵技术员</td><td>3h</td><td>螺杆清洗、液压油更换</td><td>正常</td></tr>
        </tbody>
      </table></div>
    </div>
    <div id="tab-oee"  v-show="activeTab === 'oee'">
      <div class="card"><div class="card-header"><h2 class="card-title">近 7 天 OEE</h2></div>
        <svg viewBox="0 0 700 140" preserveAspectRatio="none" style="width:100%;height:140px;"><rect x="10" y="11" width="80" height="129" fill="var(--fg)" opacity="0.45" rx="4"/><rect x="110" y="17" width="80" height="123" fill="var(--fg)" opacity="0.45" rx="4"/><rect x="210" y="7" width="80" height="133" fill="var(--accent)" opacity="0.6" rx="4"/><rect x="310" y="14" width="80" height="126" fill="var(--fg)" opacity="0.45" rx="4"/><rect x="410" y="10" width="80" height="130" fill="var(--fg)" opacity="0.45" rx="4"/><rect x="510" y="13" width="80" height="127" fill="var(--fg)" opacity="0.45" rx="4"/><rect x="610" y="11" width="80" height="129" fill="var(--fg)" opacity="0.45" rx="4"/></svg>
        <div style="display:flex;justify-content:space-between;font-size:var(--text-caption);opacity:0.5;"><span>07-05</span><span>07-06</span><span>07-07</span><span>07-08</span><span>07-09</span><span>07-10</span><span>07-11</span></div>
      </div>
    </div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">EQ-001 · 注塑机 #1 · 运行中 · 稼动 92%</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span></footer>
</MesLayout>
</template>
