<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { getEquipmentRecords } from '@/api/equipment'

type Equipment = Record<string, unknown> & { id: number }
const rows = ref<Equipment[]>([])
const loading = ref(false)
const error = ref('')
const keyword = ref('')
const value = (row: Equipment, key: string) => String(row[key] ?? '-')
const filteredRows = computed(() => {
  const term = keyword.value.trim().toLowerCase()
  return term ? rows.value.filter((row) => [row.equipmentCode, row.equipmentName, row.categoryName, row.model, row.serialNo].some((item) => String(item || '').toLowerCase().includes(term))) : rows.value
})
async function load() {
  loading.value = true; error.value = ''
  try { rows.value = await getEquipmentRecords() as Equipment[] }
  catch (e) { error.value = e instanceof Error ? e.message : '设备台账加载失败' }
  finally { loading.value = false }
}
onMounted(load)
</script>

<template>
  <MesLayout active="equip-ledger">
    <header class="app-header"><div class="header-breadcrumb"><span>设备与安灯</span><span class="bc-sep">/</span><span class="bc-current">设备台账</span></div><div class="header-actions"><button class="btn btn-secondary btn-sm" :disabled="loading" @click="load">刷新档案</button><span class="user-avatar">张</span></div></header>
    <main class="app-main">
      <div class="flex items-center justify-between mb-5"><div><h1 class="page-title" style="margin-bottom:4px">设备台账</h1><span class="text-muted">设备资产档案、归属与基础参数</span></div><input v-model="keyword" class="form-input ledger-search" placeholder="搜索编码、名称、型号或序列号"></div>
      <div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div>
      <div class="data-table-wrap"><table class="data-table"><thead><tr><th>设备编码</th><th>设备名称</th><th>设备类型</th><th>型号</th><th>序列号</th><th>制造商</th><th>购置日期</th><th>档案状态</th><th>操作</th></tr></thead><tbody><tr v-if="loading"><td colspan="9">正在加载...</td></tr><tr v-else-if="!filteredRows.length"><td colspan="9">暂无设备档案</td></tr><tr v-for="row in filteredRows" :key="row.id"><td class="cell-mono">{{ value(row, 'equipmentCode') }}</td><td>{{ value(row, 'equipmentName') }}</td><td>{{ value(row, 'categoryName') }}</td><td>{{ value(row, 'model') }}</td><td class="cell-mono">{{ value(row, 'serialNo') }}</td><td>{{ value(row, 'manufacturerName') }}</td><td>{{ value(row, 'purchaseDate') }}</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>{{ value(row, 'status') }}</span></td><td><RouterLink :to="{ path: '/equipment/detail', query: { id: row.id } }" class="btn btn-ghost btn-sm">查看档案</RouterLink></td></tr></tbody></table><div class="pagination">共 {{ filteredRows.length }} / {{ rows.length }} 台设备</div></div>
    </main>
    <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>设备资产档案</span><span class="statusbar-sep">|</span><span class="statusbar-item">已登记 {{ rows.length }} 台</span></footer>
  </MesLayout>
</template>

<style scoped>.ledger-search { width:280px; }</style>
