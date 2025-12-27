const { contextBridge, ipcRenderer } = require('electron');

// 暴露安全的API给渲染进程
contextBridge.exposeInMainWorld('electronAPI', {
  // 窗口管理
  createWindow: (config) => ipcRenderer.invoke('window:create', config),
  closeWindow: (windowId) => ipcRenderer.invoke('window:close', windowId),
  getAllWindows: () => ipcRenderer.invoke('window:getAll'),
  arrangeWindows: (columns) => ipcRenderer.invoke('window:arrange', columns),

  // 任务管理
  startTask: (config) => ipcRenderer.invoke('task:start', config),
  stopTask: (taskId) => ipcRenderer.invoke('task:stop', taskId),
  listTasks: () => ipcRenderer.invoke('task:list'),
  getTaskDetails: (taskId) => ipcRenderer.invoke('task:details', taskId),
  deleteTask: (taskId) => ipcRenderer.invoke('task:delete', taskId),

  // 自动化服务
  executeAutomation: (script) => ipcRenderer.invoke('automation:execute', script),
  stopAutomation: (automationId) => ipcRenderer.invoke('automation:stop', automationId),
  getAllAutomations: () => ipcRenderer.invoke('automation:list'),

  // 浏览器管理
  createBrowser: (options) => ipcRenderer.invoke('browser:create', options),
  closeBrowser: (browserId) => ipcRenderer.invoke('browser:close', browserId),
  getBrowsers: () => ipcRenderer.invoke('browser:list'),
  navigateBrowser: (browserId, url) => ipcRenderer.invoke('browser:navigate', browserId, url),
  browserScreenshot: (browserId, options) => ipcRenderer.invoke('browser:screenshot', browserId, options),

  // 系统相关
  openDevTools: (windowId) => ipcRenderer.invoke('system:openDevTools', windowId),
  getSystemInfo: () => ipcRenderer.invoke('system:info'),
  takeScreenshot: (options) => ipcRenderer.invoke('system:screenshot', options),

  // 事件监听
  onTaskUpdate: (callback) => {
    ipcRenderer.on('task:update', (event, data) => callback(data));
  },
  
  onWindowEvent: (callback) => {
    ipcRenderer.on('window:event', (event, data) => callback(data));
  },

  onAutomationStatus: (callback) => {
    ipcRenderer.on('automation:status', (event, data) => callback(data));
  },

  onBrowserEvent: (callback) => {
    ipcRenderer.on('browser:event', (event, data) => callback(data));
  },

  onSystemLog: (callback) => {
    ipcRenderer.on('system:log', (event, data) => callback(data));
  },

  connectToBrowser: (options) => ipcRenderer.invoke('browser:connect', options),
  scanBrowsers: () => ipcRenderer.invoke('browser:scan'),
  launchDebugChrome: (options) => ipcRenderer.invoke('browser:launch-debug'),
  executeDouyinWithExisting: (options) => ipcRenderer.invoke('douyin:with-existing', options),
});

// 移除事件监听器（防止内存泄漏）
window.addEventListener('beforeunload', () => {
  ipcRenderer.removeAllListeners('task:update');
  ipcRenderer.removeAllListeners('window:event');
  ipcRenderer.removeAllListeners('automation:status');
  ipcRenderer.removeAllListeners('browser:event');
  ipcRenderer.removeAllListeners('system:log');
});