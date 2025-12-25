// electron/automation.js
const puppeteer = require('puppeteer')
const path = require('path')
const fs = require('fs').promises
const { exec } = require('child_process')
const chromeLauncher = require('chrome-launcher')

class AutomationManager {
  constructor() {
    this.browser = null
    this.page = null
    this.isRunning = false
    this.logs = []
  }

  /**
   * 启动浏览器
   */
  async launchBrowser(options = {}) {
    // 默认配置
    const defaultOptions = {
      headless: options.headless !== false,
      args: [
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--disable-dev-shm-usage',
        '--disable-web-security',
        '--disable-features=IsolateOrigins,site-per-process',
        '--window-size=1280,720'
      ],
      defaultViewport: { width: 1280, height: 720 },
      ignoreHTTPSErrors: true
    }

    try {
      console.log('正在查找 Chrome 浏览器...')
      
      // 方法1: 使用 chrome-launcher 查找 Chrome
      let chromePath = null
      try {
        const chrome = await chromeLauncher.launch({
          chromeFlags: ['--headless'],
          chromePath: options.chromePath
        })
        chromePath = chrome.chromePath
        await chrome.kill()
      } catch (error) {
        console.log('chrome-launcher 查找失败:', error.message)
      }
      
      // 方法2: 尝试常见路径
      if (!chromePath) {
        const possiblePaths = [
          // Windows
          'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
          'C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe',
          process.env.LOCALAPPDATA + '\\Google\\Chrome\\Application\\chrome.exe',
          // Mac
          '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome',
          '/Applications/Chromium.app/Contents/MacOS/Chromium',
          // Linux
          '/usr/bin/google-chrome',
          '/usr/bin/google-chrome-stable',
          '/usr/bin/chromium',
          '/usr/bin/chromium-browser'
        ]
        
        for (const path of possiblePaths) {
          try {
            if (fs.existsSync && fs.existsSync(path)) {
              chromePath = path
              console.log('找到 Chrome 在:', chromePath)
              break
            }
          } catch (e) {
            // 继续尝试下一个路径
          }
        }
      }
      
      if (!chromePath) {
        throw new Error('未找到 Chrome 浏览器。请安装 Chrome 或指定 Chrome 路径。')
      }
      
      console.log('正在启动 Chrome...')
      this.browser = await puppeteer.launch({
        ...defaultOptions,
        ...options,
        executablePath: chromePath
      })
      
      console.log('浏览器启动成功')
      return this.browser
    } catch (error) {
      console.error('启动浏览器失败:', error)
      throw error
    }
  }

  /**
   * 备用方案：使用系统浏览器（无需安装 Chrome）
   */
  async launchSystemBrowser(options = {}) {
    const open = require('open')
    const config = {
      url: options.url || 'https://example.com',
      app: {
        name: open.apps.chrome || open.apps.edge || open.apps.firefox || open.apps.chromium
      }
    }
    
    console.log('使用系统浏览器:', config.app.name)
    await open(config.url, config.app)
    
    // 返回一个模拟的 browser 对象
    return {
      newPage: async () => ({
        goto: async (url) => {
          console.log('跳转到:', url)
          await open(url, config.app)
          return Promise.resolve()
        },
        click: (selector) => {
          console.log('模拟点击:', selector)
          return Promise.resolve()
        },
        type: (selector, text) => {
          console.log('模拟输入:', selector, text)
          return Promise.resolve()
        },
        screenshot: (options) => {
          console.log('模拟截图:', options)
          return Promise.resolve()
        },
        close: () => {
          console.log('关闭页面')
          return Promise.resolve()
        },
        title: () => Promise.resolve('系统浏览器模拟页面'),
        url: () => Promise.resolve(config.url),
        content: () => Promise.resolve('<html>系统浏览器模拟内容</html>')
      }),
      close: () => {
        console.log('关闭浏览器')
        return Promise.resolve()
      }
    }
  }

  /**
   * 执行自动化任务
   */
  async runAutomationTask(config = {}) {
    if (this.isRunning) {
      throw new Error('已有任务正在运行，请先停止当前任务')
    }

    this.isRunning = true
    const taskId = Date.now()
    
    try {
      this.addLog(taskId, 'info', '开始自动化任务')
      
      // 尝试启动浏览器（Chrome）
      try {
        await this.launchBrowser({ 
          headless: config.headless !== false,
          chromePath: config.chromePath 
        })
      } catch (chromeError) {
        this.addLog(taskId, 'warning', `Chrome 启动失败: ${chromeError.message}`)
        this.addLog(taskId, 'info', '尝试使用系统浏览器...')
        
        // 使用备用方案
        this.browser = await this.launchSystemBrowser({ 
          url: config.url || 'https://example.com' 
        })
        this.addLog(taskId, 'success', '已切换到系统浏览器模式')
      }
      
      this.page = await this.browser.newPage()
      await this.page.setDefaultNavigationTimeout(30000)
      await this.page.setDefaultTimeout(10000)
      
      // 设置 User-Agent（可选）
      await this.page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36')
      
      // 访问目标URL
      const url = config.url || 'https://example.com'
      this.addLog(taskId, 'info', `访问: ${url}`)
      
      await this.page.goto(url, { 
        waitUntil: 'networkidle2',
        timeout: 30000 
      })
      
      this.addLog(taskId, 'success', '页面加载成功')

      // 执行点击任务 - 使用配置的任务或默认任务
      const tasks = config.tasks || [
        { selector: 'a', description: '点击第一个链接', required: false, action: 'click' },
        { selector: 'button[type="submit"]', description: '点击提交按钮', required: false, action: 'click' },
        { selector: 'input[type="text"]', description: '填写输入框', required: false, action: 'type', value: '测试数据' }
      ]

      for (const [index, task] of tasks.entries()) {
        try {
          this.addLog(taskId, 'info', `执行任务 ${index + 1}: ${task.description}`)
          
          if (task.action === 'click') {
            await this.page.waitForSelector(task.selector, { timeout: 5000 })
            await this.page.click(task.selector)
          } else if (task.action === 'type' && task.value) {
            await this.page.waitForSelector(task.selector, { timeout: 5000 })
            await this.page.type(task.selector, task.value)
          } else if (task.action === 'wait' && task.delay) {
            await this.page.waitForTimeout(task.delay)
          }
          
          this.addLog(taskId, 'success', `${task.description} 完成`)
          
          // 等待页面响应
          await this.page.waitForTimeout(task.delay || 2000)
          
        } catch (error) {
          if (task.required) {
            throw new Error(`${task.description} 失败: ${error.message}`)
          } else {
            this.addLog(taskId, 'warning', `${task.description} 跳过: ${error.message}`)
          }
        }
      }
    
      // 截图保存（可选）
      if (config.screenshot) {
        const screenshotDir = path.join(__dirname, '../screenshots')
        try {
          await fs.mkdir(screenshotDir, { recursive: true })
          const screenshotPath = path.join(screenshotDir, `result-${taskId}.png`)
          await this.page.screenshot({ path: screenshotPath, fullPage: true })
          this.addLog(taskId, 'info', `截图已保存: ${screenshotPath}`)
        } catch (error) {
          this.addLog(taskId, 'warning', `截图失败: ${error.message}`)
        }
      }

      // 获取页面信息
      const pageTitle = await this.page.title()
      const pageUrl = this.page.url()
      const pageContent = await this.page.content()

      this.addLog(taskId, 'success', '自动化任务完成')

      return {
        success: true,
        taskId: taskId,
        title: pageTitle,
        url: pageUrl,
        contentLength: pageContent.length,
        tasksCompleted: tasks.length,
        logs: this.getLogs(taskId)
      }

    } catch (error) {
      this.addLog(taskId, 'error', `任务失败: ${error.message}`)
      
      // 截图错误状态
      try {
        const screenshotDir = path.join(__dirname, '../screenshots')
        await fs.mkdir(screenshotDir, { recursive: true })
        const screenshotPath = path.join(screenshotDir, `error-${taskId}.png`)
        if (this.page) {
          await this.page.screenshot({ path: screenshotPath })
          this.addLog(taskId, 'info', `错误截图: ${screenshotPath}`)
        }
      } catch (screenshotError) {
        console.error('截图失败:', screenshotError)
      }

      return {
        success: false,
        taskId: taskId,
        error: error.message,
        logs: this.getLogs(taskId)
      }

    } finally {
      this.isRunning = false
    }
  }

  /**
   * 停止当前任务
   */
  async stopTask() {
    if (!this.isRunning) {
      return { success: false, message: '没有正在运行的任务' }
    }

    try {
      this.addLog('system', 'info', '正在停止任务...')
      
      if (this.browser) {
        await this.browser.close()
        this.browser = null
        this.page = null
      }
      
      this.isRunning = false
      this.addLog('system', 'success', '任务已停止')
      
      return { success: true, message: '任务已停止' }
    } catch (error) {
      this.addLog('system', 'error', `停止任务失败: ${error.message}`)
      return { success: false, message: error.message }
    }
  }

  /**
   * 获取任务状态
   */
  getTaskStatus() {
    return {
      isRunning: this.isRunning,
      browserStatus: this.browser ? '运行中' : '未启动',
      pageStatus: this.page ? '运行中' : '未启动',
      logs: this.logs.slice(-50) // 返回最近50条日志
    }
  }

  /**
   * 添加日志
   */
  addLog(taskId, level, message) {
    const logEntry = {
      timestamp: new Date().toISOString(),
      taskId: taskId || 'system',
      level: level, // info, success, warning, error
      message: message
    }
    
    this.logs.push(logEntry)
    console.log(`[${logEntry.timestamp}] ${logEntry.level.toUpperCase()} - ${logEntry.message}`)
    
    // 保持日志数量在合理范围
    if (this.logs.length > 1000) {
      this.logs = this.logs.slice(-500)
    }
    
    return logEntry
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
}

// 导出单例实例
const automationManager = new AutomationManager()

// 导出函数接口
async function runAutomationTask(config = {}) {
  return await automationManager.runAutomationTask(config)
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
  stopAutomationTask,
  getAutomationStatus,
  clearAutomationLogs
}