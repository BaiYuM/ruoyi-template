const SimpleAutomation = require('./simple-automation')

async function testAutomation() {
  console.log('开始测试自动化系统...')
  
  const automation = new SimpleAutomation()
  
  try {
    // 启动测试任务
    const result = await automation.startTask({
      url: 'http://localhost:80',
      headless: false,
      screenshot: true,
      tasks: [
        {
          selector: 'input[type="text"]:first-of-type',
          description: '测试输入框',
          action: 'type',
          value: '自动化测试',
          required: false,
          delay: 1000
        },
        {
          selector: 'button:first-of-type',
          description: '测试按钮',
          action: 'click',
          required: false,
          delay: 2000
        }
      ]
    })
    
    console.log('测试结果:', result)
    
    // 等待任务完成
    setTimeout(async () => {
      const status = automation.getTaskStatus()
      console.log('任务状态:', status)
      
      // 停止任务
      await automation.stopTask()
      console.log('测试完成')
      
      process.exit(0)
    }, 10000)
    
  } catch (error) {
    console.error('测试失败:', error)
    process.exit(1)
  }
}

// 运行测试
testAutomation().catch(console.error)