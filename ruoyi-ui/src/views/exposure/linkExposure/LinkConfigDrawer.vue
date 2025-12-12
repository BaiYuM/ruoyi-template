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
      label-width="120px"
      class="drawer-form"
    >
      <el-form-item label="配置名称">
        <el-input
          v-model="localForm.name"
          maxlength="40"
          placeholder="请输入配置名称"
        />
      </el-form-item>

      <el-form-item label="平台账号">
        <el-select v-model="localForm.platform" placeholder="请选择">
          <el-option
            v-for="it in platformOptions"
            :key="it.value"
            :label="it.label"
            :value="it.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="批量导入分享链接">
        <el-upload :before-upload="handleBeforeUploadShare" :show-file-list="false" accept=".csv,.xls,.xlsx">
          <el-button type="primary" plain size="small">批量导入</el-button>
        </el-upload>
        <el-button plain size="small" class="ml-2">下载导入模板</el-button>
        <div class="hint" style="margin-top:6px;color:var(--muted);font-size:12px">上传 CSV 会在前端解析并填充链接；Excel 文件会上传到后端处理</div>
      </el-form-item>

      <el-form-item label="抖音视频链接">
        <el-input type="textarea" v-model="localForm.shareLinks" placeholder="一行一个链接" rows="4" />
      </el-form-item>

      <el-form-item label="评论区 - 匹配关键词">
        <el-input
          v-model="localForm.commentKeywords"
          placeholder="请输入关键字，逗号分隔"
        />
      </el-form-item>

      <el-form-item label="评论时间匹配">
        <el-select v-model="localForm.commentTime" placeholder="不限">
          <el-option label="不限" value="" />
          <el-option label="全天" value="全天" />
          <el-option label="9:00-18:00" value="9:00-18:00" />
        </el-select>
      </el-form-item>

      <el-form-item label="评论地区匹配">
        <el-input
          v-model="localForm.commentRegion"
          placeholder="请输入省/市，逗号分隔"
        />
      </el-form-item>

      <el-form-item label="私信内容">
        <el-input
          type="textarea"
          v-model="localForm.privateMessage"
          placeholder="私信内容示例"
          rows="3"
        />
      </el-form-item>

      <el-form-item label="每天次数限制">
        <el-input-number v-model="localForm.dailyLimit" :min="1" />
      </el-form-item>

      <el-form-item label="启动时间">
        <el-input-number v-model="localForm.startTime" />
      </el-form-item>

      <el-form-item label="跳过重复">
        <el-switch v-model="localForm.skipRepeat" />
      </el-form-item>

      <el-form-item label="启用">
        <el-switch v-model="localForm.enabled" />
      </el-form-item>

      <div style="text-align: right; margin-top: 12px">
        <el-button @click="onCancel">取消</el-button>
        <el-button type="primary" class="ml-2" @click="onSave">保存</el-button>
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
});
const emit = defineEmits(["update:visible", "save"]);

const visibleLocal = ref(props.visible);
const localFormRef = ref(null);

const localForm = reactive({
  id: null,
  name: "",
  platform: "",
  shareLinks: "",
  commentKeywords: "",
  commentTime: "",
  commentRegion: "",
  privateMessage: "",
  dailyLimit: 10,
  startTime: "09:00",
  skipRepeat: false,
  enabled: true,
});

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
      localForm.platform = v.platform ?? "";
      localForm.shareLinks = v.shareLinks ?? "";
      localForm.commentKeywords = v.commentKeywords ?? v.commentKeywords ?? "";
      localForm.commentTime = v.commentTime ?? "";
      localForm.commentRegion = v.commentRegion ?? "";
      localForm.privateMessage = v.privateMessage ?? "";
      localForm.dailyLimit = v.dailyLimit ?? 10;
      localForm.startTime = v.startTime ?? "09:00";
      localForm.skipRepeat = v.skipRepeat ?? false;
      localForm.enabled = v.enabled ?? !v.isClosed;
    }
  },
  { immediate: true, deep: true }
);

function onCancel() {
  visibleLocal.value = false;
}

function onSave() {
  if (!localForm.name) return;
  emit("save", { ...localForm });
  visibleLocal.value = false;
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
