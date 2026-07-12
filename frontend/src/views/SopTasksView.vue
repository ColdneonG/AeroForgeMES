<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="sop-tasks">
  
  <header class="app-header"><div class="header-breadcrumb"><span>电子SOP</span> <span class="bc-sep">/</span> <span class="bc-current">任务下发</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="notify('下发任务 — POST /api/production/sop/tasks')">+ 下发任务</button><span class="user-avatar">张</span></div></header>
  <main class="app-main" data-od-id="sopt-main">
    <h1 class="page-title">SOP任务下发</h1>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>任务编号</th><th>SOP文档</th><th>版本</th><th>目标产线</th><th>工位</th><th>下发人</th><th>下发时间</th><th>签收状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td class="cell-mono">ST-0711-0032</td><td>FS-40 总装作业指导书</td><td>v3.2</td><td>总装线 A</td><td>A01-A05</td><td>张工</td><td>07-11 08:00</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>已签收</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
        <tr><td class="cell-mono">ST-0711-0031</td><td>FS-40 绕线作业指导书</td><td>v2.1</td><td>绕线车间</td><td>WD-01-03</td><td>李组长</td><td>07-11 08:00</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>已签收</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
        <tr><td class="cell-mono">ST-0711-0030</td><td>FT-30 台扇总装指导书</td><td>v2.3</td><td>总装线 B</td><td>B01-B05</td><td>张工</td><td>07-11 08:00</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>未签收</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">催办</button></td></tr>
        <tr><td class="cell-mono">ST-0710-0029</td><td>FS-35 通电测试规范</td><td>v1.8</td><td>总装线 A</td><td>A05</td><td>王质检</td><td>07-10 14:00</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>已签收</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">详情</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">SOP任务 · 4 条 · 已签收 3 · 未签收 1</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span></footer>
</MesLayout>
</template>
