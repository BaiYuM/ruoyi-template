// electron/automation.js
const puppeteer = require('puppeteer')
const path = require('path')
const fs = require('fs').promises

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
      headless: true, // 无头模式，设置为false可以看到浏览器窗口
      args: [
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--disable-dev-shm-usage',
        '--disable-web-security',
        '--disable-features=IsolateOrigins,site-per-process'
      ],
      defaultViewport: { width: 1280, height: 720 },
      ignoreHTTPSErrors: true
    }

    try {
      console.log('正在启动浏览器...')
      this.browser = await puppeteer.launch({ ...defaultOptions, ...options })
      console.log('浏览器启动成功')
      return this.browser
    } catch (error) {
      console.error('启动浏览器失败:', error)
      throw error
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
      
      // 启动浏览器
      await this.launchBrowser({ headless: config.headless !== false })
      
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

      // 执行点击任务
      const tasks = [
        { selector: 'a', description: '点击第一个链接', required: false },
        { selector: '#myButton', description: '点击ID为myButton的按钮', required: false },
        { selector: '.submit-button', description: '点击提交按钮', required: false },
        { selector: 'a[href="/about"]', description: '点击关于链接', required: false }
      ]

      for (const [index, task] of tasks.entries()) {
        try {
          this.addLog(taskId, 'info', `执行任务 ${index + 1}: ${task.description}`)
          
          await this.page.waitForSelector(task.selector, { timeout: 5000 })
          await this.page.click(task.selector)
          
          this.addLog(taskId, 'success', `${task.description} 完成`)
          
          // 等待页面响应
          await this.page.waitForTimeout(2000)
          
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