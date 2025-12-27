const { app, BrowserWindow, ipcMain, Menu, Tray, nativeImage, dialog, shell } = require('electron');
const path = require('path');
const fs = require('fs');
const WindowManager = require('./window-manager');
const TaskManager = require('./task-manager');
const AutomationService = require('./automation-service');

class MainApp {
  constructor() {
    this.windowManager = null;
    this.taskManager = null;
    this.automationService = null;
    this.tray = null;
    this.controlWindow = null;
    this.systemStatus = {
      cpuUsage: 0,
      memoryUsage: 0,
      diskUsage: 0,
      activeWindows: 0,
      totalTasks: 0
    };
    
    // 改为 initManagers 或者保留 init 并添加 init 方法
    this.initManagers(); // 这里改成了 initManagers
  }

  initManagers() {
    this.windowManager = new WindowManager();
    this.taskManager = new TaskManager();
    this.automationService = new AutomationService(this.taskManager);
    
    // 监听连接管理器的事件
    this.automationService.connectionManager.on('browser:found', (data) => {
      console.log(`发现浏览器: 端口 ${data.port}, ${data.pageCount} 个页面`);
    });
    
    this.automationService.connectionManager.on('browser:connected', (data) => {
      console.log(`浏览器已连接: ${data.connectionId}`);
    });
    
    // 初始化其他组件
    this.setupIPC();
    this.createTray();
    this.setupAppMenu();
    this.startSystemMonitoring();
  }

  createTray() {
    try {
      const iconPath = path.join(__dirname, '../build/icon.png');
      
      // 检查图标文件是否存在
      if (!fs.existsSync(iconPath)) {
        console.warn('托盘图标文件不存在，将使用默认图标...');
        // 创建一个简单的默认图标
        const defaultIcon = nativeImage.createFromPath(path.join(__dirname, 'default-icon.png'));
        if (!defaultIcon.isEmpty()) {
          this.tray = new Tray(defaultIcon);
        } else {
          // 如果没有默认图标，创建一个空图标
          const emptyIcon = nativeImage.createEmpty();
          this.tray = new Tray(emptyIcon);
        }
      } else {
        this.tray = new Tray(iconPath);
      }
      
      const contextMenu = Menu.buildFromTemplate([
        { label: '控制面板', click: () => this.showControlPanel() },
        { label: '开始任务', click: () => this.startTask() },
        { label: '停止任务', click: () => this.stopTask() },
        { type: 'separator' },
        { label: '打开日志目录', click: () => this.openLogsDirectory() },
        { label: '重启应用', click: () => this.restartApp() },
        { type: 'separator' },
        { label: '退出', click: () => app.quit() }
      ]);
      
      this.tray.setToolTip('Ruoyi Desktop\n正在运行...');
      this.tray.setContextMenu(contextMenu);
      
    } catch (error) {
      console.error('创建托盘失败:', error);
      // 继续执行，托盘不是必须的
    }
  }

  createControlPanel() {
    this.controlWindow = new BrowserWindow({
      width: 1200,
      height: 800,
      webPreferences: {
        preload: path.join(__dirname, 'preload.js'),
        nodeIntegration: false,
        contextIsolation: true,
        sandbox: true
      },
      icon: path.join(__dirname, '../build/icon.png'),
      show: false,
      title: 'Ruoyi Desktop 控制面板'
    });

    this.controlWindow.loadFile(path.join(__dirname, 'control-panel.html'));
    
    this.controlWindow.once('ready-to-show', () => {
      this.controlWindow.show();
      if (process.env.NODE_ENV === 'development') {
        this.controlWindow.webContents.openDevTools();
      }
    });

    this.controlWindow.on('closed', () => {
      this.controlWindow = null;
    });
  }

  setupIPC() {
    // 窗口管理
    ipcMain.handle('window:create', async (event, config) => {
      return this.windowManager.createWindow(config);
    });

    ipcMain.handle('window:close', async (event, windowId) => {
      return this.windowManager.closeWindow(windowId);
    });

    ipcMain.handle('window:getAll', async () => {
      return this.windowManager.getAllWindows();
    });

    ipcMain.handle('window:arrange', async (event, columns) => {
      return this.windowManager.arrangeWindows(columns);
    });

    ipcMain.handle('browser:connect', async (event, options) => {
      return this.automationService.connectToExistingBrowser(options);
    });

    ipcMain.handle('browser:scan', async () => {
      return this.automationService.scanBrowsers();
    });

    ipcMain.handle('browser:launch-debug', async (event, options) => {
      return this.automationService.launchDebugChrome(options);
    });

    ipcMain.handle('douyin:with-existing', async (event, options) => {
      return this.automationService.executeDouyinWithExistingBrowser(options);
    });

    // 任务管理
    ipcMain.handle('task:start', async (event, taskConfig) => {
      const taskId = await this.taskManager.startTask(taskConfig);
      
      // 监听任务事件
      this.taskManager.once(`task:${taskId}:completed`, () => {
        if (this.controlWindow) {
          this.controlWindow.webContents.send('task:update', {
            type: 'completed',
            taskId
          });
        }
      });

      this.taskManager.on(`task:${taskId}:progress`, (progress) => {
        if (this.controlWindow) {
          this.controlWindow.webContents.send('task:update', {
            type: 'progress',
            taskId,
            progress
          });
        }
      });

      return taskId;
    });

    ipcMain.handle('task:stop', async (event, taskId) => {
      return this.taskManager.stopTask(taskId);
    });

    ipcMain.handle('task:list', async () => {
      return this.taskManager.getTasks();
    });

    ipcMain.handle('task:details', async (event, taskId) => {
      return this.taskManager.getTaskDetails(taskId);
    });

    ipcMain.handle('task:delete', async (event, taskId) => {
      return this.taskManager.deleteTask(taskId);
    });

    // 自动化服务
    ipcMain.handle('automation:execute', async (event, script) => {
      try {
        const automationId = await this.automationService.execute(script);
        return { success: true, automationId };
      } catch (error) {
        return { success: false, error: error.message };
      }
    });

    ipcMain.handle('automation:stop', async (event, automationId) => {
      return this.automationService.stop(automationId);
    });

    ipcMain.handle('automation:list', async () => {
      return this.automationService.getAllAutomations();
    });

    // 浏览器管理
    ipcMain.handle('browser:create', async (event, options) => {
      return this.automationService.createBrowser(options);
    });

    ipcMain.handle('browser:close', async (event, browserId) => {
      return this.automationService.closeBrowser(browserId);
    });

    ipcMain.handle('browser:list', async () => {
      return this.automationService.getBrowsers();
    });

    ipcMain.handle('browser:navigate', async (event, browserId, url) => {
      const automation = this.automationService.automations.get(browserId);
      if (automation) {
        await automation.navigate(url);
        return true;
      }
      return false;
    });

    ipcMain.handle('browser:screenshot', async (event, browserId, options) => {
      const automation = this.automationService.automations.get(browserId);
      if (automation) {
        return await automation.screenshot(options);
      }
      return null;
    });

    // 系统相关
    ipcMain.handle('system:openDevTools', async (event, windowId) => {
      const window = this.windowManager.getWindow(windowId);
      if (window) {
        window.webContents.openDevTools();
        return true;
      }
      return false;
    });

    ipcMain.handle('system:info', async () => {
      return this.systemStatus;
    });

    ipcMain.handle('system:screenshot', async (event, options) => {
      const screenshot = require('screenshot-desktop');
      try {
        const img = await screenshot({ format: options?.format || 'png' });
        return img;
      } catch (error) {
        throw new Error(`截图失败: ${error.message}`);
      }
    });
  }

  setupAppMenu() {
    const template = [
      {
        label: '文件',
        submenu: [
          { 
            label: '新建窗口', 
            accelerator: 'CmdOrCtrl+N',
            click: () => this.createNewWindow()
          },
          { 
            label: '关闭窗口', 
            accelerator: 'CmdOrCtrl+W',
            click: () => {
              const focusedWindow = BrowserWindow.getFocusedWindow();
              if (focusedWindow) focusedWindow.close();
            }
          },
          { type: 'separator' },
          { 
            label: '重启应用', 
            accelerator: 'CmdOrCtrl+R',
            click: () => this.restartApp()
          },
          { 
            label: '退出', 
            accelerator: 'CmdOrCtrl+Q', 
            click: () => app.quit() 
          }
        ]
      },
      {
        label: '编辑',
        submenu: [
          { label: '撤销', accelerator: 'CmdOrCtrl+Z', role: 'undo' },
          { label: '重做', accelerator: 'Shift+CmdOrCtrl+Z', role: 'redo' },
          { type: 'separator' },
          { label: '剪切', accelerator: 'CmdOrCtrl+X', role: 'cut' },
          { label: '复制', accelerator: 'CmdOrCtrl+C', role: 'copy' },
          { label: '粘贴', accelerator: 'CmdOrCtrl+V', role: 'paste' },
          { label: '全选', accelerator: 'CmdOrCtrl+A', role: 'selectAll' }
        ]
      },
      {
        label: '视图',
        submenu: [
          { 
            label: '重新加载', 
            accelerator: 'CmdOrCtrl+R',
            click: (item, focusedWindow) => {
              if (focusedWindow) focusedWindow.reload();
            }
          },
          { 
            label: '开发者工具', 
            accelerator: process.platform === 'darwin' ? 'Alt+Command+I' : 'Ctrl+Shift+I',
            click: (item, focusedWindow) => {
              if (focusedWindow) focusedWindow.webContents.openDevTools();
            }
          },
          { type: 'separator' },
          { label: '放大', accelerator: 'CmdOrCtrl+=', role: 'zoomIn' },
          { label: '缩小', accelerator: 'CmdOrCtrl+-', role: 'zoomOut' },
          { label: '重置缩放', accelerator: 'CmdOrCtrl+0', role: 'resetZoom' },
          { type: 'separator' },
          { label: '全屏', accelerator: process.platform === 'darwin' ? 'Ctrl+Command+F' : 'F11', role: 'togglefullscreen' }
        ]
      },
      {
        label: '任务',
        submenu: [
          { 
            label: '开始新任务',
            click: () => this.startTask()
          },
          { 
            label: '停止所有任务',
            click: () => this.stopAllTasks()
          },
          { type: 'separator' },
          { 
            label: '任务管理器',
            click: () => this.showTaskManager()
          }
        ]
      },
      {
        label: '帮助',
        submenu: [
          {
            label: '查看文档',
            click: async () => {
              await shell.openExternal('https://github.com/yangzongzhuan/RuoYi');
            }
          },
          {
            label: '报告问题',
            click: async () => {
              await shell.openExternal('https://github.com/yangzongzhuan/RuoYi/issues');
            }
          },
          { type: 'separator' },
          {
            label: '关于 Ruoyi Desktop',
            click: () => this.showAboutDialog()
          }
        ]
      }
    ];

    const menu = Menu.buildFromTemplate(template);
    Menu.setApplicationMenu(menu);
  }

  startSystemMonitoring() {
    setInterval(() => {
      const os = require('os');
      
      this.systemStatus = {
        cpuUsage: os.loadavg()[0],
        memoryUsage: Math.round((os.totalmem() - os.freemem()) / 1024 / 1024 / 1024),
        diskUsage: 0, // 这里需要额外的库来获取磁盘使用情况
        activeWindows: this.windowManager.getAllWindows().length,
        totalTasks: this.taskManager.getTasks().length
      };

      if (this.controlWindow) {
        this.controlWindow.webContents.send('system:log', {
          type: 'monitoring',
          data: this.systemStatus
        });
      }
    }, 5000);
  }

  // 辅助方法
  showControlPanel() {
    if (this.controlWindow) {
      this.controlWindow.show();
      this.controlWindow.focus();
    } else {
      this.createControlPanel();
    }
  }

  createNewWindow() {
    this.windowManager.createWindow({
      url: 'https://www.baidu.com',
      width: 1024,
      height: 768,
      title: 'Ruoyi Desktop - 新窗口'
    });
  }

  startTask() {
    dialog.showMessageBox(this.controlWindow, {
      type: 'info',
      title: '开始任务',
      message: '请选择要执行的任务类型',
      buttons: ['自动化任务', '监控任务', '数据收集', '取消']
    }).then(result => {
      if (result.response === 0) {
        // 自动化任务
        this.startAutomationTask();
      }
    });
  }

  startAutomationTask() {
    const taskConfig = {
      name: '抖音自动化任务',
      type: 'automation',
      script: [
        {
          action: 'navigate',
          url: 'https://www.douyin.com'
        },
        {
          action: 'wait',
          timeout: 5000
        },
        {
          action: 'click',
          selector: '[data-e2e="feed-video"]:first-child'
        },
        {
          action: 'wait',
          timeout: 3000
        }
      ],
      config: {
        chromePath: 'D:\\Google\\Chrome\\Application\\chrome.exe',
        headless: false
      }
    };

    this.taskManager.startTask(taskConfig);
  }

  stopTask() {
    const tasks = this.taskManager.getTasks();
    const runningTasks = tasks.filter(task => task.status === 'running');
    
    if (runningTasks.length === 0) {
      dialog.showMessageBox(this.controlWindow, {
        type: 'info',
        message: '当前没有运行中的任务'
      });
      return;
    }

    runningTasks.forEach(task => {
      this.taskManager.stopTask(task.id);
    });

    dialog.showMessageBox(this.controlWindow, {
      type: 'info',
      message: `已停止 ${runningTasks.length} 个任务`
    });
  }

  stopAllTasks() {
    const tasks = this.taskManager.getTasks();
    tasks.forEach(task => {
      if (task.status === 'running') {
        this.taskManager.stopTask(task.id);
      }
    });
  }

  showTaskManager() {
    // 这里可以打开任务管理器窗口
    this.windowManager.createWindow({
      file: path.join(__dirname, 'task-manager.html'),
      width: 800,
      height: 600,
      title: '任务管理器'
    });
  }

  openLogsDirectory() {
    const logsDir = path.join(__dirname, '../logs');
    if (!fs.existsSync(logsDir)) {
      fs.mkdirSync(logsDir, { recursive: true });
    }
    shell.openPath(logsDir);
  }

  restartApp() {
    app.relaunch();
    app.exit(0);
  }

  showAboutDialog() {
    dialog.showMessageBox(this.controlWindow, {
      type: 'info',
      title: '关于 Ruoyi Desktop',
      message: 'Ruoyi Desktop 桌面自动化应用',
      detail: `版本: 1.0.0\n基于 Electron 和 Ruoyi 框架\n作者: Ruoyi 团队\nGitHub: https://github.com/yangzongzhuan/RuoYi`,
      buttons: ['确定']
    });
  }
}

// 添加应用准备就绪的处理
app.whenReady().then(() => {
  console.log('Electron 应用启动...');
  
  // 创建 MainApp 实例
  const mainApp = new MainApp();
  
  // 显示控制面板窗口
  mainApp.showControlPanel();
  
  // macOS 特殊处理：当应用被激活但没有窗口时，打开一个窗口
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      mainApp.showControlPanel();
    }
  });
  
  // 所有窗口关闭时退出应用（除了 macOS）
  app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
      app.quit();
    }
  });
  
  console.log('Ruoyi Desktop 启动完成');
});

// 处理应用退出
app.on('before-quit', () => {
  console.log('应用正在退出...');
});

// 错误处理
process.on('uncaughtException', (error) => {
  console.error('未捕获的异常:', error);
  dialog.showErrorBox('应用程序错误', `发生未处理的错误: ${error.message}`);
});