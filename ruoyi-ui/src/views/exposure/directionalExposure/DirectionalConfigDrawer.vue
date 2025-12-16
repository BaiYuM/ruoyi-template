<template>
  <el-drawer
    v-model="visibleLocal"
    title="新增配置"
    direction="rtl"
    size="520px"
    class="config-drawer"
  >
    <el-form :model="localForm" :rules="rules" ref="localFormRef" label-position="top"  label-width="120px" class="drawer-form">
      <el-form-item label="配置名称" prop="name">
        <el-input v-model="localForm.name" maxlength="40" placeholder="请输入配置名称" />
      </el-form-item>

      <el-form-item label="平台账号" prop="account">
        <el-input v-model="localForm.account" placeholder="请输入平台账号（示例：example_account）" />
      </el-form-item>

      <el-form-item >
        <el-upload
          :before-upload="handleBeforeUpload"
          :show-file-list="false"
          accept=".csv,.xls,.xlsx"
        >
          <el-button type="primary" plain  round>批量导入目标账号</el-button>
        </el-upload>
        <div style="margin-left: 10px; color: #409EFF;">下载导入模板</div>
      </el-form-item>

      <el-form-item prop="targetAccounts">
        <template #label>
          <span>目标账号列表</span>
          <el-link
            type="primary"
            :underline="false"
            style="margin-left: 8px; font-size: 12px; vertical-align: middle"
            @click="copyList"
          >
            <el-icon style="vertical-align: middle; margin-right: 2px">
              <DocumentCopy />
            </el-icon>
            复制列表
          </el-link>
        </template>

        <el-input
          type="textarea"
          v-model="localForm.targetAccounts"
          placeholder="请输入目标账号，一行一个，或上传 CSV 自动填充"
          rows="4"
        />
      </el-form-item>

      <el-form-item label="配置类型">
        <el-radio-group v-model="localForm.configType">
          <el-radio label="评论">评论</el-radio>
          <el-radio label="私信">私信<span style="color: #409EFF;">（仅抖音）</span></el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="评论内容" prop="commentContent">
        <div>
          <div v-for="(seg, idx) in segments" :key="idx" class="mb-2 flex items-center">
            <el-tag type="info" class="mr-2">段 {{ idx + 1 }}</el-tag>
            <div class="flex-1">{{ seg }}</div>
            <el-button type="text" class="ml-2" @click="removeSegment(idx)">删除</el-button>
          </div>

          <div v-if="adding" class="mb-2 flex items-center">
            <el-input v-model="newSegment" placeholder="输入评论段，按回车或点击添加" @keyup.enter.native="confirmAdd" />
            <el-button type="primary" style="margin-top: 8px;" class="ml-2" @click="confirmAdd">添加</el-button>
            <el-button class="ml-2" @click="cancelAdd">取消</el-button>
          </div>

          <div>
            <el-button style="width: 100%; margin:8px 0;" @click="startAdd" round>+ 添加一行数据</el-button>
            <el-button style="width: 100%; margin-left: 0;" type="primary" @click="openAI('comment')" round>已有文案？ 使用AI润色</el-button>
          </div>
        </div>
      </el-form-item>


      <el-form-item label="每天次数限制" prop="dailyLimit">
        <el-input-number v-model="localForm.dailyLimit" :min="1" />
      </el-form-item>

      <el-form-item label="启动时间" prop="startTime">
        <el-input v-model="localForm.startTime" placeholder="09:00" />
      </el-form-item>

      <el-form-item label="跳过重复">
        <el-switch v-model="localForm.skipRepeat" />
      </el-form-item>

      <el-form-item label="启用">
        <el-switch v-model="localForm.enabled" />
      </el-form-item>

      <div style="text-align: left; margin-top: 12px">
        <el-button @click="onCancel" :disabled="props.saving" round>重置</el-button>
        <el-button type="primary" class="ml-2" @click="onSave" :loading="props.saving" :disabled="props.saving" round>提交</el-button>
      </div>
    </el-form>
  </el-drawer>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { DocumentCopy } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
const props = defineProps({
  visible: { type: Boolean, default: false },
  config: { type: Object, default: () => ({}) },
  platformOptions: { type: Array, default: () => [] },
  isEditing: { type: Boolean, default: false }
})
const emit = defineEmits(['update:visible', 'save'])

const visibleLocal = ref(props.visible)
const localFormRef = ref(null)

const localForm = reactive({
  id: null,
  name: '',
  account: '',
  targetAccounts: '',
  configType: '评论',
  commentContent: '',
  dailyLimit: 10,
  startTime: '09:00',
  skipRepeat: false,
  enabled: true
})

const rules = {
  name: [{ required: true, message: '请填写配置名称', trigger: 'blur' }],
  account: [{ required: true, message: '请选择平台账号', trigger: 'blur' }],
  targetAccounts:[{ required: true, message: '请输入目标账号列表', trigger: 'blur' }],
  commentContent:[{ required: true, message: '请输入评论内容', trigger: 'blur' }],
  dailyLimit:[{ required: true, type: 'number', message: '请填写每天次数限制', trigger: 'change' }],
  startTime:[{ required: true, message: '请选择启动时间', trigger: 'change' }]

}

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

function copyList() {
  const text = localForm.targetAccounts.trim()
  if (!text) {
    ElMessage.warning('列表为空，无需复制')
    return
  }
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    // 降级方案：老浏览器兼容
    const ta = document.createElement('textarea')
    ta.value = text
    document.body.appendChild(ta)
    ta.select()
    document.execCommand('copy')
    document.body.removeChild(ta)
    ElMessage.success('已复制到剪贴板')
  })
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
      localForm.account = v.account ?? ''
      localForm.targetAccounts = v.targetAccounts ?? ''
      localForm.configType = v.configType ?? '评论'
      localForm.commentContent = v.commentContent ?? ''
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
  localForm.account = ''
  localForm.targetAccounts = ''
  localForm.configType = '评论'
  localForm.commentContent = ''
  localForm.dailyLimit = 10
  localForm.startTime = '09:00'
  localForm.skipRepeat = false
  localForm.enabled = true
}

function onSave() {
  if (!localForm.name) return
  emit('save', { ...localForm })
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
