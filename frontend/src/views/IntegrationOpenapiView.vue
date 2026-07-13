<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'requestId', label: '请求ID', mono: true }, { key: 'apiPath', label: '接口路径' }, { key: 'method', label: '方法' }, { key: 'statusCode', label: '状态码' }, { key: 'createdAt', label: '请求时间' }]
</script>
<template><LiveResourceList active="integration-api" section="集成管理" title="OpenAPI请求日志" endpoint="/integration/request-logs" :columns="columns" /></template>
