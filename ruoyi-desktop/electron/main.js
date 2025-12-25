const { app, BrowserWindow, Menu, ipcMain } = require('electron')
const path = require('path')

// 导入自动化服务
const { 
  runAutomationTask, 
  stopAutomationTask, 
  getAutomationStatus,
  clearAutomationLogs 
} = require('./automation-service')

// 全局变量
let mainWindow
let automationWindow = null

// 检查开发环境
const isDev = process.env.NODE_ENV === 'development' || 
              process.env.ELECTRON === 'true' ||
              process.argv.some(arg => arg.includes('electron'))

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    show: true,
    icon: path.join(__dirname, '../build/icon.png'),
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      enableRemoteModule: false,
      preload: path.join(__dirname, 'preload.js')
    }
  })

  // 加载页面
  if (isDev) {
    // 开发环境：加载 Vue 开发服务器
    mainWindow.loadURL('http://localhost:80')
    mainWindow.webContents.openDevTools()
  } else {
    // 生产环境：加载打包文件
    mainWindow.loadFile(path.join(__dirname, '../../ruoyi-ui/dist/index.html'))
  }

  // 页面加载完成
  mainWindow.webContents.on('did-finish-load', () => {
    console.log('主窗口加载完成')
  })

  // 窗口关闭事件
  mainWindow.on('closed', () => {
    mainWindow = null
  })

  // 创建菜单
  createMenu()
}

// 创建应用菜单
function createMenu() {
  const template = [
    {
      label: '文件',
      submenu: [
        {
          label: '打开自动化控制台',
          click: () => {
            openAutomationPanel()
          }
        },
        { type: 'separator' },
        {
          label: '退出',
          accelerator: 'CmdOrCtrl+Q',
          role: 'quit'
        }
      ]
    },
    {
      label: '自动化',
      submenu: [
        {
          label: '开始测试任务',
          click: () => {
            startTestTask()
          }
        },
        {
          label: '停止所有任务',
          click: () => {
            stopAllTasks()
          }
        },
        { type: 'separator' },
        {
          label: '查看任务日志',
          click: () => {
            showAutomationLogs()
          }
        }
      ]
    },
    {
      label: '开发',
      submenu: [
        {
          label: '开发者工具',
          accelerator: 'F12',
          click: () => {
            if (mainWindow) {
              mainWindow.webContents.openDevTools()
            }
          }
        },
        {
          label: '重新加载',
          accelerator: 'CmdOrCtrl+R',
          role: 'reload'
        }
      ]
    }
  ]

  const menu = Menu.buildFromTemplate(template)
  Menu.setApplicationMenu(menu)
}

// 打开自动化控制面板
function openAutomationPanel() {
  if (automationWindow) {
    automationWindow.focus()
    return
  }

  automationWindow = new BrowserWindow({
    width: 800,
    height: 600,
    parent: mainWindow,
    modal: true,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload-automation.js')
    }
  })

  automationWindow.loadFile(path.join(__dirname, 'automation-panel.html'))

  automationWindow.on('closed', () => {
    automationWindow = null
  })
}

// 启动测试任务
async function startTestTask() {
  const result = await runAutomationTask({
    url: 'http://localhost:80',
    headless: false,
    screenshot: true,
    tasks: [
      {
        selector: 'input[type="text"]:first-of-type',
        description: '填写第一个输入框',
        action: 'type',
        value: '自动化测试数据',
        required: false,
        delay: 500
      },
      {
        selector: 'button[type="submit"]',
        description: '点击提交按钮',
        action: 'click',
        required: false,
        delay: 1000
      }
    ]
  })

  console.log('测试任务结果:', result)
}

// 停止所有任务
async function stopAllTasks() {
  const result = await stopAutomationTask()
  console.log('停止任务结果:', result)
}

// 显示自动化日志
function showAutomationLogs() {
  const status = getAutomationStatus()
  console.log('当前任务状态:', status)
}

// 注册 IPC 处理器
function registerIPCHandlers() {
  // 自动化相关
  ipcMain.handle('start-automation', async (event, config) => {
    return await runAutomationTask(config)
  })

  ipcMain.handle('stop-automation', async () => {
    return await stopAutomationTask()
  })

  ipcMain.handle('get-automation-status', () => {
    return getAutomationStatus()
  })

  ipcMain.handle('clear-automation-logs', () => {
    return clearAutomationLogs()
  })

  // 页面操作相关
  ipcMain.handle('execute-click', async (event, selector) => {
    const win = BrowserWindow.getFocusedWindow() || mainWindow
    if (!win) {
      return { success: false, error: '没有活动窗口' }
    }

    try {
      const result = await win.webContents.executeJavaScript(`
        (function() {
          try {
            const element = document.querySelector('${selector.replace(/'/g, "\\'")}')
            if (!element) {
              return { success: false, error: '元素未找到' }
            }
            
            element.click()
            return { 
              success: true, 
              element: {
                tagName: element.tagName,
                text: element.textContent?.trim() || ''
              }
            }
          } catch (error) {
            return { success: false, error: error.message }
          }
        })()
      `, true)

      return result
    } catch (error) {
      return { success: false, error: error.message }
    }
  })

  ipcMain.handle('execute-script', async (event, script) => {
    const win = BrowserWindow.getFocusedWindow() || mainWindow
    if (!win) {
      return { success: false, error: '没有活动窗口' }
    }

    try {
      const result = await win.webContents.executeJavaScript(script, true)
      return { success: true, result }
    } catch (error) {
      return { success: false, error: error.message }
    }
  })

  ipcMain.handle('get-page-info', async (event) => {
    const win = BrowserWindow.getFocusedWindow() || mainWindow
    if (!win) {
      return { success: false, error: '没有活动窗口' }
    }

    try {
      const info = await win.webContents.executeJavaScript(`
        (function() {
          return {
            title: document.title,
            url: window.location.href,
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent
          }
        })()
      `, true)

      return { success: true, ...info }
    } catch (error) {
      return { success: false, error: error.message }
    }
  })
}

// 应用准备就绪
app.whenReady().then(() => {
  // 注册 IPC 处理器
  registerIPCHandlers()
  
  // 创建主窗口
  createWindow()

  // macOS 特殊处理
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

// 所有窗口关闭
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

// 应用退出前
app.on('before-quit', async () => {
  // 停止所有自动化任务
  await stopAutomationTask()
})