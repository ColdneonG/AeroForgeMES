<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'equipmentCode', label: '设备编码', mono: true }, { key: 'equipmentName', label: '设备名称' }, { key: 'status', label: '运行状态' }, { key: 'lineId', label: '产线' }]
</script>
<template><LiveResourceList active="equip-monitor" section="设备与安灯" title="设备监控" endpoint="/equipment/equipments" :columns="columns" /></template>
