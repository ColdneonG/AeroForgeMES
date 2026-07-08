export const allMenuPermissions = [
  'dashboard:view',
  'production:view',
  'production:work-order:view',
  'production:kitting:view',
  'production:dispatch:view',
  'barcode:view',
  'barcode:type:view',
  'barcode:rule:view',
  'barcode:template:view',
  'barcode:application-rule:view',
  'barcode:generate:view',
  'barcode:trace:view',
  'shopfloor:view',
  'shopfloor:config:view',
  'shopfloor:task:view',
  'shopfloor:dispatch:view',
  'shopfloor:product-status:view',
  'shopfloor:report:view',
  'shopfloor:completion:view',
  'shopfloor:repair-order:view',
  'shopfloor:tablet-task:view',
  'shopfloor:operation:view',
  'shopfloor:sop:view',
  'shopfloor:tablet-trace:view',
  'equipment:view',
  'equipment:category:view',
  'equipment:maker:view',
  'equipment:fault-reason:view',
  'equipment:ledger:view',
  'equipment:inspection:view',
  'equipment:maintenance-task:view',
  'equipment:maintenance:view',
  'equipment:repair:view',
  'equipment:mobile-status:view',
  'equipment:oee:view',
  'equipment:energy:view',
  'equipment-integration:view',
  'wage:view',
  'process:view',
  'quality:view',
  'quality:item-category:view',
  'quality:standard-plan:view',
  'quality:first-last:view',
  'quality:patrol:view',
  'quality:inbound:view',
  'quality:outbound:view',
  'andon:view',
  'andon:type:view',
  'andon:config:view',
  'andon:reason:view',
  'andon:exception:view',
  'andon:tablet:view',
  'andon:mobile-launch:view',
  'andon:mobile-handle:view',
  'miniapp:dashboard:view',
  'miniapp:analysis:view',
  'miniapp:trace:view',
  'report:view',
  'report:material-trace:view',
  'report:product-trace:view',
  'report:output:view',
  'report:realtime:view',
  'report:defects:view',
  'report:workshop-period:view',
  'board:line:view',
  'board:workshop:view',
  'board:control-center:view',
  'erp:view',
  'erp:tasks-read:view',
  'erp:routes-read:view',
  'standard-api:view',
  'standard-api:units-write:view',
  'standard-api:work-orders-write:view',
  'standard-api:tasks-write:view',
  'standard-api:device-count-write:view',
  'standard-api:completions-read:view',
  'system:view'
]

export const allButtonPermissions = [
  'create',
  'edit',
  'delete',
  'import',
  'export',
  'release',
  'dispatch',
  'start',
  'pause',
  'resume',
  'report',
  'complete',
  'repair',
  'handle',
  'close',
  'print',
  'retry',
  'audit'
]

export const mockUser = {
  id: 1,
  username: 'admin',
  displayName: '系统管理员',
  roles: ['系统管理员', '生产计划员'],
  department: '数字化制造部',
  superAdmin: true
}

export const mockPermissions = {
  menus: allMenuPermissions,
  buttons: allButtonPermissions,
  apis: [
    '/api/auth/**',
    '/api/system/**',
    '/api/production/**',
    '/api/quality/**',
    '/api/equipment/**',
    '/api/report/**',
    '/api/integration/**'
  ],
  dataScopes: ['全部工厂', '总装车间', '电机车间', '包装车间']
}

export const mockLogin = ({ username }) => ({
  token: `mock-token-${Date.now()}`,
  user: {
    ...mockUser,
    username: username || mockUser.username,
    displayName: username === 'operator' ? '现场操作员' : mockUser.displayName,
    roles: username === 'operator' ? ['操作员'] : mockUser.roles,
    superAdmin: username !== 'operator'
  },
  permissions:
    username === 'operator'
      ? {
          menus: [
            'dashboard:view',
            'shopfloor:view',
            'shopfloor:tablet-task:view',
            'shopfloor:task:view',
            'shopfloor:operation:view',
            'shopfloor:sop:view',
            'shopfloor:tablet-trace:view',
            'barcode:trace:view',
            'andon:view',
            'andon:tablet:view',
            'andon:mobile-launch:view',
            'miniapp:trace:view'
          ],
          buttons: ['start', 'pause', 'resume', 'report', 'complete', 'repair', 'handle'],
          apis: ['/api/auth/**', '/api/production/**', '/api/equipment/andon/**'],
          dataScopes: ['总装一线']
        }
      : mockPermissions
})

export const systemUsers = [
  { id: 1, name: '高名扬', account: 'admin', roles: '系统管理员', scope: '全部工厂', status: '启用' },
  { id: 2, name: '王磊', account: 'planner', roles: '生产计划员', scope: '生产部', status: '启用' },
  { id: 3, name: '李敏', account: 'quality', roles: '质检员', scope: '质检区', status: '启用' },
  { id: 4, name: '陈强', account: 'maintainer', roles: '设备维护人员', scope: '设备组', status: '停用' }
]

export const systemRoles = [
  { role: '系统管理员', menus: 14, buttons: 18, apis: 7, dataScope: '全部数据' },
  { role: '生产计划员', menus: 6, buttons: 10, apis: 3, dataScope: '生产订单/现场管理' },
  { role: '操作员', menus: 4, buttons: 7, apis: 2, dataScope: '所在产线' },
  { role: '管理层', menus: 5, buttons: 3, apis: 2, dataScope: '看板与报表' }
]

export const auditLogs = [
  { time: '2026-07-07 08:12:20', operator: '王磊', action: '下发', from: '待下发', to: '已下发', target: 'WO20260706001' },
  { time: '2026-07-07 08:35:11', operator: '张工', action: '开工', from: '已下发', to: '生产中', target: 'TASK-0707-001' },
  { time: '2026-07-07 09:08:44', operator: '李敏', action: '提交质检', from: '待检', to: '已完成', target: 'PQC-0706-034' },
  { time: '2026-07-07 09:44:03', operator: '陈强', action: '维修完成', from: '维修中', to: '正常', target: 'EQ-AGE-04' }
]

export const kittingRows = [
  { order: 'WO20260706001', product: 'FS-500 落地扇', line: '总装一线', rate: 96, risk: '低', missing: '无', status: '可下发' },
  { order: 'WO20260706002', product: 'TF-230 台扇', line: '总装二线', rate: 78, risk: '中', missing: '前网罩 92 件', status: '待补料' },
  { order: 'WO20260706004', product: 'IF-750 工业风扇', line: '老化测试区', rate: 64, risk: '高', missing: '温控模块 12 件', status: '冻结' }
]

export const dispatchRows = [
  { id: 'DISP-0707-001', order: 'WO20260706001', process: '总装', station: 'A-01', operator: '张工', suggestion: '优先', status: '待下发' },
  { id: 'DISP-0707-002', order: 'WO20260706002', process: '扇叶装配', station: 'B-03', operator: '王工', suggestion: '等料后下发', status: '草稿' },
  { id: 'DISP-0707-003', order: 'WO20260706005', process: '包装', station: 'PK-02', operator: '赵工', suggestion: '可插单', status: '已下发' }
]

export const productionTasks = [
  { id: 'TASK-0707-001', order: 'WO20260706001', product: 'FS-500 落地扇', process: '总装', line: '总装一线', status: '生产中', good: 326, bad: 3 },
  { id: 'TASK-0707-002', order: 'WO20260706002', product: 'TF-230 台扇', process: '扇叶装配', line: '总装二线', status: '暂停', good: 188, bad: 5 },
  { id: 'TASK-0707-003', order: 'WO20260706003', product: 'CF-350 循环扇', process: '绕线', line: '电机装配线', status: '已下发', good: 0, bad: 0 }
]

export const barcodeRules = [
  { code: 'SN-FAN-DAY-SEQ', type: '产品码', pattern: 'SN + yyyyMMdd + 6位流水', status: '启用', template: '成品标签A' },
  { code: 'BOX-FAN-BATCH', type: '箱码', pattern: 'BOX + 批次 + 4位流水', status: '启用', template: '外箱标签' },
  { code: 'MAT-LOT', type: '材料码', pattern: 'MAT + 供应商 + 日期', status: '停用', template: '材料标签' }
]

export const barcodeJobs = [
  { id: 'BCG-0707-001', rule: 'SN-FAN-DAY-SEQ', product: 'FS-500 落地扇', qty: 420, printed: 420, status: '已完成' },
  { id: 'BCG-0707-002', rule: 'BOX-FAN-BATCH', product: 'TF-230 台扇', qty: 80, printed: 36, status: '打印中' },
  { id: 'BCG-0707-003', rule: 'SN-FAN-DAY-SEQ', product: 'IF-750 工业风扇', qty: 160, printed: 0, status: '待打印' }
]

export const equipmentLedger = [
  { id: 'EQ-INJ-01', type: '注塑设备', maker: '海天', dept: '注塑区', manager: '设备组', status: '运行' },
  { id: 'EQ-WND-03', type: '绕线设备', maker: 'Nidec', dept: '电机线', manager: '设备组', status: '运行' },
  { id: 'EQ-AGE-04', type: '测试设备', maker: '自研', dept: '老化区', manager: '设备组', status: '故障' }
]

export const maintenanceRows = [
  { id: 'INS-0707-01', equipment: 'EQ-INJ-01', type: '点检', owner: '陈强', due: '2026-07-07 10:00', status: '已完成' },
  { id: 'MNT-0707-03', equipment: 'EQ-WND-03', type: '保养', owner: '赵宇', due: '2026-07-07 16:00', status: '待处理' },
  { id: 'RPR-0707-02', equipment: 'EQ-AGE-04', type: '维修', owner: '陈强', due: '立即', status: '维修中' }
]

export const andonRows = [
  { id: 'ANDON-0707-001', type: '设备异常', line: '老化测试区', reason: '温控模块报警', owner: '陈强', status: '处理中' },
  { id: 'ANDON-0707-002', type: '质量异常', line: '总装一线', reason: '电机异响', owner: '李敏', status: '待处理' },
  { id: 'ANDON-0707-003', type: '物料异常', line: '总装二线', reason: '前网罩短缺', owner: '王磊', status: '已处理' }
]

export const processRows = [
  { code: 'PROC-ASM', name: '总装', type: '人工工序', sop: 'FS-500 总装 SOP', defects: '卡扣不到位/异响', status: '启用' },
  { code: 'ROUTE-FS500', name: 'FS-500 工艺路线', type: '路线', sop: '全流程 SOP', defects: '通用不良', status: '启用' },
  { code: 'PROC-AGE', name: '老化测试', type: '设备工序', sop: '老化测试 SOP', defects: '温升异常', status: '启用' }
]

export const integrationRows = [
  { id: 'SYNC-0707-001', module: 'ERP生产任务读取', direction: '读', externalNo: 'ERP-MO-9001', result: '成功', status: '成功' },
  { id: 'SYNC-0707-002', module: 'ERP工艺数据读取', direction: '读', externalNo: 'ERP-RT-3302', result: '失败：工序编码缺失', status: '失败' },
  { id: 'SYNC-0707-003', module: '标准API工单写入', direction: '写', externalNo: 'OPEN-WO-7781', result: '待同步', status: '待同步' }
]

export const reportRows = [
  { report: '产量报表', dimension: '产线/班次/产品', api: '/api/report/production-output', status: '已接入' },
  { report: '不良查询', dimension: '不良类型/工单/质检员', api: '/api/report/defects', status: '已接入' },
  { report: '物料追溯', dimension: '条码/批次/供应商', api: '/api/report/material-trace', status: '设计中' },
  { report: '车间时段报表', dimension: '车间/日期/时段', api: '/api/report/workshop-period', status: '已接入' }
]

const defaultObjects = ['FS-500 落地扇', 'TF-230 台扇', 'CF-350 循环扇', 'IF-750 工业风扇']
const defaultLines = ['总装一线', '总装二线', '电机装配线', '老化测试区']
const defaultOwners = ['王磊', '李敏', '陈强', '赵宇']
const defaultStatuses = ['启用', '待处理', '运行', '已完成']
const defaultTrend = [48, 58, 66, 72, 68, 76, 84, 79, 88, 92, 86, 95]

const feature = (key, group, title, description, code, options = {}) => ({
  key,
  group,
  title,
  description,
  code,
  objects: options.objects || defaultObjects,
  lines: options.lines || defaultLines,
  owners: options.owners || defaultOwners,
  statuses: options.statuses || defaultStatuses,
  trend: options.trend || defaultTrend
})

export const missingFeatureCatalog = [
  feature('barcode-type', '条码应用', '条码类型', '定义产品码、箱码、栈板码、材料码等编码类型。', 'BCT', {
    objects: ['产品码', '内箱码', '外箱码', '材料码'],
    statuses: ['启用', '启用', '停用', '待处理']
  }),
  feature('barcode-template', '条码应用', '条码模板', '维护标签模板、打印参数和业务字段映射。', 'BCTPL', {
    objects: ['成品标签A', '外箱标签', '材料标签', '栈板标签']
  }),
  feature('barcode-application-rule', '条码应用', '条码应用规则', '绑定存货、条码规则、标签模板和生成方式。', 'BCAPP', {
    objects: ['FS-500 成品规则', 'TF-230 箱码规则', '材料入库规则', '栈板出货规则']
  }),
  feature('shopfloor-config', '现场管理', '参数配置', '配置现场报工、开工、暂停、完工和异常处理参数。', 'SFCFG', {
    objects: ['报工方式', '扫码校验', '完工策略', '异常触发规则']
  }),
  feature('shopfloor-dispatch', '现场管理', '生产派工单', '按工艺将生产任务派到工序、工位和操作员。', 'SFDISP'),
  feature('product-status', '现场管理', '产品生产状态', '跟踪每个产品当前工序、状态、设备和责任人。', 'PSTAT', {
    objects: ['SN20260706000158', 'SN20260706000159', 'SN20260706000160', 'SN20260706000161']
  }),
  feature('production-report', '现场管理', '生产报工', '记录良品、不良、检测、设备计数、物料消耗和装箱报工。', 'RPT', {
    statuses: ['待处理', '已完成', '异常', '已完成']
  }),
  feature('production-completion', '现场管理', '生产完工单', '记录任务完工数量、完工确认和入库前状态。', 'CMP'),
  feature('repair-order', '现场管理', '返修工单', '记录产品返修原因、处理过程、复检和关闭。', 'REWORK', {
    statuses: ['待处理', '处理中', '已完成', '异常']
  }),
  feature('tablet-task', '现场管理', '平板-生产任务单', '面向产线平板的任务筛选、开工、暂停和结束。', 'TBTSK'),
  feature('tablet-trace', '现场管理', '平板-产品追溯', '现场扫码查看产品全流程生产追溯。', 'TBTRC'),
  feature('equipment-category', '设备管理', '设备类别', '维护设备分类，支撑台账、点检、保养和故障统计。', 'EQCAT', {
    objects: ['注塑设备', '绕线设备', '测试设备', '包装设备']
  }),
  feature('equipment-maker', '设备管理', '设备制造商', '维护设备制造厂商和联系信息。', 'EQMKR', {
    objects: ['海天', 'Nidec', '自研设备', '扫码设备供应商']
  }),
  feature('fault-reason', '设备管理', '故障原理', '维护设备类别对应故障原因，用于统计与预防。', 'FAULT', {
    objects: ['温控报警', '扫码失败', '张力异常', '气压不足'],
    statuses: ['启用', '启用', '待处理', '停用']
  }),
  feature('maintenance-task', '设备管理', '保养任务', '定义和执行设备定期保养任务。', 'MNT'),
  feature('equipment-inspection', '设备管理', '设备点检', '记录设备点检结果，异常可转报修。', 'INS'),
  feature('repair-task', '设备管理', '报修任务', '发起报修、指定维修人员并跟踪处理闭环。', 'RPR', {
    statuses: ['待处理', '处理中', '已完成', '异常']
  }),
  feature('mobile-equipment-status', '设备管理', '移动端-设备状态', '移动端查看设备运行、待机、故障和维修状态。', 'MEQS'),
  feature('equipment-oee', '设备管理', '设备（线）OEE', '展示设备和产线 OEE、稼动率、性能和质量指标。', 'OEE', {
    objects: ['注塑机 1#', '绕线机 3#', '装配线 2#', '老化测试台 4#']
  }),
  feature('energy-analysis', '设备管理', '能源分析', '分析关键设备能耗、产量和单位能耗。', 'ENE', {
    objects: ['注塑区电耗', '老化区电耗', '空压机气耗', '包装线电耗']
  }),
  feature('quality-item-category', '质量管理', '检验项目分类', '维护外观、电气、尺寸、包装等检验分类。', 'QCAT'),
  feature('quality-standard-plan', '质量管理', '检验标准方案', '配置检验标准、抽样方案和判定规则。', 'QPLAN'),
  feature('first-last-inspection', '质量管理', '首末件检验单', '记录首件和末件检验结果与放行状态。', 'FLI', {
    statuses: ['待处理', '已完成', '异常', '已完成']
  }),
  feature('patrol-inspection', '质量管理', '巡检单', '记录生产过程巡检项目、结果和异常。', 'PQC'),
  feature('inbound-inspection', '质量管理', '成品入库检验单', '记录成品入库前检验结果和放行状态。', 'FQCIN'),
  feature('outbound-inspection', '质量管理', '成品发货检验单', '记录发货前检验、抽检和判定结果。', 'FQCOUT'),
  feature('andon-type', '安灯管理', '安灯类型', '定义生产、质量、设备、物料等安灯类型。', 'ATYPE'),
  feature('andon-config', '安灯管理', '异常配置', '配置安灯通知对象、处理方式和升级规则。', 'ACFG'),
  feature('andon-reason', '安灯管理', '异常原因', '维护常用异常原因，支撑后续统计整改。', 'ARSN'),
  feature('andon-exception', '安灯管理', '现场生产异常', '记录现场异常发起、通知、处理和关闭。', 'AEX', {
    statuses: ['待处理', '处理中', '已完成', '异常']
  }),
  feature('tablet-andon', '安灯管理', '平板端-异常安灯', '平板端发起生产/非生产安灯并处理。', 'TBA'),
  feature('mobile-andon-launch', '安灯管理', '移动-异常安灯', '移动端快速发起异常安灯。', 'MBA'),
  feature('mobile-andon-handle', '安灯管理', '移动-异常处理', '移动端查看待处理和已处理异常。', 'MBH'),
  feature('miniapp-dashboard', '微信小程序', '实时看板', '小程序端实时查看产线和生产指标。', 'MPD'),
  feature('miniapp-analysis', '微信小程序', '生产分析', '小程序端查看产量、异常和质量趋势。', 'MPA'),
  feature('miniapp-trace', '微信小程序', '产品追溯', '小程序端扫码查询产品追溯链路。', 'MPT'),
  feature('material-trace-report', '报表分析', '关键物料追溯', '按物料批次、供应商和工单追溯关键物料。', 'RMAT'),
  feature('product-trace-report', '报表分析', '产品追溯', '按产品码聚合生产、质量、设备和物料记录。', 'RPRD'),
  feature('output-report', '报表分析', '产量报表查询', '按产线、班次、产品统计产量和达成率。', 'ROUT'),
  feature('production-realtime-report', '报表分析', '生产实时信息', '展示生产实时状态、任务进度和异常。', 'RRT'),
  feature('defect-report', '报表分析', '不良查询', '按不良类型、工单和质检员查询不良记录。', 'RDEF', {
    statuses: ['已接入', '已接入', '待处理', '异常']
  }),
  feature('workshop-period-report', '报表分析', '车间生产时段报表', '按车间、日期、时段汇总产量和停机。', 'RPER'),
  feature('line-board', '电子看板', '产线看板', '面向单条产线展示计划、产出、异常和设备状态。', 'BLN'),
  feature('workshop-board', '电子看板', '车间看板', '面向车间展示多产线生产态势。', 'BWS'),
  feature('control-center-board', '电子看板', '中控看板', '面向中控大屏展示全厂 KPI 与异常。', 'BCC'),
  feature('erp-task-read', 'ERP接口', '生产任务单读取接口', '从 ERP 读取生产任务单并写入 MES。', 'ERPT'),
  feature('erp-route-read', 'ERP接口', '工艺数据读取', '从 ERP 读取工艺路线和工序资料。', 'ERPR'),
  feature('api-unit-write', '标准API接口', '计量单位(写)', '外部系统写入 MES 计量单位。', 'APIU'),
  feature('api-work-order-write', '标准API接口', '工单(写)', '外部系统写入 MES 生产工单。', 'APIW'),
  feature('api-task-write', '标准API接口', '生产任务单(写)', '外部系统写入生产任务单。', 'APIT'),
  feature('api-device-count-write', '标准API接口', '设备计数报工(写)', '外部设备或系统写入设备计数报工。', 'APIC'),
  feature('api-completion-read', '标准API接口', '生产完工单(读)', '外部系统读取 MES 生产完工单。', 'APIR')
]
