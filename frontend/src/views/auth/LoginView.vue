<template>
  <main class="login-page">
    <section class="login-card">
      <div class="login-brand">
        <img :src="logoSquare" alt="风擎工控" />
        <div>
          <p>风擎工控</p>
          <h1>Aeroforge MES</h1>
          <span>统一身份认证与权限入口</span>
        </div>
      </div>

      <form class="login-form" @submit.prevent="submitLogin">
        <label>
          账号
          <input v-model="form.username" autocomplete="username" placeholder="admin / operator" />
        </label>
        <label>
          密码
          <input v-model="form.password" autocomplete="current-password" type="password" placeholder="任意密码" />
        </label>
        <button type="submit" :disabled="loading">{{ loading ? '登录中...' : '登录 MES' }}</button>
        <p v-if="error">{{ error }}</p>
      </form>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import logoSquare from '../../assets/icons/logo2.png'
import { login } from '../../services/authService'
import { setAuthSession } from '../../stores/auth'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const error = ref('')
const form = reactive({ username: 'admin', password: 'admin123' })

const submitLogin = async () => {
  loading.value = true
  error.value = ''
  try {
    const session = await login(form)
    setAuthSession(session)
    router.replace(route.query.redirect || '/dashboard')
  } catch (err) {
    error.value = err.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>
