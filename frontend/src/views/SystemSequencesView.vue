<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
import { usePageInteractions } from '@/composables/usePageInteractions'
const { notify } = usePageInteractions()
</script>

<template>
<MesLayout active="sys-sequences">
  
  <header class="app-header"><div class="header-breadcrumb"><span>数据与系统</span> <span class="bc-sep">/</span> <span class="bc-current">编号序列</span></div><div class="header-actions"><button class="btn btn-primary btn-sm" @click="notify('新建序列 — POST /api/system/sequences/next')">+ 新建规则</button><span class="user-avatar">陈</span></div></header>
  <main class="app-main" data-od-id="seq-main">
    <h1 class="page-title">编号序列管理</h1>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>序列编码</th><th>序列名称</th><th>前缀</th><th>日期格式</th><th>流水号长度</th><th>当前值</th><th>重置周期</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr><td class="cell-mono">SEQ-MO</td><td>生产工单号</td><td>MO-</td><td>yyyyMMdd</td><td>4</td><td>0034</td><td>每日</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button><button class="btn btn-ghost btn-sm" @click="notify('POST /api/system/sequences/next')">获取下一号</button></td></tr>
        <tr><td class="cell-mono">SEQ-DO</td><td>派工单号</td><td>DO-</td><td>yyMMdd</td><td>4</td><td>0054</td><td>每日</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">SEQ-TK</td><td>任务编号</td><td>TK-</td><td>yyMMdd</td><td>4</td><td>0186</td><td>每日</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">SEQ-QC</td><td>检验单号</td><td>QC-</td><td>yyMMdd</td><td>4</td><td>0153</td><td>每日</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">SEQ-DEF</td><td>缺陷编号</td><td>DEF-</td><td>yyMMdd</td><td>4</td><td>0042</td><td>每日</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
        <tr><td class="cell-mono">SEQ-SOP</td><td>SOP文档编号</td><td>SOP-</td><td>—</td><td>3</td><td>005</td><td>不重置</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>启用</span></td><td class="cell-actions"><button class="btn btn-ghost btn-sm">编辑</button></td></tr>
      </tbody>
    </table></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">编号序列 · 8 条规则 · 全部启用</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 陈管理员</span></footer>
</MesLayout>
</template>
