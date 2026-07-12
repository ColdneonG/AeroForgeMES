<script setup lang="ts">
import { ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
const activeTab = ref('plans')
</script>

<template>
<MesLayout active="equip-maintain">
  
  <header class="app-header"><div class="header-breadcrumb"><span>设备与安灯</span> <span class="bc-sep">/</span> <span class="bc-current">点检保养</span></div><div class="header-actions"><button class="btn btn-primary btn-sm">+ 新建计划</button><span class="user-avatar">张</span></div></header>
  <main class="app-main" data-od-id="maint-main">
    <h1 class="page-title">点检保养</h1>
    <div class="tabs" data-od-id="maint-tabs">
      <span class="tab-item" @click="activeTab = 'plans'" :class="{ active: activeTab === 'plans' }">保养计划</span>
      <span class="tab-item" @click="activeTab = 'records'" :class="{ active: activeTab === 'records' }">执行记录</span>
    </div>
    <div id="tab-plans" v-show="activeTab === 'plans'">
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>计划编号</th><th>设备</th><th>保养类型</th><th>计划日期</th><th>负责人</th><th>内容摘要</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">MP-0711-0012</td><td>注塑机 #1</td><td>周保养</td><td>07-12</td><td>赵技术员</td><td>模具清洁、润滑油加注、温控校准</td><td><span class="badge badge-status-info"><span class="badge-dot"></span>待执行</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button><button class="btn btn-primary btn-sm">执行</button></td></tr>
            <tr><td class="cell-mono">MP-0711-0011</td><td>绕线机 #1</td><td>周保养</td><td>07-12</td><td>赵技术员</td><td>张力轮清洁、导轨润滑、传感器检查</td><td><span class="badge badge-status-info"><span class="badge-dot"></span>待执行</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button><button class="btn btn-primary btn-sm">执行</button></td></tr>
            <tr><td class="cell-mono">MP-0710-0010</td><td>注塑机 #2</td><td>月度保养</td><td>07-10</td><td>赵技术员</td><td>螺杆清洗、液压油更换、安全门检查</td><td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已完成</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
            <tr><td class="cell-mono">MP-0708-0009</td><td>动平衡机 #1</td><td>周保养</td><td>07-08</td><td>赵技术员</td><td>传感器清洁、夹具检查、校准验证</td><td><span class="badge badge-status-ok"><span class="badge-dot" style="background:var(--fg);"></span>已完成</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
          </tbody>
        </table>
      </div>
    </div>
    <div id="tab-records"  v-show="activeTab === 'records'">
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>记录编号</th><th>设备</th><th>保养类型</th><th>执行时间</th><th>执行人</th><th>耗时</th><th>结果</th><th>异常备注</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">MR-0710-0032</td><td>注塑机 #2</td><td>月度保养</td><td>07-10 09:00</td><td>赵技术员</td><td>2h15min</td><td>正常</td><td>—</td></tr>
            <tr><td class="cell-mono">MR-0708-0031</td><td>动平衡机 #1</td><td>周保养</td><td>07-08 08:30</td><td>赵技术员</td><td>45min</td><td>正常</td><td>—</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">点检保养 · 4 条计划 · 待执行 2</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span></footer>
</MesLayout>
</template>
