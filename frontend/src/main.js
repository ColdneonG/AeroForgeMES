import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import i18n from './i18n'
import { clearAuthSession } from './stores/auth'
import './styles/theme.css'
import './styles/mes.css'

// 应用启动时清除残留的本地认证缓存，确保默认未登录，跳转登录页
clearAuthSession()

createApp(App).use(router).use(i18n).mount('#app')
