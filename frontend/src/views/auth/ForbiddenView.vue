<template>
  <main class="simple-state-page">
    <section>
      <strong>403</strong>
      <h1>{{ t('auth.forbidden.title') }}</h1>
      <p>{{ t('auth.forbidden.description') }}</p>
      <RouterLink to="/dashboard">{{ t('auth.forbidden.backToDashboard') }}</RouterLink>
    </section>
  </main>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
</script>
