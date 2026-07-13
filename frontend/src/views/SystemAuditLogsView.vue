<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
  <MesLayout active="sys-audit">
    <header class="app-header">
      <div class="header-breadcrumb"><span>数据与系统</span><span class="bc-sep">/</span><span class="bc-current">操作日志</span></div>
    </header>
    <main class="app-main" data-od-id="audit-main">
      <h1 class="page-title">操作审计日志</h1>
      <div class="card text-muted">审计日志查询接口尚未提供；为避免展示模拟记录，此页面暂不显示日志数据。</div>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span></footer>
  </MesLayout>
</template>
