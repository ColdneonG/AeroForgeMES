<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import MesLayout from '@/layouts/MesLayout.vue'
import { getQualityRecords, type QualityRecord } from '@/api/quality'
const route = useRoute(); const planId = computed(() => String(route.query.planId || '')); const rows = ref<QualityRecord[]>([]); const error = ref(''); const value = (row: QualityRecord, key: string) => String(row[key] ?? '-')
async function load() { if (!planId.value) { error.value = '缺少检验方案编号'; rows.value = []; return }; try { error.value = ''; rows.value = await getQualityRecords('/quality/plan-items', { planId: planId.value }) } catch (e) { error.value = e instanceof Error ? e.message : '方案明细加载失败' } }
onMounted(load); watch(planId, load)
</script>
<template><MesLayout active="quality-plan-detail"><header class="app-header"><div class="header-breadcrumb"><span>质量与追溯</span><span class="bc-sep">/</span><RouterLink to="/quality/plans">检验方案</RouterLink><span class="bc-sep">/</span><span class="bc-current">方案明细</span></div></header><main class="app-main"><h1 class="page-title">检验方案明细</h1><div v-if="error" class="alert alert-error mb-5"><span class="alert-icon">!</span>{{ error }}</div><div class="data-table-wrap"><table class="data-table"><thead><tr><th>明细ID</th><th>检验项</th><th>抽样数量</th><th>标准值</th><th>下限</th><th>上限</th><th>必检</th></tr></thead><tbody><tr v-if="!rows.length"><td colspan="7">暂无方案明细</td></tr><tr v-for="row in rows" :key="row.id"><td class="cell-mono">{{ value(row, 'id') }}</td><td>{{ value(row, 'qcItemId') }}</td><td>{{ value(row, 'sampleQty') }}</td><td>{{ value(row, 'standardValue') }}</td><td>{{ value(row, 'lowerLimit') }}</td><td>{{ value(row, 'upperLimit') }}</td><td>{{ value(row, 'requiredFlag') }}</td></tr></tbody></table></div></main><footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>方案 #{{ planId || '-' }} · {{ rows.length }} 项</span></footer></MesLayout></template>
