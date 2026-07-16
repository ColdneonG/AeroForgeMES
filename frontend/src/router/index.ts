import { createRouter, createWebHistory } from 'vue-router'
import { hasValidSession } from '@/api/session'
import { restoreSession } from '@/api/auth'
const AndonExceptionsView = () => import('@/views/AndonExceptionsView.vue')
const BarcodeListView = () => import('@/views/BarcodeListView.vue')
const BarcodeGeneratePrintView = () => import('@/views/BarcodeGeneratePrintView.vue')
const BarcodeRulesView = () => import('@/views/BarcodeRulesView.vue')
const BarcodeScanView = () => import('@/views/BarcodeScanView.vue')
const BarcodeTemplatesView = () => import('@/views/BarcodeTemplatesView.vue')
const BarcodeTypesView = () => import('@/views/BarcodeTypesView.vue')
const DashboardView = () => import('@/views/DashboardView.vue')
const EquipmentMonitorView = () => import('@/views/EquipmentMonitorView.vue')
const EquipmentDetailView = () => import('@/views/EquipmentDetailView.vue')
const EquipmentLedgerView = () => import('@/views/EquipmentLedgerView.vue')
const EquipmentMaintenanceView = () => import('@/views/EquipmentMaintenanceView.vue')
const IntegrationErpView = () => import('@/views/IntegrationErpView.vue')
const IntegrationOpenapiView = () => import('@/views/IntegrationOpenapiView.vue')
const IntegrationSyncLogsView = () => import('@/views/IntegrationSyncLogsView.vue')
const LoginView = () => import('@/views/LoginView.vue')
const ProductionOrderDetailView = () => import('@/views/ProductionOrderDetailView.vue')
const ProductionOrdersView = () => import('@/views/ProductionOrdersView.vue')
const ProductionDispatchOrdersView = () => import('@/views/ProductionDispatchOrdersView.vue')
const ProductionKittingView = () => import('@/views/ProductionKittingView.vue')
const ProductionKittingBoardView = () => import('@/views/ProductionKittingBoardView.vue')
const ProductionTasksView = () => import('@/views/ProductionTasksView.vue')
const ProfileView = () => import('@/views/ProfileView.vue')
const QualityInspectionView = () => import('@/views/QualityInspectionView.vue')
const QualityInspectionDetailView = () => import('@/views/QualityInspectionDetailView.vue')
const QualityCategoriesView = () => import('@/views/QualityCategoriesView.vue')
const QualityDefectsView = () => import('@/views/QualityDefectsView.vue')
const QualityInspectionExecView = () => import('@/views/QualityInspectionExecView.vue')
const QualityItemsView = () => import('@/views/QualityItemsView.vue')
const QualityPlanDetailView = () => import('@/views/QualityPlanDetailView.vue')
const QualityPlansView = () => import('@/views/QualityPlansView.vue')
const ReportsView = () => import('@/views/ReportsView.vue')
const ReportsControlCenterView = () => import('@/views/ReportsControlCenterView.vue')
const ReportsLineConfigView = () => import('@/views/ReportsLineConfigView.vue')
const ReportsMetricsView = () => import('@/views/ReportsMetricsView.vue')
const ReportsOutputView = () => import('@/views/ReportsOutputView.vue')
const ReportsWorkshopConfigView = () => import('@/views/ReportsWorkshopConfigView.vue')
const ShopfloorView = () => import('@/views/ShopfloorView.vue')
const SopBindingsView = () => import('@/views/SopBindingsView.vue')
const SopDocumentsView = () => import('@/views/SopDocumentsView.vue')
const SopExecutionView = () => import('@/views/SopExecutionView.vue')
const SopStepsView = () => import('@/views/SopStepsView.vue')
const SopVersionsView = () => import('@/views/SopVersionsView.vue')
const SystemUsersView = () => import('@/views/SystemUsersView.vue')
const SystemMenusView = () => import('@/views/SystemMenusView.vue')
const SystemSequencesView = () => import('@/views/SystemSequencesView.vue')
const TraceQueryView = () => import('@/views/TraceQueryView.vue')

const router = createRouter({
 history: createWebHistory(import.meta.env.BASE_URL),
 routes: [
  { path: '/', redirect: '/login' },
  { path: '/andon/exceptions', name: 'AndonExceptionsView', component: AndonExceptionsView, meta: { title: "安灯异常 — 风擎工控 AeroForge MES" } },
  { path: '/barcode/list', name: 'BarcodeListView', component: BarcodeListView, meta: { title: "条码管理 — 风擎工控 AeroForge MES" } },
  { path: '/barcode/generate-print', name: 'BarcodeGeneratePrintView', component: BarcodeGeneratePrintView, meta: { title: "条码生成与打印 — 风擎工控 AeroForge MES" } },
  { path: '/barcode/rules', name: 'BarcodeRulesView', component: BarcodeRulesView, meta: { title: "条码规则 — 风擎工控 AeroForge MES" } },
  { path: '/barcode/scan', name: 'BarcodeScanView', component: BarcodeScanView, meta: { title: "扫码终端 — 风擎工控 AeroForge MES" } },
  { path: '/barcode/templates', name: 'BarcodeTemplatesView', component: BarcodeTemplatesView, meta: { title: "打印模板 — 风擎工控 AeroForge MES" } },
  { path: '/barcode/types', name: 'BarcodeTypesView', component: BarcodeTypesView, meta: { title: "条码类型 — 风擎工控 AeroForge MES" } },
  { path: '/dashboard', name: 'DashboardView', component: DashboardView, meta: { title: "工作台 — 风擎工控 AeroForge MES" } },
  { path: '/equipment-monitor', name: 'EquipmentMonitorView', component: EquipmentMonitorView, meta: { title: "设备监控 — 风擎工控 AeroForge MES" } },
  { path: '/equipment/detail', name: 'EquipmentDetailView', component: EquipmentDetailView, meta: { title: "设备详情 — 风擎工控 AeroForge MES" } },
  { path: '/equipment/ledger', name: 'EquipmentLedgerView', component: EquipmentLedgerView, meta: { title: "设备台账 — 风擎工控 AeroForge MES" } },
  { path: '/equipment/maintenance', name: 'EquipmentMaintenanceView', component: EquipmentMaintenanceView, meta: { title: "点检保养 — 风擎工控 AeroForge MES" } },
  { path: '/integration/erp', name: 'IntegrationErpView', component: IntegrationErpView, meta: { title: "ERP集成 — 风擎工控 AeroForge MES" } },
  { path: '/integration/openapi', name: 'IntegrationOpenapiView', component: IntegrationOpenapiView, meta: { title: "OpenAPI管理 — 风擎工控 AeroForge MES" } },
  { path: '/integration/sync-logs', name: 'IntegrationSyncLogsView', component: IntegrationSyncLogsView, meta: { title: "同步日志 — 风擎工控 AeroForge MES" } },
  { path: '/login', name: 'LoginView', component: LoginView, meta: { title: "登录 — 风擎工控 AeroForge MES" } },
  { path: '/production-order-detail', name: 'ProductionOrderDetailView', component: ProductionOrderDetailView, meta: { title: "订单详情 MO-20260711-0032 — 风擎工控 AeroForge MES", sidebarActive: 'orders' } },
  { path: '/production-orders', name: 'ProductionOrdersView', component: ProductionOrdersView, meta: { title: "生产订单 — 风擎工控 AeroForge MES" } },
  { path: '/production/dispatch-orders', name: 'ProductionDispatchOrdersView', component: ProductionDispatchOrdersView, meta: { title: "派工单管理 — 风擎工控 AeroForge MES" } },
  { path: '/production/kitting', name: 'ProductionKittingView', component: ProductionKittingView, meta: { title: "齐套分析 — 风擎工控 AeroForge MES" } },
  { path: '/production/kitting-board', name: 'ProductionKittingBoardView', component: ProductionKittingBoardView, meta: { title: "缺料看板 — 风擎工控 AeroForge MES" } },
  { path: '/production/tasks', name: 'ProductionTasksView', component: ProductionTasksView, meta: { title: "生产任务 — 风擎工控 AeroForge MES" } },
  { path: '/profile', name: 'ProfileView', component: ProfileView, meta: { title: "个人资料 — 风擎工控 AeroForge MES" } },
  { path: '/quality-inspection', name: 'QualityInspectionView', component: QualityInspectionView, meta: { title: "质量检验 — 风擎工控 AeroForge MES" } },
  { path: '/quality/inspection-detail', name: 'QualityInspectionDetailView', component: QualityInspectionDetailView, meta: { title: "检验单详情 — 风擎工控 AeroForge MES", sidebarActive: 'quality' } },
  { path: '/quality/categories', name: 'QualityCategoriesView', component: QualityCategoriesView, meta: { title: "检验项分类 — 风擎工控 AeroForge MES" } },
  { path: '/quality/defects', name: 'QualityDefectsView', component: QualityDefectsView, meta: { title: "缺陷记录 — 风擎工控 AeroForge MES" } },
  { path: '/quality/inspection-exec', name: 'QualityInspectionExecView', component: QualityInspectionExecView, meta: { title: "检验执行 — 风擎工控 AeroForge MES" } },
  { path: '/quality/items', name: 'QualityItemsView', component: QualityItemsView, meta: { title: "检验项管理 — 风擎工控 AeroForge MES" } },
  { path: '/quality/plan-detail', name: 'QualityPlanDetailView', component: QualityPlanDetailView, meta: { title: "方案明细 — 风擎工控 AeroForge MES", sidebarActive: 'quality-plans' } },
  { path: '/quality/plans', name: 'QualityPlansView', component: QualityPlansView, meta: { title: "检验方案 — 风擎工控 AeroForge MES" } },
  { path: '/reports', name: 'ReportsView', component: ReportsView, meta: { title: "生产报表 — 风擎工控 AeroForge MES" } },
  { path: '/reports/control-center', name: 'ReportsControlCenterView', component: ReportsControlCenterView, meta: { title: "控制中心大屏 — 风擎工控 AeroForge MES" } },
  { path: '/reports/line-config', name: 'ReportsLineConfigView', component: ReportsLineConfigView, meta: { title: "产线看板配置 — 风擎工控 AeroForge MES" } },
  { path: '/reports/metrics', name: 'ReportsMetricsView', component: ReportsMetricsView, meta: { title: "指标管理 — 风擎工控 AeroForge MES" } },
  { path: '/reports/output', name: 'ReportsOutputView', component: ReportsOutputView, meta: { title: "产量报表 — 风擎工控 AeroForge MES" } },
  { path: '/reports/workshop-config', name: 'ReportsWorkshopConfigView', component: ReportsWorkshopConfigView, meta: { title: "车间看板配置 — 风擎工控 AeroForge MES" } },
  { path: '/shopfloor', name: 'ShopfloorView', component: ShopfloorView, meta: { title: "车间看板 — 风擎工控 AeroForge MES" } },
  { path: '/sop/bindings', name: 'SopBindingsView', component: SopBindingsView, meta: { title: "SOP绑定配置 — 风擎工控 AeroForge MES" } },
  { path: '/sop/documents', name: 'SopDocumentsView', component: SopDocumentsView, meta: { title: "SOP文档管理 — 风擎工控 AeroForge MES" } },
  { path: '/sop/execution', name: 'SopExecutionView', component: SopExecutionView, meta: { title: "SOP执行终端 — 风擎工控 AeroForge MES" } },
  { path: '/sop/steps', name: 'SopStepsView', component: SopStepsView, meta: { title: "SOP步骤编排 — 风擎工控 AeroForge MES" } },
  { path: '/sop/versions', name: 'SopVersionsView', component: SopVersionsView, meta: { title: "SOP版本 — 风擎工控 AeroForge MES" } },
  { path: '/system-users', name: 'SystemUsersView', component: SystemUsersView, meta: { title: "用户管理 — 风擎工控 AeroForge MES" } },
  { path: '/system/menus', name: 'SystemMenusView', component: SystemMenusView, meta: { title: "菜单配置 — 风擎工控 AeroForge MES" } },
  { path: '/system/sequences', name: 'SystemSequencesView', component: SystemSequencesView, meta: { title: "编号序列 — 风擎工控 AeroForge MES" } },
  { path: '/trace/query', name: 'TraceQueryView', component: TraceQueryView, meta: { title: "追溯查询 — 风擎工控 AeroForge MES" } },
  { path: '/:pathMatch(.*)*', redirect: '/' },
 ],
 scrollBehavior: (_to, _from, savedPosition) => savedPosition || { top: 0 },
})
router.afterEach((to) => { document.title = String(to.meta.title || 'AeroForge MES') })

const reducedMotionQuery = window.matchMedia('(prefers-reduced-motion: reduce)')
let prefersReducedMotion = reducedMotionQuery.matches
let initialNavigation = true
let exitOverlay: HTMLDivElement | undefined

function showPageExitOverlay() {
  exitOverlay?.remove()
  exitOverlay = document.createElement('div')
  exitOverlay.className = 'page-exit-overlay'
  document.body.appendChild(exitOverlay)
}

function clearPageExitOverlay() {
  exitOverlay?.remove()
  exitOverlay = undefined
}

// Keep the preference live when it changes at the operating-system level.
reducedMotionQuery.addEventListener('change', (event) => {
  prefersReducedMotion = event.matches
})

// Vue Router is the single navigation path for links, shortcuts, and code-driven
// navigation, so the guard provides the exit transition consistently.
router.beforeEach(async (to, from) => {
  if (to.path !== '/login' && !hasValidSession()) {
    const restored = await restoreSession()
    if (!restored) return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && hasValidSession()) return '/dashboard'
  if (initialNavigation) {
    initialNavigation = false
    return true
  }
  if (prefersReducedMotion || to.fullPath === from.fullPath) return true

  showPageExitOverlay()
  await new Promise<void>((resolve) => window.setTimeout(resolve, 200))
  return true
})

router.afterEach(() => {
  window.requestAnimationFrame(clearPageExitOverlay)
})
export default router
