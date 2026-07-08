<template>
  <section class="dashboard-feature-page">
    <div class="dashboard-feature-header">
      <div>
        <p>{{ group }}</p>
        <h1>{{ title }}</h1>
        <span>{{ $t('common.feature.demoDisabled') }}</span>
      </div>
      <div class="dashboard-feature-actions">
        <PermissionButton action="create">{{ $t('common.actions.create') }}</PermissionButton>
        <PermissionButton action="import">{{ $t('common.actions.import') }}</PermissionButton>
        <PermissionButton action="export">{{ $t('common.actions.export') }}</PermissionButton>
      </div>
    </div>

    <section class="feature-card feature-main-card">
      <div class="feature-card-title">
        <strong>{{ $t('common.feature.listTitle', { title }) }}</strong>
        <span>{{ $t('common.list.items', { count: 0 }) }}</span>
      </div>
      <table class="feature-table">
        <thead>
          <tr>
            <th v-for="column in columns" :key="column.key">{{ column.label }}</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td :colspan="columns.length" class="feature-empty">{{ $t('common.feature.noData') }}</td>
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
