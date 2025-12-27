const fs = require('fs').promises
const path = require('path')

class TaskManager {
  constructor(configPath = path.join(__dirname, 'task-config.json')) {
    this.configPath = configPath
    this.config = null
  }
  
  async loadConfig() {
    try {
      const data = await fs.readFile(this.configPath, 'utf-8')
      this.config = JSON.parse(data)
      return this.config
    } catch (error) {
      // 如果配置文件不存在，创建默认配置
      const defaultConfig = {
        douyin: {
          tasks: {
            publish_video: {
              description: "发布抖音视频",
              steps: [
                {
                  action: "click",
                  selector: ".publish-btn",
                  fallbackSelectors: ["[data-e2e=\"publish\"]"],
                  description: "点击发布按钮"
                }
              ]
            }
          }
        },
        common: {}
      }
      
      await this.saveConfig(defaultConfig)
      this.config = defaultConfig
      return this.config
    }
  }
  
  async saveConfig(config) {
    this.config = config
    await fs.writeFile(this.configPath, JSON.stringify(config, null, 2), 'utf-8')
  }
  
  getTaskConfig(platform, taskName) {
    if (!this.config) return null
    
    if (platform === 'douyin' && this.config.douyin?.tasks?.[taskName]) {
      return this.config.douyin.tasks[taskName]
    } else if (this.config.common?.[taskName]) {
      return this.config.common[taskName]
    }
    
    return null
  }
  
  addTaskConfig(platform, taskName, config) {
    if (!this.config) this.config = { douyin: { tasks: {} }, common: {} }
    
    if (platform === 'douyin') {
      if (!this.config.douyin) this.config.douyin = { tasks: {} }
      this.config.douyin.tasks[taskName] = config
    } else {
      this.config.common[taskName] = config
    }
    
    return this.saveConfig(this.config)
  }
  
  async getAvailableTasks(url) {
    await this.loadConfig()
    const tasks = []
    
    // 检测是否为抖音
    if (url.includes('douyin.com')) {
      if (this.config.douyin?.tasks) {
        Object.entries(this.config.douyin.tasks).forEach(([name, config]) => {
          tasks.push({
            platform: 'douyin',
            name: name,
            description: config.description
          })
        })
      }
    }
    
    // 添加通用任务
    if (this.config.common) {
      Object.entries(this.config.common).forEach(([name, config]) => {
        tasks.push({
          platform: 'common',
          name: name,
          description: config.description
        })
      })
    }
    
    return tasks
  }
}

// 导出单例
const taskManager = new TaskManager()
module.exports = { TaskManager, taskManager }