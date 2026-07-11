import fs from 'node:fs'
import path from 'node:path'

const root = path.resolve(import.meta.dirname, '..')
const backend = path.join(root, 'backend')
const output = path.join(root, 'docs', '前端开发后端接口清单.md')

function walk(dir) {
  return fs.readdirSync(dir, { withFileTypes: true }).flatMap((entry) => {
    const full = path.join(dir, entry.name)
    return entry.isDirectory() ? walk(full) : [full]
  })
}

const controllerFiles = walk(backend).filter((file) => /Controller\.java$/.test(file) && !/target/.test(file))
const modelFiles = walk(backend).filter((file) => /src[\\/]main[\\/]java/.test(file) && /[\\/](domain|api)[\\/]/.test(file) && /\.java$/.test(file) && !/Controller\.java$/.test(file))
const methodMap = { GetMapping: 'GET', PostMapping: 'POST', PutMapping: 'PUT', DeleteMapping: 'DELETE', PatchMapping: 'PATCH', RequestMapping: 'ANY' }

function annotationPath(args = '') {
  const match = args.match(/(?:value\s*=\s*)?["']([^"']+)["']/)
  return match?.[1] ?? ''
}

function normalizePath(base, endpoint) {
  const result = `${base}/${endpoint}`.replace(/\/+/g, '/')
  return result.startsWith('/') ? result.replace(/\/$/, '') || '/' : `/${result}`
}

function splitParams(text) {
  const result = []
  let current = ''
  let depth = 0
  for (const char of text) {
    if (char === '(' || char === '<') depth++
    if (char === ')' || char === '>') depth--
    if (char === ',' && depth === 0) {
      result.push(current.trim()); current = ''; continue
    }
    current += char
  }
  if (current.trim()) result.push(current.trim())
  return result
}

function parseParam(raw) {
  const location = raw.includes('@RequestBody') ? '请求体' : raw.includes('@PathVariable') ? '路径' : raw.includes('@RequestHeader') ? '请求头' : raw.includes('@RequestParam') ? '查询' : '查询对象'
  const required = !/required\s*=\s*false/.test(raw)
  const quoted = [...raw.matchAll(/["']([^"']+)["']/g)].map((m) => m[1])
  const clean = raw.replace(/@\w+(?:\([^)]*\))?\s*/g, '').replace(/\bfinal\s+/g, '').trim()
  const pieces = clean.split(/\s+/)
  const name = quoted[0] || pieces.at(-1) || '-'
  const type = pieces.slice(0, -1).join(' ') || 'String'
  return { name, type, location, required }
}

function serviceInfo(file) {
  const match = file.match(/backend[\\/]([^\\/]+)/)
  const module = match?.[1] ?? 'unknown'
  const info = {
    'mes-gateway': ['网关服务', '8080', '统一入口、鉴权过滤、服务转发与网关健康检查'],
    'mes-auth': ['认证服务', '8081', '登录、登出、当前用户、权限与基础用户查询'],
    'mes-system': ['系统服务', '8082', '菜单、编号序列与操作审计日志'],
    'mes-equipment': ['设备服务', '8083', '设备台账、点检、保养、维修、采集、OEE 与能源数据'],
    'mes-report': ['报表服务', '8086', '指标、驾驶舱、生产报表与电子看板聚合'],
    'mes-integration': ['集成服务', '8087', 'ERP、标准 OpenAPI、同步日志与外部系统管理'],
    'mes-production': ['生产服务', '9103', '工单、派工、任务、齐套、条码、追溯与电子 SOP'],
    'mes-quality': ['质量服务', '9104', '检验基础数据、检验单、结果与缺陷闭环']
  }[module] ?? [module, '-', '']
  return { module, name: info[0], port: info[1], purpose: info[2] }
}

function explain(method, url, methodName) {
  const action = url.split('/').at(-1)
  const names = {
    login: '校验账号凭据并签发登录令牌，同时返回当前用户和权限信息', logout: '结束当前登录会话；前端成功后应清除本地令牌与用户缓存',
    me: '读取令牌对应的当前登录用户信息', permissions: '读取当前用户的权限集合，用于菜单和按钮级权限控制', users: '查询用户列表',
    health: '服务存活检查，仅用于部署验证和运维监控', tree: '返回可用于侧边导航的层级菜单树', next: '按编号规则原子获取下一个业务编号',
    issue: '下达业务单据，进入待执行状态', 'confirm-issue': '确认已接收下达的业务单据', start: '启动业务处理并推进状态', pause: '暂停正在执行的业务对象', resume: '恢复已暂停的业务对象',
    complete: '提交完成结果并推进至完成状态', close: '关闭已完成或无需继续处理的业务对象', void: '作废业务对象并保留审计记录', enable: '启用配置记录', disable: '停用配置记录',
    approve: '审批通过当前版本或业务对象', reject: '驳回当前版本并记录原因', publish: '发布已审批版本，使其可供现场执行使用', copy: '复制现有版本生成新的可编辑版本',
    print: '登记并执行条码打印；批量路径按请求体中的条码集合处理', scan: '提交扫码事件并返回条码解析、校验和关联业务结果', parent: '建立当前条码与父级包装或产品条码的关系',
    pass: '提交检验合格判定并推进检验单状态', fail: '提交检验不合格判定并进入缺陷处理流程', retry: '重试失败的外部系统同步任务',
    lock: '为生产任务锁定适用的 SOP 版本并生成执行快照', view: '记录现场人员已查看指定 SOP 步骤', confirm: '确认指定 SOP 步骤已按要求执行',
    'report-validation': '校验任务报工前必做 SOP 步骤是否全部完成', 'running-status': '更新设备实时运行状态', 'daily': '查询按日汇总的生产产量数据'
  }
  if (names[action]) return names[action]
  if (method === 'GET' && /\{id/.test(url)) return '按主键读取单条记录详情；记录不存在时返回业务错误'
  if (method === 'GET') return '按查询条件读取列表或聚合数据；支持的筛选字段见本节参数说明'
  if (method === 'POST' && /write|read/.test(url)) return '接收外部系统业务报文，建立同步日志并执行幂等处理；结果不等同于下游业务必然成功'
  if (method === 'POST') return '创建业务记录或执行指定业务动作；成功后返回最新业务数据'
  if (method === 'PUT') return '按主键更新可编辑字段；状态流转应使用专用动作接口而非直接修改状态'
  if (method === 'DELETE') return '删除尚未进入业务流转的记录；已被引用或已执行的记录通常应改用作废接口'
  return `执行后端方法 ${methodName}`
}

function parseController(file) {
  const source = fs.readFileSync(file, 'utf8')
  const classIndex = source.indexOf('public class')
  const classPrefix = classIndex >= 0 ? source.slice(0, classIndex) : source
  const classMappings = [...classPrefix.matchAll(/@RequestMapping(?:\(([^)]*)\))?/g)]
  const base = annotationPath(classMappings.at(-1)?.[1] || '')
  const endpoints = []
  const regex = /@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping|RequestMapping)(?:\(([^)]*)\))?\s*(?:@[\w.]+(?:\([^)]*\))?\s*)*public\s+([^\s]+(?:<[^;{]+?>)?)\s+(\w+)\s*\(([^;{]*?)\)\s*\{/gs
  for (const match of source.matchAll(regex)) {
    const annotation = match[1]
    const endpoint = annotationPath(match[2] || '')
    if (annotation === 'RequestMapping' && !endpoint) continue
    const method = methodMap[annotation]
    const url = normalizePath(base, endpoint)
    const params = splitParams(match[5]).map(parseParam)
    endpoints.push({ method, url, returnType: match[3].replace(/\s+/g, ' '), methodName: match[4], params, description: explain(method, url, match[4]) })
  }
  return { ...serviceInfo(file), file: path.relative(root, file).replaceAll('\\', '/'), endpoints }
}

const services = new Map()
for (const parsed of controllerFiles.map(parseController)) {
  if (!parsed.endpoints.length) continue
  const existing = services.get(parsed.module) ?? { ...parsed, endpoints: [], files: [] }
  existing.endpoints.push(...parsed.endpoints)
  existing.files.push(parsed.file)
  services.set(parsed.module, existing)
}

function parseModel(file) {
  const source = fs.readFileSync(file, 'utf8')
  const name = source.match(/\b(?:class|record)\s+(\w+)/)?.[1]
  if (!name) return null
  const fields = []
  for (const match of source.matchAll(/(?:private|protected)\s+(?:final\s+)?([\w<>?,. ]+)\s+(\w+)\s*(?:=[^;]+)?;/g)) {
    if (match[2] === 'serialVersionUID') continue
    fields.push([match[2], match[1].replace(/\s+/g, ' ').trim()])
  }
  const record = source.match(/\brecord\s+\w+\s*\(([^)]*)\)/s)
  if (record) {
    for (const item of splitParams(record[1])) {
      const pieces = item.replace(/@\w+(?:\([^)]*\))?\s*/g, '').trim().split(/\s+/)
      if (pieces.length > 1) fields.push([pieces.at(-1), pieces.slice(0, -1).join(' ')])
    }
  }
  return fields.length ? { name, fields, file: path.relative(root, file).replaceAll('\\', '/') } : null
}
const models = modelFiles.map(parseModel).filter(Boolean).sort((a, b) => a.name.localeCompare(b.name))

const equipmentResources = [
  ['categories', '设备类别', 'category_code, category_name, status'], ['manufacturers', '设备制造商', 'manufacturer_code, manufacturer_name, contact, status'],
  ['fault-reasons', '故障原因', 'reason_code, reason_name, category_id, status'], ['equipments', '设备台账', 'equipment_code, equipment_name, category_id, manufacturer_id, line_id, station_id, model, serial_no, purchase_date, equipment_status, status'],
  ['maintenance-tasks', '保养任务', 'maintenance_no, equipment_id, plan_at, assigned_to, completed_at, result, status'], ['inspections', '设备点检记录', 'inspection_no, equipment_id, inspector_id, inspection_at, result, abnormal_desc, status'],
  ['repairs', '维修工单', 'repair_no, equipment_id, fault_reason_id, reported_by, reported_at, assigned_to, repair_start_at, repair_end_at, repair_result, status'],
  ['device-points', '设备采集点', 'equipment_id, point_code, point_name, data_type, unit_id, status'], ['collect-records', '采集记录', 'equipment_id, point_id, collect_value, collect_at, quality_flag'],
  ['count-reports', '设备计数报工', 'equipment_id, task_id, operation_task_id, count_qty, report_at, sync_log_id'], ['debug-logs', '设备调试日志', 'equipment_id, request_payload, response_payload, result, debug_at'],
  ['oee-snapshots', 'OEE 快照', 'equipment_id, line_id, stat_date, availability, performance, quality_rate, oee'], ['energy-records', '能源记录', 'equipment_id, energy_type, value, unit_id, record_at']
]
const integrationResources = [
  ['external-systems', '外部系统配置', 'system_code, system_name, auth_type, status'], ['sync-logs', '接口同步日志', 'system_id, interface_code, direction, biz_type, biz_id, external_no, idempotent_key, request_payload, response_payload, sync_status, retry_count, error_message, started_at, finished_at'],
  ['request-logs', '请求日志', 'sync_log_id, method, url, headers_json, body, requested_at'], ['response-logs', '响应日志', 'sync_log_id, http_status, body, responded_at, elapsed_ms']
]

let md = `# 风擎工控 AeroForge MES 后端接口清单（前端开发版）\n\n`
const order = ['mes-gateway', 'mes-auth', 'mes-system', 'mes-production', 'mes-quality', 'mes-equipment', 'mes-report', 'mes-integration']
md += `> 生成日期：2026-07-11  \n> 依据：当前后端 Controller、网关路由和动态资源白名单。本文记录的是**当前代码已实现接口**，不把前端 TODO 路径当作已实现接口。\n\n`
md += `## 1. 全局调用规范\n\n- 前端统一访问网关：开发环境默认 \`http://localhost:8080/api\`。前端请求封装若已配置 \`/api\` 基地址，业务方法只写如 \`/production/work-orders\`。\n- 除登录和健康检查外，生产启用鉴权后统一携带 \`Authorization: Bearer <token>\`。当前网关配置 \`auth-enabled: false\` 仅适合本地联调。\n- JSON 请求使用 \`Content-Type: application/json\`；SOP 附件和 3D 模型上传使用 \`multipart/form-data\`，不要手工固定 boundary。\n- 两套公共响应包装并存：\`Result<T>\` 与 \`ApiResponse<T>\`。前端必须通过统一拦截器解包，不要在页面中分别判断。典型结构为 \`{ code, message, data }\`；是否成功以当前公共类定义的成功码为准。\n- 列表型动态资源返回的数据通常包含记录集合及分页信息。查询参数以字符串传输；日期时间建议使用 ISO-8601 格式，数量使用数字，主键使用整数。\n- 状态流转必须调用 \`/issue\`、\`/start\`、\`/complete\`、\`/close\`、\`/void\` 等动作接口，禁止通过普通 PUT 直接篡改状态。\n- 所有用户可见错误优先展示后端 \`message\`；401 清理登录态，403 跳转无权限页，400 展示参数或状态错误，5xx 提供重试入口。\n\n`
md += `### 1.1 网关服务映射\n\n| 微服务 | 直连端口 | 网关前缀 | 职责 |\n|---|---:|---|---|\n`
const gatewayPrefixes = { 'mes-gateway': '/api/gateway', 'mes-auth': '/api/auth', 'mes-system': '/api/system', 'mes-production': '/api/production、/api/andon', 'mes-quality': '/api/quality', 'mes-equipment': '/api/equipment', 'mes-report': '/api/report', 'mes-integration': '/api/integration、/api/openapi' }
for (const key of order) {
  const s = services.get(key)
  if (s) md += `| ${s.name}（${s.module}） | ${s.port} | \`${gatewayPrefixes[key]}\` | ${s.purpose} |\n`
}
md += `\n### 1.2 通用参数约定\n\n| 名称 | 位置 | 说明 |\n|---|---|---|\n| \`id\` / \`*Id\` | 路径或请求体 | 数据库主键，必须使用后端返回值，不可用表格序号代替 |\n| \`resource\` | 路径 | 仅允许服务白名单中的资源名，详见设备/集成资源表 |\n| \`page\`, \`size\` | 查询 | 分页页码和每页数量；具体起始页以接口实际返回为准 |\n| \`keyword\` | 查询或路径 | 模糊搜索关键字；放入路径时必须使用 \`encodeURIComponent\` |\n| \`status\` | 查询 | 状态筛选；值应取接口返回的状态枚举，不在前端自行翻译后回传 |\n| 时间范围 | 查询 | 使用服务 DTO 声明的字段，例如开始/结束日期；同一页面需保持时区一致 |\n\n`

let section = 2
for (const key of order) {
  const s = services.get(key)
  if (!s) continue
  md += `## ${section++}. ${s.name}（${s.module}）\n\n**职责：** ${s.purpose}。直连端口 \`${s.port}\`，前端统一通过 8080 网关调用。\n\n`
  const grouped = new Map()
  for (const e of s.endpoints) {
    const group = e.url.split('/').slice(0, e.url.includes('/sop/') ? 5 : 4).join('/')
    grouped.set(group, [...(grouped.get(group) ?? []), e])
  }
  for (const [group, endpoints] of grouped) {
    md += `### ${group || '/'}\n\n`
    for (const e of endpoints) {
      md += `#### \`${e.method} ${e.url}\`\n\n- **用途：** ${e.description}。\n- **后端返回类型：** \`${e.returnType}\`。\n`
      if (e.params.length) {
        md += `- **参数：**\n\n  | 参数 | 位置 | 类型 | 必填 | 说明 |\n  |---|---|---|---|---|\n`
        for (const p of e.params) {
          const note = p.location === '请求体' ? 'JSON 业务对象；字段必须遵循对应 DTO/实体或资源字段约束' : p.location === '查询对象' ? '按该查询 DTO 的字段展开为 URL 查询参数' : p.location === '请求头' ? '认证或调用上下文请求头' : p.location === '路径' ? '资源定位参数，调用前必须存在' : '可选筛选或控制参数'
          md += `  | \`${p.name}\` | ${p.location} | \`${p.type}\` | ${p.required ? '是' : '否'} | ${note} |\n`
        }
        md += `\n`
      } else md += `- **参数：** 无。\n`
      md += `- **前端处理：** 成功后使用响应 \`data\` 更新页面；动作接口需防止重复点击，失败时保留当前表单或列表状态并展示后端消息。\n\n`
    }
  }
  if (key === 'mes-equipment') {
    md += `### 设备动态资源白名单\n\n动态 CRUD 路径中的 \`{resource}\` 只能取下表值。GET 列表支持以可筛选字段作为查询参数；POST/PUT 请求体不得提交表外字段。\n\n| resource | 业务含义 | 主要字段 |\n|---|---|---|\n`
    for (const row of equipmentResources) md += `| \`${row[0]}\` | ${row[1]} | \`${row[2]}\` |\n`
    md += `\n通用动作 \`POST /api/equipment/{resource}/{id}/{action}\` 支持：\`submit→PROCESSING\`、\`confirm/complete→COMPLETED\`、\`close→CLOSED\`、\`cancel→CANCELLED\`、\`enable→ENABLED\`、\`disable→DISABLED\`。并非每种资源都适合每个动作，前端应只展示业务允许的动作。\n\n`
  }
  if (key === 'mes-integration') {
    md += `### 集成动态资源白名单\n\n| resource | 业务含义 | 主要字段 |\n|---|---|---|\n`
    for (const row of integrationResources) md += `| \`${row[0]}\` | ${row[1]} | \`${row[2]}\` |\n`
    md += `\n同步接口必须传稳定的 \`idempotent_key\` 或可推导唯一业务键，避免网络重试生成重复工单。\`retry\` 仅用于失败同步日志；前端应展示重试次数和最后错误信息。\n\n`
  }
}

md += `## ${section}. 当前前后端差异与接入提醒\n\n`
md += `1. 前端 \`src/api/report.js\` 中的 \`GET /api/report/quality\`、\`GET /api/report/equipment\` 当前没有对应 Controller，属于 TODO，页面不能按已实现接口处理。\n`
md += `2. 前端 \`src/api/system.js\` 中的 \`GET /api/system/users\`、\`roles\`、\`permissions\` 当前未由 mes-system 实现；用户列表暂由 \`GET /api/auth/users\` 提供。\n`
md += `3. 设备与集成服务使用动态资源 Controller。接口文档中的通用 CRUD 只表示路由能力，实际字段、筛选项和动作必须遵守白名单表。\n`
md += `4. \`/api/mes/func-*\` 是旧版兼容路径，新前端不得新增依赖；新代码统一使用语义化的 \`/api/equipment\`、\`/api/integration\`、\`/api/openapi\` 路径。\n`
md += `5. \`/internal/status\` 属于服务内部探针，不应从业务页面调用，也不应作为前端功能接口展示。\n\n`
md += `## ${section + 1}. 前端封装建议\n\n- 每个微服务保留独立 API 模块，函数名采用“动词 + 业务对象”，例如 \`getWorkOrders\`、\`startWorkOrder\`。\n- 页面组件只调用 API 函数，不直接拼接 URL；路径参数统一编码，查询参数交给 Axios \`params\` 处理。\n- 列表请求支持取消旧请求，避免快速筛选时旧响应覆盖新结果。\n- 动作按钮根据当前状态显示，并在提交期间禁用；成功后优先用后端返回的最新对象更新行数据。\n- 上传接口使用 \`FormData\`；下载/模型文件接口按 Blob 或可访问资源响应处理。\n- 建议后续补充 OpenAPI 3 描述与枚举 Schema，使本清单可自动生成 TypeScript 类型和 API Client。\n\n`
md += `## ${section + 2}. 类型字段速查\n\n以下字段直接提取自当前后端领域模型。请求 DTO 表示前端可提交字段；VO/实体主要用于识别响应结构。数据库通用字段、校验规则和状态枚举仍以后端实现为准。\n\n`
for (const model of models) {
  md += `### \`${model.name}\`\n\n来源：\`${model.file}\`\n\n| 字段 | Java 类型 | 前端建议类型 |\n|---|---|---|\n`
  for (const [name, type] of model.fields) {
    const ts = /^(long|int|double|float|BigDecimal|Integer|Long|Double)/.test(type) ? 'number' : /^(boolean|Boolean)/.test(type) ? 'boolean' : /^(List|Set|Collection)/.test(type) ? 'array' : /^(Map)/.test(type) ? 'object' : /LocalDate|LocalDateTime|Instant|Date/.test(type) ? 'string (ISO-8601)' : 'string / object'
    md += `| \`${name}\` | \`${type}\` | \`${ts}\` |\n`
  }
  md += `\n`
}
md += `---\n\n源 Controller：\n${[...services.values()].flatMap((s) => s.files).sort().map((f) => `- \`${f}\``).join('\n')}\n`

fs.writeFileSync(output, md, 'utf8')
console.log(`generated ${output}`)
console.log(`services=${services.size}, endpoints=${[...services.values()].reduce((n, s) => n + s.endpoints.length, 0)}`)
