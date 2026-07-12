<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="sop-steps">
  
  <header class="app-header"><div class="header-breadcrumb"><span>电子SOP</span> <span class="bc-sep">/</span> <span class="bc-current">步骤编排</span></div><div class="header-actions"><button class="btn btn-primary btn-sm">+ 添加步骤</button><span class="user-avatar">张</span></div></header>
  <main class="app-main" data-od-id="sopsteps-main">
    <h1 class="page-title">步骤编排 — FS-40 落地扇总装 SOP v3.2</h1>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>步骤号</th><th>步骤标题</th><th>操作说明</th><th>关键参数</th><th>附件</th><th>排序</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td>1</td><td>安装底座与立杆</td><td>底座对准立杆，锁紧 4 颗 M6 螺丝</td><td>扭矩 5.0 N·m</td><td>1 张图</td><td>1</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">↑↓</button><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td>2</td><td>安装电机并接线</td><td>电机固定在立杆顶部，接好 3 根引线</td><td>线序：红-L/蓝-N/黄绿-E</td><td>2 张图</td><td>2</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">↑↓</button><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td>3</td><td>安装扇叶并锁紧</td><td>扇叶套入电机轴，锁紧螺母</td><td>扭矩 3.5-4.0 N·m</td><td>1 张图</td><td>3</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">↑↓</button><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td>4</td><td>安装网罩</td><td>前后网罩合拢，卡扣锁紧</td><td>6 个卡扣全部到位</td><td>—</td><td>4</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">↑↓</button><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td>5</td><td>安装开关面板</td><td>面板卡入底座槽口，接好开关线</td><td>3 档琴键 + 摇头拉杆</td><td>—</td><td>5</td><td class="cell-actions"><button class="btn btn-ghost btn-sm">↑↓</button><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">步骤编排 · SOP-FS40-001 v3.2 · 8 个步骤</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span></footer>
</MesLayout>
</template>
