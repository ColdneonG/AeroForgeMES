<template>
  <section class="mes-panel audit-panel">
    <div class="mes-panel-title">
      <strong>状态日志 / 操作审计</strong>
      <span>{{ logs.length }} 条</span>
    </div>
    <div class="audit-list">
      <article v-for="log in logs" :key="`${log.time}-${log.action}-${log.target || log.to}`">
        <strong>{{ log.action }}</strong>
        <span>{{ log.operator }} · {{ log.time }}</span>
        <p>{{ log.target || log.from + ' → ' + log.to }} {{ log.remark ? ` · ${log.remark}` : '' }}</p>
      </article>
    </div>
  </section>
</template>

<script setup>
defineProps({
  logs: { type: Array, default: () => [] }
})
</script>
