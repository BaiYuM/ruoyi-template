const Automation = require('./automation');
const ConnectionManager = require('./connection-manager');

class AutomationService {
  constructor(taskManager) {
    this.automations = new Map();
    this.connectionManager = new ConnectionManager();
    this.taskManager = taskManager;
    
    // 监听连接管理器事件
    this.connectionManager.on('browser:found', (data) => {
      console.log(`[Browser Found] 端口 ${data.port}, ${data.pageCount} 页面`);
    });
    
    this.connectionManager.on('browser:connected', (data) => {
      console.log(`[Browser Connected] ${data.connectionId}, 端口 ${data.port}`);
    });
  }

  /**
   * 扫描可用的浏览器
   */
   async scanBrowsers() {
    console.log('[AutomationService] 开始扫描浏览器...');
    try {
      const browsers = await this.connectionManager.scanForBrowsers();
      console.log(`[AutomationService] 扫描完成，找到 ${browsers.length} 个浏览器`);
      return browsers;
    } catch (error) {
      console.error('[AutomationService] 扫描浏览器失败:', error);
      return [];
    }
  }
  /**
   * 连接到已打开的浏览器
   */
  async connectToExistingBrowser(options = {}) {
    try {
      const result = await this.connectionManager.connectToBrowser(options);
      
      if (result.success) {
        const automation = new Automation({
          browserWSEndpoint: result.browserWSEndpoint,
          useExisting: true,
          headless: false
        });
        
        automation.browser = result.browser;
        automation.page = (await result.browser.pages())[0];
        
        const automationId = `auto_${Date.now()}`;
        this.automations.set(automationId, {
          automation,
          connectionId: result.connectionId,
          browserWSEndpoint: result.browserWSEndpoint,
          connectedAt: new Date()
        });
        
        // 监听事件
        automation.on('status', (message) => {
          console.log(`[Automation ${automationId}]: ${message}`);
        });
        
        automation.on('error', (error) => {
          console.error(`[Automation ${automationId} Error]:`, error);
        });
        
        return { 
          success: true, 
          automationId, 
          connectionId: result.connectionId 
        };
      } else {
        return { success: false, error: result.error };
      }
    } catch (error) {
      return { success: false, error: error.message };
    }
  }

  /**
   * 启动带调试的 Chrome
   */
  async launchDebugChrome(options = {}) {
    try {
      return await this.connectionManager.launchChromeWithDebugging(options);
    } catch (error) {
      return { success: false, error: error.message };
    }
  }

  /**
   * 执行自动化脚本
   */
  async execute(script) {
    const automationId = `auto_${Date.now()}`;
    
    try {
      const automation = new Automation({
        headless: false,
        chromePath: 'D:\\Google\\Chrome\\Application\\chrome.exe'
      });

      this.automations.set(automationId, {
        automation,
        connectedAt: new Date()
      });

      // 监听自动化事件
      automation.on('status', (message) => {
        console.log(`[Automation ${automationId}]: ${message}`);
      });

      automation.on('error', (error) => {
        console.error(`[Automation ${automationId} Error]:`, error);
      });

      automation.on('step:start', (data) => {
        console.log(`[Step ${data.index + 1} Start]:`, data.step.action);
      });

      automation.on('step:complete', (data) => {
        console.log(`[Step ${data.index + 1} Complete]:`, data.step.action);
      });

      // 执行自动化
      await this.executeScript(automation, script);
      
      return { success: true, automationId };
    } catch (error) {
      console.error(`Automation ${automationId} failed:`, error);
      this.automations.delete(automationId);
      return { success: false, error: error.message };
    }
  }

  /**
   * 执行脚本
   */
  async executeScript(automation, script) {
    let steps = [];
    
    // 解析脚本
    if (typeof script === 'string') {
      try {
        steps = JSON.parse(script);
      } catch {
        // 如果是JavaScript代码
        await automation.initialize();
        await automation.executeScript(script);
        return;
      }
    } else if (Array.isArray(script)) {
      steps = script;
    } else if (typeof script === 'object' && script.steps) {
      steps = script.steps;
    }

    // 执行步骤
    await automation.executeFlow(steps);
  }

  /**
   * 执行抖音自动化（使用现有浏览器）
   */
  async executeDouyinWithExistingBrowser(options = {}) {
    try {
      // 先扫描浏览器
      const browsers = await this.scanBrowsers();
      
      let connectionResult;
      
      if (browsers.length === 0) {
        // 启动新浏览器
        const launchResult = await this.launchDebugChrome({
          chromePath: 'D:\\Google\\Chrome\\Application\\chrome.exe',
          url: 'https://www.douyin.com',
          port: 9222
        });
        
        if (!launchResult.success) {
          throw new Error(`启动浏览器失败: ${launchResult.error}`);
        }
        
        // 等待浏览器启动
        await new Promise(resolve => setTimeout(resolve, 5000));
        
        // 重新扫描
        const newBrowsers = await this.scanBrowsers();
        if (newBrowsers.length === 0) {
          throw new Error('浏览器启动后未能检测到调试端口');
        }
        
        // 连接到第一个浏览器
        connectionResult = await this.connectToExistingBrowser({
          webSocketDebuggerUrl: newBrowsers[0].webSocketDebuggerUrl
        });
      } else {
        // 连接到第一个已存在的浏览器
        connectionResult = await this.connectToExistingBrowser({
          webSocketDebuggerUrl: browsers[0].webSocketDebuggerUrl
        });
      }
      
      if (!connectionResult.success) {
        throw new Error(connectionResult.error);
      }
      
      const automationInfo = this.automations.get(connectionResult.automationId);
      const automation = automationInfo.automation;
      
      // 执行抖音操作
      const douyinResult = await automation.douyinAutomation({
        scrollCount: options.scrollCount || 5,
        likeProbability: options.likeProbability || 0.3,
        commentProbability: options.commentProbability || 0.2,
        commentTexts: options.commentTexts || ['赞！', '不错', '学到了', '哈哈', '👍']
      });
      
      return {
        success: true,
        automationId: connectionResult.automationId,
        stats: douyinResult
      };
    } catch (error) {
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * 停止自动化
   */
  async stop(automationId) {
    const automationInfo = this.automations.get(automationId);
    if (automationInfo) {
      await automationInfo.automation.close();
      this.automations.delete(automationId);
      return true;
    }
    return false;
  }

  /**
   * 获取所有自动化实例
   */
  getAllAutomations() {
    return Array.from(this.automations.entries()).map(([id, info]) => ({
      id,
      status: info.automation.page ? 'running' : 'stopped',
      connectedAt: info.connectedAt
    }));
  }

  /**
   * 获取浏览器列表
   */
  getBrowsers() {
    return this.connectionManager.getConnectedBrowsers();
  }

  /**
   * 创建浏览器实例
   */
  async createBrowser(options = {}) {
    return await this.connectionManager.launchChromeWithDebugging(options);
  }

  /**
   * 关闭浏览器实例
   */
  async closeBrowser(connectionId) {
    return await this.connectionManager.disconnectBrowser(connectionId);
  }
}

module.exports = AutomationService;