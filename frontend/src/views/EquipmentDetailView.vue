<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import MesLayout from '@/layouts/MesLayout.vue'
import { get, put } from '@/api/client'
type Equipment = Record<string, unknown> & { id: number }
const route = useRoute(); const equipment = ref<Equipment | null>(null); const form = ref<Record<string, unknown>>({}); const editing = ref(false); const error = ref('')
const labels: Record<string, string> = { equipmentCode: '设备编码', equipmentName: '设备名称', model: '型号', serialNo: '序列号', categoryName: '设备分类', manufacturerName: '制造商', lineName: '产线', stationName: '工位', purchaseDate: '购置日期', equipmentStatus: '运行状态', status: '档案状态' }
async function load() { const id = String(route.query.id || ''); if (!id) { error.value = '缺少设备编号'; return }; try { equipment.value = await get<Equipment>(`/equipment/equipments/${id}`); form.value = { ...equipment.value } } catch (e) { error.value = e instanceof Error ? e.message : '设备加载失败' } }
async function save() { if (!equipment.value) return; try { equipment.value = await put<Equipment>(`/equipment/equipments/${equipment.value.id}`, form.value); editing.value = false } catch (e) { error.value = e instanceof Error ? e.message : '保存失败' } }
onMounted(load)
</script>
<template><MesLayout active="equip-detail"><header class="app-header"><div class="header-breadcrumb"><span>设备与安灯</span><span class="bc-sep">/</span><RouterLink to="/equipment/ledger">设备台账</RouterLink><span class="bc-sep">/</span><span class="bc-current">设备详情</span></div><div class="header-actions"><button v-if="equipment" class="btn btn-primary btn-sm" @click="editing ? save() : editing = true">{{ editing ? '保存' : '编辑' }}</button><span class="user-avatar">张</span></div></header><main class="app-main"><h1 class="page-title">{{ equipment?.equipmentName || '设备详情' }}</h1><div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div><section v-if="equipment" class="card"><table class="data-table" style="border:none"><tbody><tr v-for="(label,key) in labels" :key="key"><td style="width:160px;opacity:.55">{{ label }}</td><td><input v-if="editing && !key.endsWith('Name')" v-model="form[key]" class="form-input"><span v-else>{{ equipment[key] ?? '-' }}</span></td></tr></tbody></table></section></main></MesLayout></template>
