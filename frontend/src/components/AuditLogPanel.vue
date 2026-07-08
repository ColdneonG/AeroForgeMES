<template>
  <section class="mes-panel audit-panel">
    <div class="mes-panel-title">
      <strong>{{ t('common.auditLog.title') }}</strong>
      <span>{{ t('common.list.items', { count: logs.length }) }}</span>
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
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

defineProps({
  logs: { type: Array, default: () => [] }
})
</script>
