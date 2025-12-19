<template>
  <el-dialog
    :model-value="visible"
    title="AI助手 - 评论内容"
    width="80%"
    @close="$emit('update:visible', false)"
  >
    <!-- 横向两栏，等宽 -->
    <div class="ai-wrapper">
      <!-- 左侧聊天区 -->
      <div class="ai-chat">
        <div class="chat-box">
          <div class="message">
            <div class="avatar">🤖</div>
            <div class="text">
              你好！请问你是想在抖音评论区投放广告吗？可以告诉我你所在的行业以及想要突出的卖点是什么吗？例如价格优惠、服务快速、正规资质等。
            </div>
          </div>
        </div>

        <div class="chat-input">
          <el-input
            v-model="input"
            placeholder="请输入您的需求..."
            @keyup.enter="send"
          />
          <el-button type="primary" class="ml-2" @click="send">
            发送
          </el-button>
        </div>
      </div>

      <!-- 右侧关键词区 -->
      <div class="ai-keywords">
        <div class="keywords-inner">
          <p><strong>关键词选择</strong></p>
          <div v-if="!generated" class="placeholder">
            请在左侧输入需求，AI将为您生成关键词选项
          </div>
          <div v-else class="result">
            {{ generated }}
          </div>
        </div>
      </div>
    </div>
<!-- 
    <template #footer>
      <el-button @click="$emit('update:visible', false)">关闭</el-button>
      <el-button type="primary" @click="apply">选择并应用</el-button>
    </template> -->
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  mode: { type: String, default: 'comment' }
})
const emit = defineEmits(['update:visible', 'apply'])

const input = ref('')
const generated = ref('')

function send() {
  generated.value = input.value.trim() || '示例评论内容：不错，值得关注！'
}

function apply() {
  const text = generated.value || input.value || '示例评论内容：不错，值得关注！'
  emit('apply', text)
  emit('update:visible', false)
}
</script>

<style scoped>
/* 横向等宽容器 */
.ai-wrapper {
  display: flex;
  gap: 16px;
  height: 360px;
}

/* 左右两栏严格 1:1 */
.ai-chat,
.ai-keywords {
  flex: 1 1 0;
  display: flex;
  flex-direction: column;
}

/* 聊天消息区 */
.chat-box {
  flex: 1;
  overflow: auto;
  background: #fff;
  border: 1px solid #f0f0f0;
  padding: 16px;
}
.message {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  background: #f2f2f2;
  display: flex;
  align-items: center;
  justify-content: center;
}
.text {
  flex: 1;
  line-height: 1.5;
}

/* 底部输入区 */
.chat-input {
  display: flex;
  margin-top: 12px;
  gap: 8px;
}
.chat-input .el-input {
  flex: 1;
}

/* 右侧关键词区 */
.keywords-inner {
  height: 100%;
  border: 1px dashed #eee;
  padding: 16px;
  background: #fafafa;
}
.placeholder {
  margin-top: 40px;
  color: #999;
}
.result {
  margin-top: 12px;
  white-space: pre-wrap;
  line-height: 1.5;
}
</style>