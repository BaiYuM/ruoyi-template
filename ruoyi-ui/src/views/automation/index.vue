<!-- ruoyi-ui/src/views/automation/index.vue -->
<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <span>自动化任务管理</span>
        <span v-if="envInfo.isElectron" style="float: right; font-size: 12px; color: #666;">
          Electron 桌面版
        </span>
      </template>
      
      <el-alert
        v-if="!envInfo.isElectron"
        title="注意"
        type="warning"
        description="自动化功能仅在 Electron 桌面版中可用"
        show-icon
        style="margin-bottom: 20px;"
      />
      
      <div v-if="envInfo.isElectron">
        <!-- 自动化功能界面 -->
        <el-form :model="form" label-width="120px">
          <el-form-item label="目标URL">
            <el-input v-model="form.url" placeholder="https://example.com" style="width: 400px" />
          </el-form-item>
          
          <el-form-item label="运行模式">
            <el-radio-group v-model="form.headless">
              <el-radio :label="true">后台运行</el-radio>
              <el-radio :label="false">显示浏览器</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="runAutomation" 
              :loading="loading"
              :disabled="!isAutomationAvailable"
            >
              开始任务
            </el-button>
            <el-button @click="openAutomationWindow">
              打开控制台
            </el-button>
            <el-button @click="checkTaskStatus">
              检查状态
            </el-button>
          </el-form-item>
        </el-form>
        
        <el-divider />
        
        <!-- 任务状态显示 -->
        <div v-if="taskStatus">
          <h3>任务状态</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="运行状态">
              <el-tag :type="taskStatus.isRunning ? 'success' : 'info'">
                {{ taskStatus.isRunning ? '运行中' : '已停止' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="浏览器状态">
              {{ taskStatus.browserStatus }}
            </el-descriptions-item>
            <el-descriptions-item label="页面状态">
              {{ taskStatus.pageStatus }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      
      <div v-else>
        <el-empty description="请在 Electron 桌面版中使用自动化功能">
          <el-button type="primary" @click="downloadDesktopApp">
            下载桌面版应用
          </el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import env from '@/utils/env'

const envInfo = env.getEnvInfo()
const isAutomationAvailable = env.isFeatureAvailable('automation')

const form = reactive({
  url: 'https://example.com',
  headless: true
})

const loading = ref(false)
const taskStatus = ref(null)
let statusUpdateHandler = null

// 运行自动化任务
const runAutomation = async () => {
  if (!form.url) {
    ElMessage.error('请输入目标URL')
    return
  }
  
  if (!isAutomationAvailable) {
    ElMessage.warning('自动化功能不可用')
    return
  }
  
  loading.value = true
  
  try {
    const automationAPI = env.getAutomationAPI()
    if (automationAPI && automationAPI.runTask) {
      const result = await automationAPI.runTask({
        url: form.url,
        headless: form.headless,
        screenshot: true
      })
      
      if (result.success) {
        ElMessage.success(`任务完成！访问了: ${result.url}`)
      } else {
        ElMessage.error(`任务失败: ${result.error}`)
      }
      
      checkTaskStatus()
    } else {
      ElMessage.error('自动化 API 不可用')
    }
  } catch (error) {
    ElMessage.error(`执行失败: ${error.message}`)
  } finally {
    loading.value = false
  }
}

// 打开自动化控制台窗口
const openAutomationWindow = () => {
  env.runInElectron(({ api }) => {
    if (api && api.openAutomationWindow) {
      api.openAutomationWindow()
    }
  })
}

// 检查任务状态
const checkTaskStatus = async () => {
  const automationAPI = env.getAutomationAPI()
  if (automationAPI && automationAPI.getTaskStatus) {
    try {
      taskStatus.value = await automationAPI.getTaskStatus()
    } catch (error) {
      console.error('获取任务状态失败:', error)
    }
  }
}

// 下载桌面版应用
const downloadDesktopApp = () => {
  // 这里可以实现下载逻辑
  ElMessage.info('桌面版应用下载功能开发中...')
}

// 设置状态更新监听
const setupStatusListener = () => {
  const automationAPI = env.getAutomationAPI()
  if (automationAPI && automationAPI.onStatusUpdate) {
    statusUpdateHandler = automationAPI.onStatusUpdate((status) => {
      taskStatus.value = status
    })
  }
}

onMounted(() => {
  if (envInfo.isElectron && isAutomationAvailable) {
    setupStatusListener()
    checkTaskStatus()
    
    // 每5秒刷新一次状态
    const interval = setInterval(checkTaskStatus, 5000)
    
    // 清理定时器
    onUnmounted(() => {
      clearInterval(interval)
      if (typeof statusUpdateHandler === 'function') {
        statusUpdateHandler()
      }
    })
  }
})
</script>