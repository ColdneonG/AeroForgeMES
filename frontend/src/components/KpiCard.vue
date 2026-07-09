<template>
  <article class="mes-card kpi-card" :class="toneClass">
    <div class="kpi-label">{{ label }}</div>
    <div class="kpi-main">
      <strong>{{ value }}</strong>
      <span v-if="unit">{{ unit }}</span>
    </div>
    <div class="kpi-footer">
      <span>{{ caption }}</span>
      <b>{{ trend }}</b>
    </div>
    <div v-if="updatedAt" class="kpi-time">{{ $t('tableColumns.updatedAt') }} {{ updatedAt }}</div>
  </article>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  label: { type: String, required: true },
  value: { type: [String, Number], required: true },
  unit: { type: String, default: '' },
  caption: { type: String, default: '' },
  trend: { type: String, default: '' },
  tone: { type: String, default: 'info' },
  updatedAt: { type: String, default: '' }
})

const toneClass = computed(() => `kpi-${props.tone}`)
</script>

<style scoped>
.kpi-card {
  min-height: 124px;
  padding: 16px 18px;
  border-top: 3px solid var(--tone, var(--mes-accent));
}

.kpi-label {
  color: var(--mes-muted);
  font-size: 14px;
  font-weight: 600;
}

.kpi-main {
  display: flex;
  align-items: baseline;
  gap: 5px;
  margin-top: 6px;
}

.kpi-main strong {
  color: var(--mes-text-title);
  font-size: 31px;
  font-weight: 600;
  line-height: 1;
}

.kpi-main span {
  color: var(--mes-muted);
  font-size: 14px;
}

.kpi-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  color: var(--mes-muted);
  font-size: 13px;
}

.kpi-time {
  margin-top: 8px;
  color: var(--mes-text-weak);
  font-size: 12px;
}

.kpi-footer b {
  color: var(--tone, var(--mes-accent));
  font-weight: 600;
}

.kpi-success {
  --tone: var(--mes-success);
}

.kpi-info {
  --tone: var(--mes-info);
}

.kpi-warning {
  --tone: var(--mes-warning);
}

.kpi-danger {
  --tone: var(--mes-danger);
}

.kpi-stable {
  --tone: var(--mes-stable);
}
</style>
