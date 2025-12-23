<template>
  <div class="config-wrapper">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-button :icon="ArrowLeft" @click="goBack"></el-button>
      <h2 class="page-title">配置参数</h2>
    </div>

    <!-- 配置表单+预览调试卡片 -->
    <el-card shadow="never" class="config-card">
      <el-row :gutter="24">
        <!-- 左侧配置表单 -->
        <el-col :span="16">
          <div class="form-tip">
            提示: 因为受抖音平台限制,系统不能连续一直和用户聊天,有一定数量限制。
          </div>
          <el-form
            :model="formData"
            label-width="80px"
            :rules="formRules"
            ref="formRef"
            label-position="top"
            class="config-form"
          >
            <!-- 授权账号 -->
            <el-form-item label="*授权账号" prop="expiration_id">
              <el-select 
                v-model="formData.expiration_id" 
                placeholder="请选择"
                class="gray-input"
              >
                <el-option label="DouMaster" value="1" />
                <el-option label="Dou大师" value="2" />
                <el-option label="添澎科技" value="3" />
              </el-select>
            </el-form-item>

            <!-- 配置名称 -->
            <el-form-item label="*配置名称" prop="name">
              <el-input
                v-model="formData.name"
                placeholder="请输入"
                maxlength="12"
                show-word-limit
                class="gray-input"
              />
            </el-form-item>

            <!-- 提示词 -->
            <el-form-item label="*提示词" prop="prompt">
              <el-input
                v-model="formData.prompt"
                type="textarea"
                placeholder="请输入"
                :rows="6"
                maxlength="5000"
                show-word-limit
                class="gray-input"
              />
            </el-form-item>

            <!-- 知识库 -->
            <el-form-item label="知识库" prop="knowledge_base">
              <el-input
                v-model="formData.knowledge_base"
                type="textarea"
                placeholder="请输入"
                :rows="4"
                maxlength="5000"
                show-word-limit
                class="gray-input"
              />
              <el-link type="primary" class="mt-2 block"
                >引导添加知识库</el-link
              >
            </el-form-item>

            <!-- 携带上下文轮数 -->
            <el-form-item label="携带上下文轮数" prop="count">
              <el-slider
                v-model="formData.count"
                :min="5"
                :max="50"
                :step="5"
                show-input
                input-size="small"
                :marks="contextMarks"
                :style="{
                  '--el-slider-runway-height': '6px',
                  '--el-slider-rail-background-color': '#f0f0f0',
                  '--el-slider-process-background-color': '#ff85b3',
                }"
              />
            </el-form-item>

            <!-- 严苛指数 -->
            <el-form-item label="严苛指数" prop="level">
              <el-slider
                v-model="formData.level"
                :min="0"
                :max="1.9"
                :step="0.1"
                show-input
                input-size="small"
                input-precision="1"
                :marks="strictMarks"
                :style="{
                  '--el-slider-runway-height': '6px',
                  '--el-slider-rail-background-color': '#f0f0f0',
                  '--el-slider-runway-background-color': '#f0f0f0',
                  '--el-slider-process-background-color': '#ff85b3',
                }"
              />
            </el-form-item>

            <!-- 启用配置 -->
            <el-form-item label="启用配置">
              <el-switch
                v-model="formData.status"
                active-color="#ff2d55"
                inactive-color="#c0ccda"
                :style="{ '--el-switch-width': '40px' }"
              />
            </el-form-item>
          </el-form>

          <!-- 重置/保存按钮 -->
          <div class="form-bottom-actions">
            <el-button type="default" @click="resetForm" round>重置</el-button>
            <el-button
              type="primary"
              @click="submitForm"
              round
              :loading="loading"
              >{{ loading ? '保存中...' : '保存设置' }}</el-button
            >
          </div>
        </el-col>

        <!-- 右侧预览与调试 -->
        <el-col :span="8">
          <div class="preview-card">
            <h3 class="preview-title">预览与调试</h3>
            <!-- 调试框：圆角+阴影 -->
            <div class="preview-content" v-html="previewContent"></div>
            <div class="clear-btn-wrapper">
                <el-button 
                    round 
                    @click="clearPreview"
                    size="small"
                    style="margin-bottom: 10px; margin-right: 5px;"
                ><el-icon color="#666"><Delete  /></el-icon>清空内容</el-button>
            </div>
            
            <!-- 输入框组部分 -->
            <div class="debug-input-wrap">
              <!-- 渐变边框容器 -->
              <div class="gradient-border-container">
                <el-input
                  v-model="debugInput"
                  placeholder="请输入问题，体验客服能力"
                  size="small"
                  class="debug-input"
                  :style="{
                    border: 'none',
                    background: 'transparent',
                    '--el-input-placeholder-color': '#999'
                  }"
                />
                <!-- 蓝色圆形按钮（带向上箭头） -->
                <el-button
                  type="primary"
                  circle
                  size="small"
                  class="send-btn"
                  style="
                    width: 32px;
                    height: 32px;
                    margin-right: 4px;
                  "
                  @click="sendDebug"
                ><el-icon :size="16"><ArrowUp /></el-icon></el-button>
              </div>
            </div>

            <div class="preview-footer">
              试用体验内容均由人工智能模型生成,不代表平台立场
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="js">
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import { ArrowLeft, ArrowUp, Delete } from '@element-plus/icons-vue'
// 导入封装的request请求
import request from '@/utils/request'

// 路由实例
const router = useRouter()
const route = useRoute()

// 表单引用
const formRef = ref(null)

// 滑块刻度配置
const contextMarks = {
  5: '5',
  10: '10',
  15: '15',
  20: '20',
  25: '25',
  30: '30',
  40: '40',
  50: '50'
}

const strictMarks = {
  0: '0',
  0.5: '0.5',
  1: '1',
  1.5: '1.5',
  1.9: '1.9'
}

// 是否为编辑模式（根据路由参数判断）
const isEdit = computed(() => !!route.query.id)

// ========== 表单数据 ==========
const formData = reactive({
  expiration_id: '', // 授权账号
  name: '', // 配置名称
  prompt: '', // 提示词
  knowledge_base: '', // 知识库
  count: 5, // 携带上下文轮数（默认5）
  level: 1, // 严苛指数（默认1）
  status: true // 启用配置（默认开启）
})

// ========== 预览调试数据 ==========
const previewContent = ref('')
const debugInput = ref('')

// ========== 加载状态 ==========
const loading = ref(false)

// ========== 表单校验规则 ==========
const formRules = reactive({
  expiration_id: [{ required: true, message: '请选择授权账号', trigger: 'change' }],
  name: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  prompt: [{ required: true, message: '请输入提示词', trigger: 'blur' }]
})

// ========== 编辑模式：获取详情数据 ==========
const initEditData = async () => {
  if (isEdit.value) {
    try {
      const loadingInstance = ElLoading.service({
        lock: true,
        text: '加载中...',
        background: 'rgba(0, 0, 0, 0.7)',
      })
      
      // 使用封装的request请求
      const response = await request({
        url: `aiSerConfig/aiconfig/${route.query.id}`,
        method: 'GET',
      })
      
      loadingInstance.close()
      
      // 根据实际API返回结构调整
      if (response.code === 200) {
        const data = response.data || {}
        
        // 逐个赋值给reactive对象
        Object.keys(formData).forEach(key => {
          if (data[key] !== undefined) {
            formData[key] = data[key]
          }
        })
      } else {
        ElMessage.error(response.msg || '获取数据失败')
        router.back()
      }
    } catch (error) {
      console.error('获取配置详情失败:', error)
      ElMessage.error('网络错误或服务器异常')
      router.back()
    }
  }
}

// ========== 返回列表页 ==========
const goBack = () => {
  router.back()
}

// ========== 重置表单 ==========
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
    // 重置非表单字段（如开关）
    formData.status = true
    formData.count = 5
    formData.level = 1
  }
}

// ========== 提交配置 ==========
const submitForm = async () => {
  try {
    await formRef.value.validate()
    
    loading.value = true
    
    try {
      let response
      
      if (isEdit.value) {
        // 编辑模式 - 使用PUT
        response = await request({
          url: 'aiSerConfig/aiconfig',
          method: 'PUT',
          data: {
            id: route.query.id,
            expiration_id: formData.expiration_id,
            name: formData.name,
            prompt: formData.prompt,
            knowledge_base: formData.knowledge_base,
            count: formData.count,
            level: formData.level,
            status: formData.status ? 1 : 0 // 转换为数字
          }
        })
      } else {
        // 新增模式 - 使用POST
        response = await request({
          url: 'aiSerConfig/aiconfig',
          method: 'POST',
          data: {
            expiration_id: formData.expiration_id,
            name: formData.name,
            prompt: formData.prompt,
            knowledge_base: formData.knowledge_base,
            count: formData.count,
            level: formData.level,
            status: formData.status ? 1 : 0 // 转换为数字
          }
        })
      }
      
      loading.value = false
      
      if (response.code === 200) {
        ElMessage.success(isEdit.value ? '配置编辑成功！' : '配置新增成功！')
        
        // 延迟返回列表页
        setTimeout(() => {
          router.back()
        }, 800)
      } else {
        ElMessage.error(response.msg || '保存失败，请重试')
      }
    } catch (error) {
      loading.value = false
      console.error('保存配置失败:', error)
      ElMessage.error('保存配置失败，请检查网络连接')
    }
  } catch (err) {
    ElMessage.error('表单校验失败，请检查必填项')
  }
}

// ========== 预览调试功能 ==========
// 清空预览内容
const clearPreview = () => {
  previewContent.value = ''
  debugInput.value = ''
}

// 发送调试问题
const sendDebug = async () => {
  if (!debugInput.value.trim()) {
    ElMessage.warning('请输入问题内容')
    return
  }
  
  // 显示用户问题
  const userQuestion = `<div class="msg-user">${debugInput.value}</div>`
  previewContent.value += userQuestion
  
  try {
    // 这里可以调用实际的调试接口（如果需要）
    // 模拟AI回复
    const aiReply = `<div class="msg-ai">已收到你的问题："${debugInput.value}"，我会根据配置的提示词和知识库为你解答~</div>`
    previewContent.value += aiReply
    
    // 如果有实际调试接口，可以这样调用：
    /*
    const response = await request({
      url: 'aiSerConfig/debug',
      method: 'POST',
      data: {
        question: debugInput.value,
        config_id: route.query.id || ''
      }
    })
    
    if (response.code === 200) {
      previewContent.value += `<div class="msg-ai">${response.data.reply}</div>`
    }
    */
  } catch (error) {
    const errorReply = `<div class="msg-ai">抱歉，调试服务暂时不可用，请稍后再试。</div>`
    previewContent.value += errorReply
  }
  
  debugInput.value = ''
  
  // 滚动到底部
  setTimeout(() => {
    const previewDom = document.querySelector('.preview-content')
    if (previewDom) {
      previewDom.scrollTop = previewDom.scrollHeight
    }
  }, 100)
}

// ========== 生命周期钩子 ==========
onMounted(() => {
  // 页面加载时初始化数据
  initEditData()
})
</script>

<style scoped>
/* 全局白底 */
.config-wrapper {
  margin: 20px;
  padding: 20px;
  background-color: #ffffff; /* 改为纯白背景 */
  min-height: calc(100vh - 40px);
}

/* 页面头部 */
.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 配置卡片 */
.config-card {
  padding: 24px;
  background: #fff;
  border: none; /* 移除卡片边框，保持纯白 */
  box-shadow: none; /* 移除阴影 */
}

/* 表单提示 */
.form-tip {
  color: #858282;
  font-size: 12px;
  margin-bottom: 16px;
}

/* 配置表单 */
.config-form .el-form-item {
  margin-bottom: 20px;
}

/* 灰色输入框样式 */
.gray-input {
  --el-input-bg-color: #f5f7fa;
  --el-input-border-color: #e5e6eb;
  --el-input-text-color: #333;
  --el-input-placeholder-color: #999;
}

/* 预览调试区域输入框组 */
.gray-input-group {
  --el-input-bg-color: #f5f7fa;
  --el-input-border-color: #e5e6eb;
}

/* 严苛指数滑块样式 */
:deep(.el-slider__runway) {
  background-color: #f0f0f0 !important;
}
:deep(.el-slider__process) {
  background-color: #ff85b3 !important;
}
:deep(.el-slider__button) {
  border-color: #ff85b3 !important;
}

/* 滑块刻度样式 */
:deep(.el-slider__marks-text) {
  font-size: 11px;
  color: #666;
  margin-top: 8px;
}

:deep(.el-slider__marks) {
  height: 24px;
}

/* 表单底部按钮 */
.form-bottom-actions {
  margin-top: 20px;
  display: flex;
  gap: 12px;
}

/* 预览调试卡片 */
.preview-card {
  background: #f9f9f9;
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  border-radius: 12px; /* 圆角 */
  overflow-y: auto;
  box-shadow: 1px 2px 10px  #bebbbb; /* 轻微阴影 */
}

.preview-title {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 12px 0;
  
}

.clear-btn-wrapper {
    margin-left: 20px;
    margin-bottom: -15px;
}

.preview-content {
  flex: 1;
  min-height: 200px;
}
.debug-input-container {
  margin-top: 12px;
  width: 100%;
}

/* 容器：居中 */
.debug-input-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 12px;
}

/* 渐变边框容器 */
.gradient-border-container {
  display: flex;
  align-items: center;
  width: 80%;
  max-width: 600px;
  border-radius: 25px;
  padding: 3px;
  background: linear-gradient(90deg, #f1c40f, #2ecc71, #3498db, #9b59b6);
  position: relative;
  overflow: hidden;
}

/* 内部白色背景 */
.gradient-border-container::before {
  content: '';
  position: absolute;
  top: 2px;
  left: 2px;
  right: 2px;
  bottom: 2px;
  background: #fff;
  border-radius: 23px;
  z-index: 0;
}

/* 输入框样式 */
.debug-input {
  flex: 1;
  position: relative;
  z-index: 1;
}

/* 移除输入框默认边框和背景 */
:deep(.debug-input .el-input__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
}

/* 输入框聚焦效果 */
:deep(.debug-input .el-input__wrapper.is-focus) {
  box-shadow: none !important;
}

/* 输入框内部 */
:deep(.debug-input .el-input__inner) {
  background: transparent !important;
  padding-left: 16px;
  height: 40px;
  line-height: 40px;
}

/* 按钮样式 */
.send-btn {
  position: relative;
  z-index: 1;
}

/* 按钮hover效果 - 蓝色变深 */
:deep(.send-btn:hover) {
  background: #2980b9 !important;
  border-color: #2980b9 !important;
  transform: scale(1.05);
  transition: all 0.2s ease;
}

/* 确保箭头图标为白色 */
:deep(.send-btn .el-icon-arrow-up) {
  color: white;
  font-size: 14px;
}

/* 聊天消息样式 */
.msg-user {
  text-align: right;
  color: #333;
  margin-bottom: 8px;
}
.msg-ai {
  color: #666;
  margin-bottom: 8px;
}
.preview-footer {
  font-size: 10px;
  color: #999;
  text-align: center;
  margin-top: 8px;
}

/* 文字统计进度条样式优化 */
:deep(.el-input__count) {
  color: #666;
  font-size: 12px;
}

:deep(.el-input__count-inner) {
  background-color: #f5f7fa;
}

:deep(.el-input__count-progress) {
  background-color: #ff85b3;
}
</style>