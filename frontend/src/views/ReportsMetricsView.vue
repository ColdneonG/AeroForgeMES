<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'metricCode', label: '指标编码', mono: true }, { key: 'metricName', label: '指标名称' }, { key: 'unit', label: '单位' }, { key: 'status', label: '状态' }]
</script>
<template><LiveResourceList active="reports-metrics" section="报表与看板" title="指标管理" endpoint="/report/metrics" :columns="columns" /></template>
