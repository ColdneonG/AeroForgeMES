<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'id', label: '执行ID', mono: true }, { key: 'taskId', label: '任务' }, { key: 'versionId', label: 'SOP版本' }, { key: 'status', label: '状态' }, { key: 'executedAt', label: '执行时间' }]
</script>
<template><LiveResourceList active="sop-execution" section="电子SOP" title="SOP执行记录" endpoint="/production/sop/executions" :columns="columns" /></template>
