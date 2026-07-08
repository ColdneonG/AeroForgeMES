const statusTone = {
  ENABLED: 'ok',
  RUNNING: 'running',
  COMPLETED: 'ok',
  PENDING: 'warn',
  PROCESSING: 'warn',
  FAILED: 'danger',
  DISABLED: 'danger'
}

export const emptyFeatureConfig = {
  rows: [],
  kpis: [],
  trend: [],
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

export const featurePageConfigs = {}
