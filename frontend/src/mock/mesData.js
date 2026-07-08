export const dashboardKpis = [
  { label: '今日计划', value: 1280, unit: '台', caption: '6 条产线排产', trend: '+8%', tone: 'info' },
  { label: '完成数量', value: 936, unit: '台', caption: '截至 14:30', trend: '73.1%', tone: 'success' },
  { label: '良品率', value: '98.2', unit: '%', caption: '目标 98.5%', trend: '-0.3%', tone: 'warning' },
  { label: '设备稼动率', value: '86.4', unit: '%', caption: '关键设备 18 台', trend: '+2.1%', tone: 'success' },
  { label: '异常数量', value: 7, unit: '起', caption: '待闭环 3 起', trend: '需关注', tone: 'danger' },
  { label: '准时达成', value: '91.5', unit: '%', caption: '预计完工 18:20', trend: '稳定', tone: 'stable' }
]

export const lineStatus = [
  { name: '总装一线', product: 'FS-500 落地扇', status: '运行', output: 326, target: 420, oee: 88 },
  { name: '总装二线', product: 'TF-230 台扇', status: '待机', output: 188, target: 280, oee: 72 },
  { name: '电机装配线', product: 'MTR-65 电机', status: '运行', output: 620, target: 760, oee: 91 },
  { name: '老化测试区', product: '循环扇批次', status: '异常', output: 154, target: 220, oee: 64 },
  { name: '包装一线', product: 'FS-500 包装', status: '运行', output: 298, target: 360, oee: 84 },
  { name: '包装二线', product: 'TF-230 包装', status: '运行', output: 246, target: 300, oee: 82 }
]

export const workOrders = [
  { id: 'WO20260706001', product: 'FS-500 落地扇', plan: 420, done: 326, process: '总装', status: '进行中', priority: '高', line: '总装一线', progress: 78 },
  { id: 'WO20260706002', product: 'TF-230 台扇', plan: 280, done: 188, process: '扇叶装配', status: '暂停', priority: '中', line: '总装二线', progress: 67 },
  { id: 'WO20260706003', product: 'CF-350 循环扇', plan: 320, done: 0, process: '绕线', status: '待开始', priority: '中', line: '电机装配线', progress: 0 },
  { id: 'WO20260706004', product: 'IF-750 工业风扇', plan: 160, done: 138, process: '老化测试', status: '异常', priority: '高', line: '老化测试区', progress: 86 },
  { id: 'WO20260705009', product: 'FS-400 落地扇', plan: 260, done: 260, process: '入库', status: '已完成', priority: '低', line: '包装一线', progress: 100 },
  { id: 'WO20260706005', product: 'AF-120 空气循环扇', plan: 360, done: 224, process: '包装', status: '进行中', priority: '中', line: '包装二线', progress: 62 },
  { id: 'WO20260706006', product: 'MF-900 工业大扇', plan: 120, done: 46, process: '电检', status: '待检', priority: '高', line: '老化测试区', progress: 38 }
]

export const productionSteps = ['注塑', '冲压', '绕线', '电机装配', '扇叶装配', '总装', '老化测试', '质检', '包装', '入库']

export const scheduleRows = [
  { label: 'WO001 注塑', start: 5, width: 21 },
  { label: 'WO001 绕线', start: 18, width: 20 },
  { label: 'WO001 总装', start: 36, width: 24 },
  { label: 'WO004 老化', start: 52, width: 24 },
  { label: 'WO002 质检', start: 70, width: 18 },
  { label: 'WO005 包装', start: 62, width: 28 }
]

export const outputTrend = [62, 76, 84, 91, 88, 102, 118, 124, 109, 132, 141, 136]

export const qualityAlerts = [
  { code: 'NC20260706012', issue: '扇叶偏心', order: 'WO20260706004', status: '返工', owner: '质检-李敏' },
  { code: 'NC20260706011', issue: '电机异响', order: 'WO20260706001', status: '不合格', owner: '工程-周强' },
  { code: 'NC20260706008', issue: '扫码失败', order: 'WO20260706002', status: '待检', owner: '班组长-王磊' }
]

export const operatorTasks = [
  { title: '扫描产品 SN', status: '已完成', note: 'SN20260706000158' },
  { title: '安装前网罩', status: '进行中', note: '确认卡扣方向和锁紧状态' },
  { title: '扭矩采集', status: '待开始', note: '目标 1.8 N.m' },
  { title: '功能测试', status: '待开始', note: '三档风速、噪声、绝缘测试' },
  { title: '外观复核', status: '待开始', note: '划伤、脏污、标签完整性' }
]

export const materialChecks = [
  { name: '电机组件', code: 'BATCH-MTR-0706A', result: 'OK' },
  { name: '扇叶组件', code: 'BATCH-BLD-0706C', result: 'OK' },
  { name: '前后网罩', code: 'BATCH-GRD-0706B', result: 'OK' },
  { name: '包装纸箱', code: 'BATCH-CTN-0706D', result: 'OK' }
]

export const qualityTasks = [
  { task: 'IQC-0706-021', product: 'FS-500 落地扇', type: '成品抽检', result: '合格', passRate: 99.1, order: 'WO20260706001' },
  { task: 'PQC-0706-034', product: 'IF-750 工业风扇', type: '老化测试', result: '不合格', passRate: 94.6, order: 'WO20260706004' },
  { task: 'FQC-0706-018', product: 'TF-230 台扇', type: '外观检验', result: '返工', passRate: 96.8, order: 'WO20260706002' },
  { task: 'PQC-0706-036', product: 'CF-350 循环扇', type: '绝缘测试', result: '合格', passRate: 98.7, order: 'WO20260706003' },
  { task: 'FQC-0706-041', product: 'AF-120 空气循环扇', type: '包装检验', result: '合格', passRate: 98.9, order: 'WO20260706005' }
]

export const defectTypes = [
  { name: '外观划伤', count: 12 },
  { name: '扇叶偏心', count: 5 },
  { name: '电机异响', count: 3 },
  { name: '扫码失败', count: 8 },
  { name: '包装破损', count: 4 },
  { name: '绝缘测试不合格', count: 2 }
]

export const equipments = [
  { id: 'EQ-INJ-01', name: '注塑机 1#', area: '注塑区', status: '运行', oee: 89, reason: '正常生产', next: '07-08 模具点检' },
  { id: 'EQ-WND-03', name: '绕线机 3#', area: '电机线', status: '运行', oee: 92, reason: '正常生产', next: '07-07 张力校准' },
  { id: 'EQ-ASM-02', name: '装配线 2#', area: '总装区', status: '待机', oee: 70, reason: '等料：前网罩', next: '07-06 补料确认' },
  { id: 'EQ-AGE-04', name: '老化测试台 4#', area: '测试区', status: '故障', oee: 43, reason: '温控模块报警', next: '立即维修' },
  { id: 'EQ-SCN-07', name: '扫码枪 7#', area: '包装线', status: '维护中', oee: 58, reason: '扫码失败率偏高', next: '固件检查' },
  { id: 'EQ-QC-01', name: '检测台 1#', area: '质检区', status: '运行', oee: 85, reason: '正常检验', next: '07-09 量具校验' }
]

export const downtimeReasons = [
  { reason: '等料', minutes: 38 },
  { reason: '设备故障', minutes: 52 },
  { reason: '质量待判', minutes: 24 },
  { reason: '换型调机', minutes: 31 },
  { reason: '点检维护', minutes: 18 }
]

export const traceTree = {
  query: 'SN20260706000158',
  root: 'SN20260706000158 FS-500 落地扇',
  order: 'WO20260706001',
  batch: 'BATCH-FS500-0706A',
  nodes: [
    {
      title: '工单 WO20260706001 / 总装一线',
      children: [
        { title: '注塑：后壳 BATCH-SHELL-0706A / 注塑机 1# / 张工' },
        { title: '绕线：电机线圈 BATCH-COIL-0706D / 绕线机 3# / 陈工' },
        { title: '电机装配：MTR-65 / 装配线 1# / 刘工' },
        { title: '扇叶装配：BATCH-BLD-0706C / 扭矩 1.82 N.m / 王工' },
        { title: '老化测试：45min / 老化测试台 2# / 合格' },
        { title: '质检：FQC-0706-021 / 噪声 42dB / 合格' },
        { title: '包装：BATCH-PKG-0706B / 入库待确认' }
      ]
    }
  ],
  details: [
    ['产品型号', 'FS-500 落地扇'],
    ['工单', 'WO20260706001'],
    ['批次', 'BATCH-FS500-0706A'],
    ['当前状态', '质检合格'],
    ['最后设备', '检测台 1#'],
    ['责任班组', '总装一班']
  ]
}
