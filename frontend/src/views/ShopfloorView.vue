<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'workshopName', label: '车间' }, { key: 'lineName', label: '产线' }, { key: 'outputQty', label: '产量' }, { key: 'status', label: '状态' }]
</script>
<template><LiveResourceList active="shopfloor" section="报表与看板" title="车间看板" endpoint="/report/boards/workshop" :columns="columns" /></template>
