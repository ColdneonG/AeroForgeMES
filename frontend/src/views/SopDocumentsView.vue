<script setup lang="ts">
import LiveResourceList from '@/components/LiveResourceList.vue'
const columns = [{ key: 'sopCode', label: 'SOP编码', mono: true }, { key: 'sopName', label: 'SOP名称' }, { key: 'category', label: '分类' }, { key: 'status', label: '状态' }]
</script>
<template><LiveResourceList active="sop-docs" section="电子SOP" title="SOP文档管理" endpoint="/production/sop/documents" :columns="columns" /></template>
