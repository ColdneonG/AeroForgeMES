<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
const clock = ref(new Date().toTimeString().slice(0, 8))
let clockTimer: number | undefined
onMounted(() => { clockTimer = window.setInterval(() => { clock.value = new Date().toTimeString().slice(0, 8) }, 1000) })
onBeforeUnmount(() => { if (clockTimer) window.clearInterval(clockTimer) })
</script>

<template>
<div data-od-id="ccenter">
  <header class="cc-header">
    <RouterLink to="/dashboard" class="cc-back-btn" title="返回工作台" aria-label="返回工作台">←</RouterLink>
    <div><h1>风擎工控 · 制造控制中心</h1><span style="opacity:0.5;">AeroForge MES · 2026-07-11 白班</span></div>
    <div class="cc-clock" id="ccClock">{{ clock }}</div class="cc-clock" >
  </header>
  <div class="cc-grid">
    <div class="cc-card"><h3>今日产量</h3><div class="cc-big">1,286</div><div class="cc-label">台 · 目标 1,500 · 达成 85.7%</div><div class="cc-bar"><div class="cc-bar-fill" style="width:85.7%"></div></div></div>
    <div class="cc-card"><h3>一次合格率</h3><div class="cc-big">97.6<span style="font-size:0.5em">%</span></div><div class="cc-label">目标 ≥98.0% · ↓0.3%</div><div class="cc-bar"><div class="cc-bar-fill" style="width:97.6%"></div></div></div>
    <div class="cc-card"><h3>设备 OEE</h3><div class="cc-big">84.3<span style="font-size:0.5em">%</span></div><div class="cc-label">目标 ≥85.0% · ↑2.1%</div><div class="cc-bar"><div class="cc-bar-fill" style="width:84.3%"></div></div></div>
    <div class="cc-card"><h3>安灯活跃</h3><div class="cc-big" style="color:var(--accent);">3</div><div class="cc-label">未关闭异常 · 总装线 A×2 绕线×1</div></div>

    <div class="cc-card _full"><h3>产线实时状态</h3><div class="cc-flex">
      <div><span class="form-label">总装线 A</span><br><span style="font-weight:600;">FS-40-B3 · MO-0032</span><br><span style="font-size:var(--text-small);opacity:0.6;">360/500 · 72% · 节拍 28s</span><div class="cc-bar"><div class="cc-bar-fill" style="width:72%"></div></div></div>
      <div><span class="form-label">总装线 B</span><br><span style="font-weight:600;">FT-30-A1 · MO-0033</span><br><span style="font-size:var(--text-small);opacity:0.6;">360/800 · 45% · 节拍 22s</span><div class="cc-bar"><div class="cc-bar-fill" style="width:45%"></div></div></div>
      <div><span class="form-label">总装线 C</span><br><span style="font-weight:600;opacity:0.5;">换模中</span><br><span style="font-size:var(--text-small);opacity:0.5;">下一工单 MO-0035 待下达</span><div class="cc-bar"><div class="cc-bar-fill" style="width:0%;background:var(--muted)"></div></div></div>
    </div></div>

    <div class="cc-card _full"><h3>生产订单进度</h3>
      <table class="cc-table"><thead><tr><th>工单号</th><th>产品</th><th>计划</th><th>完工</th><th>进度</th><th>产线</th><th>状态</th></tr></thead><tbody>
        <tr><td class="cell-mono">MO-0032</td><td>FS-40-B3 落地扇</td><td>500</td><td>360</td><td><div class="cc-bar" style="width:120px;display:inline-block;vertical-align:middle;"><div class="cc-bar-fill" style="width:72%"></div></div> 72%</td><td>总装线 A</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>生产中</span></td></tr>
        <tr><td class="cell-mono">MO-0033</td><td>FT-30-A1 台扇</td><td>800</td><td>360</td><td><div class="cc-bar" style="width:120px;display:inline-block;vertical-align:middle;"><div class="cc-bar-fill" style="width:45%"></div></div> 45%</td><td>总装线 B</td><td><span class="badge badge-status-ok"><span class="badge-dot"></span>生产中</span></td></tr>
        <tr><td class="cell-mono">MO-0031</td><td>FS-35-W2 壁扇</td><td>300</td><td>282</td><td><div class="cc-bar" style="width:120px;display:inline-block;vertical-align:middle;"><div class="cc-bar-fill" style="width:94%"></div></div> 94%</td><td>总装线 A</td><td><span class="badge badge-status-warn"><span class="badge-dot"></span>待质检</span></td></tr>
        <tr><td class="cell-mono">MO-0030</td><td>FS-40-R1 遥控款</td><td>400</td><td>112</td><td><div class="cc-bar" style="width:120px;display:inline-block;vertical-align:middle;"><div class="cc-bar-fill" style="width:28%"></div></div> 28%</td><td>总装线 A</td><td><span class="badge badge-status-info"><span class="badge-dot"></span>物料等待</span></td></tr>
      </tbody></table>
    </div>

    <div class="cc-card"><h3>设备异常 TOP3</h3>
      <div style="font-size:var(--text-small);"><div style="display:flex;justify-content:space-between;padding:var(--space-2) 0;border-bottom:1px solid var(--muted);"><span>注塑机 #2 · 模温过高</span><span class="badge badge-status-error"><span class="badge-dot"></span>严重</span></div>
      <div style="display:flex;justify-content:space-between;padding:var(--space-2) 0;border-bottom:1px solid var(--muted);"><span>绕线机 #3 · 张力异常</span><span class="badge badge-status-warn"><span class="badge-dot"></span>警告</span></div>
      <div style="display:flex;justify-content:space-between;padding:var(--space-2) 0;"><span>锁螺丝机 #2 · 换模中</span><span class="badge badge-status-info"><span class="badge-dot"></span>信息</span></div></div>
    </div>
    <div class="cc-card"><h3>今日产值</h3><div class="cc-big" style="font-size:2rem;">¥128,600</div><div class="cc-label">按出厂价 ¥100/台 · 人均 15.0 台</div></div>
    <div class="cc-card"><h3>出勤统计</h3><div class="cc-big">86</div><div class="cc-label">在岗 / 88 在册 · 出勤率 97.7%</div></div>
    <div class="cc-card"><h3>本月累计</h3><div class="cc-big">18,240</div><div class="cc-label">台 · 目标 45,000 · 达成 40.5% · 工作日 11/26</div></div>
  </div>
</div>
</template>

<style>
body{overflow:auto;background:var(--fg)}.cc-header{background:var(--fg);color:#fff;padding:var(--space-5) var(--space-6);display:flex;justify-content:space-between;align-items:center}.cc-header h1{font-family:var(--font-display);font-size:2rem;font-weight:700;letter-spacing:-0.02em}.cc-back-btn{color:rgba(255,255,255,0.25);text-decoration:none;font-size:1.4rem;margin-right:var(--space-4);transition:color .2s;line-height:1;flex-shrink:0}.cc-back-btn:hover{color:rgba(255,255,255,0.6)}.cc-header .cc-clock{font-family:var(--font-mono);font-size:3rem;font-weight:700}.cc-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:var(--space-4);padding:var(--space-5);max-width:1600px;margin:0 auto}.cc-card{background:var(--bg);border-radius:var(--radius);padding:var(--space-4) var(--space-5)}._full{grid-column:span 2}._wide{grid-column:span 4}.cc-card h3{font-size:var(--text-caption);letter-spacing:0.08em;text-transform:uppercase;color:var(--fg);opacity:0.5;margin-bottom:var(--space-3)}.cc-big{font-family:var(--font-mono);font-size:2.5rem;font-weight:700;letter-spacing:-0.02em;line-height:1.1}.cc-label{font-size:var(--text-caption);opacity:0.5;margin-top:var(--space-1)}.cc-flex{display:flex;gap:var(--space-4);flex-wrap:wrap}.cc-flex>*{flex:1;min-width:120px}.cc-bar{height:8px;background:var(--muted);border-radius:4px;margin-top:var(--space-2);overflow:hidden}.cc-bar-fill{height:100%;border-radius:4px;background:var(--fg);opacity:0.55}.cc-bar-fill._warn{opacity:0.75}.cc-table{width:100%;font-size:var(--text-caption);border-collapse:collapse}.cc-table td{padding:var(--space-1) var(--space-2);border-bottom:1px solid var(--muted)}.cc-table tr:last-child td{border-bottom:none}
</style>
