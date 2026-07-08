import { missingFeatureCatalog } from '../mock/mesExtendedData'

const statusTone = {
  启用: 'ok',
  运行: 'running',
  已接入: 'ok',
  已完成: 'ok',
  待处理: 'warn',
  处理中: 'warn',
  待同步: 'warn',
  异常: 'danger',
  失败: 'danger',
  停用: 'danger'
}

const buildRows = (feature) =>
  feature.rows ||
  [1, 2, 3, 4].map((index) => ({
    id: `${feature.code}-${String(index).padStart(3, '0')}`,
    object: feature.objects[index % feature.objects.length],
    line: feature.lines[index % feature.lines.length],
    owner: feature.owners[index % feature.owners.length],
    progress: [92, 76, 58, 100][index - 1],
    status: feature.statuses[index % feature.statuses.length],
    updatedAt: `2026-07-07 ${String(8 + index).padStart(2, '0')}:20`
  }))

const buildKpis = (feature, rows) => {
  const done = rows.filter((row) => ['启用', '运行', '已接入', '已完成'].includes(row.status)).length
  const warn = rows.filter((row) => ['待处理', '处理中', '待同步'].includes(row.status)).length
  const risk = rows.filter((row) => ['异常', '失败', '停用'].includes(row.status)).length
  const avg = Math.round(rows.reduce((sum, row) => sum + Number(row.progress || 0), 0) / rows.length)

  return [
    { label: '记录总数', value: rows.length, unit: '条', caption: feature.group, trend: '实时', tone: 'info' },
    { label: '正常/完成', value: done, unit: '条', caption: '可继续流转', trend: `${Math.round((done / rows.length) * 100)}%`, tone: 'success' },
    { label: '待处理', value: warn, unit: '条', caption: '需要跟进', trend: warn ? '关注' : '清零', tone: warn ? 'warning' : 'stable' },
    { label: '风险项', value: risk, unit: '条', caption: '异常/失败/停用', trend: risk ? '需处理' : '正常', tone: risk ? 'danger' : 'success' },
    { label: '平均进度', value: avg, unit: '%', caption: feature.title, trend: avg >= 80 ? '稳定' : '推进中', tone: avg >= 80 ? 'success' : 'warning' }
  ]
}

export const featurePageConfigs = Object.fromEntries(
  missingFeatureCatalog.map((feature) => {
    const rows = buildRows(feature)
    return [
      feature.key,
      {
        ...feature,
        rows,
        kpis: buildKpis(feature, rows),
        columns: [
          { key: 'id', label: '编号' },
          { key: 'object', label: '业务对象' },
          { key: 'line', label: '范围/产线' },
          { key: 'owner', label: '责任人' },
          { key: 'progress', label: '进度' },
          { key: 'status', label: '状态' },
          { key: 'updatedAt', label: '更新时间' }
        ],
        statusTone
      }
    ]
  })
)
