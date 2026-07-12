<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { scanValue, scanResult, doScan } = usePageInteractions()
</script>

<template>
<MesLayout active="scan">
  
  <header class="app-header">
    <div class="header-breadcrumb"><span>条码与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">扫码终端</span></div>
    <div class="header-actions"><span class="user-avatar">张</span></div>
  </header>
  <main class="app-main" data-od-id="scan-main">
    <h1 class="page-title">扫码终端</h1>
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:var(--space-5);">
      <section class="card" data-od-id="scan-input">
        <div class="card-header"><h2 class="card-title">扫码输入</h2></div>
        <div class="form-group mb-4"><label class="form-label">条码值</label>
          <div class="flex gap-2"><input class="form-input" id="scanInput" type="text" placeholder="扫描或手动输入条码..." style="flex:1;font-family:var(--font-mono);font-size:1.2rem;" autofocus  v-model="scanValue" @keydown.enter="doScan" /><button class="btn btn-primary" @click="doScan">解析</button></div>
        </div>
        <div class="text-muted" style="font-size:var(--text-caption);">对接 POST /api/production/barcodes/scan · 支持扫码枪自动回车</div>
      </section>
      <section class="card" data-od-id="scan-result" id="scanResult" v-show="scanResult.visible">
        <div class="card-header"><h2 class="card-title">解析结果</h2></div>
        <table class="data-table" style="border:none;">
          <tbody>
            <tr><td style="opacity:0.5;width:100px;">条码值</td><td class="cell-mono" id="rBarcode">{{ scanResult.barcode }}</td class="cell-mono" ></tr>
            <tr><td style="opacity:0.5;">类型</td><td id="rType">{{ scanResult.type }}</td ></tr>
            <tr><td style="opacity:0.5;">物料/产品</td><td id="rItem">{{ scanResult.item }}</td ></tr>
            <tr><td style="opacity:0.5;">关联工单</td><td class="cell-mono" id="rOrder">{{ scanResult.order }}</td class="cell-mono" ></tr>
            <tr><td style="opacity:0.5;">批次</td><td id="rBatch">{{ scanResult.batch }}</td ></tr>
            <tr><td style="opacity:0.5;">状态</td><td id="rStatus"><span class="badge badge-status-ok"><span class="badge-dot"></span>{{ scanResult.status }}</span></td ></tr>
          </tbody>
        </table>
        <div class="mt-4 flex gap-2"><button class="btn btn-primary btn-sm">关联工单</button><button class="btn btn-secondary btn-sm">打印条码</button></div>
      </section>
    </div>
  </main>
  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span>
    <span class="statusbar-item">扫码终端 · 等待输入</span><span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0</span>
  </footer>
</MesLayout>
</template>
