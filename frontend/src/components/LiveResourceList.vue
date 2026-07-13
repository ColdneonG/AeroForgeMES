<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { get } from '@/api/client'

type Row = Record<string, unknown> & { id?: string | number }
interface Column { key: string; label: string; mono?: boolean }
const props = defineProps<{ active: string; section: string; title: string; endpoint: string; columns: Column[] }>()
const rows = ref<Row[]>([]); const loading = ref(false); const error = ref('')
const value = (row: Row, key: string) => String(row[key] ?? '-')
async function load() { loading.value = true; error.value = ''; try { rows.value = await get<Row[]>(props.endpoint) } catch (e) { error.value = e instanceof Error ? e.message : '数据加载失败' } finally { loading.value = false } }
onMounted(load); watch(() => props.endpoint, load)
</script>
<template><MesLayout :active="active"><header class="app-header"><div class="header-breadcrumb"><span>{{ section }}</span><span class="bc-sep">/</span><span class="bc-current">{{ title }}</span></div><div class="header-actions"><button class="btn btn-secondary btn-sm" :disabled="loading" @click="load">刷新</button><span class="user-avatar">张</span></div></header><main class="app-main"><h1 class="page-title">{{ title }}</h1><div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div><div class="data-table-wrap"><table class="data-table"><thead><tr><th v-for="column in columns" :key="column.key">{{ column.label }}</th></tr></thead><tbody><tr v-if="loading"><td :colspan="columns.length">正在加载...</td></tr><tr v-else-if="!rows.length"><td :colspan="columns.length">暂无数据</td></tr><tr v-for="(row,index) in rows" :key="row.id ?? index"><td v-for="column in columns" :key="column.key" :class="{ 'cell-mono': column.mono }">{{ value(row, column.key) }}</td></tr></tbody></table><div class="pagination">共 {{ rows.length }} 条记录</div></div></main><footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>数据库实时数据</span><span class="statusbar-sep">|</span><span class="statusbar-item">{{ title }} · {{ rows.length }} 条</span></footer></MesLayout></template>
