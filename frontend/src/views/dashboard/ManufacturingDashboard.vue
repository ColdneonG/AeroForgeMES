<template>
  <div class="plant-dashboard">
    <section class="line-panel">
      <h1>Packaging Lines</h1>

      <div class="line-grid">
        <article
          v-for="line in lines"
          :key="line.id"
          class="line-card"
          :class="{ active: line.active, collapsed: line.collapsed }"
        >
          <template v-if="!line.collapsed">
            <div class="line-top">
              <div class="line-id">
                <span class="gear-mark">++</span>
                <div>
                  <strong>{{ line.id }}</strong>
                  <span>{{ line.name }}</span>
                </div>
              </div>
              <div class="line-alerts">
                <span>{{ line.counts[0] }}</span><i class="person"></i>
                <span>{{ line.counts[1] }}</span><i class="warn"></i>
                <span>{{ line.counts[2] }}</span><i class="flag"></i>
              </div>
              <button aria-label="collapse">⌃</button>
            </div>

            <div class="card-body">
              <div class="line-copy">
                <span>Lot</span>
                <strong>{{ line.lot }}</strong>
                <span>Work Order</span>
                <strong>{{ line.workOrder }}</strong>
                <span>Product</span>
                <strong>{{ line.product }}</strong>
                <span>Items Produced / To Be Produced</span>
                <strong>{{ line.produced }}</strong>
              </div>

              <div class="donut" :style="{ '--value': line.oee }">
                <span>{{ line.oee }}%</span>
              </div>
            </div>

            <div class="card-footer">
              <div class="micro">
                <div class="tiny-bars">
                  <i v-for="(height, index) in line.forecast" :key="index" :style="{ height: height + '%' }"></i>
                </div>
                <span>Output Performance</span>
                <strong>{{ line.performance }}%</strong>
              </div>
              <div class="forecast">
                <span>Year Forecast</span>
                <strong>{{ line.forecastText }}</strong>
              </div>
            </div>
          </template>

          <template v-else>
            <div class="collapsed-head">
              <span class="gear-mark">++</span>
              <div>
                <strong>{{ line.id }}</strong>
                <span>{{ line.name }}</span>
              </div>
              <button aria-label="expand">⌄</button>
            </div>
          </template>
        </article>
      </div>
    </section>

    <section class="metric-panel">
      <div class="gauge-grid">
        <article v-for="gauge in gauges" :key="gauge.label" class="gauge-card">
          <h2>{{ gauge.label }}</h2>
          <svg viewBox="0 0 236 142" role="img" :aria-label="`${gauge.label} ${gauge.value}%`">
            <path class="arc red" d="M28 112 A90 90 0 0 1 58 45" />
            <path class="arc yellow" d="M58 45 A90 90 0 0 1 178 45" />
            <path class="arc green" d="M178 45 A90 90 0 0 1 208 112" />
            <g class="ticks">
              <text x="27" y="122">0</text>
              <text x="54" y="57">25</text>
              <text x="116" y="32">50</text>
              <text x="174" y="57">75</text>
              <text x="202" y="122">100</text>
            </g>
            <line
              class="needle"
              x1="118"
              y1="112"
              :x2="needlePoint(gauge.value).x"
              :y2="needlePoint(gauge.value).y"
            />
            <circle cx="118" cy="112" r="5" fill="#73818b" />
          </svg>
          <strong>{{ gauge.value }} %</strong>
        </article>
      </div>

      <article class="stock-card">
        <header>
          <span class="stock-icon">▥</span>
          <div>
            <h2>Stock Level</h2>
            <p>Stock picture on the dock level of packaging materials</p>
          </div>
          <button aria-label="collapse stock">⌃</button>
        </header>

        <div class="stock-table-wrap">
          <table>
            <thead>
              <tr>
                <th></th>
                <th>Material</th>
                <th>Required Qty.</th>
                <th>Actual Qty.</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in stock" :key="item.material">
                <td><span :class="['stock-state', item.state]"></span></td>
                <td>
                  <small>{{ item.code }}</small>
                  <strong>{{ item.material }}</strong>
                </td>
                <td>{{ item.required }}</td>
                <td>{{ item.actual }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
const lines = [
  {
    id: 'PCK-LINE-01',
    name: 'Packaging Line 01',
    lot: 'BAT202201',
    workOrder: 'OF0000001',
    product: '101010 MLK TYPE A',
    produced: '375.000 / 500.000',
    forecastText: '485.000',
    oee: 75,
    performance: 97,
    forecast: [30, 52, 70, 92, 66],
    counts: [7, 2, 4],
    active: true
  },
  {
    id: 'PCK-LINE-02',
    name: 'Packaging Line 02',
    lot: 'BAT202202',
    workOrder: 'OF0000005',
    product: '202020 CHOCO-RUSH PLUS',
    produced: '390.000 / 650.000',
    forecastText: '671.000',
    oee: 75,
    performance: 97,
    forecast: [35, 56, 78, 88, 62],
    counts: [4, 2, 4]
  },
  {
    id: 'PCK-LINE-03',
    name: 'Packaging Line 03',
    lot: 'BAT202201',
    workOrder: 'OF0000007',
    product: '101010 MLK TYPE A',
    produced: '375.000 / 500.000',
    forecastText: '485.000',
    oee: 17,
    performance: 65,
    forecast: [15, 24, 34, 84, 68],
    counts: [7, 2, 4]
  },
  {
    id: 'PCK-LINE-04',
    name: 'Packaging Line 04',
    lot: 'N.A.',
    workOrder: 'N.A.',
    product: 'N.A.',
    produced: 'N.A.',
    forecastText: 'N.A.',
    oee: 0,
    performance: 0,
    forecast: [8, 12, 18, 30, 12],
    counts: [4, 2, 4]
  },
  { id: 'PCK-LINE-05', name: 'Packaging Line 05', collapsed: true },
  { id: 'PCK-LINE-06', name: 'Packaging Line 06', collapsed: true },
  { id: 'PCK-LINE-07', name: 'Packaging Line 07', collapsed: true }
]

const gauges = [
  { label: 'OEE', value: 88 },
  { label: 'Availability', value: 97 },
  { label: 'Performance', value: 88 },
  { label: 'Quality', value: 95 }
]

const stock = [
  { state: 'ok', code: '19934985', material: 'CARDBOARD CARTONS X30', required: '750 PC', actual: '942 PC' },
  { state: 'danger', code: '1993475', material: 'CARDBOARD CARTONS X25', required: '500 PC', actual: '342 PC' },
  { state: 'info', code: '1993474', material: 'CARDBOARD CARTONS X20', required: '500 PC', actual: '490 PC' },
  { state: 'ok', code: '1999470 16 x 3', material: 'POLYESTER STRAP', required: '50 PC', actual: '63 PC' }
]

function needlePoint(value) {
  const angle = Math.PI - (Math.PI * value) / 100
  const radius = 76
  return {
    x: 118 + Math.cos(angle) * radius,
    y: 112 - Math.sin(angle) * radius
  }
}
</script>
