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
    { key: 'id', label: 'tableColumns.id' },
    { key: 'object', label: 'tableColumns.object' },
    { key: 'line', label: 'tableColumns.scope' },
    { key: 'owner', label: 'tableColumns.owner' },
    { key: 'progress', label: 'tableColumns.progress' },
    { key: 'status', label: 'tableColumns.status' },
    { key: 'updatedAt', label: 'tableColumns.updatedAt' }
  ],
  statusTone
}

export const featurePageConfigs = {}
