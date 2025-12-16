<template>
  <el-drawer
    v-model="visibleLocal"
    title="新增配置"
    direction="rtl"
    size="520px"
    class="config-drawer"
  >
    <el-form
      ref="localFormRef"
      :model="localForm"
      label-position="top"
      label-width="120px"
      :rules="rules"
      class="drawer-form"
    >
      <el-form-item label="配置名称" prop="name">
        <el-input
          v-model="localForm.name"
          maxlength="40"
          placeholder="请输入配置名称"
        />
      </el-form-item>

      <el-form-item label="平台账号" prop="account">
        <el-input v-model="localForm.account" placeholder="请输入平台账号（示例：example_account）" />
      </el-form-item>

      <el-form-item >
        <el-upload :before-upload="handleBeforeUploadShare" :show-file-list="false" accept=".csv,.xls,.xlsx">
          <el-button type="primary" plain round>批量导入</el-button>
        </el-upload>
        <div style="margin-left: 5px;">下载导入模板</div>
      </el-form-item>

      <el-form-item label="抖音视频链接" prop="shareLinks">
        <div>
          <div v-for="(seg, idx) in shareSegments" :key="idx" class="mb-2 flex items-center">
            <el-tag type="info" class="mr-2">链接 {{ idx + 1 }}</el-tag>
            <div class="flex-1">{{ seg }}</div>
            <el-button type="text" class="ml-2" @click="removeShareSegment(idx)">删除</el-button>
          </div>

          <div v-if="addingShare" class="mb-2 flex items-center">
            <el-input v-model="newShare" placeholder="输入视频链接，按回车或点击添加" @keyup.enter.native="confirmAddShare" />
            <el-button type="primary" class="ml-2" @click="confirmAddShare">添加</el-button>
            <el-button class="ml-2" @click="cancelAddShare">取消</el-button>
          </div>

          <div class="mt-2 flex items-center">
            <el-button style="width: 100%;" @click="startAddShare" round>+ 添加一行数据</el-button>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="评论区 - 匹配关键词" prop="commentKeywords">
        <el-input
          v-model="localForm.commentKeywords"
          placeholder="请输入关键字，逗号分隔"
        />
      </el-form-item>

      <el-form-item label="评论时间匹配" prop="commentTime">
        <el-select v-model="localForm.commentTime" placeholder="不限">
          <el-option label="不限" value="" />
          <el-option label="全天" value="全天" />
          <el-option label="9:00-18:00" value="9:00-18:00" />
        </el-select>
      </el-form-item>

      <el-form-item label="评论地区匹配" prop="commentRegion">
        <el-input
          v-model="localForm.commentRegion"
          placeholder="请输入省/市，逗号分隔"
        />
      </el-form-item>

      <el-form-item label="私信内容" prop="segments">
        <div>
          <div v-for="(seg, idx) in segments" :key="idx" class="mb-2 flex items-center">
            <el-tag type="info" class="mr-2">段 {{ idx + 1 }}</el-tag>
            <div class="flex-1">{{ seg }}</div>
            <el-button type="text" class="ml-2" @click="removeSegment(idx)">删除</el-button>
          </div>

          <div v-if="adding" class="mb-2 flex items-center">
            <el-input v-model="newSegment" placeholder="输入私信段，按回车或点击添加" @keyup.enter.native="confirmAdd" />
            <el-button type="primary" class="ml-2" @click="confirmAdd">添加</el-button>
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
        <el-time-picker
          v-model="localForm.startTime"
          format="HH:mm"
          value-format="HH:mm"
          placeholder="请选择时间"
        />
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
import { reactive, ref, watch } from "vue";

const props = defineProps({
  visible: { type: Boolean, default: false },
  config: { type: Object, default: () => ({}) },
  platformOptions: { type: Array, default: () => [] },
  isEditing: { type: Boolean, default: false },
  saving: { type: Boolean, default: false }
});
const emit = defineEmits(["update:visible", "save"]);

const visibleLocal = ref(props.visible);
const localFormRef = ref(null);

const localForm = reactive({
  id: null,
  name: "",
  shareLinks: "",
  commentKeywords: "",
  commentTime: "",
  commentRegion: "",
  account: '',
  privateMessage: "",
  dailyLimit: 10,
  startTime: "09:00",
  skipRepeat: false,
  enabled: true,
});

const rules = {
  name: [{ required: true, message: '请填写配置名称', trigger: 'blur' }],
  account: [{ required: true, message: '请选择平台账号', trigger: 'blur' }],
  shareLinks:[{ required: true, message: '请输入抖音视频链接', trigger: 'blur' }],
  commentKeywords:[{ required: true, message: '请输入评论区匹配关键词', trigger: 'blur' }],
  segments:[{ required: true, message: '请输入私信内容', trigger: 'blur' }],
  dailyLimit:[{ required: true, type: 'number', message: '请填写每天次数限制', trigger: 'change' }],
  startTime:[{ required: true, message: '请选择启动时间', trigger: 'change' }]

}

// 私信/评论 分段管理（与其它抽屉保持一致）
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

// 抖音视频链接 分段管理
const shareSegments = ref([])
const addingShare = ref(false)
const newShare = ref('')

function startAddShare() {
  addingShare.value = true
  newShare.value = ''
}

function cancelAddShare() {
  addingShare.value = false
  newShare.value = ''
}

function confirmAddShare() {
  const v = (newShare.value || '').trim()
  if (!v) return
  shareSegments.value.push(v)
  newShare.value = ''
  addingShare.value = true
}

function removeShareSegment(i) {
  shareSegments.value.splice(i, 1)
}

import { uploadDirectionalFileAsync, getParseResult } from '@/api/exposure'

function handleBeforeUploadShare(file) {
  const name = file.name || ''
  const lower = name.toLowerCase()
  if (lower.endsWith('.csv')) {
    const reader = new FileReader()
    reader.onload = (e) => {
      const text = e.target.result
      const rows = text.split(/\r?\n/).map(r => r.trim()).filter(r => r)
      localForm.shareLinks = rows.join('\n')
    }
    reader.readAsText(file, 'utf-8')
    return false
  }
  // Excel - send to backend (async)
  const form = new FormData()
  form.append('file', file)
  uploadDirectionalFileAsync(form).then((res) => {
    const data = res && res.data ? res.data : null
    const taskId = data && data.taskId ? data.taskId : null
    if (!taskId) {
      ElMessage.error('未获得任务 ID，解析失败')
      return
    }
    ElMessage.info('文件已提交，开始解析（taskId: ' + taskId + '）')
    const start = Date.now()
    const timeout = 60 * 1000
    const iv = setInterval(() => {
      getParseResult(taskId).then((r) => {
        const d = r && r.data ? r.data : null
        if (d && d.status === 'done') {
          clearInterval(iv)
          if (Array.isArray(d.parsed)) {
            localForm.shareLinks = d.parsed.join('\n')
          }
          if (d.errors && d.errors.length) {
            ElMessage.warning(`解析完成，但存在 ${d.errors.length} 条问题：${d.errors.slice(0,3).join('; ')}`)
          } else {
            ElMessage.success('文件解析并已填充链接')
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

watch(
  () => props.visible,
  (v) => (visibleLocal.value = v)
);
watch(visibleLocal, (v) => emit("update:visible", v));

watch(
  () => props.config,
  (v) => {
    if (v) {
      localForm.id = v.id ?? null;
      localForm.name = v.name ?? v.account ?? "";
      localForm.shareLinks = v.shareLinks ?? "";
      localForm.commentKeywords = v.commentKeywords ?? v.commentKeywords ?? "";
      localForm.commentTime = v.commentTime ?? "";
      localForm.commentRegion = v.commentRegion ?? "";
        localForm.account = v.account ?? '';
        localForm.privateMessage = v.privateMessage ?? "";
        // 解析 privateMessage 到 segments（支持 JSON 数组或换行分隔）
        try {
          if (localForm.privateMessage && localForm.privateMessage.trim().startsWith('[')) {
            const arr = JSON.parse(localForm.privateMessage)
            if (Array.isArray(arr)) segments.value = arr.slice()
            else segments.value = localForm.privateMessage.split('\n').map(s => s.trim()).filter(Boolean)
          } else {
            segments.value = (localForm.privateMessage || '').split('\n').map(s => s.trim()).filter(Boolean)
          }
        } catch (e) {
          segments.value = (localForm.privateMessage || '').split('\n').map(s => s.trim()).filter(Boolean)
        }
      localForm.dailyLimit = v.dailyLimit ?? 10;
      localForm.startTime = v.startTime ?? "09:00";
      localForm.skipRepeat = v.skipRepeat ?? false;
      localForm.enabled = v.enabled ?? !v.isClosed;
      // 解析 shareLinks 到 shareSegments（支持 JSON 数组或换行分隔）
      try {
        if (localForm.shareLinks && localForm.shareLinks.trim().startsWith('[')) {
          const arr = JSON.parse(localForm.shareLinks)
          if (Array.isArray(arr)) shareSegments.value = arr.slice()
          else shareSegments.value = localForm.shareLinks.split('\n').map(s => s.trim()).filter(Boolean)
        } else {
          shareSegments.value = (localForm.shareLinks || '').split('\n').map(s => s.trim()).filter(Boolean)
        }
      } catch (e) {
        shareSegments.value = (localForm.shareLinks || '').split('\n').map(s => s.trim()).filter(Boolean)
      }
    }
  },
  { immediate: true, deep: true }
);

function onCancel() {
  // reset to defaults
  localForm.id = null
  localForm.name = ''
  localForm.account = ''
  localForm.shareLinks = ''
  localForm.commentKeywords = ''
  localForm.commentTime = ''
  localForm.commentRegion = ''
  localForm.privateMessage = ''
  segments.value = []
  shareSegments.value = []
  localForm.dailyLimit = 10
  localForm.startTime = '09:00'
  localForm.skipRepeat = false
  localForm.enabled = true
  visibleLocal.value = false
}

function onSave() {
  if (!localForm.name) return;
  const payload = { ...localForm }
  payload.privateMessage = segments.value.join('\n')
  payload.shareLinks = shareSegments.value.join('\n')
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
