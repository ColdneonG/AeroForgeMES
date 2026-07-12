<script setup lang="ts">
import { ref } from 'vue'
import MesLayout from '@/layouts/MesLayout.vue'
import { useRouter } from 'vue-router'
const router = useRouter()
const activeTab = ref('records')
</script>

<template>
<MesLayout active="barcode-list">
  
  <header class="app-header">
    <div class="header-breadcrumb"><span>条码与追溯</span> <span class="bc-sep">/</span> <span class="bc-current">条码管理</span></div>
    <div class="header-actions">
      <RouterLink to="/barcode/generate" class="btn btn-primary btn-sm">+ 生成条码</RouterLink>
      <span class="user-avatar">张</span>
    </div>
  </header>
  <main class="app-main" data-od-id="barcode-main">
    <h1 class="page-title">条码管理</h1>
    <div class="tabs" data-od-id="barcode-tabs">
      <span class="tab-item" @click="activeTab = 'records'" :class="{ active: activeTab === 'records' }">条码记录</span>
      <span class="tab-item" @click="activeTab = 'generate'" :class="{ active: activeTab === 'generate' }">生成条码</span>
      <span class="tab-item" @click="router.push('/barcode/scan')">扫码终端</span>
    </div>
    <div id="tab-records" v-show="activeTab === 'records'">
      <div class="card mb-5">
        <div class="search-bar" style="margin-bottom:0;">
          <div class="form-group" style="min-width:180px;"><label class="form-label">条码值</label><input class="form-input" type="text" placeholder="搜索条码..." /></div>
          <div class="form-group" style="min-width:140px;"><label class="form-label">条码类型</label><select class="form-select"><option>全部</option><option>产品条码</option><option>物料条码</option><option>批次条码</option><option>箱码</option></select></div>
          <div class="form-group" style="min-width:130px;"><label class="form-label">状态</label><select class="form-select"><option>全部</option><option>已生成</option><option>已打印</option><option>已激活</option><option>已关联</option></select></div>
          <div class="form-group" style="min-width:160px;"><label class="form-label">关联工单</label><input class="form-input" type="text" placeholder="工单号..." /></div>
          <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">查询</button>
        </div>
      </div>
      <div class="data-table-wrap">
        <table class="data-table">
          <thead><tr><th>条码值</th><th>类型</th><th>物料/产品</th><th>关联工单</th><th>批次</th><th>生成时间</th><th>打印次数</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr><td class="cell-mono">FS40B3071100001</td><td>产品条码</td><td>FS-40-B3 落地扇</td><td>MO-0032</td><td>BAT-0711A</td><td>07-11 08:30</td><td>1</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>已激活</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">打印</button><button class="btn btn-ghost btn-sm">追溯</button></td></tr>
            <tr><td class="cell-mono">FS40B3071100002</td><td>产品条码</td><td>FS-40-B3 落地扇</td><td>MO-0032</td><td>BAT-0711A</td><td>07-11 08:30</td><td>1</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>已激活</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">打印</button><button class="btn btn-ghost btn-sm">追溯</button></td></tr>
            <tr><td class="cell-mono">MTR4000750001</td><td>物料条码</td><td>异步电动机 75W</td><td>MO-0032</td><td>BAT-0710C</td><td>07-10 14:00</td><td>2</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>已关联</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">打印</button><button class="btn btn-ghost btn-sm">追溯</button></td></tr>
            <tr><td class="cell-mono">FT30A1071100100</td><td>产品条码</td><td>FT-30-A1 台扇</td><td>MO-0033</td><td>BAT-0711B</td><td>07-11 08:35</td><td>0</td><td><span class="badge badge-status-info"><span class="badge-dot"></span>已生成</span></td><td class="cell-actions"><button class="btn btn-primary btn-sm">打印</button></td></tr>
            <tr><td class="cell-mono">BLD4000300001</td><td>物料条码</td><td>ABS 扇叶 400mm</td><td>MO-0032</td><td>BAT-0710A</td><td>07-10 10:00</td><td>1</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>已关联</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">打印</button><button class="btn btn-ghost btn-sm">追溯</button></td></tr>
          </tbody>
        </table>
        <div class="pagination"><span>共 1,286 条</span><div class="page-btns"><span class="page-btn">‹</span><span class="page-btn active">1</span><span class="page-btn">2</span><span class="page-btn">3</span><span class="page-btn">›</span></div></div>
      </div>
    </div>
    <div id="tab-generate"  v-show="activeTab === 'generate'">
      <div class="card" style="max-width:600px;">
        <div class="card-header"><h2 class="card-title">生成条码</h2><span class="text-muted">对接 POST /api/production/barcodes/generate</span></div>
        <div class="form-group mb-4"><label class="form-label">应用规则</label><select class="form-select"><option>FS-40 落地扇产品条码规则</option><option>FT-30 台扇产品条码规则</option><option>异步电动机物料条码规则</option></select></div>
        <div class="form-group mb-4"><label class="form-label">生成数量</label><input class="form-input" type="number" value="100" min="1" max="10000" style="width:120px;" /></div>
        <div class="form-group mb-4"><label class="form-label">起始流水号</label><input class="form-input" type="number" value="1" style="width:120px;" /></div>
        <button class="btn btn-primary">生成条码</button>
      </div>
    </div>
  </main>
  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span>
    <span class="statusbar-item">条码管理 · 1,286 条记录</span><span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span>
  </footer>
</MesLayout>
</template>
