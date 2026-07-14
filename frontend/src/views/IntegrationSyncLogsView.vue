<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'id', label: '日志ID', mono: true }, { key: 'systemId', label: '外部系统ID' }, { key: 'interfaceCode', label: '接口编码' }, { key: 'syncStatus', label: '同步状态' }, { key: 'startedAt', label: '时间' }]
</script>
<template><LiveResourceList active="integration-logs" section="集成管理" title="同步日志" endpoint="/integration/sync-logs" :columns="columns" /></template>
