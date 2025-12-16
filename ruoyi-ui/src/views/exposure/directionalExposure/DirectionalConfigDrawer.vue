<template>
  <el-drawer
    v-model="visibleLocal"
    title="新增配置"
    direction="rtl"
    size="520px"
    class="config-drawer"
  >
    <el-form ref="localFormRef" :model="localForm" label-width="120px" class="drawer-form">
      <el-form-item label="配置名称">
        <el-input v-model="localForm.name" maxlength="40" placeholder="请输入配置名称" />
      </el-form-item>

      <el-form-item label="平台">
        <el-select v-model="localForm.platform" placeholder="请选择">
          <el-option v-for="it in platformOptions" :key="it.value" :label="it.label" :value="it.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="平台账号">
        <el-input v-model="localForm.account" placeholder="请输入平台账号（示例：example_account）" />
      </el-form-item>

      <el-form-item label="批量导入目标账号">
        <el-upload
          :before-upload="handleBeforeUpload"
          :show-file-list="false"
          accept=".csv,.xls,.xlsx"
        >
          <el-button type="primary" plain size="small">批量导入目标账号</el-button>
        </el-upload>
        <el-button plain size="small" class="ml-2">下载导入模板</el-button>
        <div class="hint" style="margin-top:6px;color:var(--muted);font-size:12px">支持 CSV（前端解析）和 Excel（上传到后端处理）</div>
      </el-form-item>

      <el-form-item label="目标账号列表">
        <el-input type="textarea" v-model="localForm.targetAccounts" placeholder="请输入目标账号，一行一个，或上传 CSV 自动填充" rows="4" />
      </el-form-item>

      <el-form-item label="配置类型">
        <el-radio-group v-model="localForm.configType">
          <el-radio label="评论">评论</el-radio>
          <el-radio label="私信">私信（仅抖音）</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="评论内容">
        <div>
          <div v-for="(seg, idx) in segments" :key="idx" class="mb-2 flex items-center">
            <el-tag type="info" class="mr-2">段 {{ idx + 1 }}</el-tag>
            <div class="flex-1">{{ seg }}</div>
            <el-button type="text" class="ml-2" @click="removeSegment(idx)">删除</el-button>
          </div>

          <div v-if="adding" class="mb-2 flex items-center">
            <el-input v-model="newSegment" placeholder="输入评论段，按回车或点击添加" @keyup.enter.native="confirmAdd" />
            <el-button type="primary" class="ml-2" @click="confirmAdd">添加</el-button>
            <el-button class="ml-2" @click="cancelAdd">取消</el-button>
          </div>

          <div class="mt-2 flex items-center">
            <el-button size="small" @click="startAdd">+ 添加一行数据</el-button>
            <el-button type="primary" class="ml-4">已有文案？ 使用 AI 润色</el-button>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="每天次数限制">
        <el-input-number v-model="localForm.dailyLimit" :min="1" />
      </el-form-item>

      <el-form-item label="启动时间">
        <el-input v-model="localForm.startTime" placeholder="09:00" />
      </el-form-item>

      <el-form-item label="跳过重复">
        <el-switch v-model="localForm.skipRepeat" />
      </el-form-item>

      <el-form-item label="启用">
        <el-switch v-model="localForm.enabled" />
      </el-form-item>

      <div style="text-align: right; margin-top: 12px">
        <el-button @click="onCancel" :disabled="props.saving">重置</el-button>
        <el-button type="primary" class="ml-2" @click="onSave" :loading="props.saving" :disabled="props.saving">提交</el-button>
      </div>
    </el-form>
  </el-drawer>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  config: { type: Object, default: () => ({}) },
  platformOptions: { type: Array, default: () => [] },
  isEditing: { type: Boolean, default: false },
  saving: { type: Boolean, default: false }
})
const emit = defineEmits(['update:visible', 'save'])

const visibleLocal = ref(props.visible)
const localFormRef = ref(null)

const localForm = reactive({
  id: null,
  name: '',
  platform: '',
  account: '',
  targetAccounts: '',
  configType: '评论',
  commentContent: '',
  dailyLimit: 10,
  startTime: '09:00',
  skipRepeat: false,
  enabled: true
})

// 评论段管理（与自动曝光保持一致的段式编辑）
const segments = ref([])
const adding = ref(false)
const newSegment = ref('')

function startAdd() {
  adding.value = true
  newSegment.value = ''
}

function cancelAdd() {
  adding.value = false
  newSegment.value = ''
}

function confirmAdd() {
  const v = (newSegment.value || '').trim()
  if (!v) return
  segments.value.push(v)
  newSegment.value = ''
  adding.value = true
}

function removeSegment(i) {
  segments.value.splice(i, 1)
}

// handle file uploads: CSV parsed client-side; Excel sent to backend (async)
import { uploadDirectionalFileAsync, getParseResult } from '@/api/exposure'

function handleBeforeUpload(file) {
  const name = file.name || ''
  const lower = name.toLowerCase()
  if (lower.endsWith('.csv')) {
    const reader = new FileReader()
    reader.onload = (e) => {
      const text = e.target.result
      // split lines, trim, ignore empty
      const rows = text.split(/\r?\n/).map(r => r.trim()).filter(r => r)
      // populate textarea with one per line
      localForm.targetAccounts = rows.join('\n')
    }
    reader.readAsText(file, 'utf-8')
    // prevent automatic upload
    return false
  }

  // for xls/xlsx, upload to backend via api
  const form = new FormData()
  form.append('file', file)
  // 使用异步接口：提交任务 -> 轮询结果
  uploadDirectionalFileAsync(form).then((res) => {
    const data = res && res.data ? res.data : null
    const taskId = data && data.taskId ? data.taskId : null
    if (!taskId) {
      ElMessage.error('未获得任务 ID，解析失败')
      return
    }
    ElMessage.info('文件已提交，开始解析（taskId: ' + taskId + '）')
    const start = Date.now()
    const timeout = 60 * 1000 // 60s 超时
    const iv = setInterval(() => {
      getParseResult(taskId).then((r) => {
        const d = r && r.data ? r.data : null
        if (d && d.status === 'done') {
          clearInterval(iv)
          if (Array.isArray(d.parsed)) {
            localForm.targetAccounts = d.parsed.join('\n')
          }
          if (d.errors && d.errors.length) {
            ElMessage.warning(`解析完成，但存在 ${d.errors.length} 条问题：${d.errors.slice(0,3).join('; ')}`)
          } else {
            ElMessage.success('文件解析并已填充目标账号')
          }
        }
      }).catch(() => {})
      if (Date.now() - start > timeout) {
        clearInterval(iv)
        ElMessage.error('解析超时，请稍后在解析结果中查看')
      }
    }, 1000)
  }).catch(() => {
    ElMessage.error('文件上传失败')
  })
  return false
}

watch(() => props.visible, v => (visibleLocal.value = v))
watch(visibleLocal, v => emit('update:visible', v))

watch(
  () => props.config,
  v => {
    if (v) {
      localForm.id = v.id ?? null
      localForm.name = v.name ?? ''
      localForm.platform = v.platform ?? ''
      localForm.account = v.account ?? ''
      localForm.targetAccounts = v.targetAccounts ?? ''
      localForm.configType = v.configType ?? '评论'
        localForm.commentContent = v.commentContent ?? ''
        // 解析 commentContent 到 segments（支持 JSON 数组或换行分隔）
        try {
          if (localForm.commentContent && localForm.commentContent.trim().startsWith('[')) {
            const arr = JSON.parse(localForm.commentContent)
            if (Array.isArray(arr)) segments.value = arr.slice()
            else segments.value = localForm.commentContent.split('\n').map(s => s.trim()).filter(Boolean)
          } else {
            segments.value = localForm.commentContent.split('\n').map(s => s.trim()).filter(Boolean)
          }
        } catch (e) {
          segments.value = localForm.commentContent.split('\n').map(s => s.trim()).filter(Boolean)
        }
      localForm.dailyLimit = v.dailyLimit ?? 10
      localForm.startTime = v.startTime ?? '09:00'
      localForm.skipRepeat = v.skipRepeat ?? false
      localForm.enabled = v.enabled ?? !v.isClosed
    }
  },
  { immediate: true, deep: true }
)

function onCancel() {
  // reset to defaults
  localForm.id = null
  localForm.name = ''
  localForm.platform = ''
  localForm.account = ''
  localForm.targetAccounts = ''
  localForm.configType = '评论'
  localForm.commentContent = ''
  segments.value = []
  localForm.dailyLimit = 10
  localForm.startTime = '09:00'
  localForm.skipRepeat = false
  localForm.enabled = true
}

function onSave() {
  if (!localForm.name) return
  // 合并 segments 到 commentContent 字段
  const payload = { ...localForm }
  payload.commentContent = segments.value.join('\n')
  // 后端使用 status 字符串，'0' 表示启用
  payload.status = localForm.enabled ? '0' : '1'
  emit('save', payload)
  visibleLocal.value = false
}
</script>

<style scoped>
.config-drawer {
  --el-color-primary: #ff6fb6;
}
.drawer-form .el-form-item__label {
  color: #666;
}
.ml-2 {
  margin-left: 8px;
}
</style>
