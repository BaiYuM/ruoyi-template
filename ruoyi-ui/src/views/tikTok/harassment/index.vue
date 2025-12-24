<template>
  <div class="private-chase-config">
    <!-- 标题 -->
    <div class="title">
      <span class="title-text">私信追杀</span>
    </div>

    <!-- 主内容区域 -->
    <div class="main-container">
      <!-- 白色卡片 -->
      <div class="white-card">
        <!-- 左侧配置区域 -->
        <div class="left-section">
          <!-- 回访规则配置标题 -->
          <div class="config-title">回访规则配置</div>
          
          <!-- 规则说明 -->
          <div class="rule-description">
            <div class="gray-text">在24小时内，如果用户没有留资，可以主动发送回访消息，多次引导留资。</div>
            <div class="red-text">注意:如果用户已经留资，则不会对该用户继续追杀。</div>
          </div>

          <!-- 配置表单 -->
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="config-form">
            <!-- 账号选择 -->
            <el-form-item label="选择账号" prop="accountId">
              <el-select v-model="form.accountId" placeholder="请选择抖音账号" @change="handleAccountChange" style="width: 250px">
                <el-option
                  v-for="item in accountOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>

            <!-- 是否启用 -->
            <el-form-item label="是否启用" prop="isEnabled">
              <el-radio-group v-model="form.isEnabled">
                <el-radio :label="1">开启</el-radio>
                <el-radio :label="0">关闭</el-radio>
              </el-radio-group>
            </el-form-item>

            <!-- 回复间隔 -->
            <el-form-item label="回复间隔" prop="replyInterval">
              <div class="time-input-wrapper">
                <el-time-picker
                  v-model="form.replyInterval"
                  format="HH:mm:ss"
                  value-format="HH:mm:ss"
                  placeholder="选择时间间隔"
                  :clearable="false"
                  class="reply-interval-input"
                />
              </div>
            </el-form-item>

            <!-- 回访次数 -->
            <el-form-item label="回访次数" prop="visitCount">
              <div class="visit-count-wrapper">
                <el-input
                  v-model="form.visitCount"
                  placeholder="请输入回访次数"
                  class="visit-count-input"
                  type="number"
                  min="1"
                  max="10"
                />
                <span class="unit">次</span>
              </div>
            </el-form-item>

            <!-- 回复话术 -->
            <el-form-item label="回复话术">
              <div class="phrase-container">
                <!-- 话术列表 -->
                <div v-for="(phrase, idx) in segments" :key="idx" class="mb-2 flex items-center phrase-item">
                  <el-tag type="info" class="mr-2">段 {{ idx + 1 }}</el-tag>
                  <div class="flex-1 phrase-content">{{ phrase }}</div>
                  <el-button type="text" class="ml-2" @click="removeSegment(idx)">删除</el-button>
                </div>

                <!-- 添加新话术 -->
                <div v-if="adding" class="mb-2 flex items-center">
                  <el-input 
                    v-model="newSegment" 
                    placeholder="输入回访话术，按回车或点击添加" 
                    @keyup.enter.native="confirmAdd" 
                    class="flex-1"
                    maxlength="200"
                    show-word-limit
                  />
                  <el-button type="primary" class="ml-2" @click="confirmAdd">添加</el-button>
                  <el-button class="ml-2" @click="cancelAdd">取消</el-button>
                </div>

                <!-- 操作按钮 -->
                <div class="action-buttons">
                  <el-button 
                    @click="startAdd" 
                    round 
                    class="add-button"
                    :disabled="segments.length >= 5"
                  >
                    + 添加一条回访话术
                  </el-button>
                  <div class="tip-text">最多可添加5条话术，已添加 {{ segments.length }}/5</div>
                </div>
              </div>
            </el-form-item>

            <!-- 操作按钮 -->
            <div class="form-actions mt-4 flex justify-end">
              <el-button @click="resetForm" round class="reset-button">重置</el-button>
              <el-button type="primary" class="ml-4 save-button" @click="saveConfig" round>保存</el-button>
            </div>
          </el-form>
        </div>

        <!-- 分割线 -->
        <div class="divider"></div>

        <!-- 右侧预览区域 -->
        <div class="right-section">
          <!-- 手机预览卡片 -->
          <div class="phone-preview-card">
            <!-- 手机状态栏 - 改为白色背景 -->
            <div class="phone-status-bar">
              <span class="time">12:00</span>
              <div class="status-icons">
                <el-icon class="wifi-icon"><Wifi /></el-icon>
                <div class="battery-container">
                  <div class="battery-body">
                    <div class="battery-level"></div>
                  </div>
                  <div class="battery-tip"></div>
                </div>
              </div>
            </div>

            <!-- 聊天头部 -->
            <div class="chat-header">
              <div class="back-button">
                <el-icon><ArrowLeft /></el-icon>
              </div>
              <div class="chat-title-wrapper">
                <div class="chat-title">抖音昵称</div>
              </div>
              <div class="menu-button">
                <div class="menu-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>

            <!-- 聊天内容区域 -->
            <div class="chat-content">
              <!-- 用户消息 -->
              <div class="message user-message">
                <div class="message-bubble">
                  这个产品多少钱？
                </div>
              </div>

              <!-- 追杀消息列表 -->
              <div
                v-for="(phrase, index) in segments" 
                :key="index"
                class="message chase-message"
              >
                <div class="message-bubble chase-bubble">
                  {{ phrase || '这里是回访话术内容...' }}
                </div>
              </div>

              <!-- 空状态提示 -->
              <div v-if="segments.length === 0" class="empty-messages">
                <el-empty 
                  description="请先添加回访话术" 
                  :image-size="60"
                />
              </div>
            </div>

            <!-- 发送区域 -->
            <div class="send-area">
              <el-input
                placeholder="发送消息..."
                class="send-input"
              >
                <template #prefix>
                  <el-icon><Plus /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" icon="Promotion" circle class="send-button" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAccounts } from '@/api/privateChat'
import { listConfig, addConfig, updateConfig } from '@/api/tikTok/harassment'

// 预设时间选项
const presetTimes = [
  { label: '30分钟', value: '00:30:00' },
  { label: '1小时', value: '01:00:00' },
  { label: '2小时', value: '02:00:00' },
  { label: '4小时', value: '04:00:00' },
  { label: '6小时', value: '06:00:00' },
  { label: '12小时', value: '12:00:00' },
  { label: '24小时', value: '24:00:00' }
]

// 响应式数据
const form = reactive({
  id: undefined,
  accountId: undefined,
  isEnabled: 1,
  replyInterval: '01:00:00', // 默认1小时
  visitCount: '3'
})

const accountOptions = ref([])
const currentConfig = ref(null)

// 获取账号列表
function getAccountList() {
  getAccounts().then(response => {
    accountOptions.value = response.data
    if (accountOptions.value.length > 0) {
      form.accountId = accountOptions.value[0]
      handleAccountChange(form.accountId)
    }
  })
}

// 账号切换处理
function handleAccountChange(accountId) {
  listConfig({ accountId: accountId }).then(response => {
    if (response.rows && response.rows.length > 0) {
      const config = response.rows[0]
      currentConfig.value = config
      form.id = config.id
      form.isEnabled = config.isEnabled
      form.replyInterval = config.replyInterval
      form.visitCount = config.visitCount.toString()
      segments.value = JSON.parse(config.phraseList || '[]')
    } else {
      currentConfig.value = null
      form.id = undefined
      form.isEnabled = 1
      form.replyInterval = '01:00:00'
      form.visitCount = '3'
      segments.value = [
        '您好，看到您对我们的产品感兴趣，需要我为您详细介绍下吗？',
        '如果方便的话，可以留下您的联系方式，我们会安排专属顾问为您服务。',
        '期待您的回复，有任何问题都可以随时咨询我哦~'
      ]
    }
  })
}

onMounted(() => {
  getAccountList()
})

// 时间验证函数
const validateTime = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请选择回复间隔时间'))
    return
  }
  
  // 验证时间格式
  const timeRegex = /^([01]?\d|2[0-3]):([0-5]?\d):([0-5]?\d)$/
  if (!timeRegex.test(value)) {
    callback(new Error('时间格式不正确'))
    return
  }
  
  // 验证时间不能超过24小时
  const [hours, minutes, seconds] = value.split(':').map(Number)
  if (hours >= 24) {
    callback(new Error('时间不能超过24小时'))
    return
  }
  
  // 验证时间不能为00:00:00
  if (hours === 0 && minutes === 0 && seconds === 0) {
    callback(new Error('回复间隔时间不能为00:00:00'))
    return
  }
  
  callback()
}

const rules = {
  isEnabled: [{ required: true, message: '请选择是否启用', trigger: 'change' }],
  replyInterval: [
    { required: true, message: '请选择回复间隔时间', trigger: 'change' },
    { validator: validateTime, trigger: 'change' }
  ],
  visitCount: [
    { required: true, message: '请输入回访次数', trigger: 'blur' },
    { 
      pattern: /^[1-9]\d*$/,
      message: '请输入正整数',
      trigger: 'blur'
    },
    {
      validator: (rule, value, callback) => {
        const num = parseInt(value)
        if (num > 10) {
          callback(new Error('回访次数不能超过10次'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 话术段管理
const segments = ref([
  '您好，看到您对我们的产品感兴趣，需要我为您详细介绍下吗？',
  '如果方便的话，可以留下您的联系方式，我们会安排专属顾问为您服务。',
  '期待您的回复，有任何问题都可以随时咨询我哦~'
])

const adding = ref(false)
const newSegment = ref('')
const formRef = ref(null)

// 时间字符串转换为秒数
const timeToSeconds = (timeStr) => {
  if (!timeStr) return 0
  const [hours, minutes, seconds] = timeStr.split(':').map(Number)
  return hours * 3600 + minutes * 60 + seconds
}

// 秒数转换为时间字符串
const secondsToTime = (totalSeconds) => {
  const hours = Math.floor(totalSeconds / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  const seconds = totalSeconds % 60
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

// 选择预设时间
const selectPresetTime = (time) => {
  form.replyInterval = time
}

// 开始添加话术
function startAdd() {
  if (segments.value.length >= 5) {
    ElMessage.warning('最多只能添加5条话术')
    return
  }
  adding.value = true
  newSegment.value = ''
}

// 取消添加
function cancelAdd() {
  adding.value = false
  newSegment.value = ''
}

// 确认添加
function confirmAdd() {
  const v = (newSegment.value || '').trim()
  if (!v) {
    ElMessage.warning('请输入话术内容')
    return
  }
  
  if (segments.value.length >= 5) {
    ElMessage.warning('最多只能添加5条话术')
    return
  }
  
  segments.value.push(v)
  newSegment.value = ''
  adding.value = false
}

// 删除话术段
function removeSegment(i) {
  segments.value.splice(i, 1)
}

// 重置表单
function resetForm() {
  if (form.accountId) {
    handleAccountChange(form.accountId)
  } else {
    form.isEnabled = 1
    form.replyInterval = '01:00:00'
    form.visitCount = '3'
    segments.value = [
      '您好，看到您对我们的产品感兴趣，需要我为您详细介绍下吗？',
      '如果方便的话，可以留下您的联系方式，我们会安排专属顾问为您服务。',
      '期待您的回复，有任何问题都可以随时咨询我哦~'
    ]
  }
}

// 保存配置
function saveConfig() {
  formRef.value.validate(valid => {
    if (!valid) {
      ElMessage.error('请检查表单填写是否正确')
      return
    }
    
    if (segments.value.length === 0) {
      ElMessage.error('请至少添加一条回访话术')
      return
    }
    
    // 构建保存数据
    const config = {
      id: form.id,
      accountId: form.accountId,
      isEnabled: form.isEnabled,
      replyInterval: form.replyInterval,
      replyIntervalSeconds: timeToSeconds(form.replyInterval),
      visitCount: parseInt(form.visitCount),
      phraseList: JSON.stringify(segments.value),
      // 计算总时间（秒）
      totalTimeSeconds: timeToSeconds(form.replyInterval) * parseInt(form.visitCount)
    }
    
    if (config.id) {
      updateConfig(config).then(response => {
        ElMessage.success('配置更新成功')
        handleAccountChange(form.accountId)
      })
    } else {
      addConfig(config).then(response => {
        ElMessage.success('配置保存成功')
        handleAccountChange(form.accountId)
      })
    }
  })
}
</script>

<style scoped>
.private-chase-config {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
  height: 100%;
}

.title {
  margin-bottom: 24px;
}

.title-text {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.main-container {
  display: flex;
  justify-content: center;
  height: 85vh;
}

.white-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  width: 90%;
  max-width: 1400px;
  display: flex;
  overflow: hidden;
  height: 700px;
}

.left-section {
  flex: 1.2;
  padding: 32px;
  overflow-y: auto;
  padding-right: 40px;
}

.right-section {
  flex: 0.8;
  padding: 32px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.divider {
  width: 1px;
  background: #e4e7ed;
  margin: 32px 0;
}

.config-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.rule-description {
  margin-bottom: 24px;
  line-height: 1.6;
}

.gray-text {
  font-size: 12px;
  color: #666;
}

.red-text {
  font-size: 12px;
  color: #e64340;
}

/* 表单样式 */
.config-form {
  max-width: 100%;
}

.config-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  display: block;
}

.config-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

/* 是否启用单选框 */
.config-form :deep(.el-radio-group) {
  display: flex;
  gap: 20px;
}

.config-form :deep(.el-radio) {
  margin-right: 0;
}

/* 回复间隔时间选择器 */
.time-input-wrapper {
  width: 250px;
}

.reply-interval-input {
  width: 100%;
  background: #f5f7fa;
}

.reply-interval-input :deep(.el-input__wrapper) {
  background: #f5f7fa;
  border-radius: 6px;
}

.reply-interval-input :deep(.el-input__inner) {
  background: #f5f7fa;
  text-align: center;
}

.preset-tag {
  cursor: pointer;
  user-select: none;
  transition: all 0.3s;
}

.preset-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 回访次数输入框 */
.visit-count-wrapper {
  position: relative;
  width: 250px;
}

.visit-count-input {
  width: 100%;
}



.unit {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  font-size: 14px;
  z-index: 10;
  pointer-events: none;
}

/* 话术容器 */
.phrase-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.phrase-item {
  background: #f8f9fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 14px;
  transition: all 0.3s;
}

.phrase-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.phrase-content {
  color: #333;
  font-size: 14px;
  line-height: 1.5;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 16px;
}

.add-button {
  width: 100%;
  margin: 8px 0;
  background: white;
  border: 1px solid #dcdfe6;
  color: #333;
  transition: all 0.3s;
}

.add-button:hover:not(:disabled) {
  border-color: #409eff;
  color: #409eff;
  transform: translateY(-1px);
}

.add-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.tip-text {
  font-size: 12px;
  color: #999;
  text-align: left;
}

/* 表单操作按钮 */
.form-actions {
  padding: 20px 0 10px 0;
}

/* 重置按钮 */
.reset-button {
  padding: 12px 36px;
  border-radius: 20px;
  font-size: 14px;
  border: 1px solid #dcdfe6;
  color: #666;
  transition: all 0.3s;
}

.reset-button:hover {
  border-color: #409eff;
  color: #409eff;
  transform: translateY(-1px);
}

/* 保存按钮 */
.save-button {
  padding: 12px 36px;
  border-radius: 20px;
  font-size: 14px;
  background: #409eff;
  border-color: #409eff;
  transition: all 0.3s;
}

.save-button:hover {
  background: #66b1ff;
  border-color: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 手机预览卡片 */
.phone-preview-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  width: 320px;
  height: 600px;
  display: flex;
  flex-direction: column;
  border: 1px solid #e8e8e8;
}

/* 手机状态栏 - 白色背景 */
.phone-status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  background: white;
  color: #333;
  font-size: 14px;
  height: 32px;
  border-bottom: 1px solid #f0f0f0;
}

.time {
  font-weight: 500;
}

.status-icons {
  display: flex;
  align-items: center;
  gap: 12px;
}

.wifi-icon {
  font-size: 16px;
  color: #666;
}

/* 电池图标 */
.battery-container {
  display: flex;
  align-items: center;
}

.battery-body {
  width: 20px;
  height: 10px;
  border: 1px solid #666;
  border-radius: 2px;
  position: relative;
  overflow: hidden;
}

.battery-level {
  position: absolute;
  top: 1px;
  left: 1px;
  width: 14px;
  height: 8px;
  background: #666;
  border-radius: 1px;
}

.battery-tip {
  width: 2px;
  height: 4px;
  background: #666;
  border-radius: 0 1px 1px 0;
  margin-left: 1px;
}

/* 聊天头部 */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
  background: white;
  height: 56px;
}

.back-button {
  cursor: pointer;
}

.back-button .el-icon {
  font-size: 20px;
  color: #333;
}

.chat-title-wrapper {
  flex: 1;
  margin-left: 12px;
}

.chat-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.menu-button {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.3s;
}

.menu-button:hover {
  background: #e8e8e8;
}

.menu-dots {
  display: flex;
  gap: 2px;
}

.menu-dots span {
  width: 3px;
  height: 3px;
  background: #666;
  border-radius: 50%;
}

/* 聊天内容区域 */
.chat-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f9f9f9;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 消息样式 */
.message {
  display: flex;
  flex-direction: column;
  max-width: 85%;
  animation: slideIn 0.3s ease-out;
}

.user-message {
  align-self: flex-start;
}

.chase-message {
  align-self: flex-end;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  transition: transform 0.2s;
}

.user-message .message-bubble {
  background: white;
  color: #333;
  border: 1px solid #e4e7ed;
  border-radius: 18px 18px 18px 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.chase-bubble {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
  border-radius: 18px 18px 4px 18px;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
}

.chase-bubble:hover {
  transform: scale(1.02);
}

/* 空状态提示 */
.empty-messages {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-messages :deep(.el-empty__description) {
  font-size: 13px;
  color: #999;
}

/* 发送区域 */
.send-area {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: white;
  border-top: 1px solid #e4e7ed;
}

.send-input {
  flex: 1;
}

.send-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  background: #f5f7fa;
  padding: 8px 15px;
}

.send-input :deep(.el-input__inner) {
  font-size: 14px;
}

.send-button {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border: none;
  width: 36px;
  height: 36px;
  transition: all 0.3s;
}

.send-button:hover {
  background: linear-gradient(135deg, #66b1ff, #409eff);
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

/* 动画效果 */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 滚动条样式 */
.chat-content::-webkit-scrollbar {
  width: 4px;
}

.chat-content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.chat-content::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 4px;
}

.chat-content::-webkit-scrollbar-thumb:hover {
  background: #c0c4cc;
}

.left-section::-webkit-scrollbar {
  width: 4px;
}

.left-section::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.left-section::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 4px;
}

.left-section::-webkit-scrollbar-thumb:hover {
  background: #c0c4cc;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .white-card {
    flex-direction: column;
    height: auto;
  }
  
  .divider {
    width: 100%;
    height: 1px;
    margin: 0;
  }
  
  .left-section,
  .right-section {
    padding: 24px;
  }
  
  .phone-preview-card {
    width: 300px;
    height: 550px;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .private-chase-config {
    padding: 16px;
  }
  
  .main-container {
    height: auto;
  }
  
  .white-card {
    width: 100%;
    height: auto;
  }
  
  .left-section {
    padding: 20px;
  }
  
  .time-input-wrapper,
  .visit-count-wrapper {
    width: 100%;
  }
}
</style>