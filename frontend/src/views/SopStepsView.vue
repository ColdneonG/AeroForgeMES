<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import LiveResourceList from '@/components/LiveResourceList.vue'
const route = useRoute()
const endpoint = computed(() => `/production/sop/versions/${route.query.versionId || 0}/steps`)
const columns = [{ key: 'stepNo', label: '步骤号', mono: true }, { key: 'stepName', label: '步骤名称' }, { key: 'instruction', label: '作业说明' }, { key: 'status', label: '状态' }]
</script>
<template><LiveResourceList active="sop-steps" section="电子SOP" title="SOP步骤编排" :endpoint="endpoint" :columns="columns" /></template>
