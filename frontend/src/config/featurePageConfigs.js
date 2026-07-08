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
    { key: 'id', label: 'ID' },
    { key: 'object', label: 'Business Object' },
    { key: 'line', label: 'Scope / Line' },
    { key: 'owner', label: 'Owner' },
    { key: 'progress', label: 'Progress' },
    { key: 'status', label: 'Status' },
    { key: 'updatedAt', label: 'Updated' }
  ],
  statusTone
}

export const featurePageConfigs = {}
