<template>
  <el-drawer :model-value="visible" :title="isEditing ? '编辑配置' : '新增配置'" direction="rtl" size="520px" :with-header="true" @close="handleClose">
    <el-form :model="form" :rules="rules" ref="formRef" label-position="top" label-width="auto">
      <el-form-item label="配置名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入配置名称" maxlength="50" show-word-limit />
      </el-form-item>

      <el-form-item label="平台" prop="platform">
        <el-select v-model="form.platform" placeholder="请选择平台" clearable>
          <el-option v-for="p in platformOptions" :key="p.value" :label="p.label" :value="p.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="平台账号" prop="account">
        <el-input v-model="form.account" placeholder="请输入平台账号，如账号ID或昵称" maxlength="100" />
      </el-form-item>

      <el-form-item label="配置类型">
        <el-radio-group v-model="form.configType">
          <el-radio label="评论">评论</el-radio>
          <el-radio label="私信">私信(仅抖音)</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="搜索关键词" prop="searchKeywords">
        <div class="flex items-center">
          <el-input v-model="form.searchKeywords" placeholder="请输入" />
          <el-button type="text" @click="openAI('keywords')">AI帮写</el-button>
        </div>
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
        <el-input-number v-model="form.dailyLimit" :min="1" />
      </el-form-item>

      <el-form-item label="启动时间" prop="startTime">
        <el-time-picker v-model="form.startTime" format="HH:mm" placeholder="请选择时间" />
      </el-form-item>

      <el-form-item label="视频/帖子排序">
        <el-radio-group v-model="form.sortOrder">
          <el-radio label="综合">综合</el-radio>
          <el-radio label="最新">最新</el-radio>
          <el-radio label="最热">最热</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="跳过重复">
        <el-switch v-model="form.skipRepeat" />
      </el-form-item>

      <el-form-item label="启用">
        <el-switch v-model="form.enabled" />
      </el-form-item>

      <div class="drawer-actions mt-4 flex justify-end">
        <el-button @click="onReset" round>重置</el-button>
        <el-button type="primary" class="ml-4" @click="onSubmit" round>提交</el-button>
      </div>
    </el-form>

    <AIHelperDialog v-if="aiVisible" v-model:visible="aiVisible" :mode="aiMode" @apply="onAIApply"/>
  </el-drawer>
</template>

<script setup>
import { ref, watch, reactive, toRefs } from 'vue'
import AIHelperDialog from './AIHelperDialog.vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  config: { type: Object, default: () => ({}) },
  platformOptions: { type: Array, default: () => [] },
  isEditing: { type: Boolean, default: false }
})

const today = new Date(new Date().setHours(9, 0, 0, 0))
const emit = defineEmits(['update:visible', 'save'])
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  account: '',
  configType: '评论',
  searchKeywords: '',
  commentContent: '',
  dailyLimit: 10,
  startTime: today,
  sortOrder: '综合',
  skipRepeat: false,
  enabled: true
})

const rules = {
  name: [{ required: true, message: '请填写配置名称', trigger: 'blur' }],
  account: [{ required: true, message: '请选择平台账号', trigger: 'change' }]
}

// 评论段管理
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
  // keep input open
  adding.value = true
}

function removeSegment(i) {
  segments.value.splice(i, 1)
}

watch(
  () => props.config,
  (v) => {
    if (v && Object.keys(v).length) {
      // 映射后端字段到表单字段，确保类型正确
      form.id = v.id ?? null
      form.name = v.name ?? ''
      form.platform = v.platform ?? ''
      form.account = v.account ?? ''
      form.configType = v.configType ?? '评论'
      form.searchKeywords = v.searchKeywords ?? ''
      form.commentContent = v.commentContent ?? ''
      form.dailyLimit = v.dailyLimit ?? 10
      form.startTime = v.startTime ?? today
      form.sortOrder = v.sortOrder ?? '综合'
      form.skipRepeat = v.skipRepeat ?? false
      // 后端使用 status 字符串：'0' 表示启用
      form.enabled = v.status === undefined ? true : (v.status === '0')
    } else {
      // 清空表单
      onReset()
    }
  },
  { immediate: true, deep: true }
)

function handleClose() {
  emit('update:visible', false)
}

function onReset() {
  // 若为编辑模式，则回退到 props.config 的值，否则清空为默认
  const v = props.config && Object.keys(props.config).length ? props.config : null
  if (v) {
    form.id = v.id ?? null
    form.name = v.name ?? ''
    form.platform = v.platform ?? ''
    form.account = v.account ?? ''
    form.configType = v.configType ?? '评论'
    form.searchKeywords = v.searchKeywords ?? ''
    form.commentContent = v.commentContent ?? ''
    form.dailyLimit = v.dailyLimit ?? 10
    form.startTime = v.startTime ?? today
    form.sortOrder = v.sortOrder ?? '综合'
    form.skipRepeat = v.skipRepeat ?? false
    form.enabled = v.status === undefined ? true : (v.status === '0')
  } else {
    form.id = null
    form.name = ''
    form.platform = ''
    form.account = ''
    form.configType = '评论'
    form.searchKeywords = ''
    form.commentContent = ''
    form.dailyLimit = 10
    form.startTime = today
    form.sortOrder = '综合'
    form.skipRepeat = false
    form.enabled = true
  }
}

function onSubmit() {
  formRef.value.validate(valid => {
    if (!valid) return
    // 映射表单到后端 ExposureConfig 对象形态
    const payload = {
      id: form.id,
      name: form.name,
      platform: form.platform,
      account: form.account,
      configType: form.configType,
      searchKeywords: form.searchKeywords,
      commentContent: form.commentContent,
      dailyLimit: form.dailyLimit,
      startTime: form.startTime,
      sortOrder: form.sortOrder,
      skipRepeat: form.skipRepeat,
      // 后端使用 status 字符串，'0' 表示启用，'1' 禁用
      status: form.enabled ? '0' : '1'
    }
    emit('save', payload)
    emit('update:visible', false)
  })
}

function addRow() {
  // 简化：把当前 textarea 内容追加一行示意
  if (form.commentContent) form.commentContent += '\n' + form.commentContent
}

const aiVisible = ref(false)
const aiMode = ref('')

function openAI(mode) {
  aiMode.value = mode
  aiVisible.value = true
}

function onAIApply(text) {
  if (aiMode.value === 'comment') form.commentContent = text
  else if (aiMode.value === 'keywords') form.searchKeywords = text
  aiVisible.value = false
}
</script>

<style scoped>
.drawer-actions { padding: 12px 0; }
</style>
