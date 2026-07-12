<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="sys-audit">
  
  <header class="app-header"><div class="header-breadcrumb"><span>数据与系统</span> <span class="bc-sep">/</span> <span class="bc-current">操作日志</span></div><div class="header-actions"><button class="btn btn-secondary btn-sm">导出日志</button><span class="user-avatar">陈</span></div></header>
  <main class="app-main" data-od-id="audit-main">
    <h1 class="page-title">操作审计日志</h1>
    <div class="card mb-5"><div class="search-bar" style="margin-bottom:0;">
      <div class="form-group" style="min-width:160px;"><label class="form-label">操作人</label><input class="form-input" type="text" placeholder="搜索用户名..." /></div>
      <div class="form-group" style="min-width:140px;"><label class="form-label">操作类型</label><select class="form-select"><option>全部</option><option>创建</option><option>修改</option><option>删除</option><option>状态变更</option><option>登录</option></select></div>
      <div class="form-group" style="min-width:140px;"><label class="form-label">时间范围</label><input class="form-input" type="date" value="2026-07-11" /></div>
      <button class="btn btn-secondary btn-sm" style="align-self:flex-end;">查询</button>
    </div></div>
    <div class="data-table-wrap"><table class="data-table">
      <thead><tr><th>日志编号</th><th>操作人</th><th>操作类型</th><th>操作对象</th><th>对象编号</th><th>IP 地址</th><th>操作时间</th><th>详情</th></tr></thead>
      <tbody>
        <tr><td class="cell-mono">LOG-0711-0158</td><td>张建国</td><td>状态变更</td><td>生产工单</td><td>MO-0032</td><td>192.168.1.100</td><td>09:32:15</td><td>下达工单 MO-0032：草稿 → 已下达</td></tr>
        <tr><td class="cell-mono">LOG-0711-0157</td><td>李明辉</td><td>状态变更</td><td>安灯异常</td><td>EX-0711-0019</td><td>192.168.1.50</td><td>09:18:42</td><td>响应安灯 EX-0019：待响应 → 处理中</td></tr>
        <tr><td class="cell-mono">LOG-0711-0156</td><td>李检验</td><td>创建</td><td>检验单</td><td>QC-0711-0152</td><td>192.168.1.80</td><td>09:15:00</td><td>创建 IPQC 检验单，关联 MO-0032</td></tr>
        <tr><td class="cell-mono">LOG-0711-0155</td><td>陈晓燕</td><td>登录</td><td>系统</td><td>—</td><td>192.168.1.90</td><td>09:05:22</td><td>管理员登录成功</td></tr>
        <tr><td class="cell-mono">LOG-0711-0154</td><td>张建国</td><td>修改</td><td>生产工单</td><td>MO-0030</td><td>192.168.1.100</td><td>08:50:08</td><td>修改工单 MO-0030 计划日期</td></tr>
      </tbody>
    </table><div class="pagination"><span>共 158 条</span><div class="page-btns"><span class="page-btn">‹</span><span class="page-btn active">1</span><span class="page-btn">2</span><span class="page-btn">3</span><span class="page-btn">›</span></div></div></div>
  </main>
  <footer class="app-statusbar"><span class="statusbar-item"><span class="dot ok"></span>系统正常</span><span class="statusbar-sep">|</span><span class="statusbar-item">操作日志 · 158 条记录</span><span class="statusbar-sep">|</span><span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 陈管理员</span></footer>
</MesLayout>
</template>
