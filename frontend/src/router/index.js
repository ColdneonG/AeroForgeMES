import { createRouter, createWebHistory } from 'vue-router'
import MesLayout from '../layout/MesLayout.vue'
import { authState, hasPermission, isAuthenticated } from '../stores/auth'

const moduleRoutes = [
  {
    path: 'dashboard',
    name: 'dashboard',
    component: () => import('../views/dashboard/ManufacturingDashboard.vue'),
    meta: { title: '生产驾驶舱', icon: '▦', permission: 'dashboard:view', group: '电子看板' }
  },
  {
    path: 'production/work-orders',
    name: 'productionWorkOrders',
    component: () => import('../views/production/ProductionOrders.vue'),
    meta: { title: '生产工单', icon: '▤', permission: 'production:work-order:view', group: '生产订单' }
  },
  {
    path: 'production/kitting',
    name: 'productionKitting',
    component: () => import('../views/production/KittingBoard.vue'),
    meta: { title: '齐套欠料', icon: '▥', permission: 'production:kitting:view', group: '生产订单' }
  },
  {
    path: 'production/dispatch',
    name: 'productionDispatch',
    component: () => import('../views/production/DispatchOrders.vue'),
    meta: { title: '派工单', icon: '▧', permission: 'production:dispatch:view', group: '生产订单' }
  },
  {
    path: 'shopfloor/tasks',
    name: 'shopfloorTasks',
    component: () => import('../views/shopfloor/ShopfloorTasks.vue'),
    meta: { title: '生产任务单', icon: '◉', permission: 'shopfloor:task:view', group: '现场管理' }
  },
  {
    path: 'shopfloor/workbench',
    name: 'operationWorkbench',
    component: () => import('../views/shopfloor/OperationWorkbench.vue'),
    meta: { title: '工序作业台', icon: '◎', permission: 'shopfloor:operation:view', group: '现场管理' }
  },
  {
    path: 'operator-terminal',
    name: 'operatorTerminal',
    component: () => import('../views/shopfloor/OperatorTerminal.vue'),
    meta: { title: '操作员终端', icon: '◉', permission: 'shopfloor:operation:view', group: '现场管理' }
  },
  {
    path: 'shopfloor/sop',
    name: 'electronicSop',
    component: () => import('../views/shopfloor/ElectronicSop.vue'),
    meta: { title: '电子 SOP', icon: '▣', permission: 'shopfloor:sop:view', group: '现场管理' }
  },
  {
    path: 'barcode/rules',
    name: 'barcodeRules',
    component: () => import('../views/barcode/BarcodeManagement.vue'),
    meta: { title: '条码规则', icon: '◇', permission: 'barcode:rule:view', group: '条码应用' }
  },
  {
    path: 'barcode/generate',
    name: 'barcodeGenerate',
    component: () => import('../views/barcode/BarcodeGeneration.vue'),
    meta: { title: '条码生成打印', icon: '◆', permission: 'barcode:generate:view', group: '条码应用' }
  },
  {
    path: 'traceability',
    name: 'traceability',
    component: () => import('../views/barcode/TraceabilityGenealogy.vue'),
    meta: { title: '扫码追溯', icon: '◇', permission: 'barcode:trace:view', group: '条码应用' }
  },
  {
    path: 'equipment/monitoring',
    name: 'equipmentMonitoring',
    component: () => import('../views/equipment/EquipmentMonitoring.vue'),
    meta: { title: '设备监控', icon: '⚙', permission: 'equipment:view', group: '设备管理' }
  },
  {
    path: 'equipment/ledger',
    name: 'equipmentLedger',
    component: () => import('../views/equipment/EquipmentLedger.vue'),
    meta: { title: '设备台账', icon: '⚙', permission: 'equipment:ledger:view', group: '设备管理' }
  },
  {
    path: 'equipment/maintenance',
    name: 'maintenanceCenter',
    component: () => import('../views/equipment/MaintenanceCenter.vue'),
    meta: { title: '点检保养维修', icon: '⚒', permission: 'equipment:maintenance:view', group: '设备管理' }
  },
  {
    path: 'equipment-integration',
    name: 'equipmentIntegration',
    component: () => import('../views/integration/IntegrationCenter.vue'),
    props: { eyebrow: '设备对接', title: '设备计数与联调日志', description: '设备计数上报、设备联调、采集日志和失败重试。' },
    meta: { title: '设备对接', icon: '⎋', permission: 'equipment-integration:view', group: '设备对接' }
  },
  {
    path: 'wage',
    name: 'pieceworkWage',
    component: () => import('../views/wage/PieceworkWage.vue'),
    meta: { title: '计件工资', icon: '¥', permission: 'wage:view', group: '计件工资' }
  },
  {
    path: 'process',
    name: 'processManagement',
    component: () => import('../views/process/ProcessManagement.vue'),
    meta: { title: '工艺管理', icon: '⌬', permission: 'process:view', group: '工艺管理' }
  },
  {
    path: 'quality',
    name: 'quality',
    component: () => import('../views/quality/QualityInspection.vue'),
    meta: { title: '质量检验', icon: '✓', permission: 'quality:view', group: '质量管理' }
  },
  {
    path: 'andon',
    name: 'andonManagement',
    component: () => import('../views/andon/AndonManagement.vue'),
    meta: { title: '安灯管理', icon: '!', permission: 'andon:view', group: '安灯管理' }
  },
  {
    path: 'reports',
    name: 'reportCenter',
    component: () => import('../views/report/ReportCenter.vue'),
    meta: { title: '报表分析', icon: '▨', permission: 'report:view', group: '报表分析' }
  },
  {
    path: 'boards',
    redirect: '/dashboard',
    meta: { title: '电子看板', icon: '▦', permission: 'dashboard:view', group: '电子看板', hideInMenu: true }
  },
  {
    path: 'board',
    redirect: '/dashboard',
    meta: { title: '电子看板', icon: '▦', permission: 'dashboard:view', group: '电子看板', hideInMenu: true }
  },
  {
    path: 'erp',
    name: 'erpIntegration',
    component: () => import('../views/integration/ErpIntegration.vue'),
    meta: { title: 'ERP接口', icon: '⇄', permission: 'erp:view', group: 'ERP接口' }
  },
  {
    path: 'standard-api',
    name: 'standardApi',
    component: () => import('../views/integration/StandardApiCatalog.vue'),
    meta: { title: '标准API接口', icon: 'API', permission: 'standard-api:view', group: '标准API接口' }
  },
  {
    path: 'system',
    name: 'systemManagement',
    component: () => import('../views/system/SystemManagement.vue'),
    meta: { title: '系统管理', icon: '⚙', permission: 'system:view', group: '系统管理' }
  },
  {
    path: 'barcode/types',
    name: 'barcodeTypes',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '条码类型', icon: '◇', permission: 'barcode:type:view', group: '条码应用', featureKey: 'barcode-type' }
  },
  {
    path: 'barcode/templates',
    name: 'barcodeTemplates',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '条码模板', icon: '◇', permission: 'barcode:template:view', group: '条码应用', featureKey: 'barcode-template' }
  },
  {
    path: 'barcode/application-rules',
    name: 'barcodeApplicationRules',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '条码应用规则', icon: '◇', permission: 'barcode:application-rule:view', group: '条码应用', featureKey: 'barcode-application-rule' }
  },
  {
    path: 'shopfloor/config',
    name: 'shopfloorConfig',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '参数配置', icon: '◎', permission: 'shopfloor:config:view', group: '现场管理', featureKey: 'shopfloor-config' }
  },
  {
    path: 'shopfloor/dispatch',
    name: 'shopfloorDispatch',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '生产派工单', icon: '◎', permission: 'shopfloor:dispatch:view', group: '现场管理', featureKey: 'shopfloor-dispatch' }
  },
  {
    path: 'shopfloor/product-status',
    name: 'productStatus',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '产品生产状态', icon: '◎', permission: 'shopfloor:product-status:view', group: '现场管理', featureKey: 'product-status' }
  },
  {
    path: 'shopfloor/reports',
    name: 'productionReport',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '生产报工', icon: '◎', permission: 'shopfloor:report:view', group: '现场管理', featureKey: 'production-report' }
  },
  {
    path: 'shopfloor/completions',
    name: 'productionCompletion',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '生产完工单', icon: '◎', permission: 'shopfloor:completion:view', group: '现场管理', featureKey: 'production-completion' }
  },
  {
    path: 'shopfloor/repair-orders',
    name: 'repairOrder',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '返修工单', icon: '◎', permission: 'shopfloor:repair-order:view', group: '现场管理', featureKey: 'repair-order' }
  },
  {
    path: 'shopfloor/tablet-tasks',
    name: 'tabletTask',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '平板-生产任务单', icon: '◎', permission: 'shopfloor:tablet-task:view', group: '现场管理', featureKey: 'tablet-task' }
  },
  {
    path: 'shopfloor/tablet-trace',
    name: 'tabletTrace',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '平板-产品追溯', icon: '◎', permission: 'shopfloor:tablet-trace:view', group: '现场管理', featureKey: 'tablet-trace' }
  },
  {
    path: 'equipment/categories',
    name: 'equipmentCategory',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '设备类别', icon: '⚙', permission: 'equipment:category:view', group: '设备管理', featureKey: 'equipment-category' }
  },
  {
    path: 'equipment/makers',
    name: 'equipmentMaker',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '设备制造商', icon: '⚙', permission: 'equipment:maker:view', group: '设备管理', featureKey: 'equipment-maker' }
  },
  {
    path: 'equipment/fault-reasons',
    name: 'faultReason',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '故障原理', icon: '⚙', permission: 'equipment:fault-reason:view', group: '设备管理', featureKey: 'fault-reason' }
  },
  {
    path: 'equipment/maintenance-tasks',
    name: 'maintenanceTask',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '保养任务', icon: '⚒', permission: 'equipment:maintenance-task:view', group: '设备管理', featureKey: 'maintenance-task' }
  },
  {
    path: 'equipment/inspection-tasks',
    name: 'equipmentInspectionTask',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '设备点检', icon: '⚒', permission: 'equipment:inspection:view', group: '设备管理', featureKey: 'equipment-inspection' }
  },
  {
    path: 'equipment/repair-tasks',
    name: 'repairTask',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '报修任务', icon: '⚒', permission: 'equipment:repair:view', group: '设备管理', featureKey: 'repair-task' }
  },
  {
    path: 'equipment/mobile-status',
    name: 'mobileEquipmentStatus',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '移动端-设备状态', icon: '⚙', permission: 'equipment:mobile-status:view', group: '设备管理', featureKey: 'mobile-equipment-status' }
  },
  {
    path: 'equipment/oee',
    name: 'equipmentOee',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '设备（线）OEE', icon: '⚙', permission: 'equipment:oee:view', group: '设备管理', featureKey: 'equipment-oee' }
  },
  {
    path: 'equipment/energy',
    name: 'energyAnalysis',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '能源分析', icon: '⚙', permission: 'equipment:energy:view', group: '设备管理', featureKey: 'energy-analysis' }
  },
  {
    path: 'quality/item-categories',
    name: 'qualityItemCategory',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '检验项目分类', icon: '✓', permission: 'quality:item-category:view', group: '质量管理', featureKey: 'quality-item-category' }
  },
  {
    path: 'quality/standard-plans',
    name: 'qualityStandardPlan',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '检验标准方案', icon: '✓', permission: 'quality:standard-plan:view', group: '质量管理', featureKey: 'quality-standard-plan' }
  },
  {
    path: 'quality/first-last',
    name: 'firstLastInspection',
    component: () => import('../views/quality/InspectionOrders.vue'),
    props: { eyebrow: '质量管理', title: '首末件检验单', description: '首件/末件检验记录与结果判定。', listTitle: '首末件检验单', inspectionType: '首件,末件' },
    meta: { title: '首末件检验单', icon: '✓', permission: 'quality:first-last:view', group: '质量管理' }
  },
  {
    path: 'quality/patrol',
    name: 'patrolInspection',
    component: () => import('../views/quality/InspectionOrders.vue'),
    props: { eyebrow: '质量管理', title: '巡检单', description: '过程巡检记录与检验结果判定。', listTitle: '巡检单', inspectionType: '巡检' },
    meta: { title: '巡检单', icon: '✓', permission: 'quality:patrol:view', group: '质量管理' }
  },
  {
    path: 'quality/inbound',
    name: 'inboundInspection',
    component: () => import('../views/quality/InspectionOrders.vue'),
    props: { eyebrow: '质量管理', title: '成品入库检验单', description: '成品入库检验记录与结果判定。', listTitle: '成品入库检验单', inspectionType: '成品入库' },
    meta: { title: '成品入库检验单', icon: '✓', permission: 'quality:inbound:view', group: '质量管理' }
  },
  {
    path: 'quality/outbound',
    name: 'outboundInspection',
    component: () => import('../views/quality/InspectionOrders.vue'),
    props: { eyebrow: '质量管理', title: '成品发货检验单', description: '成品发货检验记录与结果判定。', listTitle: '成品发货检验单', inspectionType: '成品发货' },
    meta: { title: '成品发货检验单', icon: '✓', permission: 'quality:outbound:view', group: '质量管理' }
  },
  {
    path: 'andon/types',
    name: 'andonType',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '安灯类型', icon: '!', permission: 'andon:type:view', group: '安灯管理', featureKey: 'andon-type' }
  },
  {
    path: 'andon/configs',
    name: 'andonConfig',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '异常配置', icon: '!', permission: 'andon:config:view', group: '安灯管理', featureKey: 'andon-config' }
  },
  {
    path: 'andon/reasons',
    name: 'andonReason',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '异常原因', icon: '!', permission: 'andon:reason:view', group: '安灯管理', featureKey: 'andon-reason' }
  },
  {
    path: 'andon/exceptions',
    name: 'andonException',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '现场生产异常', icon: '!', permission: 'andon:exception:view', group: '安灯管理', featureKey: 'andon-exception' }
  },
  {
    path: 'andon/tablet',
    name: 'tabletAndon',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '平板端-异常安灯', icon: '!', permission: 'andon:tablet:view', group: '安灯管理', featureKey: 'tablet-andon' }
  },
  {
    path: 'andon/mobile-launch',
    name: 'mobileAndonLaunch',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '移动-异常安灯', icon: '!', permission: 'andon:mobile-launch:view', group: '安灯管理', featureKey: 'mobile-andon-launch' }
  },
  {
    path: 'andon/mobile-handle',
    name: 'mobileAndonHandle',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '移动-异常处理', icon: '!', permission: 'andon:mobile-handle:view', group: '安灯管理', featureKey: 'mobile-andon-handle' }
  },
  {
    path: 'miniapp/dashboard',
    name: 'miniappDashboard',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '实时看板', icon: '▦', permission: 'miniapp:dashboard:view', group: '微信小程序', featureKey: 'miniapp-dashboard' }
  },
  {
    path: 'miniapp/analysis',
    name: 'miniappAnalysis',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '生产分析', icon: '▨', permission: 'miniapp:analysis:view', group: '微信小程序', featureKey: 'miniapp-analysis' }
  },
  {
    path: 'miniapp/trace',
    name: 'miniappTrace',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '产品追溯', icon: '◇', permission: 'miniapp:trace:view', group: '微信小程序', featureKey: 'miniapp-trace' }
  },
  {
    path: 'reports/material-trace',
    name: 'materialTraceReport',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '关键物料追溯', icon: '▨', permission: 'report:material-trace:view', group: '报表分析', featureKey: 'material-trace-report' }
  },
  {
    path: 'reports/product-trace',
    name: 'productTraceReport',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '产品追溯', icon: '▨', permission: 'report:product-trace:view', group: '报表分析', featureKey: 'product-trace-report' }
  },
  {
    path: 'reports/output',
    name: 'outputReport',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '产量报表查询', icon: '▨', permission: 'report:output:view', group: '报表分析', featureKey: 'output-report' }
  },
  {
    path: 'reports/realtime',
    name: 'productionRealtimeReport',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '生产实时信息', icon: '▨', permission: 'report:realtime:view', group: '报表分析', featureKey: 'production-realtime-report' }
  },
  {
    path: 'reports/defects',
    name: 'defectReport',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '不良查询', icon: '▨', permission: 'report:defects:view', group: '报表分析', featureKey: 'defect-report' }
  },
  {
    path: 'reports/workshop-period',
    name: 'workshopPeriodReport',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '车间生产时段报表', icon: '▨', permission: 'report:workshop-period:view', group: '报表分析', featureKey: 'workshop-period-report' }
  },
  {
    path: 'boards/line',
    name: 'lineBoard',
    component: () => import('../views/boards/LineBoard.vue'),
    meta: { title: '产线看板', icon: '▦', permission: 'board:line:view', group: '电子看板', featureKey: 'line-board' }
  },
  {
    path: 'boards/workshop',
    name: 'workshopBoard',
    component: () => import('../views/boards/WorkshopBoard.vue'),
    meta: { title: '车间看板', icon: '▦', permission: 'board:workshop:view', group: '电子看板', featureKey: 'workshop-board' }
  },
  {
    path: 'boards/control-center',
    name: 'controlCenterBoard',
    component: () => import('../views/boards/ControlCenterBoard.vue'),
    meta: { title: '中控看板', icon: '▦', permission: 'board:control-center:view', group: '电子看板', featureKey: 'control-center-board' }
  },
  {
    path: 'erp/task-read',
    name: 'erpTaskRead',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '生产任务单读取接口', icon: '⇄', permission: 'erp:tasks-read:view', group: 'ERP接口', featureKey: 'erp-task-read' }
  },
  {
    path: 'erp/route-read',
    name: 'erpRouteRead',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '工艺数据读取', icon: '⇄', permission: 'erp:routes-read:view', group: 'ERP接口', featureKey: 'erp-route-read' }
  },
  {
    path: 'standard-api/units-write',
    name: 'apiUnitWrite',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '计量单位(写)', icon: 'API', permission: 'standard-api:units-write:view', group: '标准API接口', featureKey: 'api-unit-write' }
  },
  {
    path: 'standard-api/work-orders-write',
    name: 'apiWorkOrderWrite',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '工单(写)', icon: 'API', permission: 'standard-api:work-orders-write:view', group: '标准API接口', featureKey: 'api-work-order-write' }
  },
  {
    path: 'standard-api/tasks-write',
    name: 'apiTaskWrite',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '生产任务单(写)', icon: 'API', permission: 'standard-api:tasks-write:view', group: '标准API接口', featureKey: 'api-task-write' }
  },
  {
    path: 'standard-api/device-count-write',
    name: 'apiDeviceCountWrite',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '设备计数报工(写)', icon: 'API', permission: 'standard-api:device-count-write:view', group: '标准API接口', featureKey: 'api-device-count-write' }
  },
  {
    path: 'standard-api/completions-read',
    name: 'apiCompletionRead',
    component: () => import('../views/common/DashboardFeaturePage.vue'),
    meta: { title: '生产完工单(读)', icon: 'API', permission: 'standard-api:completions-read:view', group: '标准API接口', featureKey: 'api-completion-read' }
  }
]

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/auth/LoginView.vue'), meta: { public: true } },
  { path: '/403', name: 'forbidden', component: () => import('../views/auth/ForbiddenView.vue'), meta: { public: true } },
  {
    path: '/',
    component: MesLayout,
    redirect: '/dashboard',
    children: moduleRoutes
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.public) return true

  if (!isAuthenticated.value) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (!hasPermission(to.meta.permission)) {
    authState.lastError = `缺少权限：${to.meta.permission}`
    return { name: 'forbidden' }
  }

  return true
})

export const menuRoutes = moduleRoutes

export default router
