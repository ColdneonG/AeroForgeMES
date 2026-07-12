<script setup lang="ts">
import MesLayout from '@/layouts/MesLayout.vue'
</script>

<template>
<MesLayout active="kitting">

  

  <header class="app-header">
    <div class="header-breadcrumb">
      <span>生产管理</span> <span class="bc-sep">/</span> <span class="bc-current">缺料看板</span>
    </div>
    <div class="header-actions">
      <span class="badge badge-status-error" aria-label="活跃缺料 3 项">
        <span class="badge-dot"></span>3 项缺料
      </span>
      <span class="user-avatar">张</span>
    </div>
  </header>

  <main class="app-main" data-od-id="kboard-main">

    <h1 class="page-title" data-od-id="kboard-title">齐套缺料看板</h1>

    <!-- Summary banner -->
    <div class="kb-summary" data-od-id="kboard-summary" role="region" aria-label="缺料概览">
      <div class="kb-summary-stat">
        <span class="kbs-label">缺料项</span>
        <span class="kbs-value accent">3</span>
      </div>
      <div class="kb-summary-sep" aria-hidden="true"></div>
      <div class="kb-summary-stat">
        <span class="kbs-label">总缺口</span>
        <span class="kbs-value">570</span>
      </div>
      <div class="kb-summary-sep" aria-hidden="true"></div>
      <div class="kb-summary-stat">
        <span class="kbs-label">涉及工单</span>
        <span class="kbs-value" style="font-size:1.125rem;">2</span>
      </div>
      <div class="kb-summary-sep" aria-hidden="true"></div>
      <div class="kb-summary-stat">
        <span class="kbs-label">最早到货</span>
        <span class="kbs-value" style="font-size:1.125rem;">07-12</span>
      </div>
    </div>

    <!-- Shortage cards -->
    <div class="kb-grid" data-od-id="kboard-grid" role="list">

      <div class="card" data-od-id="kb-card-1" role="listitem" aria-label="缺料：彩色包装箱，缺 200 件">
        <div class="card-header">
          <h2 class="card-title">彩色包装箱</h2>
          <span class="badge badge-status-warn" style="font-size:0.6875rem;">PKG-FS40</span>
        </div>

        <div class="kb-card-meta">520×230×490mm · 关联 MO-0032</div>

        <div class="kb-card-body">
          <div>
            <div class="kb-stat-label">需求</div>
            <div class="kb-stat-num">500</div>
          </div>
          <div>
            <div class="kb-stat-label">库存</div>
            <div class="kb-stat-num">300</div>
          </div>
        </div>

        <!-- Gauge: stock / demand = 300/500 = 60% fill -->
        <div class="kb-gauge">
          <div class="kb-gauge-track">
            <div class="kb-gauge-fill critical" style="width:60%;" aria-hidden="true"></div>
          </div>
        </div>

        <div class="kb-shortfall">缺 200</div>
        <div class="kb-eta urgent"><span class="eta-dot" aria-hidden="true"></span>预计到货 07-12 · <span style="font-weight:700;">明天</span></div>
      </div>

      <div class="card" data-od-id="kb-card-2" role="listitem" aria-label="缺料：遥控接收模块，缺 220 件">
        <div class="card-header">
          <h2 class="card-title">遥控接收模块</h2>
          <span class="badge badge-status-warn" style="font-size:0.6875rem;">RCV-FS40</span>
        </div>

        <div class="kb-card-meta">433MHz 4通道 · 关联 MO-0030</div>

        <div class="kb-card-body">
          <div>
            <div class="kb-stat-label">需求</div>
            <div class="kb-stat-num">400</div>
          </div>
          <div>
            <div class="kb-stat-label">库存</div>
            <div class="kb-stat-num">180</div>
          </div>
        </div>

        <!-- Gauge: stock / demand = 180/400 = 45% fill -->
        <div class="kb-gauge">
          <div class="kb-gauge-track">
            <div class="kb-gauge-fill critical" style="width:45%;" aria-hidden="true"></div>
          </div>
        </div>

        <div class="kb-shortfall">缺 220</div>
        <div class="kb-eta soon"><span class="eta-dot" aria-hidden="true"></span>预计到货 07-13</div>
      </div>

      <div class="card" data-od-id="kb-card-3" role="listitem" aria-label="缺料：遥控器，缺 150 件">
        <div class="card-header">
          <h2 class="card-title">遥控器</h2>
          <span class="badge badge-status-warn" style="font-size:0.6875rem;">RMT-FS40</span>
        </div>

        <div class="kb-card-meta">CR2025 3键 · 关联 MO-0030</div>

        <div class="kb-card-body">
          <div>
            <div class="kb-stat-label">需求</div>
            <div class="kb-stat-num">400</div>
          </div>
          <div>
            <div class="kb-stat-label">库存</div>
            <div class="kb-stat-num">250</div>
          </div>
        </div>

        <!-- Gauge: stock / demand = 250/400 = 62.5% fill -->
        <div class="kb-gauge">
          <div class="kb-gauge-track">
            <div class="kb-gauge-fill critical" style="width:62.5%;" aria-hidden="true"></div>
          </div>
        </div>

        <div class="kb-shortfall">缺 150</div>
        <div class="kb-eta urgent"><span class="eta-dot" aria-hidden="true"></span>预计到货 07-12 · <span style="font-weight:700;">明天</span></div>
      </div>

    </div>

    <!-- Empty state (shown only when .kb-grid is empty) -->
    <div class="kb-empty" data-od-id="kboard-empty">
      <div class="kb-empty-icon">&#x2714;</div>
      <div style="font-weight:600;font-size:var(--text-h3);">当前无缺料项</div>
      <div style="font-size:var(--text-small);">所有工单物料齐套，生产可正常排程</div>
    </div>

  </main>

  <footer class="app-statusbar">
    <span class="statusbar-item"><span class="dot warn"></span>缺料 3 项</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item">涉及工单 MO-0032 · MO-0030</span>
    <span class="statusbar-sep">|</span>
    <span class="statusbar-item" style="margin-left:auto;">风擎工控 AeroForge MES v2.0 · 张工</span>
  </footer>

</MesLayout>
</template>

<style>

    /* ---- Kitting board — page-specific classes --------------------- */

    /* Summary banner */
    .kb-summary {
      display: flex;
      align-items: center;
      gap: var(--space-6);
      padding: var(--space-4) var(--space-5);
      background: var(--bg);
      border: var(--border-weight) solid var(--border);
      border-radius: var(--radius);
      margin-bottom: var(--space-5);
      flex-wrap: wrap;
    }
    .kb-summary-stat {
      display: flex;
      flex-direction: column;
      gap: var(--space-1);
    }
    .kb-summary-stat .kbs-label {
      font-size: var(--text-caption);
      color: #171a20;
      opacity: 0.55;
      letter-spacing: 0.04em;
      text-transform: uppercase;
      font-weight: 600;
    }
    .kb-summary-stat .kbs-value {
      font-family: var(--font-mono);
      font-size: 1.5rem;
      font-weight: 700;
      letter-spacing: -0.02em;
      line-height: 1.1;
    }
    .kb-summary-stat .kbs-value.accent { color: var(--accent); }
    .kb-summary-sep {
      width: 1px;
      height: 40px;
      background: var(--border);
      flex-shrink: 0;
    }

    /* Card grid — minmax auto-fills, single column below 768px */
    .kb-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: var(--space-4);
    }
    @media (max-width: 768px) {
      .kb-grid { grid-template-columns: 1fr; }
    }

    /* Card internals */
    .kb-card-meta {
      font-size: var(--text-caption);
      color: #171a20;
      opacity: 0.5;
      letter-spacing: 0.02em;
      margin-bottom: var(--space-4);
      font-family: var(--font-mono);
    }

    .kb-card-body {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: var(--space-4);
      margin-bottom: var(--space-4);
    }

    .kb-stat-label {
      font-size: var(--text-caption);
      font-weight: 600;
      letter-spacing: 0.04em;
      text-transform: uppercase;
      color: #171a20;
      opacity: 0.55;
      margin-bottom: var(--space-1);
    }

    .kb-stat-num {
      font-family: var(--font-mono);
      font-size: 1.75rem;
      font-weight: 700;
      letter-spacing: -0.02em;
      line-height: 1.1;
    }

    /* Progress bar — demand vs stock */
    .kb-gauge {
      margin-bottom: var(--space-3);
    }
    .kb-gauge-track {
      height: 8px;
      background: var(--muted);
      border-radius: 4px;
      overflow: hidden;
    }
    .kb-gauge-fill {
      height: 100%;
      border-radius: 4px;
      background: var(--fg);
      opacity: 0.35;
      transition: width 400ms var(--easing-standard, ease);
    }
    .kb-gauge-fill.critical { background: var(--accent); opacity: 0.55; }

    .kb-shortfall {
      font-family: var(--font-mono);
      font-size: 1.5rem;
      font-weight: 700;
      letter-spacing: -0.01em;
      line-height: 1.1;
      color: var(--accent);
      margin-bottom: var(--space-2);
    }

    .kb-eta {
      font-size: var(--text-caption);
      letter-spacing: 0.02em;
      display: flex;
      align-items: center;
      gap: var(--space-2);
    }
    .kb-eta .eta-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      flex-shrink: 0;
    }
    .kb-eta.urgent { color: var(--accent); font-weight: 600; }
    .kb-eta.urgent .eta-dot { background: var(--accent); }
    .kb-eta.soon   { color: #171a20; opacity: 0.6; }
    .kb-eta.soon .eta-dot   { background: var(--fg); opacity: 0.3; }
    .kb-eta.normal { color: #171a20; opacity: 0.45; }
    .kb-eta.normal .eta-dot { background: var(--border); }

    /* Empty state (hidden when cards present) */
    .kb-empty {
      display: none;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: var(--space-10) var(--space-5);
      text-align: center;
      gap: var(--space-3);
      color: #171a20;
      opacity: 0.45;
    }
    .kb-empty .kb-empty-icon {
      font-size: 2.5rem;
      line-height: 1;
      margin-bottom: var(--space-2);
    }
    .kb-grid:empty ~ .kb-empty { display: flex; }
  
</style>
