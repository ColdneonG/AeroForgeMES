<template>
  <section class="dashboard-feature-page">
    <div class="dashboard-feature-header">
      <div>
        <p>{{ group }}</p>
        <h1>{{ title }}</h1>
        <span>演示数据已关闭，当前功能页等待后端真实接口接入。</span>
      </div>
      <div class="dashboard-feature-actions">
        <PermissionButton action="create">新增</PermissionButton>
        <PermissionButton action="import">导入</PermissionButton>
        <PermissionButton action="export">导出</PermissionButton>
      </div>
    </div>

    <section class="feature-card feature-main-card">
      <div class="feature-card-title">
        <strong>{{ title }}列表</strong>
        <span>0 条</span>
      </div>
      <table class="feature-table">
        <thead>
          <tr>
            <th v-for="column in columns" :key="column.key">{{ column.label }}</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td :colspan="columns.length" class="feature-empty">暂无真实接口数据</td>
          </tr>
        </tbody>
      </table>
    </section>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import PermissionButton from '../../components/PermissionButton.vue'
import { emptyFeatureConfig } from '../../config/featurePageConfigs'

const route = useRoute()
const title = computed(() => route.meta.title || '功能页')
const group = computed(() => route.meta.group || 'MES')
const columns = emptyFeatureConfig.columns
</script>
