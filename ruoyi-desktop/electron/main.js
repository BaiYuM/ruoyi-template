// electron/main.js
const { app, BrowserWindow, Menu, ipcMain, dialog, Tray, shell } = require('electron')
const path = require('path')
const fs = require('fs')

// 导入自动化模块
const { 
  automationManager, 
  runAutomationTask, 
  stopAutomationTask,
  getAutomationStatus,
  clearAutomationLogs 
} = require('./automation')

// 判断环境
const isDev = process.env.ELECTRON === 'true'

let mainWindow = null       // RuoYi主窗口
let automationWindow = null // 自动化控制窗口
let tray = null             // 系统托盘

// 创建主窗口（RuoYi窗口）
function createMainWindow() {
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    minWidth: 1200,
    minHeight: 800,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js')
    },
    show: false,
    icon: path.join(__dirname, '../build/icon.png'),
    autoHideMenuBar: true,
    backgroundColor: '#f0f2f5'
  })

  // 加载页面
  if (isDev) {
    const devUrl = 'http://localhost:5173'
    console.log('开发模式，加载URL:', devUrl)
    mainWindow.loadURL(devUrl)
    mainWindow.webContents.openDevTools()
  } else {
    const indexPath = path.join(__dirname, '../../ruoyi-ui/dist/index.html')
    console.log('生产模式，加载文件:', indexPath)
    mainWindow.loadFile(indexPath)
  }

  // 页面加载完成后显示
  mainWindow.once('ready-to-show', () => {
    mainWindow.show()
    console.log('主窗口已显示')
  })

  mainWindow.on('closed', () => {
    mainWindow = null
  })

  // 处理外部链接
  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url)
    return { action: 'deny' }
  })

  // 处理路由404问题
  mainWindow.webContents.on('did-fail-load', () => {
    if (!isDev) {
      mainWindow.loadFile(path.join(__dirname, '../../ruoyi-ui/dist/index.html'))
    }
  })

  return mainWindow
}

// 创建自动化控制窗口
function createAutomationWindow() {
  automationWindow = new BrowserWindow({
    width: 800,
    height: 700,
    minWidth: 600,
    minHeight: 500,
    title: '自动化任务控制台',
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload-automation.js')
    },
    show: false, // 默认不显示，通过菜单打开
    icon: path.join(__dirname, '../build/icon.png'),
    backgroundColor: '#ffffff'
  })

  // 加载自动化控制界面
  const automationHtml = getAutomationHtml()
  automationWindow.loadURL(`data:text/html;charset=UTF-8,${encodeURIComponent(automationHtml)}`)

  automationWindow.on('closed', () => {
    automationWindow = null
  })

  // 窗口显示时刷新状态
  automationWindow.on('show', () => {
    if (automationWindow) {
      automationWindow.webContents.executeJavaScript(`
        if (window.refreshStatus) {
          window.refreshStatus()
        }
      `)
    }
  })

  return automationWindow
}

// 获取自动化控制界面HTML
function getAutomationHtml() {
  return `
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>自动化任务控制台</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body { 
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
      background: #f5f5f5;
      padding: 20px;
      color: #333;
    }
    
    .container {
      max-width: 800px;
      margin: 0 auto;
      background: white;
      border-radius: 8px;
      padding: 24px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.1);
    }
    
    h1 { 
      color: #409EFF; 
      margin-bottom: 20px;
      padding-bottom: 10px;
      border-bottom: 2px solid #f0f0f0;
    }
    
    .form-group { 
      margin-bottom: 20px; 
    }
    
    label { 
      display: block; 
      margin-bottom: 8px; 
      font-weight: 500;
      color: #555;
    }
    
    input, select { 
      width: 100%; 
      padding: 10px 12px;
      border: 1px solid #dcdfe6;
      border-radius: 4px;
      font-size: 14px;
      transition: border-color 0.3s;
    }
    
    input:focus, select:focus { 
      outline: none;
      border-color: #409EFF;
    }
    
    .button-group {
      display: flex;
      gap: 10px;
      margin: 20px 0;
    }
    
    button {
      padding: 10px 20px;
      border: none;
      border-radius: 4px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.3s;
    }
    
    #runBtn {
      background: #409EFF;
      color: white;
      flex: 2;
    }
    
    #runBtn:hover { background: #66b1ff; }
    #runBtn:disabled { background: #a0cfff; cursor: not-allowed; }
    
    #stopBtn {
      background: #f56c6c;
      color: white;
      flex: 1;
    }
    
    #stopBtn:hover { background: #f78989; }
    #stopBtn:disabled { background: #fab6b6; cursor: not-allowed; }
    
    #clearBtn {
      background: #909399;
      color: white;
      flex: 1;
    }
    
    #clearBtn:hover { background: #a6a9ad; }
    
    .status-card {
      background: #f8f9fa;
      padding: 16px;
      border-radius: 6px;
      margin: 20px 0;
      border-left: 4px solid #409EFF;
    }
    
    .status-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;
      font-size: 14px;
    }
    
    .status-label { color: #666; }
    .status-value { 
      font-weight: 500;
      color: #409EFF;
    }
    
    .status-value.running { color: #67c23a; }
    .status-value.stopped { color: #f56c6c; }
    
    .log-container {
      margin-top: 24px;
      border: 1px solid #ebeef5;
      border-radius: 6px;
      overflow: hidden;
    }
    
    .log-header {
      background: #f5f7fa;
      padding: 12px 16px;
      font-weight: 500;
      border-bottom: 1px solid #ebeef5;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .log-content {
      height: 300px;
      overflow-y: auto;
      padding: 16px;
      background: #fafafa;
    }
    
    .log-entry {
      margin-bottom: 10px;
      padding: 10px;
      border-radius: 4px;
      background: white;
      border-left: 4px solid #409EFF;
    }
    
    .log-entry.info { border-left-color: #409EFF; }
    .log-entry.success { border-left-color: #67c23a; }
    .log-entry.warning { border-left-color: #e6a23c; }
    .log-entry.error { border-left-color: #f56c6c; }
    
    .log-time {
      font-size: 12px;
      color: #909399;
      margin-right: 10px;
    }
    
    .log-level {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
      margin-right: 10px;
    }
    
    .log-level.info { background: #ecf5ff; color: #409EFF; }
    .log-level.success { background: #f0f9eb; color: #67c23a; }
    .log-level.warning { background: #fdf6ec; color: #e6a23c; }
    .log-level.error { background: #fef0f0; color: #f56c6c; }
    
    .log-message { 
      word-break: break-all;
      font-size: 13px;
    }
  </style>
</head>
<body>
  <div class="container">
    <h1>自动化任务控制台</h1>
    
    <div class="form-group">
      <label>目标URL:</label>
      <input type="text" id="url" value="https://example.com" placeholder="请输入目标网址">
    </div>
    
    <div class="form-group">
      <label>运行模式:</label>
      <select id="headless">
        <option value="true">后台运行（无界面）</option>
        <option value="false">显示浏览器窗口</option>
      </select>
    </div>
    
    <div class="form-group">
      <label>是否截图:</label>
      <select id="screenshot">
        <option value="true">是</option>
        <option value="false">否</option>
      </select>
    </div>
    
    <div class="button-group">
      <button id="runBtn">开始自动化任务</button>
      <button id="stopBtn">停止任务</button>
      <button id="clearBtn">清除日志</button>
    </div>
    
    <div class="status-card">
      <h3>任务状态</h3>
      <div class="status-item">
        <span class="status-label">运行状态:</span>
        <span id="taskStatus" class="status-value stopped">停止</span>
      </div>
      <div class="status-item">
        <span class="status-label">浏览器状态:</span>
        <span id="browserStatus" class="status-value">未启动</span>
      </div>
      <div class="status-item">
        <span class="status-label">页面状态:</span>
        <span id="pageStatus" class="status-value">未启动</span>
      </div>
    </div>
    
    <div class="log-container">
      <div class="log-header">
        <span>任务日志</span>
        <button id="toggleLogs" style="padding: 4px 12px; font-size: 12px;">折叠/展开</button>
      </div>
      <div class="log-content" id="logContent">
        <div class="log-entry info">
          <span class="log-time">系统</span>
          <span class="log-level info">INFO</span>
          <span class="log-message">自动化控制台已就绪</span>
        </div>
      </div>
    </div>
  </div>
  
  <script>
    const urlInput = document.getElementById('url')
    const headlessSelect = document.getElementById('headless')
    const screenshotSelect = document.getElementById('screenshot')
    const runBtn = document.getElementById('runBtn')
    const stopBtn = document.getElementById('stopBtn')
    const clearBtn = document.getElementById('clearBtn')
    const toggleLogsBtn = document.getElementById('toggleLogs')
    const logContent = document.getElementById('logContent')
    
    const taskStatus = document.getElementById('taskStatus')
    const browserStatus = document.getElementById('browserStatus')
    const pageStatus = document.getElementById('pageStatus')
    
    let logsExpanded = true
    let currentTaskId = null
    
    // 刷新任务状态
    window.refreshStatus = async () => {
      try {
        const status = await window.automation.getTaskStatus()
        
        // 更新状态显示
        taskStatus.textContent = status.isRunning ? '运行中' : '停止'
        taskStatus.className = status.isRunning ? 'status-value running' : 'status-value stopped'
        
        browserStatus.textContent = status.browserStatus
        pageStatus.textContent = status.pageStatus
        
        // 更新按钮状态
        runBtn.disabled = status.isRunning
        stopBtn.disabled = !status.isRunning
        
        // 更新日志显示
        updateLogs(status.logs)
        
      } catch (error) {
        console.error('刷新状态失败:', error)
        addLog('error', '获取状态失败: ' + error.message)
      }
    }
    
    // 添加日志
    function addLog(level, message, taskId = 'system') {
      const now = new Date()
      const timeStr = \`\${now.getHours().toString().padStart(2, '0')}:\${now.getMinutes().toString().padStart(2, '0')}:\${now.getSeconds().toString().padStart(2, '0')}\`
      
      const logEntry = document.createElement('div')
      logEntry.className = \`log-entry \${level}\`
      
      logEntry.innerHTML = \`
        <span class="log-time">\${timeStr}</span>
        <span class="log-level \${level}">\${level.toUpperCase()}</span>
        <span class="log-message">\${message}</span>
      \`
      
      logContent.appendChild(logEntry)
      logContent.scrollTop = logContent.scrollHeight
    }
    
    // 更新日志显示
    function updateLogs(logs) {
      if (!logs || logs.length === 0) return
      
      // 只显示当前任务的日志或最近的系统日志
      const relevantLogs = logs.filter(log => !currentTaskId || log.taskId === currentTaskId || log.taskId === 'system')
      
      if (relevantLogs.length > 0) {
        // 清空现有日志（除了第一条系统消息）
        while (logContent.children.length > 1) {
          logContent.removeChild(logContent.lastChild)
        }
        
        // 添加新日志
        relevantLogs.forEach(log => {
          const time = new Date(log.timestamp)
          const timeStr = \`\${time.getHours().toString().padStart(2, '0')}:\${time.getMinutes().toString().padStart(2, '0')}:\${time.getSeconds().toString().padStart(2, '0')}\`
          
          const logEntry = document.createElement('div')
          logEntry.className = \`log-entry \${log.level}\`
          
          logEntry.innerHTML = \`
            <span class="log-time">\${timeStr}</span>
            <span class="log-level \${log.level}">\${log.level.toUpperCase()}</span>
            <span class="log-message">\${log.message}</span>
          \`
          
          logContent.appendChild(logEntry)
        })
        
        logContent.scrollTop = logContent.scrollHeight
      }
    }
    
    // 开始任务
    runBtn.addEventListener('click', async () => {
      const url = urlInput.value.trim()
      if (!url) {
        addLog('warning', '请输入目标URL')
        return
      }
      
      if (!url.startsWith('http://') && !url.startsWith('https://')) {
        addLog('warning', 'URL必须以 http:// 或 https:// 开头')
        return
      }
      
      const config = {
        url: url,
        headless: headlessSelect.value === 'true',
        screenshot: screenshotSelect.value === 'true'
      }
      
      addLog('info', '开始自动化任务...')
      currentTaskId = Date.now()
      
      try {
        runBtn.disabled = true
        stopBtn.disabled = false
        
        const result = await window.automation.runTask(config)
        
        if (result.success) {
          addLog('success', \`任务完成！访问了 \${result.url}\`)
          addLog('info', \`页面标题: \${result.title}\`)
          addLog('info', \`完成 \${result.tasksCompleted} 个点击任务\`)
        } else {
          addLog('error', \`任务失败: \${result.error}\`)
        }
        
        // 更新日志显示
        if (result.logs) {
          updateLogs(result.logs)
        }
        
      } catch (error) {
        addLog('error', \`执行任务出错: \${error.message}\`)
      } finally {
        setTimeout(() => refreshStatus(), 1000)
      }
    })
    
    // 停止任务
    stopBtn.addEventListener('click', async () => {
      addLog('info', '正在停止任务...')
      
      try {
        const result = await window.automation.stopTask()
        if (result.success) {
          addLog('success', result.message)
        } else {
          addLog('warning', result.message)
        }
      } catch (error) {
        addLog('error', \`停止任务失败: \${error.message}\`)
      } finally {
        setTimeout(() => refreshStatus(), 500)
      }
    })
    
    // 清除日志
    clearBtn.addEventListener('click', async () => {
      try {
        const result = await window.automation.clearLogs()
        if (result.success) {
          // 清空显示（保留第一条系统消息）
          while (logContent.children.length > 1) {
            logContent.removeChild(logContent.lastChild)
          }
          addLog('info', '日志已清除')
        }
      } catch (error) {
        addLog('error', \`清除日志失败: \${error.message}\`)
      }
    })
    
    // 切换日志显示
    toggleLogsBtn.addEventListener('click', () => {
      logsExpanded = !logsExpanded
      logContent.style.height = logsExpanded ? '300px' : '0'
      logContent.style.padding = logsExpanded ? '16px' : '0'
      toggleLogsBtn.textContent = logsExpanded ? '折叠' : '展开'
    })
    
    // 初始化
    document.addEventListener('DOMContentLoaded', () => {
      refreshStatus()
      
      // 定时刷新状态
      setInterval(refreshStatus, 5000)
    })
  </script>
</body>
</html>
`
}

// 创建应用菜单
function createMenu() {
  const template = [
    {
      label: '文件',
      submenu: [
        { 
          label: '打开RuoYi主窗口',
          click: () => {
            if (mainWindow) {
              mainWindow.show()
              mainWindow.focus()
            }
          }
        },
        { 
          label: '打开自动化控制台',
          accelerator: 'CmdOrCtrl+A',
          click: () => {
            if (!automationWindow) {
              createAutomationWindow()
            }
            automationWindow.show()
            automationWindow.focus()
          }
        },
        { type: 'separator' },
        { role: 'quit' }
      ]
    },
    {
      label: '自动化任务',
      submenu: [
        { 
          label: '快速运行示例任务',
          accelerator: 'CmdOrCtrl+R',
          click: async () => {
            try {
              const result = await runAutomationTask({
                url: 'https://example.com',
                headless: true,
                screenshot: true
              })
              
              if (result.success) {
                dialog.showMessageBox({
                  type: 'info',
                  title: '任务完成',
                  message: '自动化任务执行成功',
                  detail: `访问了: ${result.url}\n页面标题: ${result.title}`
                })
              } else {
                dialog.showErrorBox('任务失败', result.error || '未知错误')
              }
            } catch (error) {
              dialog.showErrorBox('任务执行错误', error.message)
            }
          }
        },
        { 
          label: '停止当前任务',
          accelerator: 'CmdOrCtrl+S',
          click: async () => {
            const result = await stopAutomationTask()
            if (result.success) {
              dialog.showMessageBox({
                type: 'info',
                title: '任务停止',
                message: result.message
              })
            }
          }
        }
      ]
    },
    {
      label: '视图',
      submenu: [
        { role: 'reload' },
        { role: 'forceReload' },
        { role: 'toggleDevTools' },
        { type: 'separator' },
        { role: 'resetZoom' },
        { role: 'zoomIn' },
        { role: 'zoomOut' },
        { type: 'separator' },
        { role: 'togglefullscreen' }
      ]
    },
    {
      label: '帮助',
      submenu: [
        {
          label: '关于',
          click: () => {
            dialog.showMessageBox({
              type: 'info',
              title: '关于',
              message: 'RuoYi管理系统 + 自动化任务',
              detail: '版本 2.0.0\n集成自动化浏览器控制功能'
            })
          }
        }
      ]
    }
  ]

  const menu = Menu.buildFromTemplate(template)
  Menu.setApplicationMenu(menu)
}

// 创建系统托盘
function createTray() {
  const iconPath = path.join(__dirname, '../build/icon.png')
  
  tray = new Tray(iconPath)
  const contextMenu = Menu.buildFromTemplate([
    {
      label: '打开RuoYi',
      click: () => {
        if (mainWindow) {
          mainWindow.show()
          mainWindow.focus()
        }
      }
    },
    {
      label: '打开自动化控制台',
      click: () => {
        if (!automationWindow) {
          createAutomationWindow()
        }
        automationWindow.show()
        automationWindow.focus()
      }
    },
    { type: 'separator' },
    {
      label: '退出',
      click: () => {
        app.quit()
      }
    }
  ])
  
  tray.setToolTip('RuoYi桌面版')
  tray.setContextMenu(contextMenu)
  
  tray.on('click', () => {
    if (mainWindow) {
      if (mainWindow.isVisible()) {
        mainWindow.hide()
      } else {
        mainWindow.show()
        mainWindow.focus()
      }
    }
  })
}

// 注册IPC处理器
function registerIpcHandlers() {
  // 自动化任务相关
  ipcMain.handle('run-automation-task', async (event, config) => {
    return await runAutomationTask(config)
  })
  
  ipcMain.handle('stop-automation-task', async () => {
    return await stopAutomationTask()
  })
  
  ipcMain.handle('get-automation-status', async () => {
    return getAutomationStatus()
  })
  
  ipcMain.handle('clear-automation-logs', async () => {
    return clearAutomationLogs()
  })
  
  // 窗口控制
  ipcMain.handle('open-automation-window', () => {
    if (!automationWindow) {
      createAutomationWindow()
    }
    automationWindow.show()
    automationWindow.focus()
    return { success: true }
  })

   // 新增：消息对话框
  ipcMain.handle('show-message-box', async (event, options) => {
    return await dialog.showMessageBox(mainWindow || automationWindow, options)
  })
  
  // 新增：文件对话框
  ipcMain.handle('dialog:openFile', async () => {
    const result = await dialog.showOpenDialog({
      properties: ['openFile']
    })
    return result.filePaths
  })
  
  ipcMain.handle('dialog:saveFile', async () => {
    const result = await dialog.showSaveDialog({
      properties: ['createDirectory', 'showOverwriteConfirmation']
    })
    return result.filePath
  })
  
  // 新增：系统通知
  ipcMain.on('notification', (event, title, body) => {
    if (Notification.isSupported()) {
      new Notification({ title, body }).show()
    }
  })
}

// Electron 准备就绪
app.whenReady().then(() => {
  console.log('=== RuoYi Electron 启动 ===')
  
  // 创建主窗口
  createMainWindow()
  
  // 注册IPC处理器
  registerIpcHandlers()
  
  // 创建菜单
  createMenu()
  
  // 创建系统托盘
  createTray()
  
  // 设置应用名称
  app.setName('RuoYi桌面版')
  
  // 应用激活事件
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createMainWindow()
    }
  })
})

// 所有窗口关闭时退出应用（macOS 除外）
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

// 应用退出前清理
app.on('before-quit', () => {
  console.log('应用退出，清理资源...')
  
  // 停止所有自动化任务
  if (automationManager && automationManager.isRunning) {
    automationManager.stopTask().catch(err => {
      console.error('停止自动化任务失败:', err)
    })
  }
  
  // 关闭所有浏览器实例
  if (automationManager && automationManager.browser) {
    automationManager.browser.close().catch(err => {
      console.error('关闭浏览器失败:', err)
    })
  }
})

// 导出函数（用于测试）
module.exports = { createMainWindow, createAutomationWindow }