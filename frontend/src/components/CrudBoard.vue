<template>
  <section class="mes-workspace">
    <div class="mes-page-heading mes-page-header">
      <div class="mes-page-header-content">
        <p class="mes-page-eyebrow">{{ eyebrow }}</p>
        <h1>{{ title }}</h1>
        <span class="mes-page-description">{{ description }}</span>
      </div>
      <div class="mes-toolbar mes-page-actions">
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

    <div class="mes-filter-bar mes-filter-bar--crud">
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

    <div class="mes-board-grid mes-crud-layout">
      <section class="mes-panel mes-list-panel">
        <div class="mes-panel-title">
          <strong>{{ listTitle }}</strong>
          <span>{{ t('common.list.items', { count: filteredRows.length }) }}</span>
        </div>
        <table class="mes-table mes-table--crud">
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
              @click.stop="selectRow(row)"
            >
              <td v-for="column in columns" :key="column.key">
                <span v-if="column.key === 'status'" class="status-pill">{{ row[column.key] }}</span>
                <span v-else>{{ row[column.key] }}</span>
              </td>
              <td class="row-actions mes-row-actions">
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
              <td :colspan="columns.length + 1" class="empty-cell mes-state mes-state-empty">{{ t('common.list.noMatch') }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <aside class="mes-panel detail-panel mes-detail-panel">
        <div class="mes-panel-title">
          <strong>{{ t('common.detail.title') }}</strong>
          <span>{{ selected?.[rowKey] || t('common.detail.unselected') }}</span>
        </div>
        <slot name="detail" :selected="selected" :columns="columns">
          <dl v-if="selected" class="detail-list mes-detail-list">
            <template v-for="column in columns" :key="column.key">
              <dt>{{ column.label }}</dt>
              <dd>{{ selected[column.key] }}</dd>
            </template>
          </dl>
          <div v-else class="empty-detail mes-state mes-state-empty">{{ t('common.detail.emptyHint') }}</div>
        </slot>
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
import { buildAuditEntry, statusFlows } from '../utils/statusFlow'

const { t } = useI18n()

const props = defineProps({
  eyebrow: { type: String, default: 'MES' },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  listTitle: { type: String, default: '涓氬姟鍒楄〃' },
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

const emit = defineEmits(['primary-action', 'row-action', 'select'])

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

const statuses = computed(() => {
  const flowStatuses = props.flowType && statusFlows[props.flowType] ? Object.keys(statusFlows[props.flowType]) : []
  const dataStatuses = localRows.value.map((row) => row.status).filter(Boolean)
  return [...new Set([...flowStatuses, ...dataStatuses])]
})
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
  release: () => t('status.released'),
  dispatch: () => t('status.released'),
  start: () => t('status.running'),
  pause: () => t('status.paused'),
  resume: () => t('status.running'),
  complete: () => t('status.completed'),
  close: () => t('status.closed'),
  void: () => t('status.voided'),
  handle: () => t('status.handled'),
  accept: () => t('status.handling'),
  retry: () => t('status.syncPending'),
  finishRepair: () => t('status.normal'),
  repair: () => t('status.repairPending')
}

const getNextStatus = (action) => {
  const fn = nextStatusByAction[action]
  return fn ? fn() : null
}

const selectRow = (row) => {
  selected.value = row
  emit('select', row)
}

const addAuditEntry = ({ action, from, to, remark }) => {
  localLogs.value = [
    buildAuditEntry({
      action,
      operator: authState.user?.displayName,
      from,
      to: to || from,
      remark: remark || `${t('common.auditLog.defaultRemark')}`
    }),
    ...localLogs.value
  ]
}

defineExpose({ addAuditEntry, selectRow })

const runAction = (button, target = selected.value, source = 'primary') => {
  if (!target) return

  selected.value = target
  emit(source === 'primary' ? 'primary-action' : 'row-action', { action: button.action, button, row: target })
  if (props.handleActionsExternally) return

  const oldStatus = target.status
  const nextStatus = getNextStatus(button.action) || oldStatus
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
