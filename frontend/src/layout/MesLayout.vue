<template>
  <div class="siemens-shell mes-shell-expanded">
    <aside class="siemens-rail" aria-label="MES module navigation">
      <div class="rail-logo" title="AeroForge MES">
        <img :src="logoSquare" alt="AeroForge MES" />
      </div>

      <button
        v-for="group in moduleGroups"
        :key="group.name"
        class="rail-icon"
        :class="{ active: isGroupActive(group.name), expanded: group.name === activeGroupName && isMenuOpen }"
        :title="t(`menu.groups.${group.name}`) || group.name"
        type="button"
        @click="toggleModule(group)"
      >
        <img class="rail-icon-image" :src="group.icon" :alt="group.name" />
      </button>

      <div class="rail-spacer"></div>
      <button class="rail-avatar" :title="authState.user?.displayName || t('common.feature.unknownUser')"></button>
    </aside>

    <Transition name="module-drawer">
      <aside v-if="isMenuOpen" class="module-sidebar">
        <div class="module-title">
          <strong>{{ t(`menu.groups.${activeGroup?.name}`) || activeGroup?.name }}</strong>
          <span>{{ t('layout.featureCount', { count: activeGroup?.items.length || 0 }) }}</span>
        </div>
        <RouterLink
          v-for="item in activeGroup?.items"
          :key="item.name"
          :to="{ name: item.name }"
          class="module-link"
          @click="isMenuOpen = false"
        >
          <span>{{ item.meta.icon }}</span>
          <div>
            <strong>{{ item.meta.title }}</strong>
            <small>{{ item.meta.permission }}</small>
          </div>
        </RouterLink>
      </aside>
    </Transition>

    <section class="siemens-main" @click="closeSideMenu">
      <header class="siemens-topbar">
        <div class="product-title">
          <span class="product-title-cn">{{ t('layout.productCn') }}</span>
          <span class="product-title-en">{{ t('layout.productEn') }}</span>
        </div>
        <div class="topbar-tools">
          <span class="data-scope-chip">{{ dataScopeLabel }}</span>
          <button class="topbar-language" type="button" :title="languageTitle" @click="toggleLanguage">
            <img :src="languageIcon" alt="Language" />
            <span>{{ languageLabel }}</span>
          </button>
          <button class="topbar-user" type="button" @click="logoutUser">
            <strong>{{ authState.user?.displayName || t('layout.notLoggedIn') }}</strong>
            <span>{{ t('layout.logout') }}</span>
          </button>
          <button class="topbar-help" :title="t('layout.help')">
            <img :src="helpIcon" alt="Help" />
          </button>
        </div>
      </header>
      <main class="page-host">
        <RouterView />
      </main>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { menuRoutes } from '../router'
import { logout } from '../services/authService'
import { authState, clearAuthSession } from '../stores/auth'
import { filterRoutesByPermission } from '../utils/menu'
import { LANGUAGE_STORAGE_KEY } from '../i18n'
import logoSquare from '../assets/icons/logo2.png'
import helpIcon from '../assets/icons/help.svg'
import languageIcon from '../assets/icons/language.svg'
import andonIcon from '../assets/icons/andon.svg'
import apiIcon from '../assets/icons/api.svg'
import barcodeIcon from '../assets/icons/barcode.svg'
import dashboardIcon from '../assets/icons/dashboard.svg'
import deviceIntegrationIcon from '../assets/icons/device-integration.svg'
import equipmentIcon from '../assets/icons/equipment.svg'
import erpIcon from '../assets/icons/erp.svg'
import processIcon from '../assets/icons/process.svg'
import productionIcon from '../assets/icons/production.svg'
import qualityIcon from '../assets/icons/quality.svg'
import reportIcon from '../assets/icons/report.svg'
import settingsIcon from '../assets/icons/settings.svg'
import shopfloorIcon from '../assets/icons/shopfloor.svg'
import wageIcon from '../assets/icons/wage.svg'

const { t, locale } = useI18n()
const router = useRouter()
const route = useRoute()

const groupIcons = {
  生产订单: productionIcon,
  条码应用: barcodeIcon,
  现场管理: shopfloorIcon,
  设备管理: equipmentIcon,
  设备对接: deviceIntegrationIcon,
  计件工资: wageIcon,
  工艺管理: processIcon,
  质量管理: qualityIcon,
  安灯管理: andonIcon,
  报表分析: reportIcon,
  电子看板: dashboardIcon,
  ERP接口: erpIcon,
  标准API接口: apiIcon,
  系统管理: settingsIcon
}

const languageLabel = computed(() => (locale.value === 'zh-CN' ? '中' : 'EN'))
const languageTitle = computed(() =>
  locale.value === 'zh-CN' ? t('layout.switchToEn') : t('layout.switchToZh')
)

const visibleRoutes = computed(() => filterRoutesByPermission(menuRoutes))
const moduleGroups = computed(() => {
  const groups = new Map()
  visibleRoutes.value.forEach((item) => {
    const groupName = item.meta.group || '其他'
    if (!groups.has(groupName)) {
      groups.set(groupName, { name: groupName, icon: groupIcons[groupName] || settingsIcon, items: [] })
    }
    groups.get(groupName).items.push(item)
  })
  return [...groups.values()]
})

const activeGroupName = ref('')
const routeGroupName = ref('')
const isMenuOpen = ref(false)
const activeGroup = computed(
  () => moduleGroups.value.find((group) => group.name === activeGroupName.value) || moduleGroups.value[0]
)
const dataScopeLabel = computed(
  () => authState.permissions.dataScopes?.[0] || t('layout.dataScopeUnloaded')
)

watch(
  () => route.name,
  () => {
    const match = visibleRoutes.value.find((item) => item.name === route.name)
    if (match?.meta.group) {
      routeGroupName.value = match.meta.group
      if (!activeGroupName.value) activeGroupName.value = match.meta.group
    }
  },
  { immediate: true }
)

watch(moduleGroups, (groups) => {
  if (!activeGroupName.value && groups[0]) activeGroupName.value = groups[0].name
})

const toggleLanguage = () => {
  const next = locale.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  locale.value = next
  if (typeof document !== 'undefined') {
    document.documentElement.lang = next
  }
  if (typeof window !== 'undefined') {
    window.localStorage.setItem(LANGUAGE_STORAGE_KEY, next)
  }
}

const toggleModule = (group) => {
  if (group.items.length === 1) {
    isMenuOpen.value = false
    activeGroupName.value = group.name
    router.push({ name: group.items[0].name })
    return
  }

  const groupName = group.name
  if (activeGroupName.value === groupName) {
    isMenuOpen.value = !isMenuOpen.value
    if (!isMenuOpen.value) {
      activeGroupName.value = routeGroupName.value
    }
    return
  }

  activeGroupName.value = groupName
  isMenuOpen.value = true
}

const isGroupActive = (groupName) =>
  groupName === activeGroupName.value || (!isMenuOpen.value && groupName === routeGroupName.value)

const closeSideMenu = () => {
  isMenuOpen.value = false
  activeGroupName.value = routeGroupName.value
}

const logoutUser = async () => {
  await logout()
  clearAuthSession()
  router.replace({ name: 'login' })
}

onMounted(() => {
  if (typeof document !== 'undefined') {
    document.documentElement.lang = locale.value
  }
})
</script>
