import { createApp, type Directive } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/styles/design-system.css'

const countFrames = new WeakMap<HTMLElement, number>()
const countUp: Directive<HTMLElement> = {
  mounted(element) {
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return
    const original = element.innerHTML
    const text = element.textContent || ''
    const target = Number.parseFloat(text.replace(/[^\d.]/g, ''))
    if (!Number.isFinite(target) || target === 0) return
    const prefix = text.trim().match(/^[^\d]*/)?.[0] || ''
    const suffix = text.includes('%') ? '%' : ''
    const comma = text.includes(',')
    const started = performance.now()
    const tick = (now: number) => {
      const progress = Math.min((now - started) / 600, 1)
      const value = Math.round((1 - Math.pow(1 - progress, 3)) * target)
      element.textContent = `${prefix}${comma ? value.toLocaleString() : value}${suffix}`
      if (progress < 1) countFrames.set(element, requestAnimationFrame(tick))
      else element.innerHTML = original
    }
    countFrames.set(element, requestAnimationFrame(tick))
  },
  unmounted(element) {
    const frame = countFrames.get(element)
    if (frame) cancelAnimationFrame(frame)
    countFrames.delete(element)
  },
}

createApp(App).directive('count-up', countUp).use(router).mount('#app')
