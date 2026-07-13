<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import LiveResourceList from '@/components/LiveResourceList.vue'
const route = useRoute()
const endpoint = computed(() => `/production/sop/versions/${route.query.versionId || 0}/bindings`)
const columns = [{ key: 'bindingType', label: '绑定类型' }, { key: 'bindingValue', label: '绑定对象' }, { key: 'status', label: '状态' }]
</script>
<template><LiveResourceList active="sop-bindings" section="电子SOP" title="SOP绑定配置" :endpoint="endpoint" :columns="columns" /></template>
