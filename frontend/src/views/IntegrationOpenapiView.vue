<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="erp">
  
  <header class="app-header"><div class="header-breadcrumb"><span>集成管理</span> <span class="bc-sep">/</span> <span class="bc-current">OpenAPI</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="notify('注册应用 — 对接 /api/openapi')">+ 注册应用</button><span class="user-avatar">陈</span></div></header>
  <main class="app-main" data-od-id="oapi-main">
    <h1 class="page-title">OpenAPI 管理</h1>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>应用名称</th><th>AppKey</th><th>接入范围</th><th>调用频率限制</th><th>今日调用</th><th>创建时间</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td>ERP数据同步</td><td class="cell-mono">ak-3f8a2c1b...</td><td>工单/任务/完工</td><td>1000 次/小时</td><td>342</td><td>2026-01-15</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button><button class="btn btn-ghost btn-sm">密钥</button></td></tr>
        <tr><td>MES看板大屏</td><td class="cell-mono">ak-7d1e9f4a...</td><td>报表/指标/看板</td><td>500 次/小时</td><td>128</td><td>2026-03-20</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td>第三方WMS</td><td class="cell-mono">ak-2b5c8e0d...</td><td>条码/库存</td><td>2000 次/小时</td><td>0</td><td>2026-06-01</td><td><span class="badge badge-status-info"><span class="badge-dot"></span>未激活</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button><button class="btn btn-primary btn-sm">激活</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">OpenAPI · 3 个应用 · 今日调用 470 次</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 陈管理员</span></footer>
</MesLayout>
</template>
