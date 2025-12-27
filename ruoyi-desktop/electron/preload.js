// electron/preload.js
const { contextBridge, ipcRenderer } = require('electron')

// 暴露自动化API
contextBridge.exposeInMainWorld('automationAPI', {
  // 运行自动化任务
  runAutomationTask: (config) => {
    console.log('调用 runAutomationTask:', config)
    return ipcRenderer.invoke('run-automation-task', config)
  },
  
  // 停止自动化任务
  stopAutomationTask: () => {
    console.log('调用 stopAutomationTask')
    return ipcRenderer.invoke('stop-automation-task')
  },
  
  // 获取自动化状态
  getAutomationStatus: () => {
    console.log('调用 getAutomationStatus')
    return ipcRenderer.invoke('get-automation-status')
  },
  
  // 清除日志
  clearAutomationLogs: () => {
    console.log('调用 clearAutomationLogs')
    return ipcRenderer.invoke('clear-automation-logs')
  },
  
  // 监听自动化日志
  onAutomationLog: (callback) => {
    console.log('注册自动化日志监听器')
    ipcRenderer.on('automation-log', (event, log) => {
      console.log('收到自动化日志:', log)
      callback(log)
    })
    return () => ipcRenderer.removeListener('automation-log', callback)
  },
  
  // 监听自动化结果
  onAutomationResult: (callback) => {
    console.log('注册自动化结果监听器')
    ipcRenderer.on('automation-result', (event, result) => {
      console.log('收到自动化结果:', result)
      callback(result)
    })
    return () => ipcRenderer.removeListener('automation-result', callback)
  },
  
  // 监听准备就绪事件
  onAutomationReady: (callback) => {
    ipcRenderer.on('automation-ready', () => {
      console.log('自动化系统准备就绪')
      callback()
    })
    return () => ipcRenderer.removeListener('automation-ready', callback)
  }
})

// 暴露系统API
contextBridge.exposeInMainWorld('systemAPI', {
  // 打开文件对话框
  openFileDialog: (options) => {
    return ipcRenderer.invoke('dialog:openFile', options)
  },
  
  // 获取系统信息
  getSystemInfo: () => {
    return ipcRenderer.invoke('get-system-info')
  },
  
  // 显示通知
  showNotification: (title, body) => {
    // 这里可以添加桌面通知逻辑
    console.log('通知:', title, body)
    if (Notification.permission === 'granted') {
      new Notification(title, { body })
    }
  }
})

// 暴露环境变量
contextBridge.exposeInMainWorld('appEnv', {
  isElectron: true,
  platform: process.platform,
  version: '2.0.0',
  timestamp: new Date().toISOString()
})