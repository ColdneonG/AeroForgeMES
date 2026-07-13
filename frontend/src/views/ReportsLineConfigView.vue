<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'id', label: '配置ID', mono: true }, { key: 'boardName', label: '看板名称' }, { key: 'lineId', label: '产线' }, { key: 'status', label: '状态' }]
</script>
<template><LiveResourceList active="reports-line-config" section="报表与看板" title="产线看板配置" endpoint="/report/boards/line-config" :columns="columns" /></template>
