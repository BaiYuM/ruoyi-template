const { contextBridge, ipcRenderer } = require('electron')

// 主应用 API
contextBridge.exposeInMainWorld('electronAPI', {
  // 窗口控制
  minimizeWindow: () => ipcRenderer.send('window-minimize'),
  maximizeWindow: () => ipcRenderer.send('window-maximize'),
  closeWindow: () => ipcRenderer.send('window-close'),
  
  // 应用信息
  getAppInfo: () => ipcRenderer.invoke('get-app-info'),
  
  // 打开外部链接
  openExternal: (url) => {
    const { shell } = require('electron')
    shell.openExternal(url)
  },
  
  // 自动化相关API
  openAutomationWindow: () => ipcRenderer.invoke('open-automation-window'),
  runAutomationTask: (config) => ipcRenderer.invoke('run-automation-task', config),
  getAutomationStatus: () => ipcRenderer.invoke('get-automation-status'),
  
  // 监听自动化任务结果
  onAutomationResult: (callback) => {
    const handler = (event, result) => callback(result)
    ipcRenderer.on('automation-result', handler)
    // 提供清理函数
    return () => ipcRenderer.removeListener('automation-result', handler)
  },
  
  // 通用系统API
  platform: process.platform,
  
  // 对话框
  showMessageBox: (options) => ipcRenderer.invoke('show-message-box', options),
  
  // 文件操作
  openFileDialog: () => ipcRenderer.invoke('dialog:openFile'),
  saveFileDialog: () => ipcRenderer.invoke('dialog:saveFile'),
  
  // 系统通知
  sendNotification: (title, body) => ipcRenderer.send('notification', title, body)
})

// 环境信息 - 统一通过这里暴露
contextBridge.exposeInMainWorld('appEnv', {
  isElectron: true,
  platform: process.platform,
  nodeVersion: process.version,
  electronVersion: process.versions.electron,
  
  // 辅助函数：获取完整的运行环境信息
  getEnvironmentInfo: () => ({
    isElectron: true,
    platform: process.platform,
    nodeVersion: process.version,
    electronVersion: process.versions.electron,
    appName: process.env.npm_package_name,
    appVersion: process.env.npm_package_version,
    userAgent: navigator.userAgent
  }),
  
  // 检查特定功能是否可用
  isFeatureAvailable: (feature) => {
    const availableFeatures = {
      automation: true,      // 自动化功能
      fileSystem: true,     // 文件系统访问
      notifications: true,  // 系统通知
      tray: true,           // 系统托盘
      menu: true            // 自定义菜单
    }
    return availableFeatures[feature] || false
  }
})