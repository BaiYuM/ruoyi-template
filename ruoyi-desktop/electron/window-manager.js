const { BrowserWindow, screen } = require('electron');
const path = require('path');

class WindowManager {
  constructor() {
    this.windows = new Map();
    this.windowCounter = 0;
  }

  /**
   * 创建新窗口
   * @param {Object} config - 窗口配置
   * @returns {string} 窗口ID
   */
  createWindow(config = {}) {
    const windowId = `window_${++this.windowCounter}`;
    
    const defaultConfig = {
      width: 1024,
      height: 768,
      webPreferences: {
        preload: path.join(__dirname, 'preload.js'),
        nodeIntegration: false,
        contextIsolation: true,
        sandbox: true
      },
      show: false,
      icon: path.join(__dirname, '../build/icon.png'),
      ...config
    };

    const window = new BrowserWindow(defaultConfig);
    
    // 加载URL或本地文件
    if (config.url) {
      window.loadURL(config.url);
    } else if (config.file) {
      window.loadFile(config.file);
    } else {
      window.loadFile(path.join(__dirname, 'default-view.html'));
    }

    // 窗口准备就绪后显示
    window.once('ready-to-show', () => {
      window.show();
    });

    // 窗口关闭时清理
    window.on('closed', () => {
      this.windows.delete(windowId);
    });

    this.windows.set(windowId, {
      id: windowId,
      window,
      config: defaultConfig,
      createdAt: new Date()
    });

    return windowId;
  }

  /**
   * 关闭窗口
   * @param {string} windowId - 窗口ID
   * @returns {boolean} 是否成功关闭
   */
  closeWindow(windowId) {
    const windowInfo = this.windows.get(windowId);
    if (windowInfo) {
      windowInfo.window.close();
      this.windows.delete(windowId);
      return true;
    }
    return false;
  }

  /**
   * 获取所有窗口
   * @returns {Array} 窗口列表
   */
  getAllWindows() {
    return Array.from(this.windows.values()).map(info => ({
      id: info.id,
      config: info.config,
      createdAt: info.createdAt,
      isVisible: info.window.isVisible(),
      isFocused: info.window.isFocused()
    }));
  }

  /**
   * 获取窗口
   * @param {string} windowId - 窗口ID
   * @returns {BrowserWindow|null}
   */
  getWindow(windowId) {
    const info = this.windows.get(windowId);
    return info ? info.window : null;
  }

  /**
   * 排列窗口（平铺）
   * @param {number} columns - 列数
   */
  arrangeWindows(columns = 2) {
    const displays = screen.getAllDisplays();
    const primaryDisplay = screen.getPrimaryDisplay();
    const workArea = primaryDisplay.workArea;
    
    const windows = Array.from(this.windows.values());
    const totalWindows = windows.length;
    
    if (totalWindows === 0) return;

    const rows = Math.ceil(totalWindows / columns);
    const windowWidth = Math.floor(workArea.width / columns);
    const windowHeight = Math.floor(workArea.height / rows);

    windows.forEach((windowInfo, index) => {
      const row = Math.floor(index / columns);
      const col = index % columns;
      
      const x = workArea.x + (col * windowWidth);
      const y = workArea.y + (row * windowHeight);
      
      windowInfo.window.setBounds({
        x,
        y,
        width: windowWidth,
        height: windowHeight
      });
    });
  }
}

module.exports = WindowManager;