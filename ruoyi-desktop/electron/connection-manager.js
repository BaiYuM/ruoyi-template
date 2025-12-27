const axios = require('axios');
const EventEmitter = require('events');
const puppeteer = require('puppeteer-core');
const net = require('net');

class ConnectionManager extends EventEmitter {
  constructor() {
    super();
    this.connectedBrowsers = new Map();
    this.defaultPorts = [9222, 9223, 9224, 9225, 9226];
  }

  /**
   * 检查端口是否开放 - 修复：强制使用 IPv4
   */
  async checkPort(port, timeout = 2000) {
    return new Promise((resolve) => {
      const socket = new net.Socket();
      socket.setTimeout(timeout);
      
      socket.on('connect', () => {
        socket.destroy();
        resolve(true);
      });
      
      socket.on('timeout', () => {
        socket.destroy();
        resolve(false);
      });
      
      socket.on('error', () => {
        resolve(false);
      });
      
      // 修复：强制使用 IPv4 地址 127.0.0.1
      socket.connect(port, '127.0.0.1');
    });
  }

  /**
   * 扫描可用的浏览器连接 - 修复：所有 localhost 改为 127.0.0.1
   */
  async scanForBrowsers() {
    const results = [];
    
    console.log('开始扫描浏览器端口...');
    
    for (const port of this.defaultPorts) {
      try {
        console.log(`  检查端口 ${port}...`);
        
        // 先检查端口是否开放
        const isPortOpen = await this.checkPort(port, 1000);
        
        if (!isPortOpen) {
          console.log(`    端口 ${port} 未开放`);
          continue;
        }
        
        console.log(`    端口 ${port} 已开放，尝试连接调试接口...`);
        
        // 修复：将 localhost 改为 127.0.0.1
        const response = await axios.get(`http://127.0.0.1:${port}/json/version`, {
          timeout: 3000,
          headers: {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
          }
        });
        
        if (response.data) {
          // 获取所有页面
          const pagesResponse = await axios.get(`http://127.0.0.1:${port}/json/list`, {
            timeout: 3000
          });
          const pages = pagesResponse.data;
          
          const browserInfo = {
            port,
            browser: response.data.Browser || 'Chrome/Unknown',
            protocolVersion: response.data['Protocol-Version'],
            userAgent: response.data['User-Agent'],
            webSocketDebuggerUrl: response.data.webSocketDebuggerUrl,
            pages: pages.map(page => ({
              id: page.id,
              title: page.title || '无标题',
              url: page.url || 'about:blank',
              type: page.type || 'page',
              webSocketDebuggerUrl: page.webSocketDebuggerUrl
            }))
          };
          
          // 修复：将 WebSocket URL 中的 localhost 改为 127.0.0.1
          if (browserInfo.webSocketDebuggerUrl && browserInfo.webSocketDebuggerUrl.includes('localhost')) {
            browserInfo.webSocketDebuggerUrl = browserInfo.webSocketDebuggerUrl.replace('localhost', '127.0.0.1');
          }
          
          console.log(`    ✓ 找到浏览器: ${browserInfo.browser}, ${pages.length}个页面`);
          results.push(browserInfo);
          
          this.emit('browser:found', {
            port,
            pageCount: pages.length
          });
        }
      } catch (error) {
        // 只输出调试信息，不抛出错误
        console.log(`    ✗ 端口 ${port} 连接失败: ${error.code || error.message}`);
        continue;
      }
    }
    
    console.log(`扫描完成，找到 ${results.length} 个浏览器`);
    return results;
  }

  /**
   * 获取浏览器信息 - 修复：localhost 改为 127.0.0.1
   */
  async getBrowserInfo(port = 9222) {
    try {
      const response = await axios.get(`http://127.0.0.1:${port}/json/version`, {
        timeout: 5000
      });
      const pagesResponse = await axios.get(`http://127.0.0.1:${port}/json/list`, {
        timeout: 5000
      });
      
      return {
        success: true,
        data: {
          browser: response.data.Browser,
          protocolVersion: response.data['Protocol-Version'],
          userAgent: response.data['User-Agent'],
          webSocketDebuggerUrl: response.data.webSocketDebuggerUrl,
          pages: pagesResponse.data
        }
      };
    } catch (error) {
      console.error(`获取浏览器信息失败 (端口 ${port}):`, error.message);
      return {
        success: false,
        error: error.message,
        code: error.code
      };
    }
  }

  /**
   * 连接到浏览器 - 修复：转换 WebSocket URL 中的 localhost
   */
  async connectToBrowser(options = {}) {
    try {
      console.log('连接到浏览器...', options);
      
      let browserWSEndpoint = options.webSocketDebuggerUrl;
      let port = options.port;
      
      // 如果没有提供端点，通过端口获取
      if (!browserWSEndpoint && port) {
        const info = await this.getBrowserInfo(port);
        if (info.success) {
          browserWSEndpoint = info.data.webSocketDebuggerUrl;
        } else {
          throw new Error(`无法从端口 ${port} 获取浏览器信息: ${info.error}`);
        }
      }
      
      // 如果还是没有端点，自动扫描
      if (!browserWSEndpoint) {
        const browsers = await this.scanForBrowsers();
        if (browsers.length === 0) {
          throw new Error('未找到可连接的浏览器');
        }
        browserWSEndpoint = browsers[0].webSocketDebuggerUrl;
        port = browsers[0].port;
      }
      
      // 修复：确保 WebSocket URL 使用 127.0.0.1 而不是 localhost
      if (browserWSEndpoint && browserWSEndpoint.includes('localhost')) {
        browserWSEndpoint = browserWSEndpoint.replace('localhost', '127.0.0.1');
      }
      
      console.log(`连接 WebSocket: ${browserWSEndpoint}`);
      
      // 连接到浏览器
      const browser = await puppeteer.connect({
        browserWSEndpoint,
        defaultViewport: null
      });
      
      const connectionId = `conn_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
      this.connectedBrowsers.set(connectionId, {
        browser,
        browserWSEndpoint,
        port,
        connectedAt: new Date(),
        pages: []
      });
      
      // 获取页面信息
      const pages = await browser.pages();
      const connection = this.connectedBrowsers.get(connectionId);
      connection.pages = pages.map((page, index) => ({
        index,
        page,
        url: page.url(),
        title: page.title()
      }));
      
      console.log(`连接成功: ${connectionId}, ${pages.length}个页面`);
      this.emit('browser:connected', { 
        connectionId,
        port,
        pageCount: pages.length 
      });
      
      return {
        success: true,
        connectionId,
        browserWSEndpoint,
        browser,
        port
      };
    } catch (error) {
      console.error('连接浏览器失败:', error.message);
      return {
        success: false,
        error: error.message,
        stack: error.stack
      };
    }
  }

  /**
   * 启动 Chrome 并启用远程调试 - 修复：添加 --remote-debugging-address 参数
   */
  async launchChromeWithDebugging(options = {}) {
    const { spawn } = require('child_process');
    const path = require('path');
    const fs = require('fs');
    
    const chromePath = options.chromePath || 'D:\\Google\\Chrome\\Application\\chrome.exe';
    const port = options.port || 9222;
    const userDataDir = options.userDataDir || 'C:\\temp\\chrome_profile';
    
    // 确保目录存在
    if (!fs.existsSync(userDataDir)) {
      fs.mkdirSync(userDataDir, { recursive: true });
    }
    
    const args = [
      `--remote-debugging-port=${port}`,
      `--remote-debugging-address=127.0.0.1`, // 修复：强制使用 IPv4
      `--user-data-dir="${userDataDir}"`,
      '--no-first-run',
      '--no-default-browser-check',
      '--disable-infobars',
      '--disable-notifications',
      '--disable-popup-blocking',
      '--disable-gpu',
      '--disable-software-rasterizer',
      '--disable-dev-shm-usage',
      '--no-sandbox',
      '--disable-setuid-sandbox',
      '--disable-blink-features=AutomationControlled', // 防止被检测为自动化
      '--disable-features=IsolateOrigins,site-per-process' // 避免跨域问题
    ];
    
    if (options.url) {
      args.push(options.url);
    }
    
    console.log(`启动 Chrome: ${chromePath}`);
    console.log(`参数: ${args.join(' ')}`);
    
    try {
      const chromeProcess = spawn(chromePath, args, {
        detached: true,
        stdio: 'ignore',
        windowsHide: true
      });
      
      chromeProcess.unref();
      
      // 等待 Chrome 启动
      console.log('等待 Chrome 启动...');
      await new Promise(resolve => setTimeout(resolve, 5000));
      
      // 验证是否启动成功
      let retries = 5;
      let success = false;
      
      while (retries > 0) {
        try {
          // 修复：使用 127.0.0.1 而不是 localhost
          await axios.get(`http://127.0.0.1:${port}/json/version`, { timeout: 2000 });
          success = true;
          break;
        } catch (error) {
          console.log(`第 ${6 - retries} 次尝试连接失败，等待重试...`);
          retries--;
          await new Promise(resolve => setTimeout(resolve, 1000));
        }
      }
      
      if (!success) {
        throw new Error('Chrome 启动后无法连接调试接口');
      }
      
      return {
        success: true,
        pid: chromeProcess.pid,
        port,
        userDataDir
      };
    } catch (error) {
      console.error('启动 Chrome 失败:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * 断开连接
   */
  async disconnectBrowser(connectionId) {
    const connection = this.connectedBrowsers.get(connectionId);
    if (connection && connection.browser) {
      try {
        await connection.browser.disconnect();
        this.connectedBrowsers.delete(connectionId);
        this.emit('browser:disconnected', { connectionId });
        console.log(`断开连接: ${connectionId}`);
        return true;
      } catch (error) {
        console.error(`断开连接失败: ${error.message}`);
        return false;
      }
    }
    return false;
  }

  /**
   * 获取已连接浏览器列表
   */
  getConnectedBrowsers() {
    return Array.from(this.connectedBrowsers.entries()).map(([id, info]) => ({
      id,
      connectedAt: info.connectedAt,
      endpoint: info.browserWSEndpoint,
      port: info.port,
      pageCount: info.pages?.length || 0
    }));
  }

  /**
   * 关闭所有浏览器
   */
  async closeAll() {
    const closePromises = Array.from(this.connectedBrowsers.keys()).map(id => 
      this.disconnectBrowser(id)
    );
    await Promise.all(closePromises);
  }
}

module.exports = ConnectionManager;