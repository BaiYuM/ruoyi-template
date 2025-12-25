const SimpleAutomation = require('./simple-automation')

// 创建单例实例
const automationManager = new SimpleAutomation()

// 导出函数接口
async function runAutomationTask(config = {}) {
  return await automationManager.startTask(config)
}

async function stopAutomationTask() {
  return await automationManager.stopTask()
}

function getAutomationStatus() {
  return automationManager.getTaskStatus()
}

function clearAutomationLogs() {
  return automationManager.clearLogs()
}

// 导出模块
module.exports = {
  automationManager,
  runAutomationTask,
  stopAutomationTask,
  getAutomationStatus,
  clearAutomationLogs
}