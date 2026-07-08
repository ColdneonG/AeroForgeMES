import { createI18n } from 'vue-i18n'
import zhCN from './locales/zh-CN.json'
import enUS from './locales/en-US.json'

const LANGUAGE_STORAGE_KEY = 'fan-mes-language'
const supportedLanguages = ['zh-CN', 'en-US']

const getStoredLanguage = () => {
  if (typeof window === 'undefined') return 'zh-CN'
  const stored = window.localStorage.getItem(LANGUAGE_STORAGE_KEY)
  return supportedLanguages.includes(stored) ? stored : 'zh-CN'
}

const i18n = createI18n({
  legacy: false,
  locale: getStoredLanguage(),
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS
  }
})

export { LANGUAGE_STORAGE_KEY, supportedLanguages }
export default i18n
