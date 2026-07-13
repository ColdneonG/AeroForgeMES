<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'equipmentCode', label: '设备编码', mono: true }, { key: 'equipmentName', label: '设备名称' }, { key: 'equipmentType', label: '设备类型' }, { key: 'status', label: '状态' }, { key: 'location', label: '位置' }]
</script>
<template><LiveResourceList active="equip-ledger" section="设备与安灯" title="设备台账" endpoint="/equipment/equipments" :columns="columns" /></template>
