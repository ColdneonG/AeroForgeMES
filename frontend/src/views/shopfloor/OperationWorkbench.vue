<template>
  <section class="mes-workspace">
    <div class="mes-page-heading">
      <div>
        <p>{{ t('shopfloor.workbench.eyebrow') }}</p>
        <h1>{{ t('shopfloor.workbench.title') }}</h1>
        <span>{{ t('common.feature.demoDisabled') }}</span>
      </div>
    </div>
    <p v-if="loading" class="api-state">{{ t('common.loading.generic') }}</p>
    <p v-if="error" class="api-state error">{{ error }}</p>
    <CrudBoard
      :eyebrow="t('shopfloor.workbench.eyebrow')"
      :title="t('shopfloor.workbench.title')"
      :description="t('shopfloor.workbench.description')"
      :list-title="t('shopfloor.workbench.listTitle')"
      :rows="rows"
      :columns="columns"
      row-key="id"
      flow-type="task"
      :row-actions="rowActions"
      :handle-actions-externally="true"
    />
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import CrudBoard from '../../components/CrudBoard.vue'
import { getShopTasks } from '../../api/production'

const { t } = useI18n()
const rows = ref([])
const loading = ref(false)
const error = ref('')
const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])

const mapTask = (row) => ({
  id: row.taskNo || row.task_no || row.id,
  process: row.processName || row.process_name || row.routeId || row.route_id || '-',
  line: row.lineName || row.line_name || row.lineId || row.line_id || '-',
  good: row.goodQty ?? row.good_qty ?? row.completedQty ?? row.completed_qty ?? '-',
  bad: row.badQty ?? row.bad_qty ?? row.defectiveQty ?? row.defective_qty ?? '-',
  status: row.status || '-',
  raw: row
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    rows.value = recordsOf(await getShopTasks()).map(mapTask)
  } catch (e) {
    rows.value = []
    error.value = e?.message || t('common.error.generic')
  } finally {
    loading.value = false
  }
}

const columns = [
  { key: 'id', label: t('tableColumns.id') },
  { key: 'process', label: t('common.filter.status') },
  { key: 'line', label: t('tableColumns.line') },
  { key: 'good', label: t('tableColumns.done') },
  { key: 'bad', label: t('tableColumns.scope') },
  { key: 'status', label: t('tableColumns.status') }
]
const rowActions = [
  { label: t('statusFlow.actions.report'), action: 'report' },
  { label: t('statusFlow.actions.report'), action: 'report' },
  { label: t('statusFlow.actions.complete'), action: 'complete' },
  { label: t('statusFlow.actions.andon'), action: 'andon' }
]

onMounted(loadRows)
</script>

<style scoped>
.api-state {
  margin: 12px 24px;
  color: #52616b;
  font-size: 14px;
}

.api-state.error {
  color: #b42318;
}
</style>
