<template>
  <CrudBoard
    eyebrow="标准API接口"
    title="微服务 API 目录"
    description="从后端各微服务 Controller 的请求映射自动生成。"
    list-title="标准 API 接口目录"
    :rows="apiCatalog"
    :columns="columns"
    row-key="id"
  />
</template>

<script setup>
import CrudBoard from '../../components/CrudBoard.vue'
import apiCatalog from '../../generated/apiCatalog'

const columns = [
  { key: 'name', label: '接口名称' },
  { key: 'service', label: '所属微服务' },
  { key: 'module', label: '所属模块' },
  { key: 'method', label: '请求方式' },
  { key: 'path', label: '接口路径' },
  { key: 'description', label: '接口说明' },
  { key: 'frontendConnected', label: '是否已前端对接' },
  { key: 'status', label: '状态' }
]
</script>
