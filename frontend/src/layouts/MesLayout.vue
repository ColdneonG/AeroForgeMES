<script setup lang="ts">
import { onBeforeUnmount, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppSidebar from '@/components/AppSidebar.vue'
import { logout } from '@/api/auth'
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
async function handleLogout() {
  logout()
  await router.replace('/login')
}
onMounted(() => document.addEventListener('keydown', handleShortcut))
onBeforeUnmount(() => document.removeEventListener('keydown', handleShortcut))
</script>

<template>
  <div class="app-shell">
    <AppSidebar :active="active" />
    <slot />
    <button class="global-logout" type="button" title="退出登录" aria-label="退出登录" @click="handleLogout">
      <svg width="17" height="17" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.7" aria-hidden="true">
        <path d="M8 1v7" stroke-linecap="round"/>
        <path d="M4.1 3.3a6 6 0 1 0 7.8 0" stroke-linecap="round"/>
      </svg>
    </button>
  </div>
</template>
