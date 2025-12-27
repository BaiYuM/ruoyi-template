const EventEmitter = require('events');
const fs = require('fs').promises;
const path = require('path');

class TaskManager extends EventEmitter {
  constructor() {
    super();
    this.tasks = new Map();
    this.taskCounter = 0;
    this.taskConfigPath = path.join(__dirname, '../task-config.json');
    this.init();
  }

  async init() {
    try {
      await this.loadTaskConfig();
    } catch (error) {
      console.warn('Failed to load task config:', error.message);
      this.saveTaskConfig(); // 创建默认配置文件
    }
  }

  /**
   * 加载任务配置
   */
  async loadTaskConfig() {
    const data = await fs.readFile(this.taskConfigPath, 'utf8');
    const config = JSON.parse(data);
    
    if (config.tasks && Array.isArray(config.tasks)) {
      config.tasks.forEach(task => {
        this.tasks.set(task.id, {
          ...task,
          status: 'stopped',
          progress: 0,
          logs: []
        });
      });
    }
  }

  /**
   * 保存任务配置
   */
  async saveTaskConfig() {
    const tasks = Array.from(this.tasks.values()).map(task => ({
      id: task.id,
      name: task.name,
      type: task.type,
      script: task.script,
      schedule: task.schedule,
      config: task.config
    }));

    const config = {
      version: '1.0.0',
      tasks,
      updatedAt: new Date().toISOString()
    };

    await fs.writeFile(
      this.taskConfigPath,
      JSON.stringify(config, null, 2),
      'utf8'
    );
  }

  /**
   * 启动任务
   * @param {Object} taskConfig - 任务配置
   * @returns {string} 任务ID
   */
  async startTask(taskConfig) {
    const taskId = taskConfig.id || `task_${++this.taskCounter}`;
    
    const task = {
      id: taskId,
      name: taskConfig.name || `任务 ${taskId}`,
      type: taskConfig.type || 'automation',
      script: taskConfig.script || '',
      schedule: taskConfig.schedule || null,
      config: taskConfig.config || {},
      status: 'running',
      progress: 0,
      startTime: new Date(),
      logs: [],
      interval: null
    };

    // 根据任务类型执行
    switch (task.type) {
      case 'automation':
        await this.startAutomationTask(task);
        break;
      case 'monitoring':
        await this.startMonitoringTask(task);
        break;
      case 'data-collection':
        await this.startDataCollectionTask(task);
        break;
      default:
        throw new Error(`Unknown task type: ${task.type}`);
    }

    this.tasks.set(taskId, task);
    this.emit('task:started', task);
    await this.saveTaskConfig();
    
    return taskId;
  }

  /**
   * 启动自动化任务
   * @param {Object} task - 任务对象
   */
  async startAutomationTask(task) {
    task.logs.push({
      time: new Date(),
      level: 'info',
      message: '开始执行自动化任务'
    });

    // 模拟任务执行
    task.interval = setInterval(() => {
      task.progress += 10;
      if (task.progress >= 100) {
        task.progress = 100;
        task.status = 'completed';
        task.endTime = new Date();
        
        task.logs.push({
          time: new Date(),
          level: 'info',
          message: '自动化任务执行完成'
        });

        clearInterval(task.interval);
        this.emit('task:completed', task);
      } else {
        task.logs.push({
          time: new Date(),
          level: 'info',
          message: `任务进度: ${task.progress}%`
        });
        this.emit('task:progress', task);
      }
    }, 1000);
  }

  /**
   * 启动监控任务
   * @param {Object} task - 任务对象
   */
  async startMonitoringTask(task) {
    // 监控任务实现
    console.log('Starting monitoring task:', task.id);
  }

  /**
   * 启动数据收集任务
   * @param {Object} task - 任务对象
   */
  async startDataCollectionTask(task) {
    // 数据收集任务实现
    console.log('Starting data collection task:', task.id);
  }

  /**
   * 停止任务
   * @param {string} taskId - 任务ID
   * @returns {boolean} 是否成功停止
   */
  stopTask(taskId) {
    const task = this.tasks.get(taskId);
    if (!task) {
      return false;
    }

    if (task.interval) {
      clearInterval(task.interval);
    }

    task.status = 'stopped';
    task.endTime = new Date();
    
    task.logs.push({
      time: new Date(),
      level: 'info',
      message: '任务已停止'
    });

    this.emit('task:stopped', task);
    this.saveTaskConfig();
    
    return true;
  }

  /**
   * 获取所有任务
   * @returns {Array} 任务列表
   */
  getTasks() {
    return Array.from(this.tasks.values()).map(task => ({
      id: task.id,
      name: task.name,
      type: task.type,
      status: task.status,
      progress: task.progress,
      startTime: task.startTime,
      endTime: task.endTime,
      logCount: task.logs.length
    }));
  }

  /**
   * 获取任务详情
   * @param {string} taskId - 任务ID
   * @returns {Object|null} 任务详情
   */
  getTaskDetails(taskId) {
    const task = this.tasks.get(taskId);
    if (!task) return null;

    return {
      ...task,
      logs: [...task.logs] // 返回副本
    };
  }

  /**
   * 删除任务
   * @param {string} taskId - 任务ID
   * @returns {boolean} 是否成功删除
   */
  deleteTask(taskId) {
    const task = this.tasks.get(taskId);
    if (task) {
      if (task.status === 'running') {
        this.stopTask(taskId);
      }
      this.tasks.delete(taskId);
      this.saveTaskConfig();
      return true;
    }
    return false;
  }
}

module.exports = TaskManager;