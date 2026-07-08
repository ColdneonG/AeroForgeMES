<template>
  <section class="mes-workspace" @click="selected = null">
    <div class="mes-page-heading">
      <div>
        <p>{{ eyebrow }}</p>
        <h1>{{ title }}</h1>
        <span>{{ description }}</span>
      </div>
      <div class="mes-toolbar">
        <PermissionButton
          v-for="button in primaryActions"
          :key="button.action"
          :permission="button.permission"
          :flow-type="flowType"
          :status="selected?.status"
          :action="button.action"
          @click="runAction(button, selected, 'primary')"
        >
          {{ button.label }}
        </PermissionButton>
      </div>
    </div>

    <div class="mes-filter-bar">
      <label>
        {{ t('common.filter.keyword') }}
        <input v-model="keyword" type="search" :placeholder="t('common.filter.keywordPlaceholder')" />
      </label>
      <label>
        {{ t('common.filter.status') }}
        <select v-model="status">
          <option value="">{{ t('common.filter.statusAll') }}</option>
          <option v-for="item in statuses" :key="item" :value="item">{{ item }}</option>
        </select>
      </label>
      <span class="data-scope">{{ t('common.filter.dataScope', { scope: dataScope }) }}</span>
    </div>

    <div class="mes-board-grid">
      <section class="mes-panel">
        <div class="mes-panel-title">
          <strong>{{ listTitle }}</strong>
          <span>{{ t('common.list.items', { count: filteredRows.length }) }}</span>
        </div>
        <table class="mes-table">
          <thead>
            <tr>
              <th v-for="column in columns" :key="column.key">{{ column.label }}</th>
              <th>{{ t('common.list.action') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="row in filteredRows"
              :key="row[rowKey]"
              :class="{ selected: selected?.[rowKey] === row[rowKey] }"
              @click.stop="selected = row"
            >
              <td v-for="column in columns" :key="column.key">
                <span v-if="column.key === 'status'" class="status-pill">{{ row[column.key] }}</span>
                <span v-else>{{ row[column.key] }}</span>
              </td>
              <td class="row-actions">
                <PermissionButton
                  v-for="button in rowActions"
                  :key="button.action"
                  :permission="button.permission"
                  :flow-type="flowType"
                  :status="row.status"
                  :action="button.action"
                  @click.stop="runAction(button, row, 'row')"
                >
                  {{ button.label }}
                </PermissionButton>
              </td>
            </tr>
            <tr v-if="filteredRows.length === 0">
              <td :colspan="columns.length + 1" class="empty-cell">{{ t('common.list.noMatch') }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <aside class="mes-panel detail-panel">
        <div class="mes-panel-title">
          <strong>{{ t('common.detail.title') }}</strong>
          <span>{{ selected?.[rowKey] || t('common.detail.unselected') }}</span>
        </div>
        <dl v-if="selected" class="detail-list">
          <template v-for="column in columns" :key="column.key">
            <dt>{{ column.label }}</dt>
            <dd>{{ selected[column.key] }}</dd>
          </template>
        </dl>
        <div v-else class="empty-detail">{{ t('common.detail.emptyHint') }}</div>
      </aside>
    </div>

    <AuditLogPanel :logs="localLogs" />
  </section>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import AuditLogPanel from './AuditLogPanel.vue'
import PermissionButton from './PermissionButton.vue'
import { authState } from '../stores/auth'
import { buildAuditEntry } from '../utils/statusFlow'

const { t } = useI18n()

const props = defineProps({
  eyebrow: { type: String, default: 'MES' },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  listTitle: { type: String, default: '业务列表' },
  rows: { type: Array, default: () => [] },
  columns: { type: Array, default: () => [] },
  rowKey: { type: String, default: 'id' },
  flowType: { type: String, default: '' },
  primaryActions: {
    type: Array,
    default: () => []
  },
  rowActions: {
    type: Array,
    default: () => []
  },
  handleActionsExternally: { type: Boolean, default: false }
})

const emit = defineEmits(['primary-action', 'row-action'])

const keyword = ref('')
const status = ref('')
const selected = ref(props.rows[0] || null)
const localRows = ref(props.rows)
const localLogs = ref([])

watch(
  () => props.rows,
  (rows) => {
    localRows.value = rows
    if (!rows.some((row) => row[props.rowKey] === selected.value?.[props.rowKey])) {
      selected.value = rows[0] || null
    }
  }
)

const statuses = computed(() => [...new Set(localRows.value.map((row) => row.status).filter(Boolean))])
const dataScope = computed(() => authState.permissions.dataScopes?.join(' / ') || t('common.filter.dataScopeUnloaded'))

const filteredRows = computed(() => {
  const term = keyword.value.trim().toLowerCase()
  return localRows.value.filter((row) => {
    const matchesStatus = !status.value || row.status === status.value
    const matchesTerm =
      !term || Object.values(row).some((value) => String(value).toLowerCase().includes(term))
    return matchesStatus && matchesTerm
  })
})

const nextStatusByAction = {
  release: '已下发',
  dispatch: '已下发',
  start: '生产中',
  pause: '暂停',
  resume: '生产中',
  complete: '已完成',
  close: '已关闭',
  void: '作废',
  handle: '已处理',
  accept: '处理中',
  retry: '待同步',
  finishRepair: '正常',
  repair: '待维修'
}

const runAction = (button, target = selected.value, source = 'primary') => {
  if (!target) return

  emit(source === 'primary' ? 'primary-action' : 'row-action', { action: button.action, button, row: target })
  if (props.handleActionsExternally) return

  const oldStatus = target.status
  const nextStatus = nextStatusByAction[button.action] || oldStatus
  target.status = nextStatus
  selected.value = target
  localLogs.value = [
    buildAuditEntry({
      action: button.label,
      operator: authState.user?.displayName,
      from: oldStatus,
      to: nextStatus,
      remark: `${target[props.rowKey]} ${t('common.auditLog.defaultRemark')}`
    }),
    ...localLogs.value
  ]
}
</script>
