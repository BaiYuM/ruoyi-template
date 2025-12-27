// electron/automation.js
const { BrowserWindow, ipcMain, dialog, session } = require('electron')
const path = require('path')
const fs = require('fs').promises

// 抖音检测模块
const DOUYIN_DETECTORS = {
  isDouyinPage: (url) => {
    return url.includes('douyin.com') || url.includes('iesdouyin.com')
  },
  
  getDouyinSelectors: () => {
    return {
      // 发布按钮
      publishButton: [
        '.publish-btn',
        '[data-e2e="publish"]',
        '.upload-btn',
        'div[aria-label="发布"]'
      ],
      
      // 输入框
      inputFields: [
        'textarea',
        '[placeholder*="描述"]',
        '[placeholder*="说点什么"]',
        '[contenteditable="true"]'
      ],
      
      // 私信按钮
      messageButton: [
        '[data-e2e="message"]',
        '.message-btn',
        'a[href*="message"]'
      ],
      
      // 点赞按钮
      likeButton: [
        '[data-e2e="like"]',
        '.like-btn',
        'svg[aria-label*="喜欢"]'
      ],
      
      // 关注按钮
      followButton: [
        '[data-e2e="follow"]',
        '.follow-btn',
        'button:contains("关注")'
      ]
    }
  }
}

class AutomationManager {
  constructor() {
    this.automationWindow = null
    this.isRunning = false
    this.logs = []
    this.currentTask = null
  }

  /**
   * 启动自动化窗口（使用Electron内置浏览器）
   */
  async launchAutomationWindow(options = {}) {
    if (this.automationWindow) {
      this.automationWindow.focus()
      return this.automationWindow
    }

    try {
      // 创建BrowserWindow（Electron内置浏览器）
      this.automationWindow = new BrowserWindow({
        width: options.width || 1280,
        height: options.height || 720,
        show: options.show !== false,
        webPreferences: {
          nodeIntegration: false,
          contextIsolation: true,
          webSecurity: false,
          allowRunningInsecureContent: true,
          experimentalFeatures: true
        },
        backgroundColor: '#ffffff',
        title: '自动化浏览器',
        icon: path.join(__dirname, '../build/icon.png')
      })

      // 设置User-Agent
      if (options.userAgent) {
        this.automationWindow.webContents.setUserAgent(options.userAgent)
      }

      // 监听窗口关闭
      this.automationWindow.on('closed', () => {
        this.automationWindow = null
        this.isRunning = false
        this.addLog('system', 'info', '自动化窗口已关闭')
      })

      // 监听页面事件
      this.setupWindowListeners()

      this.addLog('system', 'success', '自动化窗口创建成功')
      return this.automationWindow

    } catch (error) {
      this.addLog('system', 'error', `创建自动化窗口失败: ${error.message}`)
      throw error
    }
  }

  /**
   * 执行通用自动化任务
   */
  async runAutomationTask(config = {}) {
    if (this.isRunning) {
      throw new Error('已有任务正在运行')
    }

    this.isRunning = true
    const taskId = Date.now()
    this.currentTask = { id: taskId, config: config }

    try {
      this.addLog(taskId, 'info', '开始自动化任务')

      // 1. 创建或获取自动化窗口
      if (!this.automationWindow) {
        await this.launchAutomationWindow({
          show: config.headless !== true, // headless为false时显示窗口
          width: config.width || 1280,
          height: config.height || 720,
          userAgent: config.userAgent || 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        })
      }

      // 2. 加载目标URL
      const url = config.url || 'https://example.com'
      this.addLog(taskId, 'info', `访问: ${url}`)
      
      await this.automationWindow.loadURL(url, {
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
      })

      // 等待页面加载
      await this.waitForPageLoad()
      this.addLog(taskId, 'success', '页面加载成功')

      // 3. 执行任务
      const tasks = config.tasks || [
        { selector: 'a:first-of-type', description: '点击第一个链接', action: 'click' },
        { selector: 'button[type="submit"]', description: '点击提交按钮', action: 'click' },
        { selector: 'input[type="text"]:first-of-type', description: '填写输入框', action: 'type', value: '测试数据' }
      ]

      let completedTasks = 0
      for (const [index, task] of tasks.entries()) {
        try {
          this.addLog(taskId, 'info', `执行任务 ${index + 1}: ${task.description}`)
          
          if (task.action === 'click') {
            await this.executeClick(task.selector, task.timeout || 5000)
          } else if (task.action === 'type' && task.value) {
            await this.executeType(task.selector, task.value, task.timeout || 5000)
          } else if (task.action === 'wait' && task.delay) {
            await this.wait(task.delay)
          }
          
          this.addLog(taskId, 'success', `${task.description} 完成`)
          completedTasks++
          
          // 等待一下
          await this.wait(task.delay || 1000)
          
        } catch (error) {
          if (task.required) {
            throw new Error(`${task.description} 失败: ${error.message}`)
          } else {
            this.addLog(taskId, 'warning', `${task.description} 跳过: ${error.message}`)
          }
        }
      }

      // 4. 截图保存（可选）
      if (config.screenshot) {
        await this.captureScreenshot(taskId)
      }

      // 5. 获取页面信息
      const pageInfo = await this.getPageInfo()

      this.addLog(taskId, 'success', '自动化任务完成')

      return {
        success: true,
        taskId: taskId,
        url: pageInfo.url,
        title: pageInfo.title,
        tasksCompleted: completedTasks,
        totalTasks: tasks.length,
        logs: this.getLogs(taskId)
      }

    } catch (error) {
      this.addLog(taskId, 'error', `任务失败: ${error.message}`)
      
      // 错误截图
      await this.captureScreenshot(`${taskId}_error`)
      
      return {
        success: false,
        taskId: taskId,
        error: error.message,
        logs: this.getLogs(taskId)
      }
    } finally {
      this.isRunning = false
      this.currentTask = null
    }
  }

  /**
   * 执行抖音自动化任务
   */
  async runDouyinTask(taskType, config = {}) {
    if (this.isRunning) {
      throw new Error('已有任务正在运行')
    }

    this.isRunning = true
    const taskId = `douyin_${Date.now()}`
    this.currentTask = { id: taskId, type: taskType, config: config }

    try {
      this.addLog(taskId, 'info', `开始抖音任务: ${taskType}`)

      // 1. 创建抖音专用窗口（模拟手机端）
      if (!this.automationWindow) {
        await this.launchAutomationWindow({
          show: config.headless !== true,
          width: 375,  // 手机宽度
          height: 667, // 手机高度
          userAgent: 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1'
        })
      }

      // 2. 加载抖音页面
      const url = config.url || 'https://www.douyin.com'
      this.addLog(taskId, 'info', `访问抖音: ${url}`)
      
      await this.automationWindow.loadURL(url)

      // 等待页面加载
      await this.waitForPageLoad()
      this.addLog(taskId, 'success', '抖音页面加载成功')

      // 3. 执行抖音任务
      const result = await this.executeDouyinAction(taskType, config)

      // 4. 截图保存
      if (config.screenshot !== false) {
        await this.captureScreenshot(`douyin_${taskId}`)
      }

      this.addLog(taskId, 'success', '抖音任务完成')

      return {
        success: true,
        taskId: taskId,
        type: 'douyin',
        action: taskType,
        result: result,
        logs: this.getLogs(taskId)
      }

    } catch (error) {
      this.addLog(taskId, 'error', `抖音任务失败: ${error.message}`)
      
      // 错误截图
      await this.captureScreenshot(`douyin_error_${taskId}`)
      
      return {
        success: false,
        taskId: taskId,
        type: 'douyin',
        action: taskType,
        error: error.message,
        logs: this.getLogs(taskId)
      }
    } finally {
      this.isRunning = false
      this.currentTask = null
    }
  }

  /**
   * 执行抖音特定操作
   */
  async executeDouyinAction(action, params = {}) {
    const scriptMap = {
      // 点赞视频
      'like_video': `
        try {
          // 查找点赞按钮
          const likeSelectors = [
            '[data-e2e="like"]',
            '.like-btn',
            'svg[aria-label*="喜欢"]',
            'div[aria-label*="点赞"]'
          ]
          
          for (const selector of likeSelectors) {
            const element = document.querySelector(selector)
            if (element) {
              element.click()
              return { success: true, message: '点赞成功' }
            }
          }
          
          return { success: false, error: '未找到点赞按钮' }
        } catch (error) {
          return { success: false, error: error.message }
        }
      `,

      // 关注用户
      'follow_user': `
        try {
          const followSelectors = [
            '[data-e2e="follow"]',
            '.follow-btn',
            'button:contains("关注")'
          ]
          
          for (const selector of followSelectors) {
            const element = document.querySelector(selector)
            if (element) {
              const text = element.textContent || element.innerText
              if (text.includes('关注') && !text.includes('已关注')) {
                element.click()
                return { success: true, message: '关注成功' }
              }
            }
          }
          
          return { success: false, error: '未找到关注按钮或已关注' }
        } catch (error) {
          return { success: false, error: error.message }
        }
      `,

      // 发送私信
      'send_message': `
        try {
          // 找到私信按钮
          const messageBtn = document.querySelector('[data-e2e="message"], .message-btn')
          if (messageBtn) {
            messageBtn.click()
            await new Promise(resolve => setTimeout(resolve, 1000))
            
            // 如果有用户名，搜索用户
            ${params.username ? `
              const searchInput = document.querySelector('input[placeholder*="搜索"]')
              if (searchInput) {
                searchInput.value = "${params.username}"
                searchInput.dispatchEvent(new Event('input', { bubbles: true }))
                await new Promise(resolve => setTimeout(resolve, 1000))
                
                // 点击第一个搜索结果
                const firstResult = document.querySelector('.search-result-item:first-child')
                if (firstResult) {
                  firstResult.click()
                  await new Promise(resolve => setTimeout(resolve, 1000))
                }
              }
            ` : ''}
            
            // 输入消息
            const messageInput = document.querySelector('textarea, [contenteditable="true"]')
            if (messageInput && "${params.message}") {
              if (messageInput.contentEditable === 'true') {
                messageInput.innerHTML = "${params.message}"
              } else {
                messageInput.value = "${params.message}"
              }
              messageInput.dispatchEvent(new Event('input', { bubbles: true }))
            }
            
            // 发送消息
            const sendBtn = document.querySelector('.send-btn, [data-e2e="send"]')
            if (sendBtn) {
              sendBtn.click()
              return { success: true, message: '私信发送成功' }
            }
          }
          
          return { success: false, error: '未找到私信相关元素' }
        } catch (error) {
          return { success: false, error: error.message }
        }
      `,

      // 发布视频
      'publish_video': `
        try {
          // 找到发布按钮
          const publishBtn = document.querySelector('[data-e2e="publish"], .publish-btn')
          if (publishBtn) {
            publishBtn.click()
            await new Promise(resolve => setTimeout(resolve, 2000))
            
            // 这里需要文件上传，但Electron中需要特殊处理
            // 实际使用时需要通过对话框选择文件
            
            // 填写描述
            ${params.description ? `
              const descInput = document.querySelector('textarea, [placeholder*="描述"]')
              if (descInput) {
                descInput.value = "${params.description}"
                descInput.dispatchEvent(new Event('input', { bubbles: true }))
              }
            ` : ''}
            
            // 发布
            const submitBtn = document.querySelector('[data-e2e="submit"], .submit-btn')
            if (submitBtn) {
              submitBtn.click()
              return { success: true, message: '发布流程已启动' }
            }
          }
          
          return { success: false, error: '未找到发布相关元素' }
        } catch (error) {
          return { success: false, error: error.message }
        }
      `
    }

    const script = scriptMap[action]
    if (!script) {
      throw new Error(`不支持的抖音操作: ${action}`)
    }

    // 在页面中执行脚本
    const result = await this.automationWindow.webContents.executeJavaScript(`(async () => { ${script} })()`)
    return result
  }

  /**
   * 执行点击操作
   */
  async executeClick(selector, timeout = 5000) {
    return await this.automationWindow.webContents.executeJavaScript(`
      (function() {
        return new Promise((resolve, reject) => {
          const element = document.querySelector('${selector.replace(/'/g, "\\'")}')
          if (!element) {
            reject(new Error('元素未找到: ${selector}'))
            return
          }
          
          // 获取元素位置
          const rect = element.getBoundingClientRect()
          const x = rect.left + rect.width / 2
          const y = rect.top + rect.height / 2
          
          // 创建并触发点击事件
          const mouseEvents = [
            new MouseEvent('mousedown', { bubbles: true, clientX: x, clientY: y }),
            new MouseEvent('mouseup', { bubbles: true, clientX: x, clientY: y }),
            new MouseEvent('click', { bubbles: true, clientX: x, clientY: y })
          ]
          
          mouseEvents.forEach(event => element.dispatchEvent(event))
          
          // 如果是链接，模拟跳转
          if (element.tagName === 'A' && element.href) {
            setTimeout(() => {
              window.location.href = element.href
            }, 100)
          }
          
          resolve({
            success: true,
            selector: '${selector}',
            tagName: element.tagName,
            text: element.textContent?.trim() || ''
          })
        })
      })()
    `)
  }

  /**
   * 执行输入操作
   */
  async executeType(selector, text, timeout = 5000) {
    return await this.automationWindow.webContents.executeJavaScript(`
      (function() {
        return new Promise((resolve, reject) => {
          const element = document.querySelector('${selector.replace(/'/g, "\\'")}')
          if (!element) {
            reject(new Error('元素未找到: ${selector}'))
            return
          }
          
          // 聚焦元素
          element.focus()
          
          // 清除原有内容
          if (element.value !== undefined) {
            element.value = ''
          } else if (element.textContent !== undefined) {
            element.textContent = ''
          } else if (element.innerHTML !== undefined) {
            element.innerHTML = ''
          }
          
          // 设置新内容
          if (element.value !== undefined) {
            element.value = '${text.replace(/'/g, "\\'")}'
          } else if (element.contentEditable === 'true') {
            element.innerHTML = '${text.replace(/'/g, "\\'")}'
          } else {
            element.textContent = '${text.replace(/'/g, "\\'")}'
          }
          
          // 触发输入事件
          element.dispatchEvent(new Event('input', { bubbles: true }))
          element.dispatchEvent(new Event('change', { bubbles: true }))
          
          resolve({
            success: true,
            selector: '${selector}',
            text: '${text}'
          })
        })
      })()
    `)
  }

  /**
   * 等待页面加载
   */
  async waitForPageLoad() {
    return new Promise((resolve) => {
      const timeout = setTimeout(() => {
        resolve()
      }, 10000)

      this.automationWindow.webContents.once('did-finish-load', () => {
        clearTimeout(timeout)
        setTimeout(() => {
          resolve()
        }, 1000)
      })
    })
  }

  /**
   * 等待指定时间
   */
  async wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms))
  }

  /**
   * 截图
   */
  async captureScreenshot(prefix = 'screenshot') {
    if (!this.automationWindow) return null

    const screenshotDir = path.join(__dirname, '../screenshots')
    await fs.mkdir(screenshotDir, { recursive: true })
    
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-')
    const screenshotPath = path.join(screenshotDir, `${prefix}_${timestamp}.png`)

    const image = await this.automationWindow.webContents.capturePage()
    await fs.writeFile(screenshotPath, image.toPNG())
    
    this.addLog('system', 'info', `截图已保存: ${screenshotPath}`)
    return screenshotPath
  }

  /**
   * 获取页面信息
   */
  async getPageInfo() {
    if (!this.automationWindow) return null

    return await this.automationWindow.webContents.executeJavaScript(`
      (function() {
        return {
          url: window.location.href,
          title: document.title,
          domain: window.location.hostname,
          isDouyin: window.location.hostname.includes('douyin.com'),
          timestamp: new Date().toISOString()
        }
      })()
    `)
  }

  /**
   * 停止任务
   */
  async stopTask() {
    if (this.automationWindow) {
      this.automationWindow.close()
      this.automationWindow = null
    }
    
    this.isRunning = false
    this.currentTask = null
    
    this.addLog('system', 'success', '任务已停止')
    return { success: true, message: '任务已停止' }
  }

  /**
   * 设置窗口监听器
   */
  setupWindowListeners() {
    // 监听页面控制台输出
    this.automationWindow.webContents.on('console-message', (event, level, message, line, sourceId) => {
      const levels = ['', 'INFO', 'WARNING', 'ERROR']
      this.addLog('page-console', 'info', `页面控制台: ${levels[level]} ${message}`)
    })

    // 监听页面标题变化
    this.automationWindow.on('page-title-updated', (event, title) => {
      this.addLog('page', 'info', `页面标题: ${title}`)
    })

    // 监听页面URL变化
    this.automationWindow.webContents.on('did-navigate', (event, url) => {
      this.addLog('page', 'info', `页面跳转: ${url}`)
    })
  }

  /**
   * 获取任务状态
   */
  getTaskStatus() {
    return {
      isRunning: this.isRunning,
      windowOpen: !!this.automationWindow,
      currentTask: this.currentTask,
      logs: this.logs.slice(-50)
    }
  }

  /**
   * 添加日志
   */
  addLog(taskId, level, message) {
    const logEntry = {
      timestamp: new Date().toISOString(),
      taskId: taskId || 'system',
      level: level,
      message: message
    }
    
    this.logs.push(logEntry)
    console.log(`[${logEntry.timestamp}] ${level.toUpperCase()} - ${message}`)
    
    // 限制日志数量
    if (this.logs.length > 1000) {
      this.logs = this.logs.slice(-500)
    }
    
    return logEntry
  }

  /**
   * 获取日志
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
}

// 创建单例实例
const automationManager = new AutomationManager()

// 导出函数接口
async function runAutomationTask(config = {}) {
  return await automationManager.runAutomationTask(config)
}

async function runDouyinTask(taskType, taskConfig = {}, url = 'https://www.douyin.com') {
  return await automationManager.runDouyinTask(taskType, { ...taskConfig, url })
}

async function runUniversalTask(config = {}) {
  // 自动检测是否为抖音页面
  const isDouyin = DOUYIN_DETECTORS.isDouyinPage(config.url)
  
  if (isDouyin && config.douyinTask) {
    return await automationManager.runDouyinTask(
      config.douyinTask,
      config.douyinConfig || {},
      config.url
    )
  } else {
    return await automationManager.runAutomationTask(config)
  }
}

async function stopAutomationTask() {
  return await automationManager.stopTask()
}

function getAutomationStatus() {
  return automationManager.getTaskStatus()
}

function clearAutomationLogs() {
  return automationManager.clearLogs()
}

// 导出模块
module.exports = {
  AutomationManager,
  automationManager,
  runAutomationTask,
  runDouyinTask,
  runUniversalTask,
  stopAutomationTask,
  getAutomationStatus,
  clearAutomationLogs
}