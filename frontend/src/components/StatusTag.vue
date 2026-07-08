<template>
  <span class="status-tag" :class="statusClass">{{ displayLabel }}</span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: { type: String, required: true },
  label: { type: String, default: '' }
})

const statusMap = {
  运行: 'running',
  正常: 'ok',
  合格: 'ok',
  已完成: 'completed',
  完成: 'completed',
  待机: 'idle',
  待开始: 'pending',
  待检: 'pending',
  进行中: 'progress',
  暂停: 'paused',
  维护中: 'maintenance',
  异常: 'exception',
  故障: 'fault',
  停机: 'fault',
  不合格: 'exception',
  返工: 'warning',
  报废: 'danger',
  信息: 'info'
}

const displayLabel = computed(() => props.label || props.status)
const statusClass = computed(() => `status-${statusMap[props.status] || 'stable'}`)
</script>
