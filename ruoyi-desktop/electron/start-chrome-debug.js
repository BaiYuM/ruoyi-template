const { spawn } = require('child_process');
const path = require('path');

/**
 * 启动带调试模式的Chrome
 */
function startChromeWithDebugging(options = {}) {
  const chromePath = options.chromePath || 'D:\\Google\\Chrome\\Application\\chrome.exe';
  const port = options.port || 9222;
  const userDataDir = options.userDataDir || path.join(__dirname, '../chrome_profile');
  const url = options.url || 'https://www.douyin.com';
  
  const args = [
    `--remote-debugging-port=${port}`,
    `--user-data-dir="${userDataDir}"`,
    '--no-first-run',
    '--no-default-browser-check',
    '--disable-infobars',
    '--disable-notifications',
    '--disable-popup-blocking',
    '--start-maximized'
  ];
  
  if (url) {
    args.push(url);
  }
  
  console.log(`正在启动 Chrome (端口: ${port})...`);
  console.log(`用户数据目录: ${userDataDir}`);
  
  const chromeProcess = spawn(chromePath, args, {
    detached: true,
    stdio: 'ignore'
  });
  
  chromeProcess.unref();
  
  console.log(`Chrome 已启动，PID: ${chromeProcess.pid}`);
  console.log(`调试地址: http://localhost:${port}`);
  console.log('等待 5 秒让 Chrome 完全启动...');
  
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        pid: chromeProcess.pid,
        port,
        userDataDir
      });
    }, 5000);
  });
}

/**
 * 停止Chrome进程
 */
function stopChrome(pid) {
  try {
    process.kill(pid);
    console.log(`已停止进程: ${pid}`);
    return true;
  } catch (error) {
    console.error(`停止进程失败: ${error.message}`);
    return false;
  }
}

// 命令行接口
if (require.main === module) {
  const command = process.argv[2];
  
  switch (command) {
    case 'start':
      startChromeWithDebugging({
        port: process.argv[3] || 9222,
        url: process.argv[4] || 'https://www.douyin.com'
      }).then(result => {
        console.log('启动完成！');
        console.log('你可以通过以下方式连接:');
        console.log(`1. 浏览器访问: http://localhost:${result.port}`);
        console.log(`2. 在控制面板中连接端口: ${result.port}`);
      });
      break;
      
    case 'stop':
      const pid = parseInt(process.argv[3]);
      if (pid) {
        stopChrome(pid);
      } else {
        console.log('请提供进程ID');
      }
      break;
      
    default:
      console.log('使用方法:');
      console.log('  node start-chrome-debug.js start [端口] [URL]');
      console.log('  node start-chrome-debug.js stop [进程ID]');
      console.log('');
      console.log('示例:');
      console.log('  node start-chrome-debug.js start 9222 https://www.douyin.com');
      console.log('  node start-chrome-debug.js stop 12345');
      break;
  }
}

module.exports = {
  startChromeWithDebugging,
  stopChrome
};