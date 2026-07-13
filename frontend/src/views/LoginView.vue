<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import LoginBackground from '@/components/LoginBackground.vue'
import symbolmarkNegative from '@/assets/images/Symbolmark-negative.png'
import wordmark from '@/assets/images/Wordmark.png'
import { ApiError } from '@/api/client'
import { login } from '@/api/auth'
const router = useRouter()
const username = ref('')
const password = ref('')
const showError = ref(false)
const loading = ref(false)
const errorMessage = ref('用户名或密码错误，请重试。')
async function handleLogin() {
  showError.value = false
  if (!username.value.trim() || !password.value.trim()) { showError.value = true; return }
  loading.value = true
  try {
    await login(username.value.trim(), password.value)
    await router.push('/dashboard')
  } catch (error) {
    errorMessage.value = error instanceof ApiError ? error.message : '登录失败，请稍后重试。'
    showError.value = true
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page" data-od-id="login-page">
    <!-- Three.js canvas — injected by JS -->
    <LoginBackground />

    <div class="login-card" data-od-id="login-card">
      <div class="login-brand" data-od-id="login-brand">
        <div class="brand-icon">
          <img :src="symbolmarkNegative" alt="AeroForge" width="56" height="56"
               style="display:block;margin:0 auto;transform:translateY(-4px);" />
        </div>
        <img :src="wordmark" alt="风擎工控 AeroForge"
             style="display:block;max-width:280px;margin:var(--space-3) auto var(--space-2);" />
        <p style="text-align:center;">制造执行系统</p>
      </div>

      <form id="loginForm" data-od-id="login-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label" for="username">用户名</label>
          <input id="username" v-model="username" class="form-input" type="text" placeholder="工号或用户名"
                 required autocomplete="username" />
        </div>
        <div class="form-group">
          <label class="form-label" for="password">密码</label>
          <input id="password" v-model="password" class="form-input" type="password" placeholder="输入密码"
                 required autocomplete="current-password" />
        </div>
        <div v-show="showError" id="loginError" class="alert alert-error" style="margin-bottom:var(--space-4);">
          <span class="alert-icon">!</span> {{ errorMessage }}
        </div>
        <button id="loginBtn" type="submit" class="btn btn-primary btn-lg" data-od-id="login-submit"
                :class="{ 'btn-loading': loading }" :disabled="loading">登 录</button>
      </form>

      <div class="login-footer">
        风擎工控 AeroForge MES v2.0 · 仅供授权用户使用
      </div>
    </div>
  </div>
</template>

<style>

    /* ---- Login page overrides for Three.js canvas ------------------ */
    .login-page {
      position: relative;
      overflow: hidden;
    }
    /* Three.js canvas sits behind everything */
    #login-canvas {
      position: fixed;
      inset: 0;
      z-index: 0;
      pointer-events: none;
    }
    /* Login card elevated above canvas */
    .login-card {
      position: relative;
      z-index: 1;
    }
</style>
