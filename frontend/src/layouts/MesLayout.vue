<script setup lang="ts">
import { onBeforeUnmount, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppSidebar from '@/components/AppSidebar.vue'
defineProps<{ active: string }>()
const router = useRouter()
const shortcuts: Record<string, string> = {
  Digit1: '/dashboard',
  Digit2: '/production-orders',
  Digit3: '/shopfloor',
  Digit4: '/quality-inspection',
  Digit5: '/equipment-monitor',
  Digit6: '/reports',
}
function handleShortcut(event: KeyboardEvent) {
  const target = event.target as HTMLElement
  if (target.matches('input, textarea, [contenteditable="true"]')) return
  if (event.ctrlKey && shortcuts[event.code]) {
    event.preventDefault()
    router.push(shortcuts[event.code])
  } else if (event.key === 'Escape' && !event.ctrlKey && !event.altKey && !event.metaKey) {
    event.preventDefault()
    router.push('/dashboard')
  }
}
onMounted(() => document.addEventListener('keydown', handleShortcut))
onBeforeUnmount(() => document.removeEventListener('keydown', handleShortcut))
</script>

<template>
  <div class="app-shell">
    <AppSidebar :active="active" />
    <slot />
  </div>
</template>
