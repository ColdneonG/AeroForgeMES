<template>
  <section class="mes-workspace">
    <div class="mes-page-heading">
      <div>
        <p>{{ t('shopfloor.sop.eyebrow') }}</p>
        <h1>{{ t('shopfloor.sop.title') }}</h1>
        <span>{{ t('common.feature.demoDisabled') }}</span>
      </div>
      <div class="mes-toolbar">
        <button class="mes-action-button" @click="confirmed = true">{{ t('common.actions.create') }}</button>
      </div>
    </div>
    <div class="sop-layout">
      <section class="mes-panel sop-preview">
        <div class="mes-panel-title">
          <strong>{{ t('common.feature.noData') }}</strong>
          <span>disabled</span>
        </div>
        <div class="sop-canvas">
          <h2>{{ t('common.feature.demoDisabled') }}</h2>
          <p>{{ t('common.feature.demoDisabled') }}</p>
        </div>
      </section>
      <section class="mes-panel">
        <div class="mes-panel-title">
          <strong>{{ t('common.auditLog.title') }}</strong>
          <span>{{ confirmed ? t('common.detail.unselected') : t('common.detail.unselected') }}</span>
        </div>
        <ul class="simple-list">
          <li>{{ t('tableColumns.id') }}：-</li>
          <li>{{ t('tableColumns.line') }}：-</li>
          <li>{{ t('tableColumns.owner') }}：-</li>
          <li>{{ t('common.filter.status') }}：{{ confirmed ? 'confirmed' : 'pending' }}</li>
        </ul>
      </section>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const confirmed = ref(false)
</script>
