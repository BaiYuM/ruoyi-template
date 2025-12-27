// electron/main.js
const { app, BrowserWindow, Menu, ipcMain, dialog } = require('electron')
const path = require('path')

// 正确导入自动化模块
const automationModule = require('./automation')

// 解构导入，确保名称正确
const { 
  automationManager,
  runAutomationTask, 
  stopAutomationTask, 
  getAutomationStatus,
  clearAutomationLogs 
} = automationModule

console.log('自动化模块导入状态:', {
  hasRunAutomationTask: typeof runAutomationTask === 'function',
  hasStopAutomationTask: typeof stopAutomationTask === 'function',
  hasGetAutomationStatus: typeof getAutomationStatus === 'function',
  hasClearAutomationLogs: typeof clearAutomationLogs === 'function'
})

// 全局变量
let mainWindow

// 检查开发环境
const isDev = process.env.NODE_ENV === 'development'

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    show: true,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js')
    }
  })

  // 加载自动化控制面板
  const panelPath = path.join(__dirname, 'automation-panel.html')
  console.log('加载面板路径:', panelPath)
  mainWindow.loadFile(panelPath)

  // 页面加载完成
  mainWindow.webContents.on('did-finish-load', () => {
    console.log('自动化控制面板加载完成')
    // 发送初始化消息
    mainWindow.webContents.send('automation-ready')
  })

  // 打开开发者工具
  mainWindow.webContents.openDevTools()

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
          label: '重新加载',
          accelerator: 'CmdOrCtrl+R',
          click: () => {
            if (mainWindow) {
              mainWindow.reload()
            }
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
          label: '运行测试任务',
          click: async () => {
            try {
              console.log('开始测试任务...')
              const result = await runAutomationTask({
                url: 'http://localhost:80',
                headless: false,
                screenshot: true,
                tasks: [
                  {
                    selector: 'input[type="text"]:first-of-type',
                    description: '测试输入',
                    action: 'type',
                    value: '自动化测试',
                    required: false,
                    delay: 500
                  }
                ]
              })
              console.log('测试任务结果:', result)
              
              // 发送结果到页面
              if (mainWindow) {
                mainWindow.webContents.send('automation-result', result)
              }
            } catch (error) {
              console.error('测试任务失败:', error)
            }
          }
        },
        {
          label: '停止所有任务',
          click: async () => {
            console.log('停止所有任务...')
            const result = await stopAutomationTask()
            console.log('停止任务结果:', result)
          }
        },
        {
          label: '查看状态',
          click: () => {
            const status = getAutomationStatus()
            console.log('当前状态:', status)
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
          label: '检查模块',
          click: () => {
            console.log('检查自动化模块:', automationModule)
            console.log('runAutomationTask 类型:', typeof runAutomationTask)
          }
        }
      ]
    }
  ]

  const menu = Menu.buildFromTemplate(template)
  Menu.setApplicationMenu(menu)
}

// 注册 IPC 处理器
function registerIPCHandlers() {
  console.log('注册 IPC 处理器...')
  
  // 自动化任务相关
  ipcMain.handle('run-automation-task', async (event, config) => {
    console.log('收到自动化任务请求:', config)
    
    try {
      // 检查函数是否存在
      if (typeof runAutomationTask !== 'function') {
        throw new Error('runAutomationTask 函数不存在')
      }
      
      const result = await runAutomationTask(config)
      console.log('任务执行结果:', result)
      
      // 发送日志到页面
      if (mainWindow && result.taskId) {
        const logs = automationManager.getLogs(result.taskId)
        logs.forEach(log => {
          mainWindow.webContents.send('automation-log', log)
        })
      }
      
      return result
    } catch (error) {
      console.error('自动化任务执行失败:', error)
      return {
        success: false,
        error: error.message,
        taskId: Date.now(),
        message: `执行失败: ${error.message}`
      }
    }
  })

  ipcMain.handle('stop-automation-task', async () => {
    console.log('收到停止任务请求')
    try {
      const result = await stopAutomationTask()
      return result
    } catch (error) {
      console.error('停止任务失败:', error)
      return {
        success: false,
        message: error.message
      }
    }
  })

  ipcMain.handle('get-automation-status', () => {
    const status = getAutomationStatus()
    console.log('获取状态:', status)
    return status
  })

  ipcMain.handle('clear-automation-logs', () => {
    console.log('清除日志')
    return clearAutomationLogs()
  })

  // 文件操作相关
  ipcMain.handle('dialog:openFile', async (event, options = {}) => {
    const win = BrowserWindow.getFocusedWindow() || mainWindow
    const result = await dialog.showOpenDialog(win, {
      title: options.title || '选择文件',
      filters: options.filters || [{ name: '所有文件', extensions: ['*'] }],
      properties: options.properties || ['openFile']
    })
    return result
  })

  ipcMain.handle('get-system-info', () => {
    return {
      platform: process.platform,
      arch: process.arch,
      nodeVersion: process.version,
      electronVersion: process.versions.electron,
      memoryUsage: process.memoryUsage()
    }
  })
}

// 应用准备就绪
app.whenReady().then(() => {
  console.log('应用准备就绪，开始初始化...')
  
  // 注册 IPC 处理器
  registerIPCHandlers()
  
  // 创建主窗口
  createWindow()
  
  // 监听窗口激活
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

// 窗口关闭
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

// 退出前清理
app.on('before-quit', async () => {
  console.log('应用退出，停止所有任务...')
  await stopAutomationTask()
})