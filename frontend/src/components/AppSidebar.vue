<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, nextTick, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { get } from '@/api/client'

const props = defineProps<{ active: string }>()
const route = useRoute()
const reminderCounts = ref<Record<string, number>>({})
const terminalStatuses = new Set(['COMPLETED', 'CLOSED', 'VOID', '关闭', '已关闭', '作废', '已作废'])

function isOpen(record: Record<string, unknown>) {
  return !terminalStatuses.has(String(record.status || '').trim().toUpperCase())
}

async function loadReminderCounts() {
  const sources: Array<[string, string]> = [
    ['orders', '/production/work-orders'], ['dispatch', '/production/dispatch-orders'],
    ['tasks', '/production/tasks'], ['kitting', '/production/kitting-analyses'],
    ['defects', '/quality/defects'], ['andon', '/andon/exceptions'],
  ]
  const results = await Promise.allSettled(sources.map(([, path]) => get<Record<string, unknown>[]>(path)))
  reminderCounts.value = Object.fromEntries(results.map((result, index) => [
    sources[index][0], result.status === 'fulfilled' ? result.value.filter(isOpen).length : 0,
  ]))
}

const SCROLL_KEY = 'sidebar-scroll-top'

function saveScroll() {
  const el = document.getElementById('sidebar-nav')
  if (el) sessionStorage.setItem(SCROLL_KEY, String(el.scrollTop))
}

function restoreScroll() {
  const el = document.getElementById('sidebar-nav')
  if (!el) return
  const saved = sessionStorage.getItem(SCROLL_KEY)
  if (saved) el.scrollTop = parseInt(saved, 10)
}

onMounted(() => {
  nextTick(() => restoreScroll())
  document.getElementById('sidebar-nav')?.addEventListener('scroll', saveScroll, { passive: true })
  void loadReminderCounts()
})

onBeforeUnmount(() => {
  saveScroll()
  document.getElementById('sidebar-nav')?.removeEventListener('scroll', saveScroll)
})

type MenuItem = { id: string; label: string; to: string; badgeKey?: string; icon: string }
type MenuSection = { label: string; items: MenuItem[] }

const sections: MenuSection[] = [
  { label: '主菜单', items: [{ id: 'dashboard', label: '工作台', to: '/dashboard', icon: 'grid' }] },
  { label: '生产管理', items: [
    { id: 'orders', label: '生产订单', to: '/production-orders', badgeKey: 'orders', icon: 'list' },
    { id: 'dispatch', label: '派工单', to: '/production/dispatch-orders', badgeKey: 'dispatch', icon: 'arrow' },
    { id: 'tasks', label: '生产任务', to: '/production/tasks', badgeKey: 'tasks', icon: 'check' },
    { id: 'kitting', label: '齐套分析', to: '/production/kitting', badgeKey: 'kitting', icon: 'boxes' },
    { id: 'kboard', label: '缺料看板', to: '/production/kitting-board', icon: 'board' },
    { id: 'shopfloor', label: '车间看板', to: '/shopfloor', icon: 'factory' },
  ]},
  { label: '质量与追溯', items: [
    { id: 'quality', label: '质量检验', to: '/quality-inspection', icon: 'quality' },
    { id: 'inspexec', label: '检验执行', to: '/quality/inspection-exec', icon: 'scan' },
    { id: 'defects', label: '缺陷记录', to: '/quality/defects', badgeKey: 'defects', icon: 'warn' },
    { id: 'quality-plans', label: '检验方案', to: '/quality/plans', icon: 'quality' },
    { id: 'quality-items', label: '检验项', to: '/quality/items', icon: 'list' },
    { id: 'quality-categories', label: '检验项分类', to: '/quality/categories', icon: 'boxes' },
  ]},
  { label: '设备与安灯', items: [
    { id: 'equipment', label: '设备监控', to: '/equipment-monitor', icon: 'gear' },
    { id: 'equip-ledger', label: '设备台账', to: '/equipment/ledger', icon: 'list' },
    { id: 'equip-maintain', label: '点检保养', to: '/equipment/maintenance', icon: 'check' },
    { id: 'equip-detail', label: '设备详情', to: '/equipment/detail', icon: 'gear' },
    { id: 'andon', label: '安灯异常', to: '/andon/exceptions', badgeKey: 'andon', icon: 'warn' },
  ]},
  { label: '条码与追溯', items: [
    { id: 'barcode', label: '条码管理', to: '/barcode/list', icon: 'scan' },
    { id: 'barcode-types', label: '条码类型', to: '/barcode/types', icon: 'boxes' },
    { id: 'barcode-rules', label: '条码规则', to: '/barcode/rules', icon: 'list' },
    { id: 'barcode-tpl', label: '打印模板', to: '/barcode/templates', icon: 'board' },
    { id: 'scan', label: '扫码终端', to: '/barcode/scan', icon: 'scan' },
    { id: 'trace', label: '追溯查询', to: '/trace/query', icon: 'arrow' },
  ]},
  { label: '电子 SOP', items: [
    { id: 'sop-docs', label: 'SOP 文档', to: '/sop/documents', icon: 'list' },
    { id: 'sopexec', label: '执行终端', to: '/sop/execution', icon: 'check' },
    { id: 'sop-steps', label: '步骤编排', to: '/sop/steps', icon: 'boxes' },
  ]},
  { label: '控制中心', items: [
    { id: 'ccenter', label: '制造大屏', to: '/reports/control-center', icon: 'board' },
    { id: 'line-config', label: '产线看板配置', to: '/reports/line-config', icon: 'gear' },
    { id: 'workshop-config', label: '车间看板配置', to: '/reports/workshop-config', icon: 'gear' },
  ]},
  { label: '集成管理', items: [
    { id: 'erp', label: 'ERP 集成', to: '/integration/erp', icon: 'arrow' },
    { id: 'sync-logs', label: '同步日志', to: '/integration/sync-logs', icon: 'list' },
    { id: 'openapi', label: 'OpenAPI', to: '/integration/openapi', icon: 'boxes' },
  ]},
  { label: '数据与系统', items: [
    { id: 'reports-main', label: '生产报表', to: '/reports', icon: 'chart' },
    { id: 'output', label: '产量报表', to: '/reports/output', icon: 'chart' },
    { id: 'metrics', label: '指标管理', to: '/reports/metrics', icon: 'gear' },
    { id: 'users', label: '用户管理', to: '/system-users', icon: 'users' },
    { id: 'sys-menus', label: '菜单配置', to: '/system/menus', icon: 'list' },
    { id: 'sys-sequences', label: '编号序列', to: '/system/sequences', icon: 'boxes' },
    { id: 'sys-audit', label: '操作日志', to: '/system/audit-logs', icon: 'check' },
  ]},
]

// Detail pages can declare the menu item they belong to in route metadata.
// This keeps the parent menu highlighted while a record detail is open.
const activeId = computed(() => String(route.meta.sidebarActive || props.active))

function isActive(item: MenuItem) {
  return item.id === activeId.value || route.path === item.to
}

function badgeFor(item: MenuItem) {
  const count = item.badgeKey ? reminderCounts.value[item.badgeKey] : 0
  return count > 0 ? String(count) : ''
}

watch(() => route.fullPath, () => { void loadReminderCounts() })
</script>

<template>
  <aside class="app-sidebar" data-od-id="page-sidebar">
    <div class="sidebar-brand">
      <div class="brand-wordmark">风擎工控<span class="brand-subtitle">AeroForge MES</span></div>
    </div>
    <nav id="sidebar-nav" class="sidebar-nav" :data-active="activeId">
      <div v-for="section in sections" :key="section.label" class="nav-section">
        <div class="nav-section-label">{{ section.label }}</div>
        <RouterLink v-for="item in section.items" :key="item.to" :to="item.to" class="nav-item" :class="{ active: isActive(item) }">
          <span class="nav-icon" aria-hidden="true">
            <svg v-if="item.icon === 'grid'" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.7"><rect x="1" y="1" width="6" height="6" rx="1"/><rect x="9" y="1" width="6" height="6" rx="1"/><rect x="1" y="9" width="6" height="6" rx="1"/><rect x="9" y="9" width="6" height="6" rx="1"/></svg>
            <svg v-else-if="item.icon === 'chart'" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.7"><rect x="1" y="10" width="4" height="5"/><rect x="6" y="6" width="4" height="9"/><rect x="11" y="2" width="4" height="13"/></svg>
            <svg v-else-if="item.icon === 'warn'" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M8 1 1 15h14L8 1Z"/><path d="M8 6v4"/><circle cx="8" cy="12" r=".7" fill="currentColor"/></svg>
            <svg v-else-if="item.icon === 'check' || item.icon === 'quality'" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.7"><path d="m2 9 3 3 9-9"/></svg>
            <svg v-else-if="item.icon === 'gear'" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.7"><circle cx="8" cy="8" r="6"/><circle cx="8" cy="8" r="2"/><path d="M8 2v2M8 12v2M2 8h2M12 8h2"/></svg>
            <svg v-else-if="item.icon === 'arrow'" width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M3 13 13 3M7 3h6v6"/></svg>
            <svg v-else width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.7"><rect x="2" y="2" width="12" height="12" rx="2"/><path d="M5 6h6M5 9h6M5 12h4"/></svg>
          </span>
          {{ item.label }}<span v-if="badgeFor(item)" class="nav-badge">{{ badgeFor(item) }}</span>
        </RouterLink>
      </div>
    </nav>
  </aside>
</template>
