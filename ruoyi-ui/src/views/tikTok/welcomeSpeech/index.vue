<template>
  <div class="private-chase-config">
    <!-- 标题 -->
    <div class="title">
      <span class="title-text">欢迎词配置</span>
    </div>

    <!-- 主内容区域 -->
    <div class="main-container">
      <!-- 白色卡片 -->
      <div class="white-card">
        <!-- 左侧配置区域 -->
        <div class="left-section">
          <!-- 配置表单 -->
          <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="config-form">
            <!-- 是否启用 -->
            <el-form-item label="是否启用" prop="isEnabled">
              <el-switch
                v-model="form.isEnabled"
                active-color="#409eff"
                inactive-color="#dcdfe6"
                inline-prompt
              />
            </el-form-item>

            <!-- 账号列表 -->
            <el-form-item label="账号列表" prop="accountList">
              <div class="account-select-wrapper">
                <el-select
                  v-model="form.accountList"
                  multiple
                  placeholder="请选择账号"
                  class="account-select"
                >
                  <el-option
                    v-for="account in accounts"
                    :key="account.id"
                    :label="account.name"
                    :value="account.id"
                  />
                </el-select>
              </div>
            </el-form-item>

            <!-- 欢迎词配置 -->
            <el-form-item label="欢迎词配置" prop="welcomeText">
              <div class="welcome-text-container">
                <!-- 话术列表 -->
                <div v-if="form.welcomeText" class="mb-2 flex items-center phrase-item">
                  <el-tag type="info" class="mr-2">欢迎词</el-tag>
                  <div class="flex-1 phrase-content">{{ form.welcomeText }}</div>
                  <el-button type="text" class="ml-2" @click="clearWelcomeText">删除</el-button>
                </div>

                <!-- 添加新话术 -->
                <div v-if="adding" class="mb-2 flex items-center add-input-container">
                  <el-input 
                    v-model="newWelcomeText" 
                    placeholder="输入欢迎词，按回车或点击添加" 
                    @keyup.enter.native="confirmAddWelcome" 
                    class="flex-1"
                  />
                  <el-button type="primary" class="ml-2" @click="confirmAddWelcome">添加</el-button>
                  <el-button class="ml-2" @click="cancelAddWelcome">取消</el-button>
                </div>

                <!-- 操作按钮 -->
                <div class="action-buttons">
                    <el-button 
                        @click="startAddWelcome" round class="add-button" :disabled="!!form.welcomeText" >
                        <el-icon class="mr-1"><Plus /></el-icon>添加一行数据
                    </el-button>
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
            <!-- 手机状态栏 - 白色背景 -->
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
                <div class="chat-title">您的企业抖音号</div>
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
              <!-- 系统提示（橘色底，像微信一样） -->
              <div class="system-notice">
                <el-icon color="#e6a23c" size="12"><Warning /></el-icon>
                <span class="warning-text">对方回复或关注你之前，只能发送一条消息</span>
              </div>

              <!-- 用户消息（这是模拟对方发的消息） -->
              <div class="message user-message" v-if="showUserMessage">
                <div class="message-bubble user-bubble">
                  您好，我看到您的产品...
                </div>
              </div>

              <!-- 欢迎词消息 -->
              <div class="message welcome-message" v-if="form.welcomeText">
                <div class="message-bubble welcome-bubble">
                  {{ form.welcomeText }}
                </div>
              </div>

              <!-- 空状态提示 -->
              <div v-if="!form.welcomeText" class="empty-messages">
                <el-empty 
                  description="请先配置欢迎词" 
                  :image-size="60"
                />
              </div>
            </div>

            <!-- 发送区域 -->
            <div class="send-area">
              <el-input
                placeholder="发送消息..."
                class="send-input"
                :disabled="true"
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
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const form = reactive({
  isEnabled: false,
  accountList: [],
  welcomeText: ''
})

// 账号列表数据
const accounts = ref([
  { id: 1, name: '抖音账号1' },
  { id: 2, name: '抖音账号2' },
  { id: 3, name: '抖音账号3' },
  { id: 4, name: '抖音账号4' },
  { id: 5, name: '抖音账号5' }
])

const rules = {
  welcomeText: [{ required: true, message: '请添加欢迎词', trigger: 'change' }]
}

// 欢迎词添加相关
const adding = ref(false)
const newWelcomeText = ref('')
const formRef = ref(null)
const showUserMessage = ref(true) // 模拟显示用户消息

// 开始添加欢迎词
function startAddWelcome() {
  adding.value = true
  newWelcomeText.value = ''
}

// 取消添加
function cancelAddWelcome() {
  adding.value = false
  newWelcomeText.value = ''
}

// 确认添加欢迎词
function confirmAddWelcome() {
  const v = (newWelcomeText.value || '').trim()
  if (!v) {
    ElMessage.warning('请输入欢迎词内容')
    return
  }
  
  form.welcomeText = v
  newWelcomeText.value = ''
  adding.value = false
}

// 清除欢迎词
function clearWelcomeText() {
  form.welcomeText = ''
}

// 重置表单
function resetForm() {
  form.isEnabled = false
  form.accountList = []
  form.welcomeText = ''
  adding.value = false
  newWelcomeText.value = ''
}

// 保存配置
function saveConfig() {
  formRef.value.validate(valid => {
    if (!valid) {
      ElMessage.error('请检查表单填写是否正确')
      return
    }
    
    if (form.accountList.length === 0) {
      ElMessage.error('请至少选择一个账号')
      return
    }
    
    // 构建保存数据
    const config = {
      isEnabled: form.isEnabled,
      accountList: form.accountList,
      welcomeText: form.welcomeText
    }
    
    console.log('保存配置:', config)
    ElMessage.success('配置保存成功')
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
  width: 95%;
  max-width: 1500px;
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

/* 账号选择器 */
.account-select-wrapper {
  width: 100%;
  max-width: 450px;
}

.account-select {
  width: 100%;
  background: #f5f7fa;
}

.account-select :deep(.el-input__inner) {
  background: #f5f7fa;
}

.account-select :deep(.el-select__tags) {
  background: #f5f7fa;
}

/* 欢迎词容器 */
.welcome-text-container {
  width: 100%;
  max-width: 450px;
}

.phrase-item {
  background: #f8f9fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 14px;
  margin-bottom: 12px;
}

.phrase-content {
  color: #333;
  font-size: 14px;
  line-height: 1.5;
}

/* 添加输入框容器 */
.add-input-container {
  width: 100%;
  background: #f8f9fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 12px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 16px;
}

/* 修改后的添加按钮样式 - 加长版 */
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
  background-color: #f0f7ff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.add-button:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 1px 4px rgba(64, 158, 255, 0.1);
}

.add-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background-color: #f5f7fa;
  color: #999;
  border-color: #e4e7ed;
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
  width: 350px;
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
  padding: 12px;
  overflow-y: auto;
  background: #f9f9f9;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 系统提示（像微信一样） */
.system-notice {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 6px 10px;
  background: rgba(230, 162, 60, 0.1);
  border-radius: 12px;
  margin: 0 auto 12px auto;
  font-size: 11px;
  color: #e6a23c;
  max-width: 90%;
  text-align: center;
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

.welcome-message {
  align-self: flex-end;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.user-bubble {
  background: white;
  color: #333;
  border: 1px solid #e4e7ed;
  border-radius: 18px 18px 18px 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.welcome-bubble {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
  border-radius: 18px 18px 4px 18px;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
}

.welcome-bubble:hover {
  transform: scale(1.02);
  transition: transform 0.2s;
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
  color: #999;
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
  
  .account-select-wrapper,
  .welcome-text-container {
    width: 100%;
  }
  
  .add-button {
    max-width: 100%;
  }
}
</style>