<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'menuName', label: '菜单名称' }, { key: 'path', label: '路由' }, { key: 'permission', label: '权限标识' }, { key: 'status', label: '状态' }]
</script>
<template><LiveResourceList active="system-menus" section="数据与系统" title="菜单配置" endpoint="/system/menus/tree" :columns="columns" /></template>
