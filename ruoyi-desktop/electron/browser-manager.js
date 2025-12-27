const puppeteer = require('puppeteer-core');
const EventEmitter = require('events');
const Automation = require('./automation');
const ConnectionManager = require('./connection-manager');

class AutomationService {
  constructor(taskManager) {
    this.automations = new Map();
    this.connectionManager = new ConnectionManager();
    this.taskManager = taskManager;
  }

  /**
   * 连接到已打开的浏览器
   */
  async connectToExistingBrowser(options = {}) {
    try {
      const result = await this.connectionManager.connectToBrowser(options);
      
      if (result.success) {
        const automation = new Automation({
          browserWSEndpoint: result.connection.endpoint,
          remoteDebugging: true,
          headless: false
        });
        
        automation.browser = result.browser;
        automation.page = (await result.browser.pages())[0];
        
        const automationId = `auto_${Date.now()}`;
        this.automations.set(automationId, automation);
        
        // 监听事件
        automation.on('status', (message) => {
          console.log(`[Automation ${automationId}]: ${message}`);
        });
        
        return { success: true, automationId, connectionId: result.connectionId };
      } else {
        return { success: false, error: result.error };
      }
    } catch (error) {
      return { success: false, error: error.message };
    }
  }

  /**
   * 扫描可用的浏览器
   */
  async scanBrowsers() {
    return await this.connectionManager.scanForBrowsers();
  }

  /**
   * 启动带调试的 Chrome
   */
  async launchDebugChrome(options = {}) {
    return await this.connectionManager.launchChromeWithDebugging(options);
  }

  /**
   * 执行抖音自动化（连接现有浏览器）
   */
  async executeDouyinWithExistingBrowser(options = {}) {
    try {
      // 1. 扫描或连接浏览器
      let connectionResult;
      if (options.browserWSEndpoint) {
        connectionResult = await this.connectToExistingBrowser({
          webSocketDebuggerUrl: options.browserWSEndpoint
        });
      } else if (options.port) {
        connectionResult = await this.connectToExistingBrowser({
          port: options.port
        });
      } else {
        // 自动扫描
        const browsers = await this.scanBrowsers();
        if (browsers.length === 0) {
          // 启动新浏览器
          await this.launchDebugChrome({
            url: 'https://www.douyin.com',
            chromePath: 'D:\\Google\\Chrome\\Application\\chrome.exe'
          });
          // 等待并重试
          await new Promise(resolve => setTimeout(resolve, 3000));
          connectionResult = await this.connectToExistingBrowser({ port: 9222 });
        } else {
          connectionResult = await this.connectToExistingBrowser({
            webSocketDebuggerUrl: browsers[0].webSocketDebuggerUrl
          });
        }
      }
      
      if (!connectionResult.success) {
        throw new Error(connectionResult.error);
      }
      
      const automation = this.automations.get(connectionResult.automationId);
      
      // 2. 执行抖音操作
      const douyinResult = await automation.executeDouyinTask({
        actions: ['scroll', 'like', 'comment'],
        scrollCount: 3,
        likeCount: 2,
        commentText: '自动化测试'
      });
      
      return {
        success: true,
        automationId: connectionResult.automationId,
        ...douyinResult
      };
    } catch (error) {
      return {
        success: false,
        error: error.message
      };
    }
  }

}

module.exports = AutomationService;

class BrowserManager extends EventEmitter {
  constructor() {
    super();
    this.browsers = new Map();
    this.browserCounter = 0;
  }

  /**
   * 创建浏览器实例
   * @param {Object} options - 浏览器选项
   * @returns {Promise<string>} 浏览器ID
   */
  async createBrowser(options = {}) {
    const browserId = `browser_${++this.browserCounter}`;
    
    try {
      const defaultOptions = {
        headless: false,
        executablePath: this.findChromePath(),
        args: [
          '--no-sandbox',
          '--disable-setuid-sandbox',
          '--disable-dev-shm-usage',
          '--window-size=1280,800'
        ],
        ...options
      };

      const browser = await puppeteer.launch(defaultOptions);
      const pages = await browser.pages();
      const page = pages[0] || await browser.newPage();

      this.browsers.set(browserId, {
        browser,
        page,
        createdAt: new Date(),
        options: defaultOptions,
        stats: {
          pages: 1,
          memoryUsage: null
        }
      });

      this.emit('browser:created', { id: browserId, options: defaultOptions });
      
      return browserId;
    } catch (error) {
      this.emit('error', { id: browserId, error: error.message });
      throw error;
    }
  }

  /**
   * 查找Chrome路径
   * @returns {string|null}
   */
  findChromePath() {
    const paths = {
      win32: [
        'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
        'C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe'
      ],
      darwin: [
        '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome'
      ],
      linux: [
        '/usr/bin/google-chrome',
        '/usr/bin/chromium-browser'
      ]
    };

    const platformPaths = paths[process.platform] || [];
    for (const chromePath of platformPaths) {
      if (require('fs').existsSync(chromePath)) {
        return chromePath;
      }
    }
    return null;
  }

  /**
   * 获取浏览器页面
   * @param {string} browserId - 浏览器ID
   * @returns {Promise<Page>}
   */
  async getPage(browserId) {
    const browserInfo = this.browsers.get(browserId);
    if (!browserInfo) {
      throw new Error(`Browser not found: ${browserId}`);
    }
    return browserInfo.page;
  }

  /**
   * 导航到URL
   * @param {string} browserId - 浏览器ID
   * @param {string} url - 目标URL
   * @param {Object} options - 导航选项
   */
  async navigate(browserId, url, options = {}) {
    const page = await this.getPage(browserId);
    await page.goto(url, {
      waitUntil: 'networkidle0',
      timeout: 30000,
      ...options
    });
    
    this.emit('browser:navigated', { id: browserId, url });
  }

  /**
   * 在浏览器中执行脚本
   * @param {string} browserId - 浏览器ID
   * @param {string} script - JavaScript脚本
   * @param {Array} args - 脚本参数
   * @returns {Promise<any>}
   */
  async executeScript(browserId, script, args = []) {
    const page = await this.getPage(browserId);
    return await page.evaluate(script, ...args);
  }

  /**
   * 截图
   * @param {string} browserId - 浏览器ID
   * @param {Object} options - 截图选项
   * @returns {Promise<Buffer>}
   */
  async screenshot(browserId, options = {}) {
    const page = await this.getPage(browserId);
    return await page.screenshot(options);
  }

  /**
   * 关闭浏览器
   * @param {string} browserId - 浏览器ID
   * @returns {Promise<boolean>}
   */
  async closeBrowser(browserId) {
    const browserInfo = this.browsers.get(browserId);
    if (browserInfo) {
      await browserInfo.browser.close();
      this.browsers.delete(browserId);
      this.emit('browser:closed', { id: browserId });
      return true;
    }
    return false;
  }

  /**
   * 获取浏览器信息
   * @param {string} browserId - 浏览器ID
   * @returns {Object|null}
   */
  getBrowserInfo(browserId) {
    const info = this.browsers.get(browserId);
    if (!info) return null;

    return {
      id: browserId,
      createdAt: info.createdAt,
      options: info.options,
      stats: info.stats,
      isConnected: info.browser.isConnected()
    };
  }

  /**
   * 获取所有浏览器
   * @returns {Array}
   */
  getBrowsers() {
    return Array.from(this.browsers.keys()).map(id => this.getBrowserInfo(id));
  }

  /**
   * 关闭所有浏览器
   */
  async closeAll() {
    const closePromises = Array.from(this.browsers.keys()).map(id => this.closeBrowser(id));
    await Promise.all(closePromises);
  }

  /**
   * 更新浏览器统计信息
   */
  async updateStats() {
    for (const [id, info] of this.browsers) {
      try {
        const pages = await info.browser.pages();
        info.stats.pages = pages.length;
        
        // 可以添加更多统计信息，如内存使用等
        this.emit('browser:stats', { id, stats: info.stats });
      } catch (error) {
        console.error(`Failed to update stats for browser ${id}:`, error);
      }
    }
  }
}

module.exports = BrowserManager;