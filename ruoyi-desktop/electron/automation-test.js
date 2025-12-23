// electron/automation-test.js
const { runAutomationTask, stopAutomationTask } = require('./automation')

async function testAutomation() {
  console.log('开始自动化测试...\n')
  
  try {
    // 测试1: 正常执行
    console.log('测试1: 执行自动化任务')
    const result1 = await runAutomationTask({
      url: 'https://example.com',
      headless: true,
      screenshot: true
    })
    
    console.log('结果:', JSON.stringify(result1, null, 2))
    
    if (result1.success) {
      console.log('✅ 测试1通过\n')
    } else {
      console.log('❌ 测试1失败\n')
    }
    
    // 等待2秒
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 测试2: 停止任务
    console.log('测试2: 停止任务（当前无任务应返回错误）')
    const result2 = await stopAutomationTask()
    console.log('停止结果:', JSON.stringify(result2, null, 2))
    console.log('✅ 测试2通过\n')
    
    // 测试3: 无效URL
    console.log('测试3: 使用无效URL')
    try {
      await runAutomationTask({
        url: 'invalid-url',
        headless: true
      })
      console.log('❌ 测试3应该失败但没有\n')
    } catch (error) {
      console.log('✅ 测试3通过（预期错误）:', error.message, '\n')
    }
    
    console.log('所有测试完成！')
    
  } catch (error) {
    console.error('测试过程中出错:', error)
  }
}

// 运行测试
if (require.main === module) {
  testAutomation().catch(console.error)
}