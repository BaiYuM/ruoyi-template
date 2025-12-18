<template>
  <el-dialog :model-value="visible" title="自动曝光设置" width="450px" @close="$emit('update:visible', false)">
    <el-form :model="form" label-width="120px" label-position="top">
        <el-form-item label="曝光间隔（秒）" required>
          <div class="range-input">
            <el-input-number v-model="form.min" :min="1" :max="9999" style="width:100px" class="range-number" />
            <span class="range-sep">~</span>
            <el-input-number v-model="form.max" :min="1" :max="9999" style="width:100px" class="range-number" />
          </div>
        </el-form-item>
      </el-form>

    <template #footer>
      <div style="text-align: left;">
        <el-button @click="onReset" round>重置</el-button>
        <el-button type="primary" @click="onSubmit" round>提交</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, watch } from 'vue'
const props = defineProps({ visible: { type: Boolean, default: false }, initial: { type: Object, default: () => ({ min: 1, max: 10 }) } })
const emit = defineEmits(['update:visible', 'save'])

const form = reactive({ min: 1, max: 10 })

// initialize from parent-provided initial settings
watch(() => props.initial, (v) => {
  if (v) {
    form.min = v.min ?? 1
    form.max = v.max ?? 10
  }
}, { immediate: true })

function onReset() {
  form.min = props.initial?.min ?? 1
  form.max = props.initial?.max ?? 10
}

function onSubmit() {
  emit('save', { min: form.min, max: form.max })
  emit('update:visible', false)
}
</script>

<style scoped>
</style>



