<template>
  <section class="board-page workshop-board-page">
    <div class="board-header">
      <div>
        <p>电子看板</p>
        <h1>车间看板</h1>
        <span>演示数据已关闭，区域数据只从真实接口加载。</span>
      </div>
      <div class="board-time">车间总览 · {{ activeAreas }}/{{ areas.length }} 区域正常</div>
    </div>

    <p v-if="loading" class="board-state">Loading workshop board...</p>
    <p v-if="error" class="board-state error">{{ error }}</p>
    <p v-if="!loading && !error && areas.length === 0" class="board-state">暂无车间看板数据</p>

    <div class="workshop-layout">
      <article v-for="area in areas" :key="area.name" class="workshop-area-card" :class="area.tone">
        <div class="area-map-cell">{{ area.short }}</div>
        <div class="area-content">
          <div class="area-title">
            <strong>{{ area.name }}</strong>
            <span>{{ area.status }}</span>
          </div>
          <p>{{ area.description }}</p>
          <div class="area-stats">
            <span>工单 {{ area.orders }}</span>
            <span>设备 {{ area.equipment }}</span>
            <span>异常 {{ area.exceptions }}</span>
          </div>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getWorkshopBoard } from '../../api/dashboard'

const areas = ref([])
const loading = ref(false)
const error = ref('')

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])
const toneOf = (status) => {
  const text = String(status || '').toUpperCase()
  if (['ALARM', 'ERROR', 'FAILED', 'DANGER'].includes(text)) return 'danger'
  if (['WARN', 'WARNING', 'PENDING', 'WAITING'].includes(text)) return 'warn'
  return 'running'
}

const mapArea = (row) => ({
  short: row.short || row.areaCode || row.area_code || row.id || '-',
  name: row.areaName || row.area_name || row.name || '-',
  status: row.status || '-',
  description: row.description || row.remark || '-',
  orders: row.orders ?? row.orderCount ?? row.order_count ?? 0,
  equipment: row.equipment || row.equipmentCount || row.equipment_count || '-',
  exceptions: row.exceptions ?? row.exceptionCount ?? row.exception_count ?? 0,
  tone: row.tone || toneOf(row.status)
})

const loadRows = async () => {
  loading.value = true
  error.value = ''
  try {
    areas.value = recordsOf(await getWorkshopBoard()).map(mapArea)
  } catch (e) {
    areas.value = []
    error.value = e?.message || 'Workshop board API is not connected yet.'
  } finally {
    loading.value = false
  }
}

const activeAreas = computed(() => areas.value.filter((area) => area.tone === 'running').length)

onMounted(loadRows)
</script>

<style scoped>
.board-state {
  margin: 12px 0;
  color: #52616b;
  font-size: 14px;
}

.board-state.error {
  color: #b42318;
}
</style>
