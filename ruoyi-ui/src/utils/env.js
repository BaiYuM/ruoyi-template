/**
 * 环境判断工具 - 统一管理所有环境相关逻辑
 */

// 版本信息
const VERSION = '1.0.0'

/**
 * 判断是否在 Electron 环境中（主要判断方法）
 * @returns {boolean}
 */
export const isElectron = () => {
  // 顺序很重要：从最可靠到最不可靠
  
  // 1. 检查 process 对象（最可靠）
  if (typeof window !== 'undefined' && window.process && window.process.versions) {
    if (window.process.versions.electron) return true
    if (window.process.type === 'renderer') return true
  }
  
  // 2. 检查 appEnv（通过 contextBridge 暴露）
  if (typeof window !== 'undefined' && window.appEnv && window.appEnv.isElectron) {
    return true
  }
  
  // 3. 检查 electronAPI（通过 contextBridge 暴露）
  if (typeof window !== 'undefined' && window.electronAPI) {
    return true
  }
  
  // 4. 检查自动化API
  if (typeof window !== 'undefined' && window.automation) {
    return true
  }
  
  // 5. 检查环境变量（由 Vite 构建时注入）
  if (typeof import.meta !== 'undefined' && import.meta.env) {
    if (import.meta.env.ELECTRON === 'true') return true
  }
  
  // 6. 检查 require（Electron 特有）
  if (typeof window !== 'undefined' && window.require && typeof window.require === 'function') {
    try {
      // 尝试 require electron，如果成功则是 Electron 环境
      window.require('electron')
      return true
    } catch (e) {
      // 不是 Electron 环境
    }
  }
  
  return false
}

/**
 * 获取详细的运行环境信息
 * @returns {Object}
 */
export const getEnvInfo = () => {
  const inElectron = isElectron()
  
  const baseInfo = {
    // 基础信息
    version: VERSION,
    isElectron: inElectron,
    isDevelopment: import.meta.env.MODE === 'development',
    isProduction: import.meta.env.MODE === 'production',
    mode: import.meta.env.MODE,
    
    // 应用配置
    baseApi: import.meta.env.VITE_APP_BASE_API,
    appTitle: import.meta.env.VITE_APP_TITLE || 'RuoYi管理系统',
    
    // 浏览器信息
    userAgent: navigator.userAgent,
    platform: navigator.platform,
    language: navigator.language,
    
    // Electron 特有信息
    electronInfo: null,
    windowInfo: null
  }
  
  if (inElectron) {
    // 获取 Electron 特有信息
    if (window.appEnv && typeof window.appEnv.getEnvironmentInfo === 'function') {
      baseInfo.electronInfo = window.appEnv.getEnvironmentInfo()
    } else {
      baseInfo.electronInfo = {
        platform: window.appEnv?.platform || 'unknown',
        nodeVersion: window.appEnv?.nodeVersion || 'unknown',
        electronVersion: window.appEnv?.electronVersion || 'unknown'
      }
    }
    
    // 窗口信息
    baseInfo.windowInfo = {
      type: window.appEnv?.windowType || 'main',
      isAutomationWindow: window.appEnv?.windowType === 'automation'
    }
  }
  
  return baseInfo
}

/**
 * 安全地获取 Electron API
 * @returns {Object|null} 返回 electronAPI 或 null
 */
export const getElectronAPI = () => {
  if (isElectron() && window.electronAPI) {
    return window.electronAPI
  }
  return null
}

/**
 * 安全地获取自动化 API
 * @returns {Object|null} 返回 automation API 或 null
 */
export const getAutomationAPI = () => {
  if (isElectron() && window.automation) {
    return window.automation
  }
  return null
}

/**
 * 运行在 Electron 环境中的代码
 * @param {Function} electronCallback - 在 Electron 中执行的函数
 * @param {Function} [fallback] - 非 Electron 环境中的备用函数
 * @returns {*} 回调函数的返回值或 null
 */
export const runInElectron = (electronCallback, fallback) => {
  if (isElectron()) {
    const api = getElectronAPI()
    const automation = getAutomationAPI()
    const env = getEnvInfo()
    
    try {
      return electronCallback({ api, automation, env })
    } catch (error) {
      console.error('Electron 回调执行失败:', error)
      return null
    }
  } else if (fallback) {
    return fallback()
  }
  return null
}

/**
 * 初始化环境相关功能
 * @returns {Object} 初始化结果
 */
export const initEnv = () => {
  const envInfo = getEnvInfo()
  
  console.log('=== 环境初始化 ===')
  console.log('环境信息:', envInfo)
  
  // 在 Electron 环境中执行特殊初始化
  if (envInfo.isElectron) {
    console.log('Electron 环境检测成功')
    
    // 设置全局变量供其他模块使用
    if (typeof window !== 'undefined') {
      window.$env = envInfo
      window.$isElectron = true
    }
    
    // 获取应用信息
    runInElectron(({ api }) => {
      if (api && api.getAppInfo) {
        api.getAppInfo().then(info => {
          console.log('应用信息:', info)
          if (window.$env) {
            window.$env.appInfo = info
          }
        }).catch(err => {
          console.warn('获取应用信息失败:', err)
        })
      }
    })
  } else {
    console.log('非 Electron 环境（浏览器模式）')
  }
  
  return envInfo
}

/**
 * 检查特定功能是否可用
 * @param {string} feature - 功能名称
 * @returns {boolean}
 */
export const isFeatureAvailable = (feature) => {
  if (!isElectron()) {
    return false
  }
  
  const featureChecks = {
    // 自动化功能
    automation: () => !!getAutomationAPI(),
    
    // 文件系统
    fileSystem: () => {
      const api = getElectronAPI()
      return !!(api && (api.openFileDialog || api.saveFileDialog))
    },
    
    // 系统通知
    notifications: () => {
      const api = getElectronAPI()
      return !!(api && api.sendNotification)
    },
    
    // 窗口控制
    windowControl: () => {
      const api = getElectronAPI()
      return !!(api && api.minimizeWindow && api.maximizeWindow && api.closeWindow)
    },
    
    // 外部链接
    externalLinks: () => {
      const api = getElectronAPI()
      return !!(api && api.openExternal)
    }
  }
  
  const check = featureChecks[feature]
  return check ? check() : false
}

// 默认导出
export default {
  VERSION,
  isElectron,
  getEnvInfo,
  getElectronAPI,
  getAutomationAPI,
  runInElectron,
  initEnv,
  isFeatureAvailable
}