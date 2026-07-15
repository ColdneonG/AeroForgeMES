<script setup lang="ts">
import { computed } from 'vue'

type Point = { label: string; value: number }
const props = withDefaults(defineProps<{ points: Point[]; height?: number; emptyText?: string }>(), { height: 230, emptyText: '暂无可视化数据' })
const chart = computed(() => {
  const points = props.points.slice(-8)
  const max = Math.max(...points.map(point => point.value), 1)
  const width = 700, left = 44, right = 18, top = 20, bottom = 34, plotHeight = 166
  const slot = (width - left - right) / Math.max(points.length, 1)
  return { width, left, right, top, bottom, plotHeight, max, slot, points }
})
function height(value: number) { return Math.max(2, Math.round((value / chart.value.max) * chart.value.plotHeight)) }
function x(index: number) { return chart.value.left + index * chart.value.slot + chart.value.slot * .16 }
function width() { return chart.value.slot * .68 }
function y(value: number) { return chart.value.top + chart.value.plotHeight - height(value) }
function tick(index: number) { return Math.round(chart.value.max * (1 - index / 4)) }
</script>

<template><div class="chart-shell"><svg v-if="chart.points.length" :viewBox="`0 0 ${chart.width} 230`" :style="{ height: `${height}px` }" role="img" aria-label="产量柱形图"><defs><linearGradient id="output-bar" x1="0" y1="0" x2="0" y2="1"><stop stop-color="#4a80dc"/><stop offset="1" stop-color="#2f61ad"/></linearGradient><linearGradient id="output-bar-active" x1="0" y1="0" x2="0" y2="1"><stop stop-color="#74a4f3"/><stop offset="1" stop-color="#2f61ad"/></linearGradient></defs><g v-for="i in 5" :key="i"><line :x1="chart.left" :x2="chart.width-chart.right" :y1="chart.top+(i-1)*chart.plotHeight/4" :y2="chart.top+(i-1)*chart.plotHeight/4" class="grid"/><text x="5" :y="chart.top+(i-1)*chart.plotHeight/4+4" class="axis">{{ tick(i-1) }}</text></g><g v-for="(point,index) in chart.points" :key="`${point.label}-${index}`" class="bar" :style="{ '--bar-delay': `${index * 90}ms` }"><rect :x="x(index)" :y="y(point.value)" :width="width()" :height="height(point.value)" :fill="index===chart.points.length-1?'url(#output-bar-active)':'url(#output-bar)'" rx="4"/><text :x="x(index)+width()/2" :y="y(point.value)-7" text-anchor="middle" class="value">{{ point.value }}</text><text :x="x(index)+width()/2" y="215" text-anchor="middle" class="axis">{{ point.label }}</text></g></svg><div v-else class="empty">{{ emptyText }}</div></div></template>
<style scoped>.chart-shell{min-height:230px}.chart-shell svg{width:100%;display:block;font-family:var(--font-mono,monospace)}.grid{stroke:#e7ebf0;stroke-width:1}.axis{fill:#778294;font-size:11px}.value{fill:#334155;font-size:11px;font-weight:700}.bar{transition:opacity .18s ease}.bar:hover{opacity:.75}.bar rect{transform-box:fill-box;transform-origin:center bottom;animation:bar-grow .62s cubic-bezier(.2,.8,.2,1) var(--bar-delay) both}.bar .value{animation:value-fade .3s ease calc(var(--bar-delay) + .42s) both}.bar .axis{animation:value-fade .28s ease calc(var(--bar-delay) + .18s) both}.empty{height:230px;display:grid;place-items:center;color:#98a2b3;font-size:13px}@keyframes bar-grow{from{transform:scaleY(0);opacity:.25}to{transform:scaleY(1);opacity:1}}@keyframes value-fade{from{opacity:0;transform:translateY(5px)}to{opacity:1;transform:translateY(0)}}@media(prefers-reduced-motion:reduce){.bar rect,.bar .value,.bar .axis{animation:none}}</style>
