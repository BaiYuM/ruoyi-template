const puppeteer = require('puppeteer-core');
const EventEmitter = require('events');
const path = require('path');
const fs = require('fs').promises;
const axios = require('axios');

class Automation extends EventEmitter {
  constructor(options = {}) {
    super();
    this.browser = null;
    this.page = null;
    this.chromePath = options.chromePath || 'D:\\Google\\Chrome\\Application\\chrome.exe';
    this.isConnectedMode = options.connectToExisting || false;
    
    this.options = {
      headless: options.headless || false,
      executablePath: this.chromePath,
      userDataDir: options.userDataDir || path.join(__dirname, '../chrome_profile'),
      defaultViewport: options.defaultViewport || { width: 1280, height: 800 },
      args: options.args || [
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--disable-dev-shm-usage',
        '--window-size=1280,800',
        '--disable-blink-features=AutomationControlled',
        '--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
      ],
      ...options
    };
    
    // 远程调试配置
    this.remoteConfig = {
      port: options.port || 9222,
      browserWSEndpoint: options.browserWSEndpoint || null,
      useExisting: options.useExisting || false
    };
    
    // 抖音专用配置
    this.douyinConfig = {
      scrollCount: options.scrollCount || 5,
      likeProbability: options.likeProbability || 0.3,
      commentProbability: options.commentProbability || 0.2,
      followProbability: options.followProbability || 0.1,
      commentTexts: options.commentTexts || ['赞！', '不错', '学到了', '哈哈', '👍'],
      ...options.douyinConfig
    };
    
    // 状态跟踪
    this.stats = {
      actions: 0,
      likes: 0,
      comments: 0,
      follows: 0,
      errors: 0,
      startTime: null,
      endTime: null
    };
  }

  // ==================== 浏览器连接方法 ====================

  /**
   * 查找已打开的Chrome调试端点
   */
  async findDebugEndpoints(port = 9222) {
    try {
      const response = await axios.get(`http://localhost:${port}/json/version`, {
        timeout: 2000
      });
      
      if (response.data) {
        // 获取所有页面
        const pagesResponse = await axios.get(`http://localhost:${port}/json/list`);
        const pages = pagesResponse.data;
        
        return {
          success: true,
          browser: response.data.Browser || 'Chrome',
          protocolVersion: response.data['Protocol-Version'],
          userAgent: response.data['User-Agent'],
          webSocketDebuggerUrl: response.data.webSocketDebuggerUrl,
          pages: pages.map(page => ({
            id: page.id,
            title: page.title,
            url: page.url,
            type: page.type,
            webSocketDebuggerUrl: page.webSocketDebuggerUrl
          }))
        };
      }
    } catch (error) {
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * 扫描所有可能的调试端口
   */
  async scanAllDebugPorts() {
    const ports = [9222, 9223, 9224, 9225, 9226, 9227, 9228, 9229];
    const results = [];
    
    for (const port of ports) {
      try {
        const result = await this.findDebugEndpoints(port);
        if (result.success) {
          results.push({
            port,
            ...result
          });
        }
      } catch (error) {
        // 端口未开放，跳过
      }
    }
    
    return results;
  }

  /**
   * 连接到已打开的浏览器
   */
  async connectToExistingBrowser(options = {}) {
    try {
      this.emit('status', '正在连接到已打开的浏览器...');
      
      let browserWSEndpoint = options.browserWSEndpoint || this.remoteConfig.browserWSEndpoint;
      let port = options.port || this.remoteConfig.port;
      
      // 如果没有提供端点，自动扫描
      if (!browserWSEndpoint) {
        const scanResults = await this.scanAllDebugPorts();
        
        if (scanResults.length === 0) {
          throw new Error('未找到已开启调试模式的Chrome浏览器');
        }
        
        // 选择第一个找到的浏览器
        const firstBrowser = scanResults[0];
        browserWSEndpoint = firstBrowser.webSocketDebuggerUrl;
        port = firstBrowser.port;
        
        this.emit('status', `发现浏览器: ${firstBrowser.browser} (端口: ${port})`);
        this.emit('status', `找到 ${firstBrowser.pages.length} 个已打开的页面`);
        
        // 显示页面信息
        firstBrowser.pages.forEach((page, index) => {
          this.emit('status', `  ${index + 1}. ${page.title || '无标题'} - ${page.url}`);
        });
      }
      
      // 连接到浏览器
      this.browser = await puppeteer.connect({
        browserWSEndpoint,
        defaultViewport: null
      });
      
      this.emit('status', '浏览器连接成功！');
      
      // 获取所有页面
      const pages = await this.browser.pages();
      
      // 自动选择页面逻辑
      if (options.selectPageByUrl) {
        // 按URL关键词选择页面
        await this.selectPageByUrl(options.selectPageByUrl);
      } else if (options.selectPageIndex !== undefined) {
        // 按索引选择页面
        await this.selectPageByIndex(options.selectPageIndex);
      } else if (pages.length > 0) {
        // 默认选择第一个页面
        this.page = pages[0];
        this.emit('status', `自动选择页面: ${await this.page.title()} - ${this.page.url()}`);
      } else {
        // 没有页面则创建新页面
        this.page = await this.browser.newPage();
        this.emit('status', '创建新页面');
      }
      
      this.isConnectedMode = true;
      return true;
      
    } catch (error) {
      this.emit('error', `连接浏览器失败: ${error.message}`);
      throw error;
    }
  }

  /**
   * 启动新浏览器
   */
  async launchNewBrowser() {
    try {
      this.emit('status', '正在启动新浏览器...');
      
      this.browser = await puppeteer.launch(this.options);
      this.page = await this.browser.newPage();
      
      // 设置页面基本配置
      await this.page.setUserAgent(this.options.args.find(arg => arg.includes('user-agent'))?.split('=')[1] || 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36');
      await this.page.setViewport(this.options.defaultViewport || { width: 1280, height: 800 });
      
      this.emit('status', '新浏览器启动成功');
      return true;
    } catch (error) {
      this.emit('error', `启动浏览器失败: ${error.message}`);
      throw error;
    }
  }

  /**
   * 初始化浏览器（智能选择模式）
   */
  async initialize(options = {}) {
    this.stats.startTime = new Date();
    
    try {
      // 优先使用连接模式
      if (this.remoteConfig.useExisting || options.connectToExisting) {
        try {
          await this.connectToExistingBrowser(options);
          this.emit('status', '使用已连接的浏览器模式');
        } catch (connectError) {
          this.emit('warning', `连接现有浏览器失败: ${connectError.message}，将启动新浏览器`);
          await this.launchNewBrowser();
        }
      } else {
        await this.launchNewBrowser();
      }
      
      return true;
    } catch (error) {
      this.emit('error', `浏览器初始化失败: ${error.message}`);
      throw error;
    }
  }

  // ==================== 页面选择方法 ====================

  /**
   * 按索引选择页面
   */
  async selectPageByIndex(index = 0) {
    if (!this.browser) {
      throw new Error('浏览器未连接');
    }
    
    const pages = await this.browser.pages();
    if (pages.length > index) {
      this.page = pages[index];
      const title = await this.page.title();
      const url = await this.page.url();
      this.emit('status', `选择页面 ${index}: ${title} - ${url}`);
      return true;
    }
    
    throw new Error(`页面索引 ${index} 不存在，当前只有 ${pages.length} 个页面`);
  }

  /**
   * 按URL关键词选择页面
   */
  async selectPageByUrl(keyword) {
    if (!this.browser) {
      throw new Error('浏览器未连接');
    }
    
    const pages = await this.browser.pages();
    for (let i = 0; i < pages.length; i++) {
      const page = pages[i];
      const url = await page.url();
      
      if (url.includes(keyword)) {
        this.page = page;
        const title = await page.title();
        this.emit('status', `找到匹配页面: ${title} - ${url}`);
        return true;
      }
    }
    
    throw new Error(`未找到包含 "${keyword}" 的页面`);
  }

  /**
   * 按标题关键词选择页面
   */
  async selectPageByTitle(keyword) {
    if (!this.browser) {
      throw new Error('浏览器未连接');
    }
    
    const pages = await this.browser.pages();
    for (let i = 0; i < pages.length; i++) {
      const page = pages[i];
      const title = await page.title();
      
      if (title.includes(keyword)) {
        this.page = page;
        const url = await page.url();
        this.emit('status', `找到匹配页面: ${title} - ${url}`);
        return true;
      }
    }
    
    throw new Error(`未找到标题包含 "${keyword}" 的页面`);
  }

  /**
   * 获取所有页面信息
   */
  async getAllPages() {
    if (!this.browser) {
      throw new Error('浏览器未连接');
    }
    
    const pages = await this.browser.pages();
    const pageInfos = [];
    
    for (let i = 0; i < pages.length; i++) {
      const page = pages[i];
      try {
        const title = await page.title();
        const url = await page.url();
        pageInfos.push({
          index: i,
          title: title || '无标题',
          url: url || 'about:blank',
          isActive: page === this.page
        });
      } catch (error) {
        pageInfos.push({
          index: i,
          title: '无法获取标题',
          url: '无法获取URL',
          isActive: page === this.page,
          error: error.message
        });
      }
    }
    
    return pageInfos;
  }

  // ==================== 基础操作方法 ====================

  /**
   * 导航到URL
   */
  async navigate(url, options = {}) {
    if (!this.page) {
      throw new Error('请先初始化浏览器');
    }

    try {
      this.emit('status', `正在导航到: ${url}`);
      
      const navigationOptions = {
        waitUntil: options.waitUntil || 'networkidle0',
        timeout: options.timeout || 30000,
        ...options
      };

      await this.page.goto(url, navigationOptions);
      this.stats.actions++;
      this.emit('status', '导航完成');
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `导航失败: ${error.message}`);
      throw error;
    }
  }

  /**
   * 等待元素
   */
  async waitForElement(selector, options = {}) {
    if (!this.page) {
      throw new Error('请先初始化浏览器');
    }

    try {
      this.emit('status', `等待元素: ${selector}`);
      
      const waitOptions = {
        timeout: options.timeout || 30000,
        visible: options.visible || true,
        ...options
      };

      await this.page.waitForSelector(selector, waitOptions);
      this.emit('status', '元素已找到');
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `等待元素超时: ${error.message}`);
      throw error;
    }
  }

  /**
   * 输入文本
   */
  async type(selector, text, options = {}) {
    if (!this.page) {
      throw new Error('请先初始化浏览器');
    }

    try {
      await this.waitForElement(selector);
      await this.page.focus(selector);
      
      if (options.clearFirst) {
        await this.page.$eval(selector, el => el.value = '');
      }
      
      await this.page.type(selector, text, { delay: options.delay || 50 });
      this.stats.actions++;
      this.emit('status', `已输入文本: ${text}`);
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `输入文本失败: ${error.message}`);
      throw error;
    }
  }

  /**
   * 点击元素
   */
  async click(selector, options = {}) {
    if (!this.page) {
      throw new Error('请先初始化浏览器');
    }

    try {
      await this.waitForElement(selector);
      
      if (options.delay) {
        await this.page.waitForTimeout(options.delay);
      }
      
      await this.page.click(selector);
      this.stats.actions++;
      this.emit('status', `已点击元素: ${selector}`);
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `点击元素失败: ${error.message}`);
      throw error;
    }
  }

  /**
   * 滚动页面
   */
  async scroll(amount = 500, direction = 'down') {
    if (!this.page) {
      throw new Error('请先初始化浏览器');
    }

    try {
      this.emit('status', `正在滚动页面: ${direction} ${amount}px`);
      
      await this.page.evaluate((amount, direction) => {
        if (direction === 'down') {
          window.scrollBy(0, amount);
        } else if (direction === 'up') {
          window.scrollBy(0, -amount);
        } else if (direction === 'toBottom') {
          window.scrollTo(0, document.body.scrollHeight);
        } else if (direction === 'toTop') {
          window.scrollTo(0, 0);
        }
      }, amount, direction);
      
      this.stats.actions++;
      await this.page.waitForTimeout(1000);
      this.emit('status', '滚动完成');
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `滚动失败: ${error.message}`);
      throw error;
    }
  }

  /**
   * 截图
   */
  async screenshot(options = {}) {
    if (!this.page) {
      throw new Error('请先初始化浏览器');
    }

    try {
      this.emit('status', '正在截图...');
      
      const screenshotOptions = {
        path: options.path || path.join(__dirname, '../screenshots', `screenshot_${Date.now()}.png`),
        type: options.type || 'png',
        fullPage: options.fullPage || false,
        quality: options.quality || 90,
        ...options
      };

      // 确保截图目录存在
      const dir = path.dirname(screenshotOptions.path);
      await fs.mkdir(dir, { recursive: true });
      
      const buffer = await this.page.screenshot(screenshotOptions);
      this.stats.actions++;
      this.emit('status', `截图已保存: ${screenshotOptions.path}`);
      
      return buffer;
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `截图失败: ${error.message}`);
      throw error;
    }
  }

  // ==================== 抖音专用方法 ====================

  /**
   * 抖音自动化主函数
   */
  async douyinAutomation(options = {}) {
    try {
      this.emit('status', '开始抖音自动化...');
      
      const config = {
        scrollCount: options.scrollCount || this.douyinConfig.scrollCount,
        likeProbability: options.likeProbability || this.douyinConfig.likeProbability,
        commentProbability: options.commentProbability || this.douyinConfig.commentProbability,
        followProbability: options.followProbability || this.douyinConfig.followProbability,
        commentTexts: options.commentTexts || this.douyinConfig.commentTexts,
        maxVideos: options.maxVideos || 20,
        ...options
      };
      
      // 1. 确保在抖音页面
      await this.ensureOnDouyin();
      
      // 2. 执行主循环
      for (let i = 0; i < config.scrollCount; i++) {
        this.emit('status', `第 ${i + 1}/${config.scrollCount} 轮操作`);
        
        // 滚动加载
        await this.scroll(800, 'down');
        
        // 随机点击视频
        if (Math.random() < 0.7) { // 70%概率点击视频
          await this.douyinClickRandomVideo();
          await this.page.waitForTimeout(3000);
          
          // 点赞
          if (Math.random() < config.likeProbability) {
            await this.douyinLikeVideo();
            await this.page.waitForTimeout(1000);
          }
          
          // 评论
          if (Math.random() < config.commentProbability) {
            const randomComment = config.commentTexts[Math.floor(Math.random() * config.commentTexts.length)];
            await this.douyinCommentVideo(randomComment);
            await this.page.waitForTimeout(1500);
          }
          
          // 关注
          if (Math.random() < config.followProbability) {
            await this.douyinFollowAuthor();
            await this.page.waitForTimeout(1000);
          }
          
          // 返回首页
          await this.page.goBack();
          await this.page.waitForTimeout(2000);
        }
        
        // 随机延迟
        const delay = 2000 + Math.random() * 3000;
        await this.page.waitForTimeout(delay);
      }
      
      this.emit('status', '抖音自动化完成');
      return this.getStats();
      
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `抖音自动化失败: ${error.message}`);
      throw error;
    }
  }

  /**
   * 确保在抖音页面
   */
  async ensureOnDouyin() {
    if (!this.page) {
      throw new Error('页面未初始化');
    }
    
    const currentUrl = await this.page.url();
    
    if (!currentUrl.includes('douyin.com')) {
      this.emit('status', '当前不在抖音页面，正在跳转...');
      await this.navigate('https://www.douyin.com');
      await this.page.waitForTimeout(5000);
    } else {
      this.emit('status', '已在抖音页面');
    }
    
    // 检查登录状态
    await this.checkDouyinLoginStatus();
  }

  /**
   * 检查抖音登录状态
   */
  async checkDouyinLoginStatus() {
    try {
      // 检查是否有登录弹窗
      const loginModal = await this.page.$('.dy-account-close, .login-panel, .modal-login');
      if (loginModal) {
        this.emit('warning', '检测到登录弹窗，请确保抖音账号已登录');
        // 可以添加自动关闭弹窗逻辑
        try {
          await loginModal.click();
          await this.page.waitForTimeout(1000);
        } catch (e) {
          // 忽略点击错误
        }
      }
      
      // 检查用户头像是否存在（登录标志）
      const userAvatar = await this.page.$('.avatar, .user-avatar, [data-e2e="user-avatar"]');
      if (userAvatar) {
        this.emit('status', '抖音账号已登录');
        return true;
      } else {
        this.emit('warning', '可能未检测到登录状态，继续执行...');
        return false;
      }
    } catch (error) {
      this.emit('warning', `检查登录状态失败: ${error.message}`);
      return false;
    }
  }

  /**
   * 随机点击抖音视频
   */
  async douyinClickRandomVideo() {
    try {
      // 抖音视频选择器列表
      const videoSelectors = [
        '[data-e2e="feed-video"]',
        '.xg-video-container',
        '.video-card',
        'div[class*="video"]',
        'a[href*="/video/"]',
        'div[class*="card"]'
      ];
      
      let clicked = false;
      
      for (const selector of videoSelectors) {
        try {
          const videos = await this.page.$$(selector);
          if (videos.length > 0) {
            // 随机选择一个视频（避免点击第一个，因为可能是广告）
            const randomIndex = Math.floor(Math.random() * Math.min(videos.length, 10)) + 1;
            const videoToClick = videos[Math.min(randomIndex, videos.length - 1)];
            
            await videoToClick.click();
            clicked = true;
            this.stats.actions++;
            this.emit('status', `点击视频成功 (选择器: ${selector})`);
            break;
          }
        } catch (error) {
          continue;
        }
      }
      
      if (!clicked) {
        // 备用方案：点击屏幕中央
        const { width, height } = this.page.viewport();
        await this.page.mouse.click(width / 2, height / 2);
        this.emit('status', '使用备用方案点击屏幕中央');
      }
      
      return clicked;
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `点击视频失败: ${error.message}`);
      return false;
    }
  }

  /**
   * 点赞当前视频
   */
  async douyinLikeVideo() {
    try {
      const likeSelectors = [
        '[data-e2e="browse-like"]',
        '.like-icon',
        'div[class*="like"]',
        'svg[aria-label="点赞"]',
        'button:has-text("赞")'
      ];
      
      for (const selector of likeSelectors) {
        try {
          const likeButton = await this.page.$(selector);
          if (likeButton) {
            await likeButton.click();
            this.stats.likes++;
            this.stats.actions++;
            this.emit('status', '点赞成功');
            return true;
          }
        } catch (error) {
          continue;
        }
      }
      
      this.emit('warning', '未找到点赞按钮');
      return false;
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `点赞失败: ${error.message}`);
      return false;
    }
  }

  /**
   * 评论视频
   */
  async douyinCommentVideo(text = '自动化测试评论') {
    try {
      const commentSelectors = [
        '[data-e2e="browse-comment"]',
        '.comment-icon',
        'div[class*="comment"]',
        'svg[aria-label="评论"]'
      ];
      
      for (const selector of commentSelectors) {
        try {
          const commentButton = await this.page.$(selector);
          if (commentButton) {
            await commentButton.click();
            await this.page.waitForTimeout(1500);
            
            // 输入评论
            const commentInputSelectors = [
              '.comment-input',
              'input[placeholder*="评论"]',
              'textarea[placeholder*="评论"]',
              '.input-area'
            ];
            
            let inputFound = false;
            for (const inputSelector of commentInputSelectors) {
              try {
                const commentInput = await this.page.$(inputSelector);
                if (commentInput) {
                  await commentInput.click();
                  await commentInput.type(text, { delay: 50 });
                  inputFound = true;
                  break;
                }
              } catch (error) {
                continue;
              }
            }
            
            if (!inputFound) {
              this.emit('warning', '未找到评论输入框');
              return false;
            }
            
            // 发送评论
            await this.page.waitForTimeout(500);
            const sendSelectors = [
              '.comment-send',
              'button:has-text("发送")',
              'button[type="submit"]'
            ];
            
            for (const sendSelector of sendSelectors) {
              try {
                const sendButton = await this.page.$(sendSelector);
                if (sendButton) {
                  await sendButton.click();
                  this.stats.comments++;
                  this.stats.actions++;
                  this.emit('status', `评论成功: ${text}`);
                  
                  // 关闭评论框
                  await this.page.waitForTimeout(1000);
                  const closeSelectors = ['.close-icon', '.icon-close', 'button[aria-label="关闭"]'];
                  for (const closeSelector of closeSelectors) {
                    try {
                      const closeButton = await this.page.$(closeSelector);
                      if (closeButton) {
                        await closeButton.click();
                        break;
                      }
                    } catch (e) {
                      continue;
                    }
                  }
                  
                  return true;
                }
              } catch (error) {
                continue;
              }
            }
            
            this.emit('warning', '未找到发送按钮');
            return false;
          }
        } catch (error) {
          continue;
        }
      }
      
      this.emit('warning', '未找到评论按钮');
      return false;
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `评论失败: ${error.message}`);
      return false;
    }
  }

  /**
   * 关注作者
   */
  async douyinFollowAuthor() {
    try {
      const followSelectors = [
        '[data-e2e="follow-button"]',
        '.follow-btn',
        'button:has-text("关注")',
        'div[class*="follow"]'
      ];
      
      for (const selector of followSelectors) {
        try {
          const followButton = await this.page.$(selector);
          if (followButton) {
            const buttonText = await this.page.evaluate(el => el.textContent, followButton);
            
            // 检查是否是"关注"按钮（不是"已关注"）
            if (buttonText.includes('关注') && !buttonText.includes('已关注')) {
              await followButton.click();
              this.stats.follows++;
              this.stats.actions++;
              this.emit('status', '关注成功');
              return true;
            }
          }
        } catch (error) {
          continue;
        }
      }
      
      this.emit('warning', '未找到关注按钮或已关注');
      return false;
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `关注失败: ${error.message}`);
      return false;
    }
  }

  /**
   * 获取抖音页面信息
   */
  async getDouyinPageInfo() {
    try {
      const info = await this.page.evaluate(() => {
        const result = {
          title: document.title,
          url: window.location.href,
          isLoggedIn: false,
          videoCount: 0,
          userInfo: null
        };
        
        // 检查登录状态
        const userElements = document.querySelectorAll('.avatar, .user-avatar, [data-e2e="user-avatar"]');
        result.isLoggedIn = userElements.length > 0;
        
        // 统计视频数量
        const videoElements = document.querySelectorAll('[data-e2e="feed-video"], .video-card, .xg-video-container');
        result.videoCount = videoElements.length;
        
        // 获取用户信息（如果有）
        const userName = document.querySelector('.user-name, .nickname, [data-e2e="user-name"]');
        if (userName) {
          result.userInfo = {
            name: userName.textContent?.trim()
          };
        }
        
        return result;
      });
      
      return info;
    } catch (error) {
      return {
        error: error.message
      };
    }
  }

  // ==================== 实用工具方法 ====================

  /**
   * 执行JavaScript脚本
   */
  async executeScript(script, args = []) {
    if (!this.page) {
      throw new Error('请先初始化浏览器');
    }

    try {
      this.emit('status', '正在执行脚本...');
      const result = await this.page.evaluate(script, ...args);
      this.stats.actions++;
      this.emit('status', '脚本执行完成');
      return result;
    } catch (error) {
      this.stats.errors++;
      this.emit('error', `脚本执行失败: ${error.message}`);
      throw error;
    }
  }

  /**
   * 等待指定时间
   */
  async wait(ms) {
    await this.page.waitForTimeout(ms);
  }

  /**
   * 获取页面标题
   */
  async getTitle() {
    if (!this.page) {
      throw new Error('请先初始化浏览器');
    }
    return await this.page.title();
  }

  /**
   * 获取页面URL
   */
  async getUrl() {
    if (!this.page) {
      throw new Error('请先初始化浏览器');
    }
    return await this.page.url();
  }

  /**
   * 获取页面内容
   */
  async getContent() {
    if (!this.page) {
      throw new Error('请先初始化浏览器');
    }
    return await this.page.content();
  }

  /**
   * 获取统计信息
   */
  getStats() {
    this.stats.endTime = new Date();
    const duration = this.stats.endTime - this.stats.startTime;
    
    return {
      ...this.stats,
      duration: `${Math.floor(duration / 1000)}秒`,
      actionsPerMinute: duration > 0 ? ((this.stats.actions / duration) * 60000).toFixed(2) : 0,
      successRate: this.stats.actions > 0 ? 
        ((this.stats.actions - this.stats.errors) / this.stats.actions * 100).toFixed(2) + '%' : '0%'
    };
  }

  // ==================== 执行流程方法 ====================

  /**
   * 执行自动化流程
   */
  async executeFlow(steps) {
    if (!this.page) {
      await this.initialize();
    }

    try {
      for (let i = 0; i < steps.length; i++) {
        const step = steps[i];
        this.emit('step:start', { index: i, step });
        
        switch (step.action) {
          case 'navigate':
            await this.navigate(step.url, step.options);
            break;
          case 'type':
            await this.type(step.selector, step.text, step.options);
            break;
          case 'click':
            await this.click(step.selector, step.options);
            break;
          case 'wait':
            await this.wait(step.timeout || 1000);
            break;
          case 'waitForElement':
            await this.waitForElement(step.selector, step.options);
            break;
          case 'screenshot':
            await this.screenshot(step.options);
            break;
          case 'scroll':
            await this.scroll(step.amount, step.direction);
            break;
          case 'evaluate':
            await this.executeScript(step.script, step.args || []);
            break;
          case 'douyinAutomation':
            await this.douyinAutomation(step.config);
            break;
          case 'douyinLike':
            await this.douyinLikeVideo();
            break;
          case 'douyinComment':
            await this.douyinCommentVideo(step.text);
            break;
          case 'douyinFollow':
            await this.douyinFollowAuthor();
            break;
          default:
            throw new Error(`未知的操作: ${step.action}`);
        }
        
        this.emit('step:complete', { index: i, step });
      }
      
      this.emit('flow:complete', { 
        totalSteps: steps.length,
        stats: this.getStats()
      });
      
      return this.getStats();
    } catch (error) {
      this.emit('flow:error', { 
        error: error.message,
        stats: this.getStats()
      });
      throw error;
    }
  }

  // ==================== 清理和关闭 ====================

  /**
   * 关闭浏览器
   */
  async close() {
    try {
      if (this.browser) {
        this.emit('status', '正在关闭浏览器...');
        
        if (this.isConnectedMode) {
          // 如果是连接模式，只断开连接
          await this.browser.disconnect();
        } else {
          // 如果是启动模式，关闭浏览器
          await this.browser.close();
        }
        
        this.browser = null;
        this.page = null;
        this.emit('status', '浏览器已关闭');
      }
    } catch (error) {
      this.emit('error', `关闭浏览器失败: ${error.message}`);
      throw error;
    }
  }

  /**
   * 重启浏览器
   */
  async restart(options = {}) {
    await this.close();
    await this.initialize(options);
    this.emit('status', '浏览器已重启');
  }

  /**
   * 重新加载页面
   */
  async reload() {
    if (!this.page) {
      throw new Error('页面未初始化');
    }
    
    await this.page.reload();
    this.emit('status', '页面已重新加载');
  }

  /**
   * 创建新的标签页
   */
  async newTab(url) {
    if (!this.browser) {
      throw new Error('浏览器未初始化');
    }
    
    const newPage = await this.browser.newPage();
    this.page = newPage;
    
    if (url) {
      await this.navigate(url);
    }
    
    this.emit('status', '已创建新标签页');
    return newPage;
  }
}

module.exports = Automation;