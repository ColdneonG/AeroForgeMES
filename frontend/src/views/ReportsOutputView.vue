<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'statDate', label: '日期' }, { key: 'lineName', label: '产线' }, { key: 'outputQty', label: '产量' }, { key: 'qualifiedQty', label: '合格数量' }]
</script>
<template><LiveResourceList active="reports-output" section="报表与看板" title="产量报表" endpoint="/report/production-output/daily" :columns="columns" /></template>
