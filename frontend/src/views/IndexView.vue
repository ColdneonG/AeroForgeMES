<script setup lang="ts">
import symbolmarkNegative from '@/assets/images/Symbolmark-negative.png'
import wordmark from '@/assets/images/Wordmark.png'
</script>

<template>
<main data-od-id="launcher-main" class="launcher-hero">
    <div class="hero-icon"><img :src="symbolmarkNegative" alt="AeroForge" width="72" height="72" style="display:block;transform:translateY(-4px);" /></div>
    <img :src="wordmark" alt="风擎工控 AeroForge MES" style="display:block;max-width:400px;margin:var(--space-3) auto var(--space-2);" />
    <h1 data-od-id="launcher-title" style="font-size:var(--text-h2);opacity:0.6;font-weight:400;">离散制造执行系统</h1>
    <p>离散制造执行系统原型。覆盖生产订单、车间作业、质量检验、设备管理、安灯异常、报表看板与系统管理。</p>
  </main>

  <div class="launcher-section">
    <h2>核心业务</h2>
  </div>
  <nav data-od-id="launcher-core" class="launcher-grid">
    <RouterLink to="/dashboard" class="launcher-card" data-od-id="launcher-card-dashboard">
      <span class="lc-module">工作台</span>
      <span class="lc-name">生产工作台</span>
      <span class="lc-desc">全局 KPI、订单状态、产线概览与实时预警</span>
    </RouterLink>
    <RouterLink to="/production-orders" class="launcher-card" data-od-id="launcher-card-orders">
      <span class="lc-module">生产管理</span>
      <span class="lc-name">生产订单</span>
      <span class="lc-desc">订单排程、状态跟踪、工单下达与物料齐套检查</span>
    </RouterLink>
    <RouterLink to="/production-order-detail" class="launcher-card" data-od-id="launcher-card-order-detail">
      <span class="lc-module">生产管理</span>
      <span class="lc-name">订单详情</span>
      <span class="lc-desc">订单全生命周期：BOM、工艺路线、进度追踪、质量记录</span>
    </RouterLink>
    <RouterLink to="/shopfloor" class="launcher-card" data-od-id="launcher-card-shopfloor">
      <span class="lc-module">车间作业</span>
      <span class="lc-name">车间看板</span>
      <span class="lc-desc">工位状态、在制品进度、人员分配与设备稼动</span>
    </RouterLink>
    <RouterLink to="/quality-inspection" class="launcher-card" data-od-id="launcher-card-quality">
      <span class="lc-module">质量管理</span>
      <span class="lc-name">质量检验</span>
      <span class="lc-desc">来料/过程/成品检验、缺陷登记与不合格品处理</span>
    </RouterLink>
    <RouterLink to="/equipment-monitor" class="launcher-card" data-od-id="launcher-card-equipment">
      <span class="lc-module">设备管理</span>
      <span class="lc-name">设备监控</span>
      <span class="lc-desc">设备状态实时监控、稼动率分析、维保提醒</span>
    </RouterLink>
    <RouterLink to="/andon/exceptions" class="launcher-card" data-od-id="launcher-card-andon">
      <span class="lc-module">安灯管理</span>
      <span class="lc-name">安灯异常</span>
      <span class="lc-desc">产线异常呼叫、班组长响应、恢复确认与关闭闭环</span>
    </RouterLink>
  </nav>

  <div class="launcher-section">
    <h2>生产管理详细</h2>
  </div>
  <nav data-od-id="launcher-production" class="launcher-grid">
    <RouterLink to="/production/dispatch-orders" class="launcher-card" data-od-id="launcher-card-dispatch">
      <span class="lc-module">生产管理</span>
      <span class="lc-name">派工单</span>
      <span class="lc-desc">工单→派工→任务链路，按产线/班次分配作业</span>
    </RouterLink>
    <RouterLink to="/production/tasks" class="launcher-card" data-od-id="launcher-card-tasks">
      <span class="lc-module">生产管理</span>
      <span class="lc-name">生产任务</span>
      <span class="lc-desc">任务领用、报工、暂停/求助、完工汇报</span>
    </RouterLink>
    <RouterLink to="/production/kitting" class="launcher-card" data-od-id="launcher-card-kitting">
      <span class="lc-module">生产管理</span>
      <span class="lc-name">齐套分析</span>
      <span class="lc-desc">BOM 物料齐套检查、缺料预警与预计到货追踪</span>
    </RouterLink>
  </nav>

  <div class="launcher-section">
    <h2>质量管理详细</h2>
  </div>
  <nav data-od-id="launcher-quality" class="launcher-grid">
    <RouterLink to="/quality/inspection-exec" class="launcher-card" data-od-id="launcher-card-inspexec">
      <span class="lc-module">质量管理</span>
      <span class="lc-name">检验执行</span>
      <span class="lc-desc">检验项目录入、量测数据采集、合格/让步/不合格判定</span>
    </RouterLink>
    <RouterLink to="/quality/defects" class="launcher-card" data-od-id="launcher-card-defects">
      <span class="lc-module">质量管理</span>
      <span class="lc-name">缺陷记录</span>
      <span class="lc-desc">缺陷登记、严重等级评定、处理方式与闭环管理</span>
    </RouterLink>
  </nav>

  <div class="launcher-section">
    <h2>报表与系统</h2>
  </div>
  <nav data-od-id="launcher-reports" class="launcher-grid">
    <RouterLink to="/reports" class="launcher-card" data-od-id="launcher-card-reports">
      <span class="lc-module">生产报表</span>
      <span class="lc-name">制造驾驶舱</span>
      <span class="lc-desc">日/周/月产量统计、不良率趋势、计件工资汇总</span>
    </RouterLink>
    <RouterLink to="/reports/output" class="launcher-card" data-od-id="launcher-card-output">
      <span class="lc-module">生产报表</span>
      <span class="lc-name">产量报表</span>
      <span class="lc-desc">按日/周/月/产品维度的产量、不良率与达成率分析</span>
    </RouterLink>
    <RouterLink to="/reports/metrics" class="launcher-card" data-od-id="launcher-card-metrics">
      <span class="lc-module">生产报表</span>
      <span class="lc-name">指标管理</span>
      <span class="lc-desc">指标定义、快照数据与历史趋势查询</span>
    </RouterLink>
    <RouterLink to="/system-users" class="launcher-card" data-od-id="launcher-card-users">
      <span class="lc-module">系统管理</span>
      <span class="lc-name">用户与权限</span>
      <span class="lc-desc">用户管理、角色分配、操作权限与登录审计</span>
    </RouterLink>
    <RouterLink to="/login" class="launcher-card" data-od-id="launcher-card-login">
      <span class="lc-module">入口</span>
      <span class="lc-name">登录页</span>
      <span class="lc-desc">AeroForge MES 身份认证入口</span>
    </RouterLink>
  </nav>

  <footer style="text-align:center; padding: var(--space-8) var(--space-5); font-size: var(--text-caption); opacity: 0.4; letter-spacing: 0.04em;">
    风擎工控 AeroForge MES · Tesla 设计系统 · 电风扇离散制造场景
  </footer>
</template>

<style>

    body { background: var(--surface); overflow-y: auto; }
  
</style>
