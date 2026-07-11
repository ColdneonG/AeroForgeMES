<template>
  <div class="sop3d-viewer">
    <div ref="host" class="sop3d-canvas"></div>
    <div class="sop3d-toolbar">
      <button type="button" @click="resetCamera">复位</button>
      <div class="mode-switch" aria-label="3D control mode">
        <button type="button" :class="{ active: mode === 'rotate' }" @click="setMode('rotate')">旋转</button>
        <button type="button" :class="{ active: mode === 'pan' }" @click="setMode('pan')">平移</button>
      </div>
      <span v-if="loading">加载 {{ progress }}%</span>
      <span v-else-if="error" class="error">{{ error }}</span>
      <span v-else-if="!src">未绑定 GLB 模型</span>
      <span v-else>{{ mode === 'pan' ? '左键拖动平移 / 滚轮缩放' : '左键旋转 / 滚轮缩放' }}</span>
    </div>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js'

const props = defineProps({
  src: {
    type: String,
    default: ''
  }
})

const host = ref(null)
const loading = ref(false)
const progress = ref(0)
const error = ref('')
const mode = ref('rotate')

let scene
let camera
let renderer
let controls
let model
let frameId
let resizeObserver

const init = () => {
  if (!host.value || renderer) return

  scene = new THREE.Scene()
  scene.background = new THREE.Color(0xf3f6f8)

  camera = new THREE.PerspectiveCamera(45, 1, 0.01, 5000)
  camera.position.set(2.4, 1.6, 2.8)

  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2))
  renderer.outputColorSpace = THREE.SRGBColorSpace
  host.value.appendChild(renderer.domElement)

  controls = new OrbitControls(camera, renderer.domElement)
  controls.enableDamping = true
  controls.enablePan = true
  controls.dampingFactor = 0.08
  controls.screenSpacePanning = true
  controls.panSpeed = 1.2
  applyControlMode()

  scene.add(new THREE.HemisphereLight(0xffffff, 0x607080, 2.2))
  const keyLight = new THREE.DirectionalLight(0xffffff, 2)
  keyLight.position.set(4, 6, 5)
  scene.add(keyLight)
  const fillLight = new THREE.DirectionalLight(0xffffff, 0.7)
  fillLight.position.set(-4, 2, -3)
  scene.add(fillLight)

  resizeObserver = new ResizeObserver(resize)
  resizeObserver.observe(host.value)
  resize()
  animate()
}

const animate = () => {
  frameId = window.requestAnimationFrame(animate)
  controls?.update()
  renderer?.render(scene, camera)
}

const resize = () => {
  if (!host.value || !renderer || !camera) return
  const rect = host.value.getBoundingClientRect()
  const width = Math.max(1, rect.width)
  const height = Math.max(1, rect.height)
  renderer.setSize(width, height, false)
  camera.aspect = width / height
  camera.updateProjectionMatrix()
}

const clearModel = () => {
  if (!model) return
  scene.remove(model)
  model.traverse((node) => {
    if (node.geometry) node.geometry.dispose()
    if (node.material) {
      const materials = Array.isArray(node.material) ? node.material : [node.material]
      materials.forEach((material) => {
        Object.values(material).forEach((value) => {
          if (value && typeof value.dispose === 'function') value.dispose()
        })
        material.dispose()
      })
    }
  })
  model = null
}

const fitModel = () => {
  if (!model || !camera || !controls) return
  const box = new THREE.Box3().setFromObject(model)
  const size = box.getSize(new THREE.Vector3())
  const center = box.getCenter(new THREE.Vector3())
  const maxSize = Math.max(size.x, size.y, size.z) || 1
  const distance = maxSize / (2 * Math.tan((camera.fov * Math.PI) / 360))

  controls.target.copy(center)
  camera.position.copy(center).add(new THREE.Vector3(distance * 0.9, distance * 0.55, distance * 1.2))
  camera.near = Math.max(maxSize / 1000, 0.01)
  camera.far = maxSize * 100
  camera.updateProjectionMatrix()
  controls.update()
}

const loadModel = async () => {
  await nextTick()
  init()
  clearModel()
  error.value = ''
  progress.value = 0
  if (!props.src) return

  loading.value = true
  const loader = new GLTFLoader()
  loader.load(
    props.src,
    (gltf) => {
      model = gltf.scene
      scene.add(model)
      loading.value = false
      progress.value = 100
      fitModel()
    },
    (event) => {
      if (event.total > 0) {
        progress.value = Math.round((event.loaded / event.total) * 100)
      }
    },
    (loadError) => {
      loading.value = false
      error.value = loadError?.message || 'GLB 加载失败'
    }
  )
}

const resetCamera = () => {
  fitModel()
}

const applyControlMode = () => {
  if (!controls) return
  controls.mouseButtons.LEFT = mode.value === 'pan' ? THREE.MOUSE.PAN : THREE.MOUSE.ROTATE
  controls.mouseButtons.MIDDLE = THREE.MOUSE.DOLLY
  controls.mouseButtons.RIGHT = mode.value === 'pan' ? THREE.MOUSE.ROTATE : THREE.MOUSE.PAN
  controls.touches.ONE = mode.value === 'pan' ? THREE.TOUCH.PAN : THREE.TOUCH.ROTATE
  controls.touches.TWO = THREE.TOUCH.DOLLY_PAN
}

const setMode = (nextMode) => {
  mode.value = nextMode
  applyControlMode()
}

const dispose = () => {
  if (frameId) window.cancelAnimationFrame(frameId)
  resizeObserver?.disconnect()
  clearModel()
  controls?.dispose()
  renderer?.dispose()
  renderer?.domElement?.remove()
  scene = null
  camera = null
  renderer = null
  controls = null
}

watch(() => props.src, loadModel)
onMounted(loadModel)
onBeforeUnmount(dispose)
</script>

<style scoped>
.sop3d-viewer {
  position: relative;
  min-height: 360px;
  overflow: hidden;
  background: #f3f6f8;
  border: 1px solid #d7dde1;
}

.sop3d-canvas {
  width: 100%;
  height: 420px;
}

.sop3d-toolbar {
  position: absolute;
  right: 10px;
  bottom: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 7px 9px;
  color: #53666f;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #d7dde1;
}

.sop3d-toolbar button {
  min-height: 28px;
  padding: 0 10px;
  color: #ffffff;
  background: #00799f;
  border: 0;
  cursor: pointer;
}

.mode-switch {
  display: inline-flex;
  overflow: hidden;
  border: 1px solid #b9c8d1;
}

.mode-switch button {
  color: #53666f;
  background: #ffffff;
  border: 0;
  border-left: 1px solid #d7dde1;
}

.mode-switch button:first-child {
  border-left: 0;
}

.mode-switch button.active {
  color: #ffffff;
  background: #00799f;
}

.sop3d-toolbar .error {
  color: #b42318;
}
</style>
