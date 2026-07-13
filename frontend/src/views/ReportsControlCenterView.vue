<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { get } from '@/api/client'

const clock = ref(new Date().toLocaleTimeString('zh-CN', { hour12: false }))
const board = ref<Record<string, unknown>>({})
const error = ref('')
const entries = computed(() => Object.entries(board.value))

onMounted(async () => {
  window.setInterval(() => { clock.value = new Date().toLocaleTimeString('zh-CN', { hour12: false }) }, 1000)
  try { board.value = await get<Record<string, unknown>>('/report/boards/control-center') }
  catch (reason) { error.value = reason instanceof Error ? reason.message : '控制中心数据加载失败' }
})
</script>

<template>
  <div data-od-id="ccenter">
    <header class="cc-header">
      <RouterLink to="/dashboard" class="cc-back-btn" aria-label="返回工作台">←</RouterLink>
      <div><h1>风擎工控 · 制造控制中心</h1><span>实时数据看板</span></div>
      <div class="cc-clock">{{ clock }}</div>
    </header>
    <main class="cc-grid">
      <div v-if="error" class="cc-card _wide">{{ error }}</div>
      <template v-else-if="entries.length">
        <section v-for="([key, value]) in entries" :key="key" class="cc-card">
          <h3>{{ key }}</h3><pre>{{ typeof value === 'string' ? value : JSON.stringify(value, null, 2) }}</pre>
        </section>
      </template>
      <div v-else class="cc-card _wide">暂无控制中心实时数据</div>
    </main>
  </div>
</template>

<style>
body { overflow:auto; background:var(--fg) }.cc-header { background:var(--fg); color:#fff; padding:var(--space-5) var(--space-6); display:flex; justify-content:space-between; align-items:center }.cc-header h1 { margin:0; font-size:2rem }.cc-back-btn { color:#fff; font-size:1.5rem; text-decoration:none }.cc-clock { font-family:var(--font-mono); font-size:2rem }.cc-grid { display:grid; grid-template-columns:repeat(4,1fr); gap:var(--space-4); padding:var(--space-5); max-width:1600px; margin:0 auto }.cc-card { background:var(--bg); border-radius:var(--radius); padding:var(--space-4) }.cc-card h3 { margin:0 0 var(--space-3); opacity:.6 }.cc-card pre { white-space:pre-wrap; overflow-wrap:anywhere; margin:0; font-size:var(--text-caption) }._wide { grid-column:span 4 }
</style>
