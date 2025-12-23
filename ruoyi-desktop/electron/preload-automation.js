const { contextBridge, ipcRenderer } = require('electron')

// 自动化窗口专属API
contextBridge.exposeInMainWorld('automation', {
  // 运行自动化任务
  runTask: (config) => ipcRenderer.invoke('run-automation-task', config),
  
  // 停止当前任务
  stopTask: () => ipcRenderer.invoke('stop-automation-task'),
  
  // 获取任务状态
  getTaskStatus: () => ipcRenderer.invoke('get-automation-status'),
  
  // 清除日志
  clearLogs: () => ipcRenderer.invoke('clear-automation-logs'),
  
  // 打开主窗口
  openMainWindow: () => ipcRenderer.invoke('open-main-window'),
  
  // 监听任务状态更新
  onStatusUpdate: (callback) => {
    const handler = (event, status) => callback(status)
    ipcRenderer.on('automation-status-update', handler)
    return () => ipcRenderer.removeListener('automation-status-update', handler)
  }
})

// 环境信息（与主窗口保持一致）
contextBridge.exposeInMainWorld('appEnv', {
  isElectron: true,
  platform: process.platform,
  nodeVersion: process.version,
  electronVersion: process.versions.electron,
  windowType: 'automation',
  
  getEnvironmentInfo: () => ({
    isElectron: true,
    platform: process.platform,
    nodeVersion: process.version,
    electronVersion: process.versions.electron,
    windowType: 'automation',
    isAutomationWindow: true
  })
})