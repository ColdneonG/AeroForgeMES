<template>
  <section class="mes-workspace electronic-sop-page">
    <div class="mes-page-heading">
      <div>
        <p>现场管理 / 电子 SOP</p>
        <h1>电子 SOP 编制与现场执行</h1>
        <span>受控版本、任务锁版、步骤确认、附件上传和 GLB 三维预览。</span>
      </div>
      <div class="mes-toolbar">
        <button class="mes-action-button" :class="{ 'is-disabled': activeTab === 'manage' }" @click="activeTab = 'manage'">SOP 管理</button>
        <button class="mes-action-button" :class="{ 'is-disabled': activeTab === 'execute' }" @click="activeTab = 'execute'">现场执行</button>
        <button class="mes-action-button" @click="refreshAll">刷新</button>
      </div>
    </div>

    <p v-if="loading" class="api-state">正在加载电子 SOP 数据...</p>
    <p v-if="error" class="api-state error">{{ error }}</p>

    <div v-if="activeTab === 'manage'" class="sop-manage-grid">
      <section class="mes-panel">
        <div class="mes-panel-title">
          <strong>SOP 主档</strong>
          <span>{{ documents.length }} 条</span>
        </div>
        <form class="sop-form" @submit.prevent="saveDocument">
          <input v-model="docForm.sopCode" placeholder="SOP 编码" />
          <input v-model="docForm.sopName" placeholder="SOP 名称" />
          <select v-model="docForm.category">
            <option value="STANDARD_OPERATION">标准作业</option>
            <option value="CHANGEOVER">换线换型</option>
            <option value="EQUIPMENT_OPERATION">设备操作</option>
            <option value="QUALITY_INSPECTION">质量检验</option>
            <option value="SAFETY">安全作业</option>
          </select>
          <button class="mes-action-button" type="submit">保存主档</button>
        </form>
        <div class="sop-table-wrap">
          <table class="mes-table">
            <thead>
              <tr><th>编码</th><th>名称</th><th>分类</th><th>状态</th></tr>
            </thead>
            <tbody>
              <tr
                v-for="doc in documents"
                :key="doc.id"
                :class="{ selected: selectedDocument?.id === doc.id }"
                @click="selectDocument(doc)"
              >
                <td>{{ doc.sopCode }}</td>
                <td>{{ doc.sopName }}</td>
                <td>{{ doc.category }}</td>
                <td><span class="status-pill">{{ doc.status }}</span></td>
              </tr>
              <tr v-if="!documents.length"><td colspan="4" class="empty-cell">暂无 SOP 主档</td></tr>
            </tbody>
          </table>
        </div>
      </section>

      <section class="mes-panel">
        <div class="mes-panel-title">
          <strong>版本控制</strong>
          <span>{{ selectedDocument?.sopCode || '未选择主档' }}</span>
        </div>
        <form class="sop-form" @submit.prevent="createVersionForDocument">
          <input v-model="versionForm.versionNo" placeholder="版本号，如 V1.0" />
          <select v-model="versionForm.revisionType">
            <option value="NEW">新建</option>
            <option value="MINOR">一般修订</option>
            <option value="MAJOR">重大修订</option>
            <option value="EMERGENCY">紧急修订</option>
          </select>
          <button class="mes-action-button" type="submit" :disabled="!selectedDocument">创建版本</button>
        </form>
        <div class="version-list">
          <article
            v-for="version in versions"
            :key="version.id"
            :class="{ active: selectedVersion?.id === version.id }"
            @click="selectVersion(version)"
          >
            <strong>{{ version.versionNo }}</strong>
            <span>{{ version.revisionType }} / {{ version.status }}</span>
          </article>
          <p v-if="!versions.length" class="empty-detail">选择主档后创建版本</p>
        </div>
        <div class="sop-actions">
          <button class="mes-action-button" :disabled="!selectedVersion" @click="copyVersionForDocument">复制新版</button>
          <button class="mes-action-button" :disabled="!selectedVersion" @click="submitVersion">提交</button>
          <button class="mes-action-button" :disabled="!selectedVersion" @click="approveVersion">审核通过</button>
          <button class="mes-action-button" :disabled="!selectedVersion" @click="publishVersion">发布</button>
          <button class="mes-action-button" :disabled="!selectedVersion" @click="disableVersion">停用</button>
        </div>
      </section>

      <section class="mes-panel sop-editor">
        <div class="mes-panel-title">
          <strong>步骤与内容</strong>
          <span>{{ selectedVersion?.versionNo || '未选择版本' }}</span>
        </div>
        <form class="sop-step-form" @submit.prevent="addStep">
          <input v-model.number="stepForm.stepNo" type="number" placeholder="序号" />
          <input v-model="stepForm.stepName" placeholder="步骤名称" />
          <select v-model="stepForm.contentType">
            <option value="TEXT">文字</option>
            <option value="IMAGE">图片</option>
            <option value="VIDEO">视频</option>
            <option value="PDF">PDF</option>
            <option value="MODEL_3D">三维模型</option>
          </select>
          <label><input v-model="stepForm.keyStep" type="checkbox" /> 关键</label>
          <label><input v-model="stepForm.parameterRequired" type="checkbox" /> 参数</label>
          <label><input v-model="stepForm.photoRequired" type="checkbox" /> 拍照</label>
          <textarea v-model="stepForm.instruction" placeholder="操作说明"></textarea>
          <button class="mes-action-button" type="submit" :disabled="!isVersionEditable">新增步骤</button>
        </form>
        <div class="sop-table-wrap">
          <table class="mes-table">
            <thead>
              <tr><th>序号</th><th>步骤</th><th>内容</th><th>要求</th><th>操作</th></tr>
            </thead>
            <tbody>
              <tr v-for="step in steps" :key="step.id">
                <td>{{ step.stepNo }}</td>
                <td>{{ step.stepName }}</td>
                <td>{{ step.contentType }}</td>
                <td>
                  <span v-if="step.keyStep">关键 </span>
                  <span v-if="step.confirmRequired">确认 </span>
                  <span v-if="step.parameterRequired">参数 </span>
                  <span v-if="step.photoRequired">拍照</span>
                </td>
                <td class="row-actions">
                  <label class="file-button">
                    附件
                    <input type="file" @change="(event) => uploadAttachmentForStep(event, step.id)" />
                  </label>
                  <button class="mes-action-button" :disabled="!isVersionEditable" @click="removeStep(step)">删除</button>
                </td>
              </tr>
              <tr v-if="!steps.length"><td colspan="5" class="empty-cell">当前版本还没有步骤</td></tr>
            </tbody>
          </table>
        </div>
      </section>

      <section class="mes-panel">
        <div class="mes-panel-title">
          <strong>附件、GLB 与绑定</strong>
          <span>{{ isVersionEditable ? '草稿可维护' : '发布后只读' }}</span>
        </div>
        <div class="upload-row">
          <label class="file-button">
            上传通用附件
            <input type="file" @change="uploadVersionAttachment" />
          </label>
          <label class="file-button">
            上传 GLB 模型
            <input type="file" accept=".glb,model/gltf-binary" @change="uploadModelForVersion" />
          </label>
        </div>
        <ul class="attachment-list">
          <li v-for="file in attachments" :key="file.id">
            <strong>{{ file.attachmentType }}</strong>
            <span>{{ file.fileName }}</span>
          </li>
          <li v-if="!attachments.length">暂无附件</li>
        </ul>
        <form class="sop-form binding-form" @submit.prevent="addBinding">
          <select v-model="bindingForm.bindingType">
            <option value="PRODUCT_ROUTE_STATION">产品+路线+工位</option>
            <option value="PRODUCT_ROUTE">产品+路线</option>
            <option value="TASK">任务指定</option>
          </select>
          <input v-model="bindingForm.productId" placeholder="产品ID" />
          <input v-model="bindingForm.routeId" placeholder="路线ID" />
          <input v-model="bindingForm.workstationId" placeholder="工位ID" />
          <input v-model="bindingForm.taskId" placeholder="任务ID" />
          <button class="mes-action-button" type="submit" :disabled="!isVersionEditable">新增绑定</button>
        </form>
        <div class="binding-list">
          <article v-for="binding in bindings" :key="binding.id">
            <strong>{{ binding.bindingType }}</strong>
            <span>产品 {{ binding.productId || '-' }} / 路线 {{ binding.routeId || '-' }} / 工位 {{ binding.workstationId || '-' }} / 优先级 {{ binding.priority }}</span>
            <button class="mes-action-button" :disabled="!isVersionEditable" @click="removeBinding(binding)">删除</button>
          </article>
          <p v-if="!bindings.length" class="empty-detail">暂无绑定</p>
        </div>
      </section>
    </div>

    <div v-else class="sop-execute-grid">
      <section class="mes-panel">
        <div class="mes-panel-title">
          <strong>当前任务</strong>
          <span>{{ tasks.length }} 条任务</span>
        </div>
        <div class="task-list">
          <article
            v-for="task in tasks"
            :key="task.id"
            :class="{ active: selectedTaskId === task.id }"
            @click="selectTask(task.id)"
          >
            <strong>{{ task.taskNo || task.task_no || task.id }}</strong>
            <span>产品 {{ task.productName || task.productId || '-' }} / 线体 {{ task.lineName || task.lineId || '-' }}</span>
            <em>{{ task.status }}</em>
          </article>
          <p v-if="!tasks.length" class="empty-detail">暂无生产任务</p>
        </div>
      </section>

      <section class="mes-panel execution-main">
        <div class="mes-panel-title">
          <strong>{{ sopPackage?.document?.sopName || '任务电子 SOP' }}</strong>
          <span>{{ sopPackage?.version?.versionNo || '未锁版' }}</span>
        </div>
        <div class="execution-toolbar">
          <button class="mes-action-button" :disabled="!selectedTaskId" @click="openTask">加载并锁版</button>
          <button class="mes-action-button" :disabled="!selectedTaskId" @click="checkBeforeReport">报工前校验</button>
          <span>{{ validationMessage }}</span>
        </div>
        <div v-if="sopPackage" class="task-meta">
          <span>任务 {{ sopPackage.snapshot.taskId }}</span>
          <span>SOP {{ sopPackage.document.sopCode }}</span>
          <span>锁定 {{ formatTime(sopPackage.snapshot.lockedAt) }}</span>
          <span>规则 {{ sopPackage.snapshot.matchRule }}</span>
        </div>
        <div v-if="sopPackage" class="step-layout">
          <aside class="step-nav">
            <button
              v-for="(step, index) in sopPackage.steps"
              :key="step.id"
              :class="{ active: index === currentStepIndex, done: isStepConfirmed(step.id) }"
              @click="selectExecutionStep(index)"
            >
              <strong>{{ step.stepNo }}</strong>
              <span>{{ step.stepName }}</span>
            </button>
          </aside>
          <main v-if="currentStep" class="step-content">
            <header>
              <div>
                <p>{{ currentStep.contentType }}</p>
                <h2>{{ currentStep.stepName }}</h2>
              </div>
              <span class="status-pill">{{ isStepConfirmed(currentStep.id) ? '已确认' : '待确认' }}</span>
            </header>
            <p class="instruction">{{ currentStep.instruction }}</p>
            <Sop3dViewer v-if="showModelViewer" :src="modelUrl" />
            <div v-else class="media-preview">
              <template v-if="currentMedia.length">
                <img v-for="media in imageMedia" :key="media.id" :src="media.fileUrl" :alt="media.fileName" />
                <video v-for="media in videoMedia" :key="media.id" :src="media.fileUrl" controls></video>
                <iframe v-for="media in pdfMedia" :key="media.id" :src="media.fileUrl"></iframe>
              </template>
              <p v-else>当前步骤以文字说明为主，可在管理端上传图片、视频、PDF 或 GLB。</p>
            </div>
            <div class="confirm-box">
              <label v-if="currentStep.parameterRequired">
                参数记录
                <input v-model="stepParameter" placeholder='例如 {"torque":"2.5N·m"}' />
              </label>
              <button class="mes-action-button" :disabled="isStepConfirmed(currentStep.id)" @click="confirmCurrentStep">确认当前步骤</button>
            </div>
          </main>
        </div>
        <p v-else class="empty-detail">选择任务后加载 SOP，系统会按绑定规则锁定具体版本。</p>
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { authState } from '../../stores/auth'
import { getShopTasks } from '../../api/production'
import {
  approveSopVersion,
  confirmSopStep,
  copySopVersion,
  createSopBinding,
  createSopDocument,
  createSopStep,
  createSopVersion,
  deleteSopBinding,
  deleteSopStep,
  disableSopVersion as disableSopVersionApi,
  listSopAttachments,
  listSopBindings,
  listSopDocuments,
  listSopSteps,
  listSopVersions,
  openTaskSop,
  publishSopVersion as publishSopVersionApi,
  submitSopVersion,
  uploadSopAttachment,
  uploadSopModel,
  updateSopDocument,
  validateSopBeforeReport,
  viewSopStep
} from '../../api/sop'
import Sop3dViewer from '../../components/sop3d/Sop3dViewer.vue'

const activeTab = ref('execute')
const loading = ref(false)
const error = ref('')
const documents = ref([])
const versions = ref([])
const steps = ref([])
const attachments = ref([])
const bindings = ref([])
const selectedDocument = ref(null)
const selectedVersion = ref(null)
const tasks = ref([])
const selectedTaskId = ref(null)
const sopPackage = ref(null)
const currentStepIndex = ref(0)
const stepParameter = ref('')
const validationMessage = ref('')

const operatorId = computed(() => authState.user?.id || 1000001)
const isVersionEditable = computed(() => ['DRAFT', 'REJECTED'].includes(selectedVersion.value?.status))
const currentStep = computed(() => sopPackage.value?.steps?.[currentStepIndex.value] || null)
const stepRecordsById = computed(() => {
  const map = new Map()
  ;(sopPackage.value?.stepRecords || []).forEach((record) => map.set(record.stepId, record))
  return map
})
const currentMedia = computed(() => {
  if (!currentStep.value) return []
  return (sopPackage.value?.attachments || []).filter((file) => !file.stepId || file.stepId === currentStep.value.id)
})
const currentStepModelAttachment = computed(() => {
  if (!currentStep.value) return null
  return currentMedia.value.find((file) => file.stepId === currentStep.value.id && file.attachmentType === 'MODEL_3D') || null
})
const modelUrl = computed(() => {
  if (currentStepModelAttachment.value?.fileUrl) return currentStepModelAttachment.value.fileUrl
  const modelVersionId = sopPackage.value?.version?.modelVersionId
  return modelVersionId ? `/api/production/sop/model-files/${modelVersionId}` : ''
})
const showModelViewer = computed(() => currentStep.value?.contentType === 'MODEL_3D' && Boolean(modelUrl.value))
const imageMedia = computed(() => currentMedia.value.filter((file) => file.attachmentType === 'IMAGE'))
const videoMedia = computed(() => currentMedia.value.filter((file) => file.attachmentType === 'VIDEO'))
const pdfMedia = computed(() => currentMedia.value.filter((file) => file.attachmentType === 'PDF'))

const docForm = reactive({
  sopCode: '',
  sopName: '',
  category: 'STANDARD_OPERATION'
})
const versionForm = reactive({
  versionNo: '',
  revisionType: 'NEW'
})
const stepForm = reactive({
  stepNo: 10,
  stepName: '',
  contentType: 'TEXT',
  keyStep: false,
  parameterRequired: false,
  photoRequired: false,
  instruction: ''
})
const bindingForm = reactive({
  bindingType: 'PRODUCT_ROUTE_STATION',
  productId: '9400001',
  routeId: '9700201',
  workstationId: '9600001',
  taskId: ''
})

const recordsOf = (payload) => (Array.isArray(payload) ? payload : payload?.records || payload?.data || [])
const numberOrNull = (value) => {
  if (value === '' || value === null || value === undefined) return null
  const number = Number(value)
  return Number.isFinite(number) ? number : null
}

const withLoading = async (work) => {
  loading.value = true
  error.value = ''
  try {
    return await work()
  } catch (err) {
    error.value = err?.message || '电子 SOP 操作失败'
    throw err
  } finally {
    loading.value = false
  }
}

const loadDocuments = async () => {
  documents.value = recordsOf(await listSopDocuments())
  if (!selectedDocument.value && documents.value[0]) {
    await selectDocument(documents.value[0])
  }
}

const loadVersionDetail = async () => {
  if (!selectedVersion.value) return
  const versionId = selectedVersion.value.id
  ;[steps.value, attachments.value, bindings.value] = await Promise.all([
    listSopSteps(versionId),
    listSopAttachments(versionId),
    listSopBindings(versionId)
  ])
}

const selectDocument = async (doc) => {
  selectedDocument.value = doc
  docForm.sopCode = doc.sopCode
  docForm.sopName = doc.sopName
  docForm.category = doc.category || 'STANDARD_OPERATION'
  versions.value = recordsOf(await listSopVersions(doc.id))
  selectedVersion.value = versions.value[0] || null
  await loadVersionDetail()
}

const selectVersion = async (version) => {
  selectedVersion.value = version
  await loadVersionDetail()
}

const saveDocument = () => withLoading(async () => {
  const payload = { ...docForm, ownerId: operatorId.value, status: 'ENABLED' }
  const doc = selectedDocument.value
    ? await updateSopDocument(selectedDocument.value.id, payload)
    : await createSopDocument(payload)
  await loadDocuments()
  await selectDocument(doc)
})

const createVersionForDocument = () => withLoading(async () => {
  if (!selectedDocument.value) return
  const version = await createSopVersion(selectedDocument.value.id, {
    versionNo: versionForm.versionNo || undefined,
    revisionType: versionForm.revisionType,
    operatorId: operatorId.value
  })
  versions.value = recordsOf(await listSopVersions(selectedDocument.value.id))
  await selectVersion(version)
})

const copyVersionForDocument = () => withLoading(async () => {
  if (!selectedVersion.value) return
  const version = await copySopVersion(selectedVersion.value.id, {
    revisionType: 'MINOR',
    operatorId: operatorId.value,
    remark: `copy from ${selectedVersion.value.versionNo}`
  })
  versions.value = recordsOf(await listSopVersions(selectedDocument.value.id))
  await selectVersion(version)
})

const runVersionAction = (action) => withLoading(async () => {
  if (!selectedVersion.value) return
  selectedVersion.value = await action(selectedVersion.value.id, { operatorId: operatorId.value })
  versions.value = recordsOf(await listSopVersions(selectedDocument.value.id))
})

const submitVersion = () => runVersionAction(submitSopVersion)
const approveVersion = () => runVersionAction(approveSopVersion)
const publishVersion = () => runVersionAction(publishSopVersionApi)
const disableVersion = () => runVersionAction(disableSopVersionApi)

const addStep = () => withLoading(async () => {
  if (!selectedVersion.value) return
  await createSopStep(selectedVersion.value.id, {
    stepNo: stepForm.stepNo,
    stepName: stepForm.stepName,
    contentType: stepForm.contentType,
    instruction: stepForm.instruction,
    keyStep: stepForm.keyStep,
    confirmRequired: true,
    parameterRequired: stepForm.parameterRequired,
    photoRequired: stepForm.photoRequired,
    skipAllowed: false,
    enabled: true
  })
  stepForm.stepNo += 10
  stepForm.stepName = ''
  stepForm.instruction = ''
  await loadVersionDetail()
})

const removeStep = (step) => withLoading(async () => {
  await deleteSopStep(step.id)
  await loadVersionDetail()
})

const uploadFile = async (event, handler) => {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file || !selectedVersion.value) return
  const formData = new FormData()
  formData.append('file', file)
  await withLoading(async () => {
    await handler(formData)
    await loadVersionDetail()
  })
}

const uploadAttachmentForStep = (event, stepId) => uploadFile(event, (formData) => {
  formData.append('stepId', stepId)
  return uploadSopAttachment(selectedVersion.value.id, formData)
})
const uploadVersionAttachment = (event) => uploadFile(event, (formData) => uploadSopAttachment(selectedVersion.value.id, formData))
const uploadModelForVersion = (event) => uploadFile(event, (formData) => {
  formData.append('operatorId', operatorId.value)
  return uploadSopModel(selectedVersion.value.id, formData)
})

const addBinding = () => withLoading(async () => {
  if (!selectedVersion.value) return
  await createSopBinding(selectedVersion.value.id, {
    bindingType: bindingForm.bindingType,
    productId: numberOrNull(bindingForm.productId),
    routeId: numberOrNull(bindingForm.routeId),
    workstationId: numberOrNull(bindingForm.workstationId),
    taskId: numberOrNull(bindingForm.taskId),
    priority: bindingForm.bindingType === 'TASK' ? 100 : 80,
    confirmMode: 'PER_TASK',
    status: 'ACTIVE'
  })
  await loadVersionDetail()
})

const removeBinding = (binding) => withLoading(async () => {
  await deleteSopBinding(binding.id)
  await loadVersionDetail()
})

const loadTasks = async () => {
  tasks.value = recordsOf(await getShopTasks())
  if (!selectedTaskId.value && tasks.value[0]) selectedTaskId.value = tasks.value[0].id
}

const selectTask = async (taskId) => {
  selectedTaskId.value = taskId
  validationMessage.value = ''
}

const openTask = () => withLoading(async () => {
  if (!selectedTaskId.value) return
  sopPackage.value = await openTaskSop(selectedTaskId.value, { operatorId: operatorId.value })
  currentStepIndex.value = 0
  stepParameter.value = ''
  await viewCurrentStep()
})

const selectExecutionStep = async (index) => {
  currentStepIndex.value = index
  stepParameter.value = ''
  await viewCurrentStep()
}

const viewCurrentStep = async () => {
  if (!sopPackage.value?.snapshot || !currentStep.value) return
  await viewSopStep(sopPackage.value.snapshot.id, currentStep.value.id, { operatorId: operatorId.value })
}

const confirmCurrentStep = () => withLoading(async () => {
  if (!sopPackage.value?.snapshot || !currentStep.value) return
  await confirmSopStep(sopPackage.value.snapshot.id, currentStep.value.id, {
    operatorId: operatorId.value,
    parameterJson: currentStep.value.parameterRequired ? normalizeParameter(stepParameter.value) : null,
    result: 'CONFIRMED',
    idempotencyKey: `sop-${sopPackage.value.snapshot.id}-${currentStep.value.id}`
  })
  sopPackage.value = await openTaskSop(selectedTaskId.value, { operatorId: operatorId.value })
})

const checkBeforeReport = () => withLoading(async () => {
  if (!selectedTaskId.value) return
  const result = await validateSopBeforeReport(selectedTaskId.value)
  validationMessage.value = result.allowed ? '校验通过，可以报工' : (result.messages?.join('；') || 'SOP 未完成')
})

const normalizeParameter = (value) => {
  if (!value) return null
  try {
    JSON.parse(value)
    return value
  } catch {
    return JSON.stringify({ value })
  }
}

const isStepConfirmed = (stepId) => Boolean(stepRecordsById.value.get(stepId)?.confirmedAt)
const formatTime = (value) => (value ? String(value).replace('T', ' ').slice(0, 19) : '-')

const refreshAll = () => withLoading(async () => {
  await Promise.all([loadDocuments(), loadTasks()])
  if (sopPackage.value && selectedTaskId.value) {
    sopPackage.value = await openTaskSop(selectedTaskId.value, { operatorId: operatorId.value })
  }
})

onMounted(refreshAll)
</script>

<style scoped>
.api-state {
  margin: 0 0 12px;
  color: #52616b;
}

.api-state.error {
  color: #b42318;
}

.sop-manage-grid {
  display: grid;
  grid-template-columns: minmax(360px, 0.9fr) minmax(320px, 0.7fr);
  gap: 14px;
  align-items: start;
}

.sop-editor {
  grid-column: span 2;
}

.sop-form,
.sop-step-form {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-bottom: 1px solid #e3eaed;
}

.sop-form {
  grid-template-columns: repeat(3, minmax(0, 1fr)) auto;
}

.binding-form {
  grid-template-columns: repeat(5, minmax(0, 1fr)) auto;
  border-top: 1px solid #e3eaed;
}

.sop-step-form {
  grid-template-columns: 90px minmax(180px, 1fr) 130px repeat(3, auto) 170px;
  align-items: center;
}

.sop-step-form textarea {
  grid-column: 1 / -2;
  min-height: 64px;
  resize: vertical;
}

.sop-form input,
.sop-form select,
.sop-step-form input,
.sop-step-form select,
.sop-step-form textarea,
.confirm-box input {
  min-height: 32px;
  padding: 7px 9px;
  color: #25323b;
  background: #f9fbfc;
  border: 1px solid #cbd8de;
  border-radius: 4px;
}

.sop-table-wrap {
  max-height: 360px;
  overflow: auto;
}

.version-list,
.task-list,
.binding-list,
.attachment-list {
  display: grid;
  gap: 8px;
  margin: 0;
  padding: 12px;
}

.version-list article,
.task-list article,
.binding-list article {
  display: grid;
  gap: 5px;
  padding: 10px;
  background: #f7fafb;
  border: 1px solid #e2e9ec;
  border-left: 4px solid #b9c8d1;
  cursor: pointer;
}

.version-list article.active,
.task-list article.active {
  background: #edf8fb;
  border-left-color: #00799f;
}

.version-list span,
.task-list span,
.binding-list span,
.attachment-list span,
.task-list em {
  color: #687a82;
  font-size: 12px;
  font-style: normal;
}

.sop-actions,
.upload-row,
.execution-toolbar,
.confirm-box {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding: 12px;
}

.file-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 5px 10px;
  color: #006f8e;
  background: #ffffff;
  border: 1px solid #b9c8d1;
  border-radius: 4px;
  cursor: pointer;
}

.file-button input {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
}

.attachment-list {
  list-style: none;
}

.attachment-list li {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  padding: 8px 10px;
  background: #f7fafb;
  border: 1px solid #e2e9ec;
}

.sop-execute-grid {
  display: grid;
  grid-template-columns: 330px minmax(0, 1fr);
  gap: 14px;
  min-height: 650px;
}

.execution-main {
  min-height: 650px;
}

.task-meta {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
  padding: 0 12px 12px;
}

.task-meta span {
  padding: 9px;
  color: #53666f;
  background: #f7fafb;
  border: 1px solid #e2e9ec;
}

.step-layout {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 12px;
  min-height: 540px;
  padding: 12px;
}

.step-nav {
  display: grid;
  align-content: start;
  gap: 8px;
}

.step-nav button {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  gap: 8px;
  align-items: center;
  min-height: 54px;
  text-align: left;
  background: #ffffff;
  border: 1px solid #cbd8de;
  border-left: 4px solid #b9c8d1;
  cursor: pointer;
}

.step-nav button.active {
  background: #edf8fb;
  border-left-color: #00799f;
}

.step-nav button.done {
  border-left-color: #2e7d5b;
}

.step-content {
  min-width: 0;
  padding: 14px;
  background: #ffffff;
  border: 1px solid #d8e1e5;
}

.step-content header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.step-content h2,
.step-content p {
  margin: 0;
}

.step-content header p {
  color: #00799f;
  font-size: 12px;
  font-weight: 700;
}

.instruction {
  margin: 12px 0 14px !important;
  color: #3d4f58;
  line-height: 1.8;
}

.media-preview {
  display: grid;
  min-height: 320px;
  place-items: center;
  padding: 14px;
  color: #71818b;
  background: #f7fafb;
  border: 1px solid #d8e1e5;
}

.media-preview img,
.media-preview video,
.media-preview iframe {
  width: 100%;
  max-height: 420px;
  object-fit: contain;
  border: 0;
}

.media-preview iframe {
  height: 420px;
}

.confirm-box {
  padding: 14px 0 0;
}

.confirm-box label {
  display: grid;
  flex: 1;
  min-width: 260px;
  gap: 6px;
  color: #53666f;
}
</style>
