<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'id', label: '日志ID', mono: true }, { key: 'systemName', label: '外部系统' }, { key: 'syncType', label: '同步类型' }, { key: 'status', label: '状态' }, { key: 'createdAt', label: '时间' }]
</script>
<template><LiveResourceList active="integration-logs" section="集成管理" title="同步日志" endpoint="/integration/sync-logs" :columns="columns" /></template>
