<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
  <MesLayout active="sop-tasks">
    <header class="app-header">
      <div class="header-breadcrumb"><span>电子 SOP</span><span class="bc-sep">/</span><span class="bc-current">任务下发</span></div>
    </header>
    <main class="app-main" data-od-id="sopt-main">
      <h1 class="page-title">SOP 任务下发</h1>
      <div class="card text-muted">SOP 任务列表接口尚未提供；模拟任务记录已关闭。</div>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span></footer>
  </MesLayout>
</template>
