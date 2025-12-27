package com.ruoyi.system.service.impl;

import com.ruoyi.system.config.DesktopAutomationProperties;
import com.ruoyi.system.service.IDesktopAutomationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 桌面自动化服务实现
 *
 * @author ruoyi
 * @date 2025-12-26
 */
@Service
public class DesktopAutomationServiceImpl implements IDesktopAutomationService
{
    private static final Logger log = LoggerFactory.getLogger(DesktopAutomationServiceImpl.class);
    
    /** 最大等待时间(秒) */
    private static final int MAX_WAIT_SECONDS = 30;
    
    /** 轮询间隔(毫秒) */
    private static final int POLL_INTERVAL_MS = 500;

    @Autowired
    private DesktopAutomationProperties automationProperties;

    private Process desktopProcess;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean httpServerReady = new AtomicBoolean(false);

    @Override
    public boolean startDesktopApp()
    {
        // 检查是否已运行且HTTP服务可用
        if (isHttpServerAvailable())
        {
            log.info("桌面自动化应用已在运行且HTTP服务可用");
            return true;
        }

        // 如果进程还在但HTTP不可用，先停止
        if (desktopProcess != null && desktopProcess.isAlive())
        {
            log.warn("进程存在但HTTP服务不可用，重新启动");
            stopDesktopApp();
        }

        try
        {
            String appPath = automationProperties.getAppPath();
            log.info("启动桌面自动化应用，路径: {}", appPath);

            // 根据操作系统选择正确的命令
            ProcessBuilder processBuilder;
            if (System.getProperty("os.name").toLowerCase().contains("win"))
            {
                processBuilder = new ProcessBuilder(
                    "cmd", "/c", 
                    "cd /d " + appPath + " && set ELECTRON=true && npx electron ."
                );
            }
            else
            {
                processBuilder = new ProcessBuilder(
                    "sh", "-c", 
                    "cd " + appPath + " && ELECTRON=true npx electron ."
                );
            }
            
            processBuilder.redirectErrorStream(true);
            httpServerReady.set(false);
            
            desktopProcess = processBuilder.start();
            isRunning.set(true);
            
            log.info("桌面应用进程已启动，PID: {}", desktopProcess.pid());

            // 异步读取进程输出
            startOutputReader();

            // 等待HTTP服务就绪
            boolean ready = waitForHttpServer();
            
            if (ready)
            {
                log.info("桌面自动化应用启动成功，HTTP服务已就绪");
                return true;
            }
            else
            {
                log.error("桌面应用启动超时，HTTP服务未就绪");
                return false;
            }
        }
        catch (Exception e)
        {
            log.error("启动桌面自动化应用失败", e);
            isRunning.set(false);
            return false;
        }
    }
    
    /**
     * 启动输出读取线程
     */
    private void startOutputReader()
    {
        CompletableFuture.runAsync(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(desktopProcess.getInputStream())))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    log.info("[Electron] {}", line);
                    
                    // 检测HTTP服务启动成功
                    if (line.contains("9876") || line.contains("服务器运行在端口"))
                    {
                        httpServerReady.set(true);
                    }
                }
            }
            catch (IOException e)
            {
                log.error("读取Electron输出失败", e);
            }
            finally
            {
                isRunning.set(false);
                httpServerReady.set(false);
                log.warn("Electron进程已结束");
            }
        });
    }
    
    /**
     * 等待HTTP服务就绪
     */
    private boolean waitForHttpServer()
    {
        long startTime = System.currentTimeMillis();
        long maxWaitMs = MAX_WAIT_SECONDS * 1000L;
        
        while (System.currentTimeMillis() - startTime < maxWaitMs)
        {
            // 检查进程是否还在运行
            if (desktopProcess != null && !desktopProcess.isAlive())
            {
                log.error("桌面应用进程意外结束");
                return false;
            }
            
            // 检查HTTP服务是否可用
            if (isHttpServerAvailable())
            {
                httpServerReady.set(true);
                return true;
            }
            
            try
            {
                TimeUnit.MILLISECONDS.sleep(POLL_INTERVAL_MS);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * 检查HTTP服务是否可用
     */
    private boolean isHttpServerAvailable()
    {
        try
        {
            URL url = new URL(String.format("http://localhost:%d/api/automation/status", 
                automationProperties.getHttpPort()));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode == 200;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    @Override
    public boolean stopDesktopApp()
    {
        if (desktopProcess == null)
        {
            log.info("桌面自动化应用未在运行");
            isRunning.set(false);
            return true;
        }

        try
        {
            log.info("正在停止桌面自动化应用");
            
            desktopProcess.destroy();
            
            if (desktopProcess.waitFor(5, TimeUnit.SECONDS))
            {
                log.info("桌面自动化应用已停止");
            }
            else
            {
                desktopProcess.destroyForcibly();
                log.info("桌面自动化应用已强制停止");
            }

            isRunning.set(false);
            httpServerReady.set(false);
            desktopProcess = null;

            return true;
        }
        catch (Exception e)
        {
            log.error("停止桌面自动化应用失败", e);
            return false;
        }
    }

    @Override
    public boolean isDesktopAppRunning()
    {
        return isRunning.get() && desktopProcess != null && desktopProcess.isAlive() && isHttpServerAvailable();
    }

    @Override
    public boolean restartDesktopApp()
    {
        log.info("重启桌面自动化应用");
        stopDesktopApp();
        
        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        
        return startDesktopApp();
    }
}