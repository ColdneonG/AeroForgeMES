# AeroForge UI 迁移清单

本文件记录 `tmp` 原生页面到 Vue 3 路由的迁移关系。原生脚本中的导航、激活态、快捷键和显隐逻辑已经在 Vue 布局与各业务组件中使用 Router、响应式状态和 Vue 事件实现；业务数据继续来自现有 API 层。

## 页面与路由

| 原生页面 | Vue 路由 | Vue 页面/承载组件 |
| --- | --- | --- |
| `login.html` | `/login` | `LoginView.vue` |
| `dashboard.html`, `index.html` | `/dashboard` | `ManufacturingDashboard.vue` |
| `production-orders.html`, `production-order-detail.html` | `/production/work-orders` | `ProductionOrders.vue`（详情在页内） |
| `production/dispatch-orders.html` | `/production/dispatch` | `DispatchOrders.vue` |
| `production/kitting.html`, `production/kitting-board.html` | `/production/kitting` | `KittingBoard.vue` |
| `production/tasks.html`, `shopfloor.html` | `/shopfloor/tasks` | `ShopfloorTasks.vue` |
| 工序作业终端 | `/shopfloor/workbench`, `/operator-terminal` | `OperationWorkbench.vue`, `OperatorTerminal.vue` |
| `quality-inspection.html`, `quality/inspection-exec.html` | `/quality` | `QualityInspection.vue` |
| `quality/plans.html`, `quality/plan-detail.html`, `quality/items.html`, `quality/categories.html`, `quality/defects.html` | `/quality/*` | `InspectionOrders.vue` / `DashboardFeaturePage.vue`（按路由配置） |
| `equipment-monitor.html`, `equipment/detail.html` | `/equipment/monitoring` | `EquipmentMonitoring.vue`（详情在页内） |
| `equipment/ledger.html` | `/equipment/ledger` | `EquipmentLedger.vue` |
| `equipment/maintenance.html` | `/equipment/maintenance` | `MaintenanceCenter.vue` |
| `andon/exceptions.html` | `/andon`, `/andon/exceptions` | `AndonManagement.vue` / 配置化业务页 |
| `barcode/list.html`, `barcode/rules.html` | `/barcode/rules` | `BarcodeManagement.vue` |
| `barcode/types.html`, `barcode/templates.html` | `/barcode/types`, `/barcode/templates` | 配置化业务页 |
| `barcode/scan.html`, `trace/query.html` | `/traceability` | `TraceabilityGenealogy.vue` |
| 条码生成与打印 | `/barcode/generate` | `BarcodeGeneration.vue` |
| `sop/documents.html`, `sop/versions.html`, `sop/steps.html`, `sop/tasks.html`, `sop/bindings.html` | `/shopfloor/sop` | `ElectronicSop.vue`（页内模块） |
| `sop/execution.html` | `/operator-terminal` | `OperatorTerminal.vue` |
| `reports.html`, `reports/output.html`, `reports/metrics.html` | `/reports`, `/reports/output` | `ReportCenter.vue` / 配置化报表页 |
| `reports/control-center.html` | `/boards/control-center` | `ControlCenterBoard.vue` |
| `reports/line-config.html` | `/boards/line` | `LineBoard.vue` |
| `reports/workshop-config.html` | `/boards/workshop` | `WorkshopBoard.vue` |
| `integration/erp.html` | `/erp` | `ErpIntegration.vue` |
| `integration/sync-logs.html` | `/equipment-integration` | `IntegrationCenter.vue` |
| `integration/openapi.html` | `/standard-api` | `StandardApiCatalog.vue` |
| `system-users.html`, `system/menus.html`, `system/sequences.html`, `system/audit-logs.html` | `/system` | `SystemManagement.vue`（页内模块） |

## 保留的业务边界

- `src/api/**`、`src/services/**`：请求封装、Token 注入、错误处理、字段适配。
- `src/stores/auth.js`：登录会话、用户、权限与数据范围状态。
- `src/router/index.js`：路由守卫、登录重定向和逐路由权限校验。
- 生产、质量、设备、条码、SOP、报表、系统、集成、安灯页面中的查询、分页、CRUD、上传下载、扫码与实时数据逻辑。
- `DashboardFeaturePage.vue` 通过 `featurePageConfigs.js` 复用同类配置型页面，避免复制静态 HTML。

## 视觉来源

- `src/assets/styles/aeroforge-design-system.css`：从原型迁入的设计令牌、全局框架及组件样式。
- `src/styles/aeroforge-migration.css`：现有业务组件类名到 AeroForge 设计令牌的兼容层。
- `src/assets/images/`：Logo、符号标和 Favicon；构建不依赖工作区根目录的 `tmp`。
