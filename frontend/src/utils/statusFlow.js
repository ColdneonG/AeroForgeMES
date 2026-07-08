export const statusFlows = {
  workOrder: {
    草稿: ['edit', 'import', 'release', 'void'],
    待下发: ['edit', 'release', 'void'],
    已下发: ['start', 'complete'],
    生产中: ['pause', 'report', 'complete'],
    暂停: ['start', 'resume', 'report', 'complete'],
    已完成: ['close'],
    已关闭: ['audit'],
    作废: ['audit']
  },
  task: {
    已下发: ['start', 'repair'],
    生产中: ['pause', 'report', 'complete', 'andon'],
    暂停: ['resume', 'report', 'repair'],
    已完成: ['audit'],
    返修: ['start', 'report', 'complete'],
    已关闭: ['audit']
  },
  equipment: {
    正常: ['inspect', 'repair', 'disable'],
    运行: ['inspect', 'repair', 'andon'],
    待机: ['inspect', 'repair'],
    待维修: ['acceptRepair', 'finishRepair'],
    维修中: ['finishRepair'],
    停用: ['audit'],
    故障: ['acceptRepair', 'finishRepair']
  },
  andon: {
    待处理: ['accept', 'handle', 'close'],
    处理中: ['handle', 'close'],
    已处理: ['close'],
    已关闭: ['audit'],
    作废: ['audit']
  },
  integration: {
    待同步: ['retry', 'close'],
    失败: ['retry', 'close'],
    成功: ['audit'],
    已关闭: ['audit']
  }
}

export const canTransition = (type, status, action) => {
  const actions = statusFlows[type]?.[status] || []
  return actions.includes(action)
}

export const buildAuditEntry = ({ action, operator, from, to, remark }) => ({
  time: new Date().toLocaleString('zh-CN', { hour12: false }),
  operator: operator || '当前用户',
  action,
  from,
  to: to || from,
  remark: remark || '页面操作提交'
})
