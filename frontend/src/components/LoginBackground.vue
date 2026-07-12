<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import * as THREE from 'three'

const canvas = ref<HTMLCanvasElement | null>(null)
let renderer: THREE.WebGLRenderer | undefined
let scene: THREE.Scene | undefined
let camera: THREE.PerspectiveCamera | undefined
let geom: THREE.BufferGeometry | undefined
let mat: THREE.PointsMaterial | undefined
let accentGeom: THREE.BufferGeometry | undefined
let accentMat: THREE.PointsMaterial | undefined
let whiteTex: THREE.CanvasTexture | undefined
let blueTex: THREE.CanvasTexture | undefined
let animationFrame = 0
let reducedMotion: MediaQueryList | undefined

const COUNT = 220
const SPREAD_X = 36
const SPREAD_Y = 22
const SPREAD_Z = 14

function makeSprite(size: number, color: THREE.Color, alpha: number) {
  const spriteCanvas = document.createElement('canvas')
  spriteCanvas.width = spriteCanvas.height = size
  const context = spriteCanvas.getContext('2d')
  if (!context) throw new Error('Canvas 2D context unavailable')
  const gradient = context.createRadialGradient(size / 2, size / 2, 0, size / 2, size / 2, size / 2)
  gradient.addColorStop(0, `rgba(${color.r * 255},${color.g * 255},${color.b * 255},${alpha})`)
  gradient.addColorStop(0.3, `rgba(${color.r * 255},${color.g * 255},${color.b * 255},${alpha * 0.6})`)
  gradient.addColorStop(1, 'rgba(0,0,0,0)')
  context.fillStyle = gradient
  context.fillRect(0, 0, size, size)
  return new THREE.CanvasTexture(spriteCanvas)
}

function resize() {
  if (!renderer || !camera) return
  const width = window.innerWidth
  const height = window.innerHeight
  renderer.setSize(width, height, false)
  camera.aspect = width / Math.max(height, 1)
  camera.updateProjectionMatrix()
}

onMounted(() => {
  if (!canvas.value) return
  try {
    reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)')

    renderer = new THREE.WebGLRenderer({ canvas: canvas.value, antialias: true, alpha: false })
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
    renderer.setClearColor(0x171a20, 1)

    scene = new THREE.Scene()
    camera = new THREE.PerspectiveCamera(60, 2, 0.5, 80)
    camera.position.set(0, 0, 20)
    camera.lookAt(0, 0, 0)

    whiteTex = makeSprite(64, new THREE.Color(1, 1, 1), 0.9)
    blueTex = makeSprite(64, new THREE.Color(0x3e / 255, 0x6a / 255, 0xe1 / 255), 1)

    const positions = new Float32Array(COUNT * 3)
    const colors = new Float32Array(COUNT * 3)
    const sizes = new Float32Array(COUNT)
    const velocities = new Float32Array(COUNT)
    const accentColor = new THREE.Color(0x3e / 255, 0x6a / 255, 0xe1 / 255)
    const mutedColor = new THREE.Color(1, 1, 1)

    for (let index = 0; index < COUNT; index++) {
      const offset = index * 3
      positions[offset] = (Math.random() - 0.5) * SPREAD_X
      positions[offset + 1] = (Math.random() - 0.5) * SPREAD_Y
      positions[offset + 2] = (Math.random() - 0.5) * SPREAD_Z
      const isAccent = Math.random() < 0.15
      const color = isAccent ? accentColor : mutedColor
      colors[offset] = color.r
      colors[offset + 1] = color.g
      colors[offset + 2] = color.b
      sizes[index] = isAccent ? 0.45 + Math.random() * 0.5 : 0.18 + Math.random() * 0.35
      velocities[index] = 0.15 + Math.random() * 0.55
    }

    geom = new THREE.BufferGeometry()
    geom.setAttribute('position', new THREE.BufferAttribute(positions, 3))
    geom.setAttribute('color', new THREE.BufferAttribute(colors, 3))
    geom.setAttribute('size', new THREE.BufferAttribute(sizes, 1))
    mat = new THREE.PointsMaterial({
      size: 0.7,
      map: whiteTex,
      vertexColors: true,
      blending: THREE.AdditiveBlending,
      depthWrite: false,
      depthTest: false,
      transparent: true,
      opacity: 0.72,
    })
    scene.add(new THREE.Points(geom, mat))

    const accentCount = 18
    const accentPositions = new Float32Array(accentCount * 3)
    const accentSizes = new Float32Array(accentCount)
    const accentVelocities = new Float32Array(accentCount)
    for (let index = 0; index < accentCount; index++) {
      const offset = index * 3
      accentPositions[offset] = (Math.random() - 0.5) * SPREAD_X
      accentPositions[offset + 1] = (Math.random() - 0.5) * SPREAD_Y
      accentPositions[offset + 2] = (Math.random() - 0.5) * SPREAD_Z
      accentSizes[index] = 0.9 + Math.random() * 0.8
      accentVelocities[index] = 0.08 + Math.random() * 0.3
    }

    accentGeom = new THREE.BufferGeometry()
    accentGeom.setAttribute('position', new THREE.BufferAttribute(accentPositions, 3))
    accentGeom.setAttribute('size', new THREE.BufferAttribute(accentSizes, 1))
    accentMat = new THREE.PointsMaterial({
      size: 1.1,
      map: blueTex,
      blending: THREE.AdditiveBlending,
      depthWrite: false,
      depthTest: false,
      transparent: true,
      opacity: 0.55,
    })
    scene.add(new THREE.Points(accentGeom, accentMat))

    const clock = new THREE.Clock()
    const animate = () => {
      if (!renderer || !scene || !camera || !geom || !accentGeom) return
      const delta = Math.min(clock.getDelta(), 0.1)
      const elapsed = clock.elapsedTime

      if (!reducedMotion?.matches) {
        camera.position.x = Math.sin(elapsed * 0.12) * 1.5
        camera.position.y = Math.cos(elapsed * 0.08) * 0.8
        camera.lookAt(0, 2, 0)

        const values = geom.attributes.position.array as Float32Array
        for (let index = 0; index < COUNT; index++) {
          const offset = index * 3
          values[offset + 1] += velocities[index] * delta
          values[offset] += Math.sin(elapsed * 0.4 + index * 0.3) * 0.015
          if (values[offset + 1] > SPREAD_Y / 2 + 2) {
            values[offset + 1] = -SPREAD_Y / 2 - 2
            values[offset] = (Math.random() - 0.5) * SPREAD_X
            values[offset + 2] = (Math.random() - 0.5) * SPREAD_Z
          }
        }
        geom.attributes.position.needsUpdate = true

        const accentValues = accentGeom.attributes.position.array as Float32Array
        for (let index = 0; index < accentCount; index++) {
          const offset = index * 3
          accentValues[offset + 1] += accentVelocities[index] * delta
          accentValues[offset] += Math.sin(elapsed * 0.3 + index * 0.7) * 0.02
          if (accentValues[offset + 1] > SPREAD_Y / 2 + 2) {
            accentValues[offset + 1] = -SPREAD_Y / 2 - 2
            accentValues[offset] = (Math.random() - 0.5) * SPREAD_X
            accentValues[offset + 2] = (Math.random() - 0.5) * SPREAD_Z
          }
        }
        accentGeom.attributes.position.needsUpdate = true
      }

      renderer.render(scene, camera)
      animationFrame = requestAnimationFrame(animate)
    }

    resize()
    window.addEventListener('resize', resize)
    animate()
  } catch {
    // Keep the original carbon background when WebGL is unavailable.
  }
})

onBeforeUnmount(() => {
  cancelAnimationFrame(animationFrame)
  window.removeEventListener('resize', resize)
  geom?.dispose()
  accentGeom?.dispose()
  mat?.dispose()
  accentMat?.dispose()
  whiteTex?.dispose()
  blueTex?.dispose()
  renderer?.dispose()
  renderer?.forceContextLoss()
  scene?.clear()
})
</script>

<template>
  <canvas id="login-canvas" ref="canvas" aria-hidden="true"></canvas>
</template>
