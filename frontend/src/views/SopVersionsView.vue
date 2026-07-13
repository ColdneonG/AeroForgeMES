<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import LiveResourceList from '@/components/LiveResourceList.vue'
const route = useRoute()
const endpoint = computed(() => `/production/sop/documents/${route.query.sopId || 0}/versions`)
const columns = [{ key: 'versionNo', label: '版本号', mono: true }, { key: 'versionName', label: '版本名称' }, { key: 'status', label: '状态' }, { key: 'createdAt', label: '创建时间' }]
</script>
<template><LiveResourceList active="sop-versions" section="电子SOP" title="SOP版本" :endpoint="endpoint" :columns="columns" /></template>
