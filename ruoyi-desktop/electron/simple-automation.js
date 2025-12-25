const { BrowserWindow, ipcMain, screen } = require('electron')
const path = require('path')
const fs = require('fs').promises
const os = require('os')

class SimpleAutomation {
  constructor() {
    this.automationWindow = null
    this.isRunning = false
    this.logs = []
    this.taskQueue = []
    this.currentTask = null
  }

  /**
   * 添加日志
   */
  addLog(taskId, level, message) {
    const logEntry = {
      timestamp: new Date().toISOString(),
      taskId,
      level, // info, success, warning, error
      message
    }
    
    this.logs.push(logEntry)
    console.log(`[${new Date().toLocaleTimeString()}] [${level.toUpperCase()}] ${message}`)
    
    // 发送日志到所有窗口
    BrowserWindow.getAllWindows().forEach(win => {
      if (!win.isDestroyed() && win.webContents) {
        win.webContents.send('automation-log', logEntry)
      }
    })
    
    return logEntry
  }

  /**
   * 开始自动化任务
   */
  async startTask(config) {
    if (this.isRunning) {
      throw new Error('已有任务正在运行，请先停止当前任务')
    }

    this.isRunning = true
    const taskId = Date.now()
    
    try {
      this.addLog(taskId, 'info', '开始自动化任务')
      
      // 验证配置
      if (!config.url) {
        throw new Error('请提供目标URL')
      }
      
      // 创建自动化窗口
      this.automationWindow = new BrowserWindow({
        width: 1280,
        height: 800,
        show: !config.headless, // 根据配置显示/隐藏窗口
        webPreferences: {
          nodeIntegration: false,
          contextIsolation: true,
          enableRemoteModule: false,
          preload: path.join(__dirname, 'preload-automation.js')
        }
      })
      
      // 设置窗口事件
      this.setupWindowEvents(taskId, config)
      
      // 加载目标页面
      this.addLog(taskId, 'info', `访问: ${config.url}`)
      await this.automationWindow.loadURL(config.url)
      
      // 返回任务ID，任务将异步执行
      return {
        success: true,
        taskId,
        message: '任务已开始执行'
      }
      
    } catch (error) {
      this.isRunning = false
      this.addLog(taskId, 'error', `任务启动失败: ${error.message}`)
      return {
        success: false,
        taskId,
        error: error.message
      }
    }
  }

  /**
   * 设置窗口事件
   */
  setupWindowEvents(taskId, config) {
    const win = this.automationWindow
    
    // 页面加载完成
    win.webContents.on('did-finish-load', async () => {
      this.addLog(taskId, 'success', '页面加载成功')
      
      // 延迟执行，确保页面完全渲染
      setTimeout(async () => {
        try {
          await this.executeAutomationTasks(taskId, config)
        } catch (error) {
          this.addLog(taskId, 'error', `任务执行失败: ${error.message}`)
        }
      }, 2000)
    })
    
    // 页面加载失败
    win.webContents.on('did-fail-load', (event, errorCode, errorDescription) => {
      this.addLog(taskId, 'error', `页面加载失败: ${errorCode} - ${errorDescription}`)
      this.stopTask()
    })
    
    // 页面标题更新
    win.webContents.on('page-title-updated', (event, title) => {
      this.addLog(taskId, 'info', `页面标题: ${title}`)
    })
    
    // 窗口关闭
    win.on('closed', () => {
      this.automationWindow = null
      this.isRunning = false
    })
  }

  /**
   * 执行自动化任务
   */
  async executeAutomationTasks(taskId, config) {
    try {
      // 获取配置的任务列表
      const tasks = config.tasks || this.getDefaultTasks()
      
      for (const [index, task] of tasks.entries()) {
        try {
          this.addLog(taskId, 'info', `执行任务 ${index + 1}: ${task.description}`)
          
          // 根据任务类型执行不同操作
          const result = await this.executeTask(task)
          
          if (result.success) {
            this.addLog(taskId, 'success', `${task.description} 完成`)
          } else {
            this.addLog(taskId, 'warning', `${task.description} 失败: ${result.error}`)
            
            // 如果任务必需，则停止执行
            if (task.required) {
              throw new Error(`必需任务失败: ${task.description}`)
            }
          }
          
          // 任务间等待
          if (task.delay) {
            await this.delay(task.delay)
          }
          
        } catch (error) {
          this.addLog(taskId, 'error', `任务执行异常: ${error.message}`)
          throw error
        }
      }
      
      // 截图
      if (config.screenshot) {
        await this.takeScreenshot(taskId)
      }
      
      // 获取页面信息
      const pageInfo = await this.getPageInfo()
      
      this.addLog(taskId, 'success', '自动化任务完成')
      
      // 发送完成通知
      this.sendTaskResult(taskId, {
        success: true,
        taskId,
        pageInfo,
        tasksCompleted: tasks.length,
        logs: this.getLogs(taskId)
      })
      
    } catch (error) {
      this.addLog(taskId, 'error', `自动化流程失败: ${error.message}`)
      
      // 发送失败通知
      this.sendTaskResult(taskId, {
        success: false,
        taskId,
        error: error.message,
        logs: this.getLogs(taskId)
      })
    } finally {
      // 任务完成后停止
      setTimeout(() => this.stopTask(), 3000)
    }
  }

  /**
   * 执行单个任务
   */
  async executeTask(task) {
    const script = this.getTaskScript(task)
    return await this.automationWindow.webContents.executeJavaScript(script, true)
  }

  /**
   * 获取任务脚本
   */
  getTaskScript(task) {
    const selector = task.selector ? task.selector.replace(/'/g, "\\'") : ''
    
    switch(task.action || 'click') {
      case 'click':
        return `
          (function() {
            try {
              const element = document.querySelector('${selector}')
              if (!element) {
                return { success: false, error: '元素未找到: ${selector}' }
              }
              
              element.click()
              console.log('点击了:', '${selector}')
              
              // 高亮显示点击的元素
              const originalStyle = element.style.outline
              element.style.outline = '3px solid #1890ff'
              setTimeout(() => {
                element.style.outline = originalStyle
              }, 1000)
              
              return { 
                success: true,
                element: {
                  tagName: element.tagName,
                  id: element.id,
                  className: element.className,
                  text: element.textContent?.trim() || element.value || ''
                }
              }
            } catch (error) {
              return { success: false, error: error.message }
            }
          })()
        `
        
      case 'type':
        return `
          (function() {
            try {
              const element = document.querySelector('${selector}')
              if (!element) {
                return { success: false, error: '元素未找到: ${selector}' }
              }
              
              element.value = '${task.value || ''}'
              element.dispatchEvent(new Event('input', { bubbles: true }))
              element.dispatchEvent(new Event('change', { bubbles: true }))
              
              console.log('输入了:', '${task.value}', '到', '${selector}')
              
              // 高亮显示
              const originalStyle = element.style.outline
              element.style.outline = '3px solid #52c41a'
              setTimeout(() => {
                element.style.outline = originalStyle
              }, 1000)
              
              return { 
                success: true,
                element: {
                  tagName: element.tagName,
                  id: element.id,
                  className: element.className,
                  value: element.value
                }
              }
            } catch (error) {
              return { success: false, error: error.message }
            }
          })()
        `
        
      case 'wait':
        return `
          (function() {
            return new Promise(resolve => {
              setTimeout(() => {
                resolve({ success: true, message: '等待完成' })
              }, ${task.delay || 1000})
            })
          })()
        `
        
      case 'scroll':
        return `
          (function() {
            try {
              window.scrollTo(${task.x || 0}, ${task.y || 0})
              return { success: true, message: '滚动完成' }
            } catch (error) {
              return { success: false, error: error.message }
            }
          })()
        `
        
      default:
        return `
          (function() {
            return { success: false, error: '未知的操作类型: ${task.action}' }
          })()
        `
    }
  }

  /**
   * 获取默认任务列表
   */
  getDefaultTasks() {
    return [
      {
        selector: 'a',
        description: '点击第一个链接',
        action: 'click',
        required: false,
        delay: 1000
      },
      {
        selector: 'button[type="submit"]',
        description: '点击提交按钮',
        action: 'click',
        required: false,
        delay: 1000
      },
      {
        selector: 'input[type="text"]',
        description: '填写输入框',
        action: 'type',
        value: '自动化测试',
        required: false,
        delay: 500
      }
    ]
  }

  /**
   * 截图
   */
  async takeScreenshot(taskId) {
    try {
      const screenshotDir = path.join(__dirname, '../screenshots')
      await fs.mkdir(screenshotDir, { recursive: true })
      
      const screenshotPath = path.join(screenshotDir, `screenshot-${taskId}.png`)
      
      // 获取窗口大小
      const size = this.automationWindow.getSize()
      
      // 截图
      const image = await this.automationWindow.capturePage({
        x: 0,
        y: 0,
        width: size[0],
        height: size[1]
      })
      
      // 保存为PNG
      await fs.writeFile(screenshotPath, image.toPNG())
      
      this.addLog(taskId, 'info', `截图已保存: ${screenshotPath}`)
      
      return screenshotPath
    } catch (error) {
      this.addLog(taskId, 'warning', `截图失败: ${error.message}`)
      return null
    }
  }

  /**
   * 获取页面信息
   */
  async getPageInfo() {
    try {
      return await this.automationWindow.webContents.executeJavaScript(`
        (function() {
          return {
            title: document.title,
            url: window.location.href,
            elements: {
              buttons: document.querySelectorAll('button').length,
              inputs: document.querySelectorAll('input, textarea, select').length,
              links: document.querySelectorAll('a').length,
              images: document.querySelectorAll('img').length
            },
            cookies: document.cookie ? document.cookie.split(';').length : 0,
            timestamp: new Date().toISOString()
          }
        })()
      `, true)
    } catch (error) {
      return {
        title: '获取失败',
        url: '获取失败',
        elements: { buttons: 0, inputs: 0, links: 0, images: 0 },
        cookies: 0,
        timestamp: new Date().toISOString()
      }
    }
  }

  /**
   * 发送任务结果
   */
  sendTaskResult(taskId, result) {
    BrowserWindow.getAllWindows().forEach(win => {
      if (!win.isDestroyed() && win.webContents) {
        win.webContents.send('automation-result', result)
      }
    })
  }

  /**
   * 停止任务
   */
  stopTask() {
    if (this.automationWindow) {
      this.automationWindow.close()
      this.automationWindow = null
    }
    
    this.isRunning = false
    this.addLog('system', 'info', '任务已停止')
    
    return { success: true, message: '任务已停止' }
  }

  /**
   * 获取任务状态
   */
  getTaskStatus() {
    return {
      isRunning: this.isRunning,
      browserStatus: this.automationWindow ? '运行中' : '未启动',
      logs: this.logs.slice(-50)
    }
  }

  /**
   * 获取指定任务的日志
   */
  getLogs(taskId) {
    return this.logs.filter(log => log.taskId === taskId)
  }

  /**
   * 清除日志
   */
  clearLogs() {
    this.logs = []
    return { success: true, message: '日志已清除' }
  }

  /**
   * 延迟函数
   */
  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms))
  }
}

module.exports = SimpleAutomation