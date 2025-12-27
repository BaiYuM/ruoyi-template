// electron/main.js
const { app, BrowserWindow, Menu, ipcMain, dialog } = require('electron')
const path = require('path')
const http = require('http')

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

// HTTP服务器端口
const HTTP_PORT = process.env.DESKTOP_HTTP_PORT || 9876

// 启动HTTP服务器
function startHttpServer() {
  const server = http.createServer((req, res) => {
    // 设置CORS头
    res.setHeader('Access-Control-Allow-Origin', '*')
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type')

    // 处理预检请求
    if (req.method === 'OPTIONS') {
      res.writeHead(200)
      res.end()
      return
    }

    // 解析请求路径
    const url = new URL(req.url, `http://${req.headers.host}`)
    const pathname = url.pathname

    if (req.method === 'POST' && pathname === '/api/automation/start') {
      // 接收POST数据
      let body = ''
      req.on('data', chunk => {
        body += chunk.toString()
      })
      req.on('end', async () => {
        try {
          const config = JSON.parse(body)
          const result = await runAutomationTask(config)
          
          res.writeHead(200, { 'Content-Type': 'application/json' })
          res.end(JSON.stringify({
            success: true,
            message: '自动化任务已启动',
            data: result
          }))
        } catch (error) {
          res.writeHead(500, { 'Content-Type': 'application/json' })
          res.end(JSON.stringify({
            success: false,
            message: error.message
          }))
        }
      })
    } else if (req.method === 'POST' && pathname === '/api/automation/stop') {
      // 停止自动化任务
      try {
        const result = await stopAutomationTask()
        
        res.writeHead(200, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({
          success: true,
          message: '自动化任务已停止',
          data: result
        }))
      } catch (error) {
        res.writeHead(500, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({
          success: false,
          message: error.message
        }))
      }
    } else if (req.method === 'GET' && pathname === '/api/automation/status') {
      // 获取自动化状态
      try {
        const status = getAutomationStatus()
        
        res.writeHead(200, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({
          success: true,
          data: status
        }))
      } catch (error) {
        res.writeHead(500, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({
          success: false,
          message: error.message
        }))
      }
    } else if (req.method === 'POST' && pathname === '/api/automation/clear-logs') {
      // 清除日志
      try {
        const result = clearAutomationLogs()
        
        res.writeHead(200, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({
          success: true,
          message: '日志已清除',
          data: result
        }))
      } catch (error) {
        res.writeHead(500, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({
          success: false,
          message: error.message
        }))
      }
    } else {
      // 404
      res.writeHead(404, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({
        success: false,
        message: 'API端点不存在'
      }))
    }
  })

  server.listen(HTTP_PORT, () => {
    console.log(`自动化HTTP服务器运行在端口 ${HTTP_PORT}`)
  })

  return server
}

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