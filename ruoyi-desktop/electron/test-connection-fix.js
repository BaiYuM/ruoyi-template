const ConnectionManager = require('./connection-manager');

async function test() {
  console.log('=== 测试连接管理器修复 ===');
  
  const cm = new ConnectionManager();
  
  // 测试1: 扫描浏览器
  console.log('\n1. 扫描浏览器...');
  const browsers = await cm.scanForBrowsers();
  console.log(`找到 ${browsers.length} 个浏览器`);
  
  if (browsers.length > 0) {
    console.log('浏览器详情:');
    browsers.forEach((browser, i) => {
      console.log(`  ${i+1}. 端口: ${browser.port}, 页面: ${browser.pages.length}, WebSocket: ${browser.webSocketDebuggerUrl}`);
    });
    
    // 测试2: 连接到第一个浏览器
    console.log('\n2. 连接到第一个浏览器...');
    const connectResult = await cm.connectToBrowser({
      webSocketDebuggerUrl: browsers[0].webSocketDebuggerUrl
    });
    
    if (connectResult.success) {
      console.log(`连接成功: ${connectResult.connectionId}`);
      console.log(`页面数: ${connectResult.browser.pages().length}`);
      
      // 断开连接
      await cm.disconnectBrowser(connectResult.connectionId);
      console.log('已断开连接');
    } else {
      console.log(`连接失败: ${connectResult.error}`);
    }
  }
  
  // 测试3: 启动新浏览器（可选）
  console.log('\n3. 启动新Chrome实例...');
  const launchResult = await cm.launchChromeWithDebugging({
    chromePath: 'D:\\Google\\Chrome\\Application\\chrome.exe',
    url: 'https://www.douyin.com',
    port: 9222
  });
  
  if (launchResult.success) {
    console.log(`启动成功，PID: ${launchResult.pid}, 端口: ${launchResult.port}`);
  } else {
    console.log(`启动失败: ${launchResult.error}`);
  }
  
  console.log('\n=== 测试完成 ===');
}

test().catch(console.error);