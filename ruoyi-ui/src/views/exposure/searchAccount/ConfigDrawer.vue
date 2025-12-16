<template>
  <el-drawer v-model="visibleLocal" title="保存配置" direction="rtl" size="420px" class="config-drawer">
    <div class="drawer-header" style="padding:12px 20px">
      <!-- <h3 style="margin:0;color:#333">{{ isEditing ? '编辑配置' : '新增配置' }}</h3> -->
    </div>

    <el-form ref="localFormRef" :rules="rules" label-width="auto" label-position="top" :model="localForm" class="drawer-form">
      <el-form-item label="配置名称" prop="name">
        <el-input v-model="localForm.name" maxlength="20" placeholder="请输入配置名称" />
      </el-form-item>

      <el-form-item label="平台账号" prop="platform">
        <el-select v-model="localForm.platform" placeholder="请选择">
          <el-option v-for="item in platformOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="搜索关键词" prop="keywords">
        <el-input v-model="localForm.keywords" placeholder="请输入，逗号分隔" />
      </el-form-item>

      <el-form-item label="昵称匹配关键词" prop="nicknameKeywords">
        <el-input v-model="localForm.nicknameKeywords" placeholder="请输入昵称关键词" />
      </el-form-item>

      <el-form-item label="私信内容" prop="privateMessage">
        <el-input type="textarea" v-model="localForm.privateMessage" placeholder="私信内容示例" />
      </el-form-item>

      <el-form-item label="每天次数" prop="dailyLimit">
        <el-input-number v-model="localForm.dailyLimit" :min="1" />
      </el-form-item>

      <el-form-item label="启动时间" prop="startTime">
        <el-time-picker v-model="localForm.startTime" placeholder="请选择时间" format="HH:mm" value-format="HH:mm" />
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
import { reactive, ref, watch, toRefs } from 'vue'

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
  platform: '',
  keywords: '',
  nicknameKeywords: '',
  privateMessage: '',
  dailyLimit: 10,
  startTime: '09:00',
  skipRepeat: false,
  enabled: true
})

const rules = {
  name: [{ required: true, message: '请填写配置名称', trigger: 'blur' }],
  platform: [{ required: true, message: '请选择平台账号', trigger: 'blur' }],
  keywords:[{ required: true, message: '请输入搜索关键词', trigger: 'blur' }],
  nicknameKeywords:[{ required: true, message: '请输入昵称匹配关键词', trigger: 'blur' }],
  privateMessage:[{ required: true, message: '请输入私信内容', trigger: 'blur' }],
  dailyLimit:[{ required: true, type: 'number', message: '请填写每天次数限制', trigger: 'change' }],
  startTime:[{ required: true, message: '请选择启动时间', trigger: 'change' }]

}

watch(() => props.visible, v => (visibleLocal.value = v))
watch(visibleLocal, v => emit('update:visible', v))

// when config changes, copy to localForm to show
watch(
  () => props.config,
  v => {
    if (v) {
      localForm.id = v.id ?? null
      localForm.name = v.name ?? v.account ?? ''
      localForm.platform = v.platform ?? ''
      localForm.keywords = v.keywords ?? v.extra ?? ''
      localForm.nicknameKeywords = v.nicknameKeywords ?? ''
      localForm.privateMessage = v.privateMessage ?? ''
      localForm.dailyLimit = v.dailyLimit ?? 10
      localForm.startTime = v.startTime ?? '09:00'
      localForm.skipRepeat = v.skipRepeat ?? false
      localForm.enabled = v.enabled ?? !(v.isClosed)
    }
  },
  { immediate: true, deep: true }
)


function onCancel() {
  // reset to defaults
  localForm.id = null
  localForm.name = ''
  localForm.platform = ''
  localForm.keywords = ''
  localForm.nicknameKeywords = ''
  localForm.privateMessage = ''
  localForm.commentRegion = ''
  localForm.dailyLimit = 10
  localForm.startTime = '09:00'
  localForm.skipRepeat = false
  localForm.enabled = true
}

function onSave() {
  // simple validation: require name
  if (!localForm.name) {
    // eslint-disable-next-line no-console
    console.warn('name required')
    return
  }
  emit('save', { ...localForm })
  visibleLocal.value = false
}
</script>

<style scoped>
.required-label::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
}
.config-drawer {
  --el-color-primary: #ff6fb6;
}
.drawer-form .el-form-item__label {
  color: #666;
}
.drawer-form .el-input,
.drawer-form .el-select,
.drawer-form .el-time-picker {
  border-radius: 6px;
}
.ml-2 { margin-left: 8px }
</style>
