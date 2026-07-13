<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
  <MesLayout active="sys-sequences">
    <header class="app-header">
      <div class="header-breadcrumb"><span>数据与系统</span><span class="bc-sep">/</span><span class="bc-current">编号序列</span></div>
    </header>
    <main class="app-main" data-od-id="seq-main">
      <h1 class="page-title">编号序列管理</h1>
      <div class="card text-muted">系统目前仅提供编号生成接口，尚未提供序列规则查询接口；模拟序列数据已关闭。</div>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span></footer>
  </MesLayout>
</template>
