<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'id', label: '配置ID', mono: true }, { key: 'boardName', label: '看板名称' }, { key: 'workshopId', label: '车间' }, { key: 'status', label: '状态' }]
</script>
<template><LiveResourceList active="reports-workshop-config" section="报表与看板" title="车间看板配置" endpoint="/report/boards/workshop-config" :columns="columns" /></template>
