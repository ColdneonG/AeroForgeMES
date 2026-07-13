<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'id', label: '用户ID', mono: true }, { key: 'username', label: '用户名' }, { key: 'displayName', label: '显示名称' }]
</script>
<template><LiveResourceList active="system-users" section="数据与系统" title="用户管理" endpoint="/auth/users" :columns="columns" /></template>
