<template>
  <el-dialog :model-value="visible" title="AI助手 - 评论内容" width="80%">
    <div class="ai-helper flex">
      <div class="ai-chat flex-1 pr-4">
        <div class="chat-box" style="height:360px; overflow:auto; background:#fff; border:1px solid #f0f0f0; padding:16px;">
          <div class="message">
            <div class="avatar">🤖</div>
            <div class="text">你好！ 请问你是想在抖音评论区投放广告吗？可以告诉我你所在的行业以及想要突出的卖点是什么吗？例如价格优惠、服务快速、正规资质等。</div>
          </div>
        </div>
        <div class="mt-3 flex items-center">
          <el-input v-model="input" placeholder="请输入您的需求..." />
          <el-button type="primary" class="ml-2" @click="send">发送</el-button>
        </div>
      </div>
      <div class="ai-keywords w-1/3 pl-4">
        <div style="height:360px; border:1px dashed #eee; padding:16px; background:#fafafa;">
          <p><strong>关键词选择</strong></p>
          <div style="color:#999; margin-top:40px;">请在左侧输入需求，AI将为您生成关键词选项</div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="$emit('update:visible', false)">关闭</el-button>
        <el-button type="primary" @click="apply">选择并应用</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
const props = defineProps({ visible: { type: Boolean, default: false }, mode: { type: String, default: 'comment' } })
const emit = defineEmits(['update:visible', 'apply'])

const input = ref('')
const generated = ref('')

function send() {
  // 简化：把 input 直接当作生成结果
  generated.value = input.value || '示例评论内容：不错，值得关注！'
}

function apply() {
  const text = generated.value || input.value || '示例评论内容：不错，值得关注！'
  emit('apply', text)
}
</script>

<style scoped>
.chat-box .message { display:flex; gap:8px; align-items:flex-start; }
.chat-box .avatar { width:36px; height:36px; border-radius:4px; background:#f2f2f2; display:flex; align-items:center; justify-content:center }
.ai-helper {
  display: flex;
  flex-direction: row;   /* 明确横向 */
  gap: 16px;
}
.ai-chat {
  flex: 1 1 0;           /* 占剩余宽度 */
  min-width: 0;          /* 防止被内容撑爆 */
}
.ai-keywords {
  flex: 0 0 33.3333%;    /* 固定 1/3 */
  max-width: 33.3333%;
}
</style>
