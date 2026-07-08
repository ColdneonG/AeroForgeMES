<template>
  <main class="login-page">
    <section class="login-card">
      <div class="login-brand">
        <img :src="logoSquare" :alt="$t('auth.login.brandName')" />
        <div>
          <p>{{ $t('auth.login.brandName') }}</p>
          <h1>{{ $t('auth.login.brandTitle') }}</h1>
          <span>{{ $t('auth.login.brandSubtitle') }}</span>
        </div>
      </div>

      <form class="login-form" @submit.prevent="submitLogin">
        <label>
          {{ $t('auth.login.username') }}
          <input v-model="form.username" autocomplete="username" :placeholder="$t('auth.login.usernamePlaceholder')" />
        </label>
        <label>
          {{ $t('auth.login.password') }}
          <input v-model="form.password" autocomplete="current-password" type="password" :placeholder="$t('auth.login.passwordPlaceholder')" />
        </label>
        <button type="submit" :disabled="loading">{{ loading ? $t('auth.login.submitting') : $t('auth.login.submit') }}</button>
        <p v-if="error">{{ error }}</p>
      </form>
    </section>
  </main>
</template>

<script setup>
import { nextTick, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import logoSquare from '../../assets/icons/logo2.png'
import { login } from '../../services/authService'
import { setAuthSession, isAuthenticated } from '../../stores/auth'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const loading = ref(false)
const error = ref('')
const form = reactive({ username: '', password: '' })

const getRedirectTarget = () => {
  const redirect = Array.isArray(route.query.redirect) ? route.query.redirect[0] : route.query.redirect
  return redirect || { name: 'dashboard' }
}

const submitLogin = async () => {
  loading.value = true
  error.value = ''
  try {
    const session = await login(form)
    setAuthSession(session)
    await nextTick()
    if (!isAuthenticated.value) {
      error.value = t('auth.login.authStateError')
      return
    }
    await router.push(getRedirectTarget())
  } catch (err) {
    error.value = err.message || t('auth.login.loginFailed')
  } finally {
    loading.value = false
  }
}
</script>
