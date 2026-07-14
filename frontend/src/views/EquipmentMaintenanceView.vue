<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'maintenanceNo', label: '任务编号', mono: true }, { key: 'equipmentId', label: '设备' }, { key: 'assignedTo', label: '负责人' }, { key: 'planAt', label: '计划时间' }, { key: 'status', label: '状态' }]
</script>
<template><LiveResourceList active="equip-maint" section="设备与安灯" title="点检保养" endpoint="/equipment/maintenance-tasks" :columns="columns" /></template>
