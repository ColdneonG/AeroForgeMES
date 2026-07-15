<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js'
import { getBlob } from '@/api/client'

const props = defineProps<{ modelVersionId: number | null | undefined }>()
const canvas = ref<HTMLCanvasElement | null>(null)
const loading = ref(false), error = ref('')
let cleanup = () => {}

async function load() {
  cleanup(); error.value = ''
  if (!props.modelVersionId || !canvas.value) return
  loading.value = true
  try {
    const blob = await getBlob(`/production/sop/model-files/${props.modelVersionId}`)
    const url = URL.createObjectURL(blob)
    const renderer = new THREE.WebGLRenderer({ canvas: canvas.value, antialias: true, alpha: true })
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
    const scene = new THREE.Scene(); scene.background = new THREE.Color(0xf8fafc)
    const camera = new THREE.PerspectiveCamera(45, 1, .01, 1000); camera.position.set(2, 1.5, 2.5)
    const controls = new OrbitControls(camera, renderer.domElement); controls.enableDamping = true
    scene.add(new THREE.HemisphereLight(0xffffff, 0x64748b, 2.4))
    const light = new THREE.DirectionalLight(0xffffff, 2); light.position.set(3, 5, 4); scene.add(light)
    const grid = new THREE.GridHelper(4, 12, 0xcbd5e1, 0xe2e8f0); scene.add(grid)
    const resize = () => { const width = canvas.value?.clientWidth || 1; const height = canvas.value?.clientHeight || 1; renderer.setSize(width, height, false); camera.aspect = width / height; camera.updateProjectionMatrix() }
    resize(); const observer = new ResizeObserver(resize); observer.observe(canvas.value)
    let frame = 0; const render = () => { frame = requestAnimationFrame(render); controls.update(); renderer.render(scene, camera) }; render()
    await new Promise<void>((resolve, reject) => new GLTFLoader().load(url, gltf => { const model = gltf.scene; scene.add(model); const box = new THREE.Box3().setFromObject(model); const size = box.getSize(new THREE.Vector3()).length() || 1; const center = box.getCenter(new THREE.Vector3()); model.position.sub(center); camera.position.set(size, size * .7, size); controls.target.set(0, 0, 0); controls.update(); resolve() }, undefined, reject))
    cleanup = () => { cancelAnimationFrame(frame); observer.disconnect(); controls.dispose(); scene.traverse(node => { const mesh = node as THREE.Mesh; if (mesh.geometry) mesh.geometry.dispose(); const material = mesh.material as THREE.Material | THREE.Material[]; (Array.isArray(material) ? material : [material]).forEach(item => item?.dispose()) }); renderer.dispose(); URL.revokeObjectURL(url) }
  } catch (e) { error.value = e instanceof Error ? e.message : '3D 模型加载失败' } finally { loading.value = false }
}
onMounted(load)
watch(() => props.modelVersionId, load)
onBeforeUnmount(() => cleanup())
</script>

<template><div class="viewer"><canvas ref="canvas"></canvas><span v-if="loading" class="viewer-message">模型加载中...</span><span v-else-if="error" class="viewer-message error">{{ error }}</span><span v-else-if="!modelVersionId" class="viewer-message">当前 SOP 版本未关联 3D 模型</span></div></template>
<style scoped>.viewer{position:relative;height:300px;overflow:hidden;border:1px solid var(--color-border,#e2e8f0);border-radius:6px;background:#f8fafc}.viewer canvas{width:100%;height:100%;display:block}.viewer-message{position:absolute;inset:0;display:grid;place-items:center;color:#64748b;font-size:13px;pointer-events:none}.error{color:#b42318}</style>
