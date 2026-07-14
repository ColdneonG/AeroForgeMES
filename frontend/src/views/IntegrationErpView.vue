<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'systemCode', label: '系统编码', mono: true }, { key: 'systemName', label: '系统名称' }, { key: 'authType', label: '认证方式' }, { key: 'status', label: '状态' }]
</script>
<template><LiveResourceList active="integration-erp" section="集成管理" title="ERP与外部系统" endpoint="/integration/external-systems" :columns="columns" /></template>
