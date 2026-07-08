<template>
  <section v-if="config" class="dashboard-feature-page">
    <div class="dashboard-feature-header">
      <div>
        <p>{{ config.group }}</p>
        <h1>{{ config.title }}</h1>
        <span>{{ config.description }}</span>
      </div>
      <div class="dashboard-feature-actions">
        <PermissionButton action="create">新增</PermissionButton>
        <PermissionButton action="import">导入</PermissionButton>
        <PermissionButton action="export">导出</PermissionButton>
      </div>
    </div>

    <div class="feature-kpi-grid">
      <KpiCard
        v-for="kpi in config.kpis"
        :key="kpi.label"
        :label="kpi.label"
        :value="kpi.value"
        :unit="kpi.unit"
        :caption="kpi.caption"
        :trend="kpi.trend"
        :tone="kpi.tone"
      />
    </div>

    <div class="feature-layout-grid">
      <section class="feature-card feature-main-card">
        <div class="feature-card-title">
          <strong>{{ config.title }}列表</strong>
          <label>
            <span>筛选</span>
            <input v-model="keyword" type="search" placeholder="编号 / 对象 / 责任人 / 状态" />
          </label>
        </div>
        <table class="feature-table">
          <thead>
            <tr>
              <th v-for="column in config.columns" :key="column.key">{{ column.label }}</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="row in filteredRows"
              :key="row.id"
              :class="{ selected: selected?.id === row.id }"
              @click="selected = row"
            >
              <td v-for="column in config.columns" :key="column.key">
                <div v-if="column.key === 'progress'" class="feature-progress">
                  <span :style="{ width: `${row.progress}%` }"></span>
                  <b>{{ row.progress }}%</b>
                </div>
                <span v-else-if="column.key === 'status'" class="siemens-status" :class="config.statusTone[row.status]">
                  {{ row.status }}
                </span>
                <span v-else>{{ row[column.key] }}</span>
              </td>
              <td class="feature-row-actions">
                <PermissionButton action="edit" @click.stop="appendLog('编辑', row)">编辑</PermissionButton>
                <PermissionButton action="audit" @click.stop="appendLog('审计', row)">审计</PermissionButton>
              </td>
            </tr>
            <tr v-if="filteredRows.length === 0">
              <td :colspan="config.columns.length + 1" class="feature-empty">暂无匹配数据</td>
            </tr>
          </tbody>
        </table>
      </section>

      <aside class="feature-card feature-side-card">
        <div class="feature-card-title">
          <strong>状态概览</strong>
          <span>{{ selected?.id || '未选择' }}</span>
        </div>
        <div class="feature-status-stack">
          <article v-for="item in statusSummary" :key="item.status">
            <span class="siemens-status" :class="config.statusTone[item.status]">{{ item.status }}</span>
            <strong>{{ item.count }}</strong>
            <div class="feature-bar"><i :style="{ width: `${item.rate}%` }"></i></div>
          </article>
        </div>
        <dl v-if="selected" class="feature-detail-list">
          <template v-for="column in config.columns" :key="column.key">
            <dt>{{ column.label }}</dt>
            <dd>{{ selected[column.key] }}</dd>
          </template>
        </dl>
      </aside>
    </div>

    <div class="feature-bottom-grid">
      <section class="feature-card">
        <div class="feature-card-title">
          <strong>趋势展示</strong>
          <span>近 12 时段</span>
        </div>
        <div class="feature-mini-chart">
          <i v-for="value in config.trend" :key="value" :style="{ height: `${value}%` }"></i>
        </div>
      </section>
      <section class="feature-card">
        <div class="feature-card-title">
          <strong>状态日志 / 操作审计</strong>
          <span>{{ logs.length }} 条</span>
        </div>
        <div class="feature-log-list">
          <article v-for="log in logs" :key="`${log.time}-${log.action}-${log.target}`">
            <strong>{{ log.action }}</strong>
            <span>{{ log.operator }} · {{ log.time }}</span>
            <p>{{ log.target }} · {{ log.remark }}</p>
          </article>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import KpiCard from '../../components/KpiCard.vue'
import PermissionButton from '../../components/PermissionButton.vue'
import { authState } from '../../stores/auth'
import { featurePageConfigs } from '../../config/featurePageConfigs'

const route = useRoute()
const keyword = ref('')
const selected = ref(null)
const logs = ref([])

const config = computed(() => featurePageConfigs[route.meta.featureKey])

const filteredRows = computed(() => {
  const rows = config.value?.rows || []
  const term = keyword.value.trim().toLowerCase()
  if (!term) return rows
  return rows.filter((row) => Object.values(row).some((value) => String(value).toLowerCase().includes(term)))
})

const statusSummary = computed(() => {
  const rows = config.value?.rows || []
  const total = rows.length || 1
  return Object.entries(
    rows.reduce((summary, row) => {
      summary[row.status] = (summary[row.status] || 0) + 1
      return summary
    }, {})
  ).map(([status, count]) => ({ status, count, rate: Math.round((count / total) * 100) }))
})

const appendLog = (action, row) => {
  logs.value.unshift({
    action,
    target: row.id,
    operator: authState.user?.displayName || '当前用户',
    time: new Date().toLocaleString('zh-CN', { hour12: false }),
    remark: `${config.value.title}页面操作`
  })
}

watch(
  config,
  (value) => {
    selected.value = value?.rows?.[0] || null
    logs.value = [
      {
        action: '加载',
        target: value?.title || '功能页面',
        operator: authState.user?.displayName || '系统',
        time: new Date().toLocaleString('zh-CN', { hour12: false }),
        remark: '页面数据已刷新'
      }
    ]
  },
  { immediate: true }
)
</script>
