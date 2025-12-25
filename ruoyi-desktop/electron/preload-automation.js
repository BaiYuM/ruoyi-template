const { contextBridge, ipcRenderer } = require('electron')

// 暴露 API 给渲染进程
contextBridge.exposeInMainWorld('automationAPI', {
  // 自动化控制
  startAutomation: (config) => ipcRenderer.invoke('start-automation', config),
  stopAutomation: () => ipcRenderer.invoke('stop-automation'),
  getAutomationStatus: () => ipcRenderer.invoke('get-automation-status'),
  clearAutomationLogs: () => ipcRenderer.invoke('clear-automation-logs'),
  
  // 页面操作
  executeClick: (selector) => ipcRenderer.invoke('execute-click', selector),
  executeScript: (script) => ipcRenderer.invoke('execute-script', script),
  getPageInfo: () => ipcRenderer.invoke('get-page-info'),
  
  // 事件监听
  onAutomationLog: (callback) => {
    ipcRenderer.on('automation-log', (event, log) => callback(log))
  },
  
  onAutomationResult: (callback) => {
    ipcRenderer.on('automation-result', (event, result) => callback(result))
  },
  
  removeListeners: () => {
    ipcRenderer.removeAllListeners('automation-log')
    ipcRenderer.removeAllListeners('automation-result')
  }
})