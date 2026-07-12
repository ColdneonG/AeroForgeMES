<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="sop-docs">
  
  <header class="app-header">
    <div class="header-breadcrumb"><span>电子SOP</span> <span class="bc-sep">/</span> <span class="bc-current">SOP文档</span></div>
    <div class="header-actions">
      <button class="btn btn-primary btn-sm" @click="notify('上传SOP — POST /api/production/sop/documents')">+ 上传文档</button>
      <span class="user-avatar">张</span>
    </div>
  </header>
  <main class="app-main" data-od-id="sop-main">
    <h1 class="page-title">SOP文档管理</h1>
    <div class="card mb-5">
      <div class="search-bar" style="margin-bottom:0;">
        <div class="form-group" style="min-width:180px;"><label class="form-label">文档名称</label><input class="form-input" type="text" placeholder="搜索..." /></div>
        <div class="form-group" style="min-width:140px;"><label class="form-label">产品</label><select class="form-select"><option>全部</option><option>FS-40 落地扇</option><option>FT-30 台扇</option><option>FS-35 壁扇</option></select></div>
        <div class="form-group" style="min-width:140px;"><label class="form-label">工序</label><select class="form-select"><option>全部</option><option>定子绕线</option><option>转子压装</option><option>总装配</option><option>出厂检验</option><option>包装入库</option></select></div>
        <div class="form-group" style="min-width:130px;"><label class="form-label">状态</label><select class="form-select"><option>全部</option><option>草稿</option><option>已发布</option><option>已归档</option></select></div>
        <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">筛选</button>
      </div>
    </div>
    <div class="data-table-wrap">
      <table class="data-table">
        <thead><tr><th>文档编号</th><th>文档名称</th><th>产品/工序</th><th>版本</th><th>作者</th><th>更新时间</th><th>附件数</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr><td class="cell-mono">SOP-FS40-001</td><td>FS-40 落地扇总装作业指导书</td><td>FS-40-B3 / 总装配</td><td>v3.2</td><td>张工</td><td>07-10</td><td>5</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot"></span>已发布</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">查看</button><button class="btn btn-ghost btn-sm" @click="notify('下发任务 — POST /api/production/sop/tasks')">下发</button></td></tr>
          <tr><td class="cell-mono">SOP-FS40-002</td><td>FS-40 定子绕线作业指导书</td><td>FS-40-B3 / 绕线</td><td>v2.1</td><td>李组长</td><td>07-08</td><td>3</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot"></span>已发布</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">查看</button><button class="btn btn-ghost btn-sm" @click="notify('下发任务')">下发</button></td></tr>
          <tr><td class="cell-mono">SOP-FT30-001</td><td>FT-30 台扇总装作业指导书</td><td>FT-30-A1 / 总装配</td><td>v2.3</td><td>张工</td><td>07-05</td><td>4</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot"></span>已发布</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">查看</button><button class="btn btn-ghost btn-sm">下发</button></td></tr>
          <tr><td class="cell-mono">SOP-FS35-001</td><td>FS-35 壁扇通电测试规范</td><td>FS-35-W2 / 出厂检验</td><td>v1.8</td><td>王质检</td><td>06-28</td><td>2</td>
            <td><span class="badge badge-status-ok"><span class="badge-dot"></span>已发布</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">查看</button><button class="btn btn-ghost btn-sm">下发</button></td></tr>
          <tr><td class="cell-mono">SOP-FS40-003</td><td>FS-40 遥控款组装特别说明</td><td>FS-40-R1 / 总装配</td><td>v1.0</td><td>张工</td><td>07-11</td><td>1</td>
            <td><span class="badge badge-status-info"><span class="badge-dot"></span>草稿</span></td>
            <td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button><button class="btn btn-primary btn-sm">发布</button></td></tr>
        </tbody>
      </table>
      <div class="pagination"><span>共 8 份文档</span><div class="page-btns"><span class="page-btn active">1</span><span class="page-btn">›</span></div></div>
    </div>
  </main>
  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span>
    <span class="statusbar-item">SOP文档 · 8 份 · 已发布 7 · 草稿 1</span><span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span>
  </footer>
</MesLayout>
</template>
