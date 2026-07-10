import { mkdir, readdir, readFile, writeFile } from 'node:fs/promises'
import { dirname, join, relative, resolve } from 'node:path'

const frontendRoot = resolve(import.meta.dirname, '..')
const backendRoot = resolve(frontendRoot, '..', 'backend')
const outputFile = join(frontendRoot, 'src', 'generated', 'apiCatalog.js')

const moduleNames = {
  'mes-auth': '认证与授权',
  'mes-system': '系统管理',
  'mes-production': '生产与现场管理',
  'mes-quality': '质量管理',
  'mes-equipment': '设备管理',
  'mes-report': '报表分析',
  'mes-integration': '接口集成',
  'mes-gateway': '网关'
}

const walk = async (directory) => {
  const entries = await readdir(directory, { withFileTypes: true })
  const files = await Promise.all(entries.map((entry) => {
    const entryPath = join(directory, entry.name)
    return entry.isDirectory() ? walk(entryPath) : Promise.resolve(entryPath)
  }))
  return files.flat()
}

const joinPath = (base, endpoint) => `${base || ''}${endpoint || ''}`.replace(/\/+/g, '/') || '/'
const controllerPattern = /@RequestMapping(?:\(\s*"([^"]*)"\s*\))?(?:\s*@[^\r\n]+)*\s*public class (\w+)/
const mappingPattern = /@(Get|Post|Put|Delete|Request)Mapping(?:\(\s*"([^"]*)"\s*\))?\s*(?:\r?\n\s*@[^\r\n]+)*\r?\n\s*public [^{;()]+?\s+(\w+)\s*\(/g

const frontendSources = async () => {
  const files = (await walk(join(frontendRoot, 'src'))).filter((file) => /\.(js|vue)$/.test(file))
  return Promise.all(files.map((file) => readFile(file, 'utf8')))
}

const main = async () => {
  const sourceTexts = await frontendSources()
  const controllerFiles = (await walk(backendRoot)).filter((file) => file.endsWith('Controller.java'))
  const rows = []

  for (const file of controllerFiles) {
    const source = await readFile(file, 'utf8')
    const controller = source.match(controllerPattern)
    if (!controller) continue

    const [, basePath = '', controllerName] = controller
    const service = relative(backendRoot, file).split(/[\\/]/)[0]
    let match
    mappingPattern.lastIndex = 0
    while ((match = mappingPattern.exec(source))) {
      const [, method, endpoint = '', methodName] = match
      const path = joinPath(basePath, endpoint)
      if (path.startsWith('/api/integration/erp/')) continue
      const clientPath = path.replace(/^\/api/, '')
      const connected = sourceTexts.some((text) => text.includes(`'${clientPath}'`) || text.includes(`\"${clientPath}\"`))
      rows.push({
        id: `${service}:${method}:${path}:${methodName}`,
        name: `${controllerName}.${methodName}`,
        service,
        module: moduleNames[service] || service,
        method: method.toUpperCase(),
        path,
        description: `Controller 方法：${methodName}()`,
        frontendConnected: connected ? '已前端对接' : '未前端对接',
        status: '可用'
      })
    }
  }

  rows.sort((a, b) => a.service.localeCompare(b.service) || a.path.localeCompare(b.path) || a.method.localeCompare(b.method))
  await mkdir(dirname(outputFile), { recursive: true })
  await writeFile(outputFile, `// 由 scripts/generate-api-catalog.mjs 从后端 Controller 映射自动生成，请勿手工修改。\nexport default ${JSON.stringify(rows, null, 2)}\n`, 'utf8')
}

main()
