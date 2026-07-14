<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'id', label: '请求ID', mono: true }, { key: 'syncLogId', label: '同步日志ID' }, { key: 'url', label: '接口路径' }, { key: 'method', label: '方法' }, { key: 'requestedAt', label: '请求时间' }]
</script>
<template><LiveResourceList active="integration-api" section="集成管理" title="OpenAPI请求日志" endpoint="/integration/request-logs" :columns="columns" /></template>
