const SimpleAutomation = require('./simple-automation')

// 导出单例实例
const automationManager = new AutomationManager()

// 导出函数接口
async function runAutomationTask(config = {}) {
  return await automationManager.runAutomationTask(config)
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

// 导出模块 - 确保这些函数都正确导出
module.exports = {
  AutomationManager,
  automationManager,
  runAutomationTask,      // 确保这个导出
  stopAutomationTask,     // 确保这个导出
  getAutomationStatus,    // 确保这个导出
  clearAutomationLogs     // 确保这个导出
}