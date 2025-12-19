<template>
    <el-dialog
        v-model="visible"
        width="500px"
        :close-on-click-modal="false"
        :show-close="false"
        @close="handleClose"
    >
        <div style="background-color: #ebf6fe;">
            <div class="tip-content">
                <!-- 标题居中 -->
                <div class="tip-title">
                    <el-icon class="tip-icon"><WarningFilled /></el-icon>
                    <span>功能使用提示</span>
                </div>
                <!-- 提示文本与列表左对齐 -->
                <p class="tip-desc">该功能<span style="color: #04c652;">仅支持抖音企业号或员工号</span>使用：</p>
                <ul class="tip-list">
                    <li>普通账号在授权时会提示 没有相关权限；</li>
                    <li>假如授权成功，显示在系统中，但无法实际接收私信。</li>
                </ul>

                <div class="suggestion-box">
                    <span class="suggestion-title">使用建议：</span>
                    <ul class="suggestion-list">
                    <li>授权时请仔细查看 手机端抖音的授权提示，确认所授权的账号类型；</li>
                    <li>如果账号并非企业号/员工号，建议不要继续授权，以免造成无效操作；</li>
                    <li>已授权的普通账号即使显示正常，也无法接收私信。</li>
                    </ul>
                </div>

                <div class="risk-note">
                    使用该功能即表示您已充分了解并接受相关风险。
                </div>
            </div>
        </div>
        <template #footer>
        <!-- 按钮居中 + 文字加粗 -->
            <div class="footer-btn-wrap">
                <el-button 
                type="primary" 
                class="confirm-btn"
                @click="handleConfirm"
                >
                我已知晓，仅授权企业号/员工号
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup lang="js">
// Vue3.3+ 无需导入 defineProps/defineEmits
import { ref, watch } from 'vue'
// 正确导入 Element Plus 图标
import { WarningFilled } from '@element-plus/icons-vue'

// 接收外部传入的显示状态（v-model 绑定）
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

// 向外暴露事件
const emit = defineEmits(['update:modelValue', 'confirm', 'close'])

// 内部显示状态
const visible = ref(props.modelValue)

// 同步外部 modelValue 到内部
watch(
  () => props.modelValue,
  (val) => {
    visible.value = val
  },
  { immediate: true }
)

// 同步内部状态到外部
watch(
  () => visible.value,
  (val) => {
    emit('update:modelValue', val)
  }
)

// 关闭弹窗触发事件
const handleClose = () => {
  emit('close')
}

// 确认弹窗触发事件
const handleConfirm = () => {
  visible.value = false
  emit('confirm')
}
</script>

<style scoped>
.tip-content {
  padding: 10px 30px;
}
.tip-title {
  display: flex;
  align-items: center;
  justify-content: center; /* 标题居中 */
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}
.tip-icon {
  color: #409eff;
  margin-right: 8px;
  font-size: 18px;
}
/* 提示文本左对齐 */
.tip-desc {
  color: #010101;
  margin-bottom: 10px;
  text-align: left; /* 左对齐 */
  margin-left: 0;
  font-size: 14px;
}
/* 列表左对齐 */
.tip-list {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.8;
  padding-left: 0; /* 清除默认内边距 */
  margin-left: 0;  /* 清除默认外边距 */
  list-style-position: inside; /* 列表标记在内容内部，保证左对齐 */
  font-size: 12px;
}
.suggestion-box {
  background-color: #dfedfd;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 15px;
}
.suggestion-title {
  color: #333;
  margin-left: 20px;
  color: #409eff;
  font-weight: bold;
  display: block; /* 独占一行，保证列表对齐 */
}
.suggestion-icon {
  color: #409eff;
  margin-right: 6px;
}
/* 使用建议列表左对齐 */
.suggestion-list {
  color: #666;
  margin-left: 0;   /* 清除默认外边距 */
  padding-left: 20px; /* 统一缩进 */
  margin-top: 8px;
  line-height: 1.8;
  list-style-position: inside;
  font-size: 12px;
}
.risk-note {
  color: #34bcb0;
  font-size: 12px;
  text-align: center;
  margin: 10px 0;
  height: 40px;
  line-height: 40px;
  border-radius: 10px;
  border: 1px solid #04c652;
  background-color: #d7eff5;
}
/* 按钮容器居中 */
.footer-btn-wrap {
  display: flex;
  justify-content: center;
  width: 100%;
  height: 40px;
}
/* 按钮文字加粗 */
:deep(.el-button) {
  font-weight: bold !important;
}

.confirm-btn {
  width: 70%;
  height: 40px;
  background: linear-gradient(to right, #fd3a4f 35%, #f546e1 100%) !important;
  border-color: transparent !important;
  font-weight: bold !important;
}

.confirm-btn:hover {
  background: linear-gradient(to right, #fc2842 35%, #f330db 100%) !important;
}
</style>