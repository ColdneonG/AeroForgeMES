<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import * as THREE from 'three'
import { get } from '@/api/client'
import { getAndonExceptions } from '@/api/production'
import { formatDisplayNumber, formatPercent } from '@/utils/number'
import pageBackground from '@/assets/images/ioftv/pageBg.png'
import topBackground from '@/assets/images/ioftv/top.png'
import lightBeam from '@/assets/images/ioftv/guang.png'
import headerBar from '@/assets/images/ioftv/juxing1.png'
import panelCorner from '@/assets/images/ioftv/left_top_lan.png'
import leftSlash from '@/assets/images/ioftv/xiezuo.png'
import rightSlash from '@/assets/images/ioftv/xieyou.png'
import kpiIcon1 from '@/assets/images/ioftv/center-details-data1.png'
import kpiIcon2 from '@/assets/images/ioftv/center-details-data2.png'
import kpiIcon3 from '@/assets/images/ioftv/center-details-data3.png'
import kpiIcon4 from '@/assets/images/ioftv/center-details-data4.png'

type BoardItem = Record<string, unknown>
type ControlCenterBoard = {
  outputTotal?: number
  kpis?: BoardItem[]
  trend?: number[]
  planTrend?: number[]
  lines?: BoardItem[]
  alerts?: BoardItem[]
  workOrders?: BoardItem[]
}
const board = ref<ControlCenterBoard>({})
const screenRoot = ref<HTMLElement>()
const lineBoard = ref<BoardItem[]>([])
const workshopBoard = ref<BoardItem[]>([])
const andonRecords = ref<BoardItem[]>([])
const error = ref('')
const now = ref(new Date())
let clockTimer = 0
let refreshTimer = 0
let trendTimer = 0
const activeTrendIndex = ref(0)
let destroyThreeHover = () => {}

const number = (value: unknown) => {
  const result = Number(value)
  return Number.isFinite(result) ? result : 0
}
const text = (value: unknown, fallback = '—') => String(value ?? '').trim() || fallback
const percent = formatPercent
const dateText = computed(() => now.value.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'short' }))
const timeText = computed(() => now.value.toLocaleTimeString('zh-CN', { hour12: false }))
const trendValues = computed(() => board.value.trend?.length ? board.value.trend.map(number) : [0, 0, 0, 0, 0, 0, 0])
const planTrendValues = computed(() => board.value.planTrend?.length === trendValues.value.length ? board.value.planTrend.map(number) : trendValues.value.map((value) => Math.min(100, Math.max(value, 78))))
const trendPoints = computed(() => {
  const values = trendValues.value
  const width = 475
  const height = 180
  const step = width / Math.max(values.length - 1, 1)
  return values.map((value, index) => `${index * step},${height - number(value) * 1.45}`).join(' ')
})
const trendArea = computed(() => `0,180 ${trendPoints.value} 475,180`)
const planTrendPoints = computed(() => planTrendValues.value.map((value, index) => `${index * (475 / Math.max(planTrendValues.value.length - 1, 1))},${180 - value * 1.45}`).join(' '))
const trendCoordinates = computed(() => trendValues.value.map((value, index) => ({ x: index * (475 / Math.max(trendValues.value.length - 1, 1)), y: 180 - value * 1.45, value })))
const forecastTrendPoints = computed(() => {
  const points = trendCoordinates.value
  const last = points.at(-1) || { x: 475, y: 180, value: 0 }
  const previous = points.at(-2) || last
  return `${last.x},${last.y} 520,${Math.max(12, Math.min(176, last.y - (previous.y - last.y) * .55))}`
})
const trendTooltip = computed(() => {
  const index = Math.min(activeTrendIndex.value, trendValues.value.length - 1)
  return { index, actual: trendValues.value[index] || 0, plan: planTrendValues.value[index] || 0, point: trendCoordinates.value[index] || { x: 0, y: 180 } }
})
const outputTotal = computed(() => number(board.value.outputTotal).toLocaleString('zh-CN'))
const lines = computed(() => lineBoard.value.length ? lineBoard.value : (board.value.lines || []))
const kpis = computed(() => board.value.kpis || [])
const alerts = computed(() => board.value.alerts || [])
const workOrders = computed(() => board.value.workOrders || [])
const rollingAlerts = computed(() => alerts.value.length > 2 ? [...alerts.value, ...alerts.value] : alerts.value)
const lineRanking = computed(() => lines.value
  .map((line) => ({ name: text(line.lineName), value: Math.min(Math.max(number(line.rate), 0), 100) }))
  .sort((a, b) => b.value - a.value)
  .slice(0, 4))
const trendPeak = computed(() => {
  const values = trendValues.value
  const value = Math.max(...values.map(number))
  const index = values.findIndex((item) => number(item) === value)
  const x = (475 / Math.max(values.length - 1, 1)) * Math.max(index, 0)
  return { value, x, y: 180 - value * 1.45 }
})
const kpiIcons = [kpiIcon1, kpiIcon2, kpiIcon3, kpiIcon4]
const oeeValue = computed(() => {
  const item = kpis.value.find((kpi) => text(kpi.label).toUpperCase().includes('OEE'))
  return Math.min(Math.max(number(item?.value), 0), 100)
})
const topologyDefinitions = [
  { name: '物料上料', code: 'FEED-01', icon: 'feed' },
  { name: '电机装配', code: 'ASM-02', icon: 'motor' },
  { name: '扇叶装配', code: 'ASM-03', icon: 'blade' },
  { name: '性能检测', code: 'TEST-04', icon: 'test' },
  { name: '包装入库', code: 'PACK-05', icon: 'pack' },
]
const topologyStatus = (item: BoardItem) => {
  const status = text(item.equipment ?? item.status, 'RUNNING').toUpperCase()
  if (/(ALARM|FAULT|WARN|告警|故障)/.test(status)) return 'alert'
  if (/(IDLE|STANDBY|OFFLINE|待机|停机)/.test(status)) return 'idle'
  return 'running'
}
const topologyStations = computed(() => topologyDefinitions.map((definition, index) => {
  const source = lines.value[index] || lines.value[index % Math.max(lines.value.length, 1)] || {}
  const output = number(source.outputQty ?? source.completedQty ?? source.output)
  const rate = Math.min(Math.max(number(source.rate ?? source.completionRate), 0), 100)
  return {
    ...definition,
    status: topologyStatus(source),
    workOrder: text(source.workOrderNo, `WO-${String(index + 1).padStart(3, '0')}`),
    output: output || Math.round(rate * 12.5),
    cycle: number(source.cycleTime ?? source.taktTime ?? source.tempo) || 18 + index * 2,
    rate,
  }
}))
const isClosedAndon = (record: BoardItem) => /CLOSED|RESOLVED|已关闭|已恢复/i.test(text(record.status, ''))
const recoveredAlerts = computed(() => andonRecords.value.filter(isClosedAndon)
  .sort((left, right) => new Date(String(right.closedAt ?? 0)).getTime() - new Date(String(left.closedAt ?? 0)).getTime())
  .slice(0, 3))
const alertTrend = computed(() => {
  const bins = Array.from({ length: 12 }, () => 0)
  const nowMs = now.value.getTime()
  andonRecords.value.forEach((record) => {
    const triggeredAt = new Date(String(record.triggeredAt ?? '')).getTime()
    const hoursAgo = (nowMs - triggeredAt) / 3_600_000
    if (Number.isFinite(triggeredAt) && hoursAgo >= 0 && hoursAgo < 24) bins[Math.min(11, Math.floor((24 - hoursAgo) / 2))] += 1
  })
  return bins
})
const safeDuration = computed(() => {
  const latest = recoveredAlerts.value[0]
  const closedAt = new Date(String(latest?.closedAt ?? '')).getTime()
  if (!Number.isFinite(closedAt) || closedAt > now.value.getTime()) return '暂无安全时长数据'
  const minutes = Math.floor((now.value.getTime() - closedAt) / 60_000)
  return `连续安全运行 ${Math.floor(minutes / 1440)} 天 ${String(Math.floor(minutes / 60) % 24).padStart(2, '0')}:${String(minutes % 60).padStart(2, '0')}`
})

async function loadBoard() {
  try {
    error.value = ''
    const [overview, lines, workshops, andons] = await Promise.all([
      get<ControlCenterBoard>('/report/boards/control-center'),
      get<BoardItem[]>('/report/boards/line'),
      get<BoardItem[]>('/report/boards/workshop'),
      getAndonExceptions(),
    ])
    board.value = overview
    lineBoard.value = lines
    workshopBoard.value = workshops
    andonRecords.value = andons
  } catch (reason) {
    error.value = reason instanceof Error ? reason.message : '控制中心数据加载失败'
  }
}

function setupThreeHoverCards() {
  const root = screenRoot.value
  if (!root || window.matchMedia('(prefers-reduced-motion: reduce)').matches) return

  const renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true, powerPreference: 'low-power' })
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 1.5))
  renderer.setClearColor(0x000000, 0)
  Object.assign(renderer.domElement.style, { position: 'absolute', inset: '0', width: '100%', height: '100%', pointerEvents: 'none', zIndex: '3', opacity: '.92' })

  const scene = new THREE.Scene()
  const camera = new THREE.PerspectiveCamera(42, 1, 0.1, 20)
  camera.position.z = 5
  const particleCount = 46
  const positions = new Float32Array(particleCount * 3)
  const colors = new Float32Array(particleCount * 3)
  for (let index = 0; index < particleCount; index += 1) {
    const offset = index * 3
    positions[offset] = (Math.random() - .5) * 4.8
    positions[offset + 1] = (Math.random() - .5) * 3.2
    positions[offset + 2] = (Math.random() - .5) * 1.4
    colors[offset] = .12 + Math.random() * .25
    colors[offset + 1] = .7 + Math.random() * .3
    colors[offset + 2] = 1
  }
  const geometry = new THREE.BufferGeometry()
  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3))
  const particles = new THREE.Points(geometry, new THREE.PointsMaterial({ size: .065, vertexColors: true, transparent: true, opacity: .86, blending: THREE.AdditiveBlending, depthWrite: false }))
  const rings = new THREE.Group()
  rings.add(new THREE.Mesh(new THREE.RingGeometry(1.28, 1.31, 64), new THREE.MeshBasicMaterial({ color: 0x30e9ff, transparent: true, opacity: .35, side: THREE.DoubleSide })))
  rings.add(new THREE.Mesh(new THREE.RingGeometry(.78, .8, 48), new THREE.MeshBasicMaterial({ color: 0xa777ff, transparent: true, opacity: .25, side: THREE.DoubleSide })))
  scene.add(particles, rings)

  let active: HTMLElement | null = null
  let tiltX = 0
  let tiltY = 0
  let frame = 0
  const animate = (time: number) => {
    if (active) {
      particles.rotation.z = time * .00024
      particles.rotation.x += (tiltY * .16 - particles.rotation.x) * .045
      rings.rotation.z = -time * .00032
      rings.rotation.x += (tiltY * .24 - rings.rotation.x) * .04
      rings.rotation.y += (tiltX * .24 - rings.rotation.y) * .04
      renderer.render(scene, camera)
    }
    frame = requestAnimationFrame(animate)
  }
  frame = requestAnimationFrame(animate)

  const cards = [...root.querySelectorAll<HTMLElement>('.panel')]
  const cleanups = cards.map((card) => {
    const enter = () => {
      active = card
      const bounds = card.getBoundingClientRect()
      camera.aspect = bounds.width / Math.max(bounds.height, 1)
      camera.updateProjectionMatrix()
      renderer.setSize(bounds.width, bounds.height, false)
      card.appendChild(renderer.domElement)
      card.classList.add('three-hover-active')
    }
    const move = (event: PointerEvent) => {
      if (active !== card) return
      const bounds = card.getBoundingClientRect()
      tiltX = ((event.clientX - bounds.left) / bounds.width - .5) * 2
      tiltY = ((event.clientY - bounds.top) / bounds.height - .5) * 2
      card.style.setProperty('--three-rotate-x', `${-tiltY * 3.5}deg`)
      card.style.setProperty('--three-rotate-y', `${tiltX * 4.5}deg`)
    }
    const leave = () => {
      card.classList.remove('three-hover-active')
      card.style.removeProperty('--three-rotate-x')
      card.style.removeProperty('--three-rotate-y')
      if (active === card) {
        active = null
        renderer.domElement.remove()
      }
    }
    card.addEventListener('pointerenter', enter)
    card.addEventListener('pointermove', move)
    card.addEventListener('pointerleave', leave)
    return () => {
      card.removeEventListener('pointerenter', enter)
      card.removeEventListener('pointermove', move)
      card.removeEventListener('pointerleave', leave)
    }
  })
  destroyThreeHover = () => {
    cancelAnimationFrame(frame)
    cleanups.forEach((cleanup) => cleanup())
    renderer.domElement.remove()
    geometry.dispose()
    ;(particles.material as THREE.Material).dispose()
    rings.traverse((node) => {
      if (node instanceof THREE.Mesh) { node.geometry.dispose(); (node.material as THREE.Material).dispose() }
    })
    renderer.dispose()
  }
}

onMounted(() => {
  void loadBoard()
  window.requestAnimationFrame(setupThreeHoverCards)
  clockTimer = window.setInterval(() => { now.value = new Date() }, 1000)
  refreshTimer = window.setInterval(() => { void loadBoard() }, 60_000)
  trendTimer = window.setInterval(() => { activeTrendIndex.value = (activeTrendIndex.value + 1) % Math.max(trendValues.value.length, 1) }, 2600)
})

onBeforeUnmount(() => {
  destroyThreeHover()
  window.clearInterval(clockTimer)
  window.clearInterval(refreshTimer)
  window.clearInterval(trendTimer)
})
</script>

<template>
  <main ref="screenRoot" class="screen" :style="{ '--ioftv-page-bg': `url(${pageBackground})`, '--ioftv-top': `url(${topBackground})`, '--ioftv-light': `url(${lightBeam})`, '--ioftv-header-bar': `url(${headerBar})`, '--ioftv-panel-corner': `url(${panelCorner})`, '--ioftv-left-slash': `url(${leftSlash})`, '--ioftv-right-slash': `url(${rightSlash})` }" data-od-id="ccenter">
    <div class="ambient-flylines" aria-hidden="true"><i v-for="n in 7" :key="n"></i></div>
    <header class="screen-header">
      <RouterLink to="/dashboard" class="back" aria-label="返回工作台"></RouterLink>
      <div class="header-rule"><i></i></div>
      <div class="screen-title">
        <h1>风擎工控智能制造中心</h1>
      </div>
      <div class="header-rule reverse"><i></i></div>
      <div class="clock"><span>{{ dateText }}</span><strong>{{ timeText }}</strong></div>
    </header>

    <div v-if="error" class="screen-error">
      <strong>数据连接异常</strong><span>{{ error }}</span><button type="button" @click="loadBoard">重新加载</button>
    </div>

    <section v-else class="screen-grid">
      <aside class="column left-column">
        <section class="panel kpi-panel">
          <div class="panel-title"><i></i><h2>制造关键指标</h2><span>LIVE</span></div>
          <div class="kpi-list">
            <article v-for="(kpi, index) in kpis" :key="`${text(kpi.label)}-${index}`" class="kpi-row">
              <div class="kpi-icon" :class="`is-${text(kpi.tone, 'running')}`"><span class="kpi-aura"></span><img :src="kpiIcons[index % kpiIcons.length]" alt="" /></div>
              <div><p>{{ text(kpi.label) }}</p><small>{{ text(kpi.note, '实时监测') }}</small></div>
              <strong :class="`value-${text(kpi.tone, 'running')}`">{{ typeof kpi.value === 'number' ? formatDisplayNumber(kpi.value) : text(kpi.value) }}</strong>
            </article>
            <div v-if="!kpis.length" class="empty">暂无指标数据</div>
          </div>
        </section>

        <section class="panel alert-panel">
          <div class="panel-title"><i></i><h2>实时安灯预警</h2><span class="warn-dot">{{ alerts.length }}</span></div>
          <div v-if="alerts.length" class="alert-list" :class="{ 'is-scrolling': alerts.length > 2 }">
            <div class="alert-track">
            <article v-for="(alert, index) in rollingAlerts" :key="`${text(alert.code)}-${index}`" class="alert-item" :class="`is-${text(alert.tone, 'warn')}`">
              <span class="alert-code"><i></i>{{ text(alert.code, 'ANDON') }}</span><p>{{ text(alert.text) }}</p><small>{{ text(alert.status) }}</small>
            </article>
            </div>
          </div>
          <div v-else class="safe-state safe-monitor">
            <div class="safe-radar" aria-hidden="true"><i></i><i></i><i></i><b></b><span></span></div>
            <div class="safe-copy"><strong>当前未发现待处理异常</strong><span>{{ safeDuration }}</span></div>
            <div class="safe-trend"><div><span>近 24 小时告警趋势</span><small>0 ACTIVE</small></div><ol><li v-for="(count, index) in alertTrend" :key="index" :style="{ height: `${Math.max(10, count * 32)}%` }"></li></ol><em>24H</em></div>
            <div class="recovered-alerts"><div class="recovered-title"><span>最近恢复</span><small>{{ recoveredAlerts.length }} 条</small></div><article v-for="(record, index) in recoveredAlerts" :key="`${text(record.andonNo)}-${index}`"><i></i><b>{{ text(record.andonNo, 'ANDON') }}</b><span>{{ text(record.reasonName ?? record.exceptionType, '已恢复异常') }}</span><time>{{ text(record.closedAt, '—').replace('T', ' ') }}</time></article><p v-if="!recoveredAlerts.length">暂无已恢复告警记录</p></div>
          </div>
        </section>
      </aside>

      <section class="center-column">
        <section class="hero panel">
          <span class="hero-scan" aria-hidden="true"></span>
          <div class="hero-copy"><span>当日累计产出</span><strong v-count-up>{{ outputTotal }}</strong><small>UNIT / PCS</small></div>
          <div class="hero-orbit"><span class="orbit-satellite one"></span><span class="orbit-satellite two"></span><div class="orbit-core"><span>生产状态</span><b>运行中</b></div></div>
          <div class="hero-stats"><span>在线产线 <b>{{ lines.filter((line) => text(line.status) === 'RUNNING').length }}</b></span><span>待办工单 <b>{{ workOrders.length }}</b></span></div>
        </section>

        <section class="panel trend-panel">
          <div class="panel-title"><i></i><h2>产线产出趋势</h2><span>最近 7 个采集点</span></div>
          <div class="chart-wrap">
            <div class="chart-y"><span>100</span><span>50</span><span>0</span></div>
            <div class="trend-stage">
              <svg viewBox="0 0 520 180" preserveAspectRatio="xMidYMid meet" aria-label="产出趋势图">
                <defs><linearGradient id="trendFill" x1="0" x2="0" y1="0" y2="1"><stop stop-color="#19d8ff" stop-opacity=".38"/><stop offset="1" stop-color="#19d8ff" stop-opacity="0"/></linearGradient></defs>
                <path d="M0 45H520M0 90H520M0 135H520" class="grid-line" /><path d="M0 64H520" class="warning-line" /><path d="M0 30H520" class="goal-line" />
                <polygon :points="trendArea" fill="url(#trendFill)" /><polyline :points="planTrendPoints" class="plan-trend-line" /><polyline :points="trendPoints" class="trend-line" /><polyline :points="forecastTrendPoints" class="forecast-trend-line" />
                <circle v-for="(point, index) in trendCoordinates" :key="index" :cx="point.x" :cy="point.y" r="3" class="trend-data-dot" :class="{ active: index === activeTrendIndex }" @mouseenter="activeTrendIndex = index" />
                <circle :cx="trendPeak.x" :cy="trendPeak.y" r="5" class="peak-dot" /><text :x="trendPeak.x" :y="Math.max(trendPeak.y - 12, 12)" class="peak-label">峰值 {{ trendPeak.value }}%</text>
              </svg>
              <div class="trend-tooltip" :style="{ left: `${Math.min(83, Math.max(3, trendTooltip.point.x / 5.2))}%` }"><b>采样点 {{ trendTooltip.index + 1 }}</b><span>实际 {{ percent(trendTooltip.actual) }}</span><span>计划 {{ percent(trendTooltip.plan) }}</span></div>
              <div class="trend-timeline"><i></i><span>00:00</span><span>08:00</span><span>16:00</span><span>NOW</span></div>
            </div>
          </div>
        </section>

        <section class="panel line-panel topology-panel">
          <div class="panel-title"><i></i><h2>产线拓扑图</h2><span>LIVE FLOW · {{ lines.length }} 条产线</span></div>
          <div class="production-topology">
            <div class="topology-caption"><span>风扇制造主线</span><small>物料流与工位状态实时映射</small></div>
            <div class="topology-track">
              <template v-for="(station, index) in topologyStations" :key="station.code">
                <article class="topology-station" :class="`is-${station.status}`">
                  <div class="station-head"><span>{{ station.code }}</span><b>{{ station.status === 'running' ? '正常' : station.status === 'idle' ? '待机' : '告警' }}</b></div>
                  <div class="station-node">
                    <span class="node-glow"></span>
                    <svg class="station-machine" viewBox="0 0 48 48" aria-hidden="true"><path d="M9 34h30v5H9zM13 17h22v17H13zM18 12h12v5H18zM17 22h5v7h-5zM26 22h5v7h-5zM8 39h5v4H8zm27 0h5v4h-5z" /><path class="machine-accent" d="M7 14h7M34 14h7M24 7v5" /></svg>
                  </div>
                  <h3>{{ station.name }}</h3><p>{{ station.workOrder }}</p>
                  <dl><div><dt>产量</dt><dd>{{ formatDisplayNumber(station.output) }}</dd></div><div><dt>节拍</dt><dd>{{ station.cycle.toFixed(2) }}<small>s</small></dd></div></dl>
                  <div class="station-rate"><i :style="{ width: `${station.rate}%` }"></i><span>{{ percent(station.rate) }}</span></div>
                </article>
                <div v-if="index < topologyStations.length - 1" class="topology-link" aria-hidden="true"><i></i><i></i><i></i><span>WIP</span></div>
              </template>
              <span class="wip-carrier carrier-one" aria-hidden="true"></span><span class="wip-carrier carrier-two" aria-hidden="true"></span>
            </div>
          </div>
        </section>
      </section>

      <aside class="column right-column">
        <section class="panel order-panel">
          <div class="panel-title"><i></i><h2>在制工单</h2><span>WORK ORDER</span></div>
          <div class="order-list">
            <article v-for="(order, index) in workOrders.slice(0, 5)" :key="`${text(order.workOrderNo)}-${index}`" class="order-item">
              <span class="order-no">{{ text(order.workOrderNo) }}</span><b>{{ text(order.productName) }}</b>
              <div><span><i :style="{ width: `${Math.min(number(order.rate), 100)}%` }"></i></span><strong>{{ percent(order.rate) }}</strong></div>
              <small :class="text(order.warning) === 'NONE' ? 'normal' : 'warning'">{{ text(order.warning, '正常') }}</small>
            </article>
            <div v-if="!workOrders.length" class="empty">暂无在制工单</div>
          </div>
        </section>
        <section class="panel workshop-panel">
          <div class="panel-title"><i></i><h2>车间态势</h2><span>{{ workshopBoard.length }} 个区域</span></div>
          <div v-if="workshopBoard.length" class="workshop-list">
            <article v-for="(area, index) in workshopBoard.slice(0, 3)" :key="`${text(area.id)}-${index}`" class="workshop-item" :class="`tone-${text(area.tone, 'running')}`">
              <span class="workshop-orb"></span><div><b>{{ text(area.areaName) }}</b><small>{{ text(area.description, '实时区域数据') }}</small></div><dl><div><dt>产出</dt><dd>{{ number(area.outputQty) }}</dd></div><div><dt>异常</dt><dd>{{ number(area.exceptions) }}</dd></div></dl>
            </article>
          </div>
          <div v-else class="empty">暂无车间数据</div>
          <div v-if="lineRanking.length" class="capsule-ranking">
            <div v-for="(line, index) in lineRanking" :key="line.name" class="capsule-row" :style="{ '--capsule-color': ['#37a2da', '#32c5e9', '#67e0e3', '#9fe6b8'][index] }">
              <span>{{ line.name }}</span><div><i :style="{ width: `${line.value}%` }"></i></div><b>{{ percent(line.value) }}</b>
            </div>
          </div>
        </section>
        <section class="panel equipment-panel">
          <div class="panel-title"><i></i><h2>设备健康度</h2><span>OEE MONITOR</span></div>
          <div class="health-ring" :style="{ '--gauge': `${oeeValue * 3.6}deg` }"><div class="gauge-inner"><span>设备综合效率</span><b>{{ oeeValue ? oeeValue.toFixed(2) : '—' }}<small>%</small></b></div></div>
          <div class="health-legend"><span><i class="good"></i>正常运行</span><span><i class="caution"></i>关注设备</span></div>
        </section>
      </aside>
    </section>

  </main>
</template>

<style scoped>
.screen { min-height:100vh; color:#d8f7ff; background:#03050c var(--ioftv-page-bg) center/cover fixed; padding:16px; box-sizing:border-box; font-family:"Microsoft YaHei",Arial,sans-serif; overflow:hidden; }
.screen::before { content:""; position:fixed; z-index:0; left:0; right:0; top:60px; height:56px; pointer-events:none; background:var(--ioftv-light) center 80px/cover no-repeat; }
.screen-header { height:60px; position:relative; z-index:1; display:flex; align-items:center; gap:18px; background:var(--ioftv-top) center/cover no-repeat; margin-bottom:4px; }
.back { position:relative; width:42px; height:42px; flex:none; text-decoration:none; }.back::before { content:""; position:absolute; left:15px; top:13px; width:14px; height:14px; border-left:2px solid #67e7ff; border-bottom:2px solid #67e7ff; transform:rotate(45deg); filter:drop-shadow(0 0 5px #00b7e7); }.header-rule { height:6px; flex:1; max-width:140px; background:var(--ioftv-header-bar) center/100% 100% no-repeat; align-self:flex-start; margin-top:-2px; }.header-rule.reverse { transform:rotate(180deg); }.header-rule i { display:none; }.screen-title { position:absolute; left:50%; transform:translateX(-50%); text-align:center; min-width:490px; }.panel-title span { color:#5edaf2; letter-spacing:2px; font-size:10px; }.screen-title h1 { margin:0; font-size:32px; font-weight:900; letter-spacing:6px; background:linear-gradient(92deg,#0072ff 0%,#00eaff 49%,#01aaff 100%); -webkit-background-clip:text; -webkit-text-fill-color:transparent; }.clock { position:absolute; top:6px; right:14px; width:224px; text-align:right; color:#bcefff; font-size:12px; }.clock span,.clock strong { display:block; }.clock strong { margin-top:2px; color:#d9faff; font:19px/1.1 Consolas,monospace; letter-spacing:1px; }
.screen-grid { position:relative; z-index:1; display:grid; grid-template-columns:minmax(260px,1fr) minmax(500px,1.8fr) minmax(260px,1fr); gap:16px; height:calc(100vh - 96px); min-height:620px; }.screen-grid::before,.screen-grid::after { content:""; position:absolute; top:10px; width:36px; height:70px; background-size:contain; background-repeat:no-repeat; opacity:.9; pointer-events:none; }.screen-grid::before { left:-8px; background-image:var(--ioftv-left-slash); }.screen-grid::after { right:-8px; background-image:var(--ioftv-right-slash); }.column,.center-column { min-height:0; display:flex; flex-direction:column; gap:16px; }.panel { border:1px solid rgba(55,206,244,.34); background:rgba(4,20,47,.66); box-shadow:inset 0 0 28px rgba(0,164,220,.08); position:relative; overflow:hidden; }.panel::before { content:""; position:absolute; z-index:1; top:0; left:0; width:100%; height:26px; background:var(--ioftv-panel-corner) left top/auto 26px no-repeat; opacity:.85; pointer-events:none; }.panel::after { content:""; position:absolute; top:0; left:0; width:38px; height:2px; background:#43e4ff; box-shadow:0 0 11px #43e4ff; }.panel-title { height:39px; display:flex; align-items:center; gap:8px; padding:0 14px; border-bottom:1px solid rgba(82,205,240,.16); }.panel-title i { width:4px; height:13px; background:#24d9ff; box-shadow:0 0 7px #24d9ff; }.panel-title h2 { font-size:14px; margin:0; letter-spacing:1px; color:#e8fbff; }.panel-title span { margin-left:auto; letter-spacing:1px; }.kpi-panel { flex:1.05; }.alert-panel { flex:.95; }.kpi-list { padding:8px 14px; }.kpi-row { display:grid; grid-template-columns:28px 1fr auto; gap:9px; align-items:center; min-height:53px; border-bottom:1px solid rgba(91,221,249,.1); }.kpi-icon { width:24px; height:24px; display:grid; place-items:center; }.kpi-icon img { width:100%; height:100%; object-fit:contain; }.kpi-row p { margin:0 0 3px; font-size:13px; }.kpi-row small,.line-row small { color:#77aabc; font-size:10px; }.kpi-row strong { font:20px/1 Consolas,monospace; }.value-running { color:#47eeff; }.value-warn,.value-danger { color:#ffc456; }.alert-list { padding:6px 12px; }.alert-item { display:grid; grid-template-columns:58px 1fr auto; gap:7px; padding:10px 4px; align-items:center; border-bottom:1px solid rgba(250,170,57,.13); }.alert-code { color:#ffbd58; font:11px Consolas; }.alert-item p { margin:0; font-size:12px; overflow:hidden; white-space:nowrap; text-overflow:ellipsis; }.alert-item small { color:#eab360; font-size:10px; }.safe-state { min-height:105px; display:flex; align-items:center; justify-content:center; flex-direction:column; gap:8px; color:#7fe7bd; font-size:13px; }.safe-state img { width:42px; height:42px; object-fit:contain; }.safe-monitor { position:relative; align-items:stretch; justify-content:flex-start; min-height:0; padding:9px 14px 11px; gap:5px; overflow:hidden; background:radial-gradient(circle at 16% 24%,rgba(34,227,187,.11),transparent 34%); }.safe-monitor::before { content:""; position:absolute; inset:0; border:1px solid rgba(63,238,195,.16); animation:safe-border-pulse 4s linear infinite; pointer-events:none; }.safe-radar { position:absolute; left:16px; top:10px; width:68px; height:68px; border:1px solid rgba(84,241,202,.52); border-radius:50%; background:repeating-radial-gradient(circle,transparent 0 14px,rgba(83,235,190,.19) 15px 16px),linear-gradient(90deg,transparent 49%,rgba(91,244,201,.26) 50% 51%,transparent 52%),linear-gradient(transparent 49%,rgba(91,244,201,.26) 50% 51%,transparent 52%); box-shadow:0 0 16px rgba(47,232,178,.32),inset 0 0 17px rgba(47,232,178,.15); }.safe-radar::after { content:""; position:absolute; left:50%; top:50%; width:50%; height:50%; transform-origin:0 0; background:conic-gradient(from 0deg,rgba(84,255,203,.38),transparent 28deg); animation:radar-sweep 3.6s linear infinite; }.safe-radar i { position:absolute; inset:8px; border:1px solid rgba(108,255,220,.27); border-radius:50%; animation:radar-ripple 3.2s ease-out infinite; }.safe-radar i:nth-child(2) { animation-delay:-1.05s; }.safe-radar i:nth-child(3) { animation-delay:-2.1s; }.safe-radar b { position:absolute; z-index:1; left:31px; top:31px; width:6px; height:6px; border-radius:50%; background:#8cffd8; box-shadow:0 0 10px #70ffd1; }.safe-copy { min-height:70px; padding-left:85px; display:flex; justify-content:center; flex-direction:column; }.safe-copy strong { color:#a9ffdc; font-size:13px; letter-spacing:.4px; }.safe-copy span { margin-top:5px; color:#72caaa; font:10px Consolas; }.safe-trend { position:relative; padding:6px 8px 8px; border-top:1px solid rgba(81,232,191,.18); border-bottom:1px solid rgba(81,232,191,.18); }.safe-trend > div,.recovered-title { display:flex; justify-content:space-between; color:#8bd9bf; font-size:10px; }.safe-trend small,.recovered-title small { color:#55aa94; font:9px Consolas; }.safe-trend ol { height:21px; display:flex; align-items:flex-end; gap:3px; margin:4px 0 0; padding:0; list-style:none; }.safe-trend li { flex:1; min-height:2px; background:linear-gradient(#4af0c3,rgba(74,240,195,.25)); box-shadow:0 0 5px rgba(79,245,202,.5); animation:trend-breathe 2.5s ease-in-out infinite; }.safe-trend li:nth-child(3n) { animation-delay:-1.1s; }.safe-trend em { position:absolute; right:8px; bottom:2px; color:#438c78; font:8px Consolas; font-style:normal; }.recovered-alerts { min-height:0; }.recovered-title { margin-bottom:2px; }.recovered-alerts article { display:grid; grid-template-columns:6px 62px 1fr auto; align-items:center; gap:5px; min-height:17px; border-top:1px solid rgba(76,211,175,.1); color:#8fc8b7; font-size:9px; }.recovered-alerts article i { width:5px; height:5px; border-radius:50%; background:#55efbd; box-shadow:0 0 6px #55efbd; }.recovered-alerts article b { color:#b0f3d9; font:9px Consolas; }.recovered-alerts article span { overflow:hidden; white-space:nowrap; text-overflow:ellipsis; }.recovered-alerts time { color:#5a9c89; font:8px Consolas; }.recovered-alerts p { margin:5px 0 0; color:#5b9d89; font-size:10px; text-align:center; }.empty { color:#6595a8; text-align:center; padding:24px; font-size:12px; } @keyframes radar-sweep { to { transform:rotate(360deg); } } @keyframes radar-ripple { 0% { transform:scale(.35); opacity:.9; } 100% { transform:scale(1.1); opacity:0; } } @keyframes safe-border-pulse { 50% { border-color:rgba(95,255,211,.4); box-shadow:inset 0 0 15px rgba(69,244,193,.08); } } @keyframes trend-breathe { 50% { opacity:.35; } }
.hero { height:144px; flex:none; display:grid; grid-template-columns:1fr 190px 1fr; align-items:center; padding:0 35px; }.hero-copy span,.hero-copy small { display:block; color:#73bed0; font-size:11px; letter-spacing:2px; }.hero-copy strong { display:block; margin:7px 0 3px; color:#fff; font:42px/1 Consolas,monospace; text-shadow:0 0 14px #16d9ff; }.hero-orbit { width:124px; height:124px; justify-self:center; border:1px solid #2bdfff; border-radius:50%; box-shadow:0 0 0 9px rgba(18,185,238,.12),0 0 22px #007aa8 inset; display:grid; place-items:center; animation:pulse 3s infinite ease-in-out; }.orbit-core { width:82px; height:82px; border-radius:50%; background:radial-gradient(circle,#1ad8ed,#085a7d); display:grid; place-content:center; text-align:center; box-shadow:0 0 18px #1ae0ff; }.orbit-core span { font-size:10px; color:#c7faff; }.orbit-core b { font-size:15px; color:white; margin-top:5px; }.hero-stats { text-align:right; display:grid; gap:12px; color:#78bdcf; font-size:12px; }.hero-stats b { margin-left:8px; color:#53e8ff; font:20px Consolas; }.trend-panel { flex:.9; min-height:180px; }.chart-wrap { height:calc(100% - 40px); min-height:130px; display:flex; padding:8px 20px 12px 38px; }.chart-wrap svg { flex:1; overflow:visible; }.chart-y { display:flex; flex-direction:column; justify-content:space-between; padding:3px 7px 6px 0; color:#548498; font:9px Consolas; }.grid-line { stroke:rgba(85,204,231,.18); stroke-width:1; stroke-dasharray:4 5; }.trend-line { fill:none; stroke:#27e3ff; stroke-width:3; filter:drop-shadow(0 0 5px #08c8e9); }.line-panel { flex:1.12; }.line-table { font-size:11px; }.line-head,.line-row { display:grid; grid-template-columns:1.35fr 1.05fr 1fr .75fr; align-items:center; gap:10px; padding:0 15px; }.line-head { height:28px; color:#6399ad; background:rgba(35,127,160,.1); font-size:10px; }.line-row { min-height:48px; border-top:1px solid rgba(79,192,228,.1); }.line-row b,.line-row small { display:block; }.line-row > span { color:#a8dcea; font-family:Consolas; font-size:10px; }.progress { display:flex; align-items:center; gap:5px; }.progress > span,.order-item div > span { height:5px; flex:1; background:#123a50; overflow:hidden; }.progress i,.order-item i { display:block; height:100%; background:linear-gradient(90deg,#06abc7,#3cedff); box-shadow:0 0 6px #23dffc; }.progress b { color:#59def2; font:10px Consolas; }.normal,.warning { font-style:normal; font-size:10px; }.normal { color:#4ceab1; }.warning { color:#ffc45a; }
.order-panel { flex:1.2; }.equipment-panel { flex:.8; }.order-list { padding:4px 12px; }.order-item { padding:10px 2px; border-bottom:1px solid rgba(77,197,227,.12); }.order-no { float:right; color:#6eb2c4; font:9px Consolas; }.order-item b { display:block; margin-bottom:7px; font-size:12px; color:#e1f7fb; }.order-item div { display:flex; align-items:center; gap:7px; }.order-item strong { font:10px Consolas; color:#56dff2; }.order-item small { display:block; margin-top:5px; font-size:10px; }.health-ring { width:125px; height:125px; margin:12px auto 7px; border-radius:50%; border:9px solid #176582; border-top-color:#2ce6ff; border-right-color:#2ce6ff; display:flex; flex-direction:column; align-items:center; justify-content:center; box-shadow:0 0 13px rgba(20,215,255,.3); }.health-ring span { color:#75b8c8; font-size:10px; }.health-ring b { font:26px Consolas; color:#f0feff; margin-top:5px; }.health-ring small { color:#57dff4; font-size:12px; }.health-legend { display:flex; justify-content:center; gap:20px; color:#85b5c0; font-size:10px; }.health-legend i { width:6px; height:6px; display:inline-block; border-radius:50%; margin-right:4px; }.good { background:#4be6a9; }.caution { background:#ffbf4d; }.screen-error { position:relative; z-index:2; margin:80px auto; padding:30px; width:min(560px,90%); display:grid; gap:12px; text-align:center; border:1px solid #e8a941; background:#10243a; color:#ffce75; }.screen-error button { justify-self:center; cursor:pointer; color:#091a2b; border:0; background:#49dfff; padding:8px 20px; }
@keyframes pulse { 50% { transform:scale(1.03); box-shadow:0 0 0 12px rgba(18,185,238,.06),0 0 30px #007aa8 inset; } }
.panel-title { height:43px; padding:0 16px; }.panel-title i { height:16px; }.panel-title h2 { font-size:17px; }.panel-title span { font-size:11px; }
.kpi-list { padding:8px 16px; }.kpi-row { grid-template-columns:32px 1fr auto; gap:10px; min-height:58px; }.kpi-icon { font-size:22px; }.kpi-row p { margin-bottom:4px; font-size:15px; }.kpi-row small,.line-row small { font-size:11px; }.kpi-row strong { font-size:24px; }
.alert-list { padding:6px 14px; }.alert-item { grid-template-columns:66px 1fr auto; gap:8px; padding:11px 4px; }.alert-code { font-size:12px; }.alert-item p { font-size:14px; }.alert-item small { font-size:11px; }.safe-state { font-size:15px; }.empty { font-size:14px; }
.hero-copy span,.hero-copy small { font-size:13px; }.hero-copy strong { font-size:48px; }.orbit-core span { font-size:11px; }.orbit-core b { font-size:17px; }.hero-stats { font-size:14px; }.hero-stats b { font-size:23px; }
.line-table { font-size:13px; }.line-head { height:31px; font-size:12px; }.line-row { min-height:53px; }.line-row > span,.progress b,.normal,.warning { font-size:11px; }
.order-item { padding:12px 2px; }.order-no { font-size:10px; }.order-item b { font-size:14px; }.order-item strong,.order-item small { font-size:11px; }.health-ring span { font-size:11px; }.health-ring b { font-size:30px; }.health-ring small { font-size:14px; }.health-legend { font-size:11px; }
.kpi-icon { position:relative; overflow:visible; }.kpi-icon img { position:relative; z-index:1; }.kpi-aura { position:absolute; inset:-7px; border:1px solid rgba(54,231,255,.45); border-radius:50%; animation:icon-pulse 2.4s ease-out infinite; }.kpi-icon.is-warn .kpi-aura,.kpi-icon.is-danger .kpi-aura { border-color:rgba(255,188,75,.65); }
.alert-code { display:flex; align-items:center; gap:5px; }.alert-code i { width:7px; height:7px; flex:none; border-radius:50%; background:#ffbd58; box-shadow:0 0 8px #ffbd58; animation:warning-pulse 1.4s ease-in-out infinite; }
.hero { background:radial-gradient(circle at 50% 50%,rgba(1,132,184,.19),transparent 44%),rgba(4,20,47,.66); }.hero-orbit::before,.hero-orbit::after { content:""; position:absolute; border:1px dashed rgba(37,224,255,.46); border-radius:50%; animation:orbit-spin 10s linear infinite; }.hero-orbit::before { width:145px; height:145px; }.hero-orbit::after { width:170px; height:170px; border-color:rgba(0,114,255,.34); animation-direction:reverse; animation-duration:16s; }.orbit-satellite { position:absolute; width:7px; height:7px; z-index:2; border-radius:50%; background:#65f0ff; box-shadow:0 0 10px #65f0ff; }.orbit-satellite.one { top:-6px; right:25px; }.orbit-satellite.two { bottom:11px; left:-6px; background:#ba7dff; box-shadow:0 0 10px #ba7dff; }.spark { height:22px; width:37px; margin-left:3px; display:flex; align-items:flex-end; gap:2px; }.spark i { width:3px; min-height:3px; background:linear-gradient(#7af8ff,#1478f2); box-shadow:0 0 4px #3ae9ff; }.progress { min-width:0; }.progress > span { min-width:45px; }.health-ring { border:0; padding:8px; background:conic-gradient(#27e9ff var(--gauge),rgba(34,94,127,.28) 0); position:relative; }.gauge-inner { width:100%; height:100%; background:#071b36 radial-gradient(circle,#123b60 0%,#061529 69%); border-radius:50%; display:flex; flex-direction:column; align-items:center; justify-content:center; box-shadow:inset 0 0 17px rgba(36,222,255,.22); }.health-ring::after { content:""; position:absolute; inset:-6px; border:1px dashed rgba(43,225,255,.55); border-radius:50%; animation:orbit-spin 12s linear infinite; }.health-ring span,.health-ring b { position:relative; z-index:1; }
@keyframes icon-pulse { 0% { transform:scale(.75); opacity:1; } 100% { transform:scale(1.35); opacity:0; } } @keyframes warning-pulse { 50% { transform:scale(1.6); opacity:.45; } } @keyframes orbit-spin { to { transform:rotate(360deg); } }
.screen-nav { display:flex; gap:5px; white-space:nowrap; }.screen-nav button { border:1px solid rgba(75,218,247,.32); background:rgba(5,38,71,.72); color:#75cddd; padding:5px 8px; font-size:11px; cursor:pointer; transition:.2s; }.screen-nav button:hover,.screen-nav button.active { color:#eaffff; border-color:#39e6ff; box-shadow:0 0 12px rgba(44,224,255,.4) inset,0 0 8px rgba(44,224,255,.28); background:rgba(11,98,141,.5); }
.board-mode-screen { position:relative; z-index:1; width:min(1600px,calc(100% - 72px)); min-height:calc(100vh - 112px); margin:0 auto; display:flex; flex-direction:column; gap:16px; }.board-mode-hero { min-height:126px; display:grid; grid-template-columns:1fr auto 1fr; align-items:center; padding:0 32px; background:radial-gradient(circle at 50% 0,rgba(19,153,216,.25),transparent 44%),rgba(4,20,47,.72); }.eyebrow { color:#3ee7ff; letter-spacing:3px; font:11px Consolas,monospace; }.board-mode-hero h2 { margin:6px 0; color:#effcff; font-size:28px; letter-spacing:3px; }.board-mode-hero p { margin:0; color:#74b7c8; font-size:12px; }.mode-total { text-align:center; min-width:220px; }.mode-total span,.mode-total small { display:block; color:#70b9cb; font-size:11px; letter-spacing:2px; }.mode-total strong { display:inline-block; margin:7px 7px 0 0; color:#eafeff; font:42px/1 Consolas,monospace; text-shadow:0 0 13px #21dfff; }.mode-summary { justify-self:end; display:grid; gap:10px; color:#7ac6d7; font-size:12px; }.mode-summary b { display:inline-block; min-width:26px; margin-left:8px; color:#59e8ff; text-align:right; font:23px Consolas; }.mode-content-grid { flex:1; min-height:0; display:grid; grid-template-columns:minmax(0,2.35fr) minmax(270px,.8fr); gap:16px; }.mode-table-panel { min-height:530px; }.mode-line-head,.mode-line-row { display:grid; grid-template-columns:1.5fr 1.1fr 1.15fr .65fr; gap:16px; align-items:center; padding:0 22px; }.mode-line-head { height:38px; color:#5f9fb2; background:rgba(28,133,177,.12); font-size:11px; }.mode-line-row { min-height:77px; border-top:1px solid rgba(86,202,235,.14); }.mode-line-row b,.mode-line-row small { display:block; }.mode-line-row b { color:#e4faff; font-size:15px; }.mode-line-row small { margin-top:4px; color:#6daabd; font-size:11px; }.mode-line-row > span { color:#add9e3; font:12px Consolas; }.mode-progress { display:flex; align-items:center; gap:10px; }.mode-progress > span { height:7px; flex:1; overflow:hidden; background:#123c56; }.mode-progress i { display:block; height:100%; background:linear-gradient(90deg,#09aeca,#4bf0ff); box-shadow:0 0 8px #39e8ff; }.mode-progress b { color:#62e3f5; font:13px Consolas; }.mode-aside { min-height:0; display:grid; grid-template-rows:1.25fr .75fr; gap:16px; }.mode-alert { display:grid; grid-template-columns:70px 1fr auto; gap:8px; padding:13px 14px; border-bottom:1px solid rgba(255,184,69,.16); font-size:12px; }.mode-alert b { color:#ffbc59; font:11px Consolas; }.mode-alert span { overflow:hidden; white-space:nowrap; text-overflow:ellipsis; }.mode-alert small { color:#d9a85a; }.mode-kpi { display:flex; justify-content:space-between; padding:12px 15px; border-bottom:1px solid rgba(75,201,231,.12); color:#85c0cf; font-size:12px; }.mode-kpi b { color:#edfeff; font:19px Consolas; }.workshop-grid { flex:1; display:grid; grid-template-columns:repeat(auto-fit,minmax(315px,1fr)); gap:16px; align-content:start; }.workshop-card { min-height:250px; background:linear-gradient(145deg,rgba(11,57,93,.74),rgba(3,17,41,.82)); }.workshop-card.tone-warn,.workshop-card.tone-warning { border-color:rgba(255,184,67,.55); }.workshop-status { display:flex; align-items:center; gap:14px; padding:24px 20px 17px; }.status-orb { width:34px; height:34px; border:2px solid #42e7ff; border-radius:50%; box-shadow:0 0 16px #19dfff,inset 0 0 12px #19dfff; }.tone-warn .status-orb,.tone-warning .status-orb { border-color:#ffc260; box-shadow:0 0 16px #ffb648,inset 0 0 12px #ffb648; }.workshop-status b,.workshop-status small { display:block; }.workshop-status b { color:#e5fbff; font-size:14px; }.workshop-status small { margin-top:5px; color:#65aabe; font:11px Consolas; }.workshop-card dl { display:grid; grid-template-columns:repeat(3,1fr); margin:0 20px; border-top:1px solid rgba(88,201,231,.18); }.workshop-card dl div { padding:17px 8px; text-align:center; border-right:1px solid rgba(88,201,231,.14); }.workshop-card dl div:last-child { border:0; }.workshop-card dt { color:#6baebe; font-size:11px; }.workshop-card dd { margin:8px 0 0; color:#ecfeff; font:23px Consolas; }.workshop-empty { grid-column:1/-1; }
.right-column .order-panel { flex:1.05; }.right-column .workshop-panel { flex:1; }.right-column .equipment-panel { flex:.75; }.workshop-list { padding:3px 13px; }.workshop-item { display:grid; grid-template-columns:18px 1fr auto; align-items:center; gap:8px; min-height:52px; border-bottom:1px solid rgba(84,203,235,.13); }.workshop-orb { width:11px; height:11px; border-radius:50%; background:#36e8ff; box-shadow:0 0 10px #36e8ff; }.tone-warn .workshop-orb,.tone-warning .workshop-orb { background:#ffbd59; box-shadow:0 0 10px #ffbd59; }.workshop-item b,.workshop-item small { display:block; }.workshop-item b { color:#dffaff; font-size:12px; }.workshop-item small { max-width:120px; margin-top:3px; overflow:hidden; color:#69aabc; font-size:9px; white-space:nowrap; text-overflow:ellipsis; }.workshop-item dl { display:flex; gap:9px; margin:0; }.workshop-item dl div { text-align:right; }.workshop-item dt { color:#5f9fb2; font-size:9px; }.workshop-item dd { margin:2px 0 0; color:#eafbff; font:12px Consolas; }
.trend-stage { position:relative; flex:1; min-width:0; }.trend-stage svg { display:block; width:100%; height:auto; max-height:calc(100% - 19px); margin:auto; }.goal-line { stroke:#9d78ff; stroke-width:1.6; stroke-dasharray:6 5; opacity:.85; }.warning-line { stroke:#ffb54b; stroke-width:1.4; stroke-dasharray:3 5; opacity:.78; }.plan-trend-line { fill:none; stroke:#9d78ff; stroke-width:2.2; stroke-dasharray:7 4; filter:drop-shadow(0 0 4px rgba(157,120,255,.65)); }.forecast-trend-line { fill:none; stroke:#50dff2; stroke-width:2; stroke-dasharray:5 5; opacity:.8; }.trend-data-dot { fill:#8ff5ff; stroke:#0b7fa7; stroke-width:2; cursor:pointer; }.trend-data-dot.active { fill:#c6ffff; stroke:#fff; filter:drop-shadow(0 0 7px #4cf5ff); animation:current-point 1.45s ease-in-out infinite; }.peak-dot { fill:#9d78ff; stroke:#eadfff; stroke-width:2; filter:drop-shadow(0 0 6px #9d78ff); }.peak-label { fill:#d9ccff; font:11px Consolas; text-anchor:middle; }.trend-tooltip { position:absolute; z-index:2; top:7px; display:grid; gap:2px; min-width:93px; padding:5px 7px; border:1px solid rgba(124,220,255,.5); background:rgba(4,25,55,.92); box-shadow:0 0 13px rgba(42,213,255,.24); color:#a7e8f4; font:9px Consolas; pointer-events:none; transform:translateX(-50%); }.trend-tooltip b { color:#effdff; font-size:10px; }.trend-tooltip span:last-child { color:#c8b8ff; }.trend-timeline { position:absolute; right:0; bottom:0; left:0; height:14px; display:flex; align-items:flex-end; justify-content:space-between; color:#5d97aa; font:8px Consolas; }.trend-timeline::before { content:""; position:absolute; right:0; bottom:10px; left:0; height:1px; background:rgba(44,193,220,.28); }.trend-timeline i { position:absolute; bottom:9px; left:0; width:55px; height:3px; background:linear-gradient(90deg,transparent,#8cf7ff,transparent); box-shadow:0 0 9px #3ddcf1; animation:timeline-scan 4.2s linear infinite; }.alert-list { overflow:hidden; }.alert-track { transform:translateY(0); }.alert-list.is-scrolling .alert-track { animation:alert-roll 12s linear infinite; }.alert-list.is-scrolling:hover .alert-track { animation-play-state:paused; } @keyframes current-point { 50% { transform:scale(1.85); opacity:.58; } } @keyframes timeline-scan { to { left:calc(100% - 55px); } }
.topology-panel { flex:1.45; min-height:286px; }.production-topology { height:calc(100% - 43px); min-height:242px; padding:11px 18px 14px; overflow:hidden; box-sizing:border-box; background:radial-gradient(ellipse at 50% 100%,rgba(0,147,220,.13),transparent 58%); }.topology-caption { display:flex; align-items:baseline; justify-content:space-between; color:#d8f9ff; }.topology-caption span { font-size:13px; font-weight:700; letter-spacing:2px; }.topology-caption small { color:#629cb2; font:10px Consolas; }.topology-track { position:relative; display:flex; align-items:center; justify-content:space-between; height:calc(100% - 20px); min-height:195px; }.topology-track::before { content:""; position:absolute; left:6%; right:6%; top:49px; height:2px; background:linear-gradient(90deg,transparent,#22dfff 10%,#197ec8 90%,transparent); box-shadow:0 0 10px rgba(31,219,255,.6); }.topology-station { position:relative; z-index:2; width:18%; min-width:0; padding:8px 7px 7px; box-sizing:border-box; text-align:center; border:1px solid rgba(52,205,237,.32); background:linear-gradient(160deg,rgba(11,65,100,.84),rgba(5,22,53,.82)); box-shadow:inset 0 0 16px rgba(38,213,255,.08); }.topology-station::after { content:""; position:absolute; left:50%; top:44px; width:14px; height:14px; transform:translateX(-50%); border-radius:50%; background:#39efff; box-shadow:0 0 0 5px rgba(46,225,255,.15),0 0 15px #20dcff; }.station-head { display:flex; justify-content:space-between; color:#65b7ca; font:8px Consolas; }.station-head b { font-size:8px; color:#4beab4; }.station-node { position:relative; display:grid; place-items:center; width:42px; height:42px; margin:8px auto 7px; border:1px solid #36dbf6; border-radius:50%; background:rgba(7,61,100,.9); box-shadow:0 0 14px rgba(28,219,255,.55),inset 0 0 12px rgba(21,191,250,.27); }.node-glow { position:absolute; inset:-5px; border:1px dashed rgba(63,236,255,.7); border-radius:50%; animation:orbit-spin 7s linear infinite; }.station-machine { position:relative; width:27px; fill:#d2fbff; filter:drop-shadow(0 0 4px #26ddff); }.machine-accent { fill:none; stroke:#3ee9ff; stroke-width:2.5; stroke-linecap:round; }.topology-station h3 { margin:0; color:#effdff; font-size:11px; letter-spacing:.5px; }.topology-station p { overflow:hidden; margin:4px 0 5px; color:#7ec2d1; font:9px Consolas; white-space:nowrap; text-overflow:ellipsis; }.topology-station dl { display:grid; grid-template-columns:1fr 1fr; margin:0 0 6px; }.topology-station dl div + div { border-left:1px solid rgba(67,201,232,.17); }.topology-station dt { color:#5d99aa; font-size:8px; }.topology-station dd { margin:2px 0 0; color:#e0fbff; font:11px Consolas; }.topology-station dd small { margin-left:1px; color:#6eb7c9; font-size:8px; }.station-rate { display:flex; align-items:center; gap:4px; }.station-rate::before { content:""; height:3px; flex:1; background:#103c58; }.station-rate i { position:absolute; bottom:8px; left:7px; height:3px; max-width:calc(100% - 43px); background:linear-gradient(90deg,#09afcf,#45f6ff); box-shadow:0 0 6px #32e9ff; }.station-rate span { margin-left:auto; color:#65e6f6; font:8px Consolas; }.topology-link { position:relative; z-index:1; flex:1; height:25px; min-width:8px; margin-top:-63px; }.topology-link i { position:absolute; top:11px; width:4px; height:4px; border-radius:50%; background:#b9ffff; box-shadow:0 0 8px 2px #27e9ff; animation:flow-particle 1.9s linear infinite; }.topology-link i:nth-child(1) { left:10%; }.topology-link i:nth-child(2) { left:42%; animation-delay:-.63s; }.topology-link i:nth-child(3) { left:74%; animation-delay:-1.26s; }.topology-link span { position:absolute; top:17px; left:50%; transform:translateX(-50%); color:#438da3; font:7px Consolas; }.topology-station.is-idle { border-color:rgba(126,174,189,.36); filter:saturate(.65); }.topology-station.is-idle::after { background:#7ca0aa; box-shadow:0 0 0 5px rgba(124,160,170,.12),0 0 12px #7ca0aa; }.topology-station.is-idle .station-head b { color:#a5bbc2; }.topology-station.is-alert { border-color:rgba(255,182,76,.72); box-shadow:inset 0 0 16px rgba(255,168,39,.13),0 0 12px rgba(255,156,37,.16); animation:station-alert 1.2s ease-in-out infinite; }.topology-station.is-alert::after { background:#ffae43; box-shadow:0 0 0 5px rgba(255,174,67,.16),0 0 16px #ffae43; }.topology-station.is-alert .station-head b { color:#ffc05b; }.wip-carrier { position:absolute; z-index:3; top:46px; width:9px; height:9px; border:2px solid #d4ffff; border-radius:2px; background:#1595d1; box-shadow:0 0 10px #26e4ff; animation:carrier-flow 13s linear infinite; }.carrier-one { animation-delay:-2s; }.carrier-two { top:54px; animation-delay:-8.5s; }.topology-station:hover { transform:translateY(-3px); border-color:#5eeeff; transition:.2s; } @keyframes flow-particle { 0% { transform:translateX(-5px); opacity:0; } 15%,85% { opacity:1; } 100% { transform:translateX(22px); opacity:0; } } @keyframes carrier-flow { from { left:7%; } to { left:91%; } } @keyframes station-alert { 50% { box-shadow:inset 0 0 18px rgba(255,168,39,.2),0 0 19px rgba(255,156,37,.28); } }
.capsule-ranking { padding:5px 14px 12px; }.capsule-row { display:grid; grid-template-columns:65px 1fr 35px; align-items:center; gap:7px; margin-top:5px; color:#bcecff; font-size:11px; }.capsule-row > span { overflow:hidden; white-space:nowrap; text-overflow:ellipsis; }.capsule-row > div { height:9px; border-radius:6px; overflow:hidden; background:rgba(99,153,208,.18); box-shadow:inset 0 0 4px rgba(18,215,255,.22); }.capsule-row i { display:block; height:100%; border-radius:6px; background:linear-gradient(90deg,var(--capsule-color),#d2fcff); box-shadow:0 0 8px var(--capsule-color); transition:width .6s; }.capsule-row b { color:var(--capsule-color); font:11px Consolas; text-align:right; }.workshop-panel { flex:1; }.equipment-panel { flex:.72; } @keyframes alert-roll { to { transform:translateY(-50%); } }
.ambient-flylines { position:fixed; z-index:0; inset:80px 0 0; overflow:hidden; pointer-events:none; }.ambient-flylines i { position:absolute; width:2px; height:2px; border-radius:50%; background:#8df7ff; box-shadow:0 0 12px 3px #1cdfff; opacity:0; animation:flyline 7s linear infinite; }.ambient-flylines i::after { content:""; position:absolute; right:0; bottom:0; width:130px; height:1px; transform-origin:right; transform:rotate(-25deg); background:linear-gradient(90deg,transparent,rgba(62,232,255,.82)); }.ambient-flylines i:nth-child(1) { left:8%; top:16%; animation-delay:-1s; }.ambient-flylines i:nth-child(2) { left:24%; top:68%; animation-delay:-4s; }.ambient-flylines i:nth-child(3) { left:42%; top:22%; animation-delay:-2s; }.ambient-flylines i:nth-child(4) { left:61%; top:76%; animation-delay:-5.5s; }.ambient-flylines i:nth-child(5) { left:78%; top:32%; animation-delay:-3s; }.ambient-flylines i:nth-child(6) { left:89%; top:62%; animation-delay:-6s; }.ambient-flylines i:nth-child(7) { left:52%; top:47%; animation-delay:-.5s; }
.panel { transform-style:preserve-3d; transition:transform .32s cubic-bezier(.2,.8,.2,1),box-shadow .32s ease,border-color .32s ease; will-change:transform; }.panel.three-hover-active { z-index:8; transform:perspective(900px) translateY(-8px) scale(1.012) rotateX(var(--three-rotate-x,0deg)) rotateY(var(--three-rotate-y,0deg)); border-color:rgba(103,239,255,.9); box-shadow:0 18px 32px rgba(0,0,0,.42),inset 0 0 36px rgba(35,218,255,.18),0 0 24px rgba(31,213,255,.3); }
.panel { isolation:isolate; animation:panel-breathe 5s ease-in-out infinite; }.hero-scan { position:absolute; z-index:-1; inset:-45% auto -45% -30%; width:30%; transform:rotate(18deg); background:linear-gradient(90deg,transparent,rgba(72,234,255,.13),transparent); animation:hero-scan 5.8s ease-in-out infinite; }.trend-line { stroke-dasharray:760; stroke-dashoffset:760; animation:chart-draw 2.2s ease-out forwards,chart-glow 2.8s ease-in-out 2.2s infinite; }.target-line { stroke-dashoffset:42; animation:target-flow 2s linear infinite; }.peak-dot { animation:peak-flash 1.4s ease-in-out infinite; }
@keyframes flyline { 0% { transform:translate3d(-60px,60px,0); opacity:0; } 12% { opacity:.85; } 72% { opacity:.5; } 100% { transform:translate3d(220px,-145px,0); opacity:0; } } @keyframes panel-breathe { 50% { border-color:rgba(74,223,255,.68); box-shadow:inset 0 0 34px rgba(0,180,229,.14),0 0 15px rgba(29,196,245,.12); } } @keyframes edge-scan { 0%,100% { left:0; width:38px; } 50% { left:calc(100% - 56px); width:56px; } } @keyframes hero-scan { 0%,18% { transform:translateX(0) rotate(18deg); opacity:0; } 38% { opacity:1; } 70%,100% { transform:translateX(510%) rotate(18deg); opacity:0; } } @keyframes chart-draw { to { stroke-dashoffset:0; } } @keyframes chart-glow { 50% { filter:drop-shadow(0 0 11px #22e6ff); } } @keyframes target-flow { to { stroke-dashoffset:0; } } @keyframes peak-flash { 50% { r:8px; filter:drop-shadow(0 0 11px #ff4ca3); } }
@media (prefers-reduced-motion: reduce) { *,*::before,*::after { animation-duration:.01ms !important; animation-iteration-count:1 !important; scroll-behavior:auto !important; } }
@media (max-width:1100px) { .screen { overflow:auto; }.screen-grid { height:auto; grid-template-columns:1fr; }.column { min-height:350px; }.center-column { min-height:600px; }.screen-title { min-width:0; }.header-rule { display:none; } }
</style>
