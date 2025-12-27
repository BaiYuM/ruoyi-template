package com.ruoyi.system.service.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.config.DesktopAutomationProperties;
import com.ruoyi.system.domain.AutomationConfig;
import com.ruoyi.system.service.IDesktopAutomationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // 桌面应用进程引用
    private Process desktopProcess = null;

    @Autowired
    private DesktopAutomationProperties properties;

    // 桌面应用HTTP服务端口
    private String getDesktopHttpBaseUrl()
    {
        return "http://localhost:" + properties.getHttpPort();
    }

    @Override
    public String startAutomationTask(AutomationConfig config)
    {
        try
        {
            // 检查桌面应用是否已启动
            if (!isDesktopAppRunning())
            {
                log.info("桌面应用未运行，正在启动...");
                startDesktopApp();
                
                // 等待应用启动
                Thread.sleep(5000);
            }

            // 构建自动化任务配置参数
            String taskConfig = buildTaskConfig(config);
            
            log.info("启动自动化任务，配置: {}", taskConfig);
            
            // 通过HTTP API发送任务配置到桌面应用
            String result = sendAutomationTask(taskConfig);
            
            return result;
        }
        catch (Exception e)
        {
            log.error("启动自动化任务失败", e);
            return "启动自动化任务失败: " + e.getMessage();
        }
    }

    @Override
    public String stopAutomationTask()
    {
        try
        {
            log.info("停止自动化任务");
            
            // 通过HTTP API发送停止命令到桌面应用
            String result = sendStopCommand();
            
            return result;
        }
        catch (Exception e)
        {
            log.error("停止自动化任务失败", e);
            return "停止自动化任务失败: " + e.getMessage();
        }
    }

    @Override
    public String getAutomationStatus()
    {
        try
        {
            log.info("获取自动化任务状态");
            
            // 通过HTTP API查询桌面应用状态
            String status = queryAutomationStatus();
            
            return status;
        }
        catch (Exception e)
        {
            log.error("获取自动化状态失败", e);
            return "获取自动化状态失败: " + e.getMessage();
        }
    }

    @Override
    public String startDesktopApp()
    {
        try
        {
            if (isDesktopAppRunning())
            {
                log.info("桌面应用已在运行中");
                return "桌面应用已在运行中";
            }

            log.info("启动桌面应用，路径: {}", properties.getAppPath());
            
            // 构建启动命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(properties.getAppPath()));
            processBuilder.command("npm", "start");
            
            // 设置环境变量
            processBuilder.environment().put("NODE_ENV", "production");
            processBuilder.environment().put("ELECTRON_RUN_AS_NODE", "1"); // 以Node模式运行
            processBuilder.environment().put("DESKTOP_HTTP_PORT", String.valueOf(properties.getHttpPort())); // 设置HTTP端口
            
            // 启动进程
            desktopProcess = processBuilder.start();
            
            // 异步读取进程输出
            CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(desktopProcess.getInputStream())))
                {
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        log.info("桌面应用输出: {}", line);
                    }
                }
                catch (IOException e)
                {
                    log.error("读取桌面应用输出失败", e);
                }
            });
            
            // 异步读取错误输出
            CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(desktopProcess.getErrorStream())))
                {
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        log.error("桌面应用错误: {}", line);
                    }
                }
                catch (IOException e)
                {
                    log.error("读取桌面应用错误输出失败", e);
                }
            });
            
            log.info("桌面应用启动成功");
            return "桌面应用启动成功";
        }
        catch (Exception e)
        {
            log.error("启动桌面应用失败", e);
            desktopProcess = null;
            return "启动桌面应用失败: " + e.getMessage();
        }
    }

    @Override
    public String stopDesktopApp()
    {
        try
        {
            if (desktopProcess != null && desktopProcess.isAlive())
            {
                log.info("关闭桌面应用");
                
                // 销毁进程
                desktopProcess.destroy();
                
                // 等待进程结束
                if (!desktopProcess.waitFor(5, TimeUnit.SECONDS))
                {
                    // 强制销毁
                    desktopProcess.destroyForcibly();
                }
                
                desktopProcess = null;
                
                log.info("桌面应用已关闭");
                return "桌面应用已关闭";
            }
            else
            {
                log.info("桌面应用未运行");
                return "桌面应用未运行";
            }
        }
        catch (Exception e)
        {
            log.error("关闭桌面应用失败", e);
            return "关闭桌面应用失败: " + e.getMessage();
        }
    }

    @Override
    public boolean isDesktopAppRunning()
    {
        return desktopProcess != null && desktopProcess.isAlive();
    }

    /**
     * 构建任务配置参数
     */
    private String buildTaskConfig(AutomationConfig config)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"url\": \"").append(config.getTargetUrl() != null ? config.getTargetUrl() : "http://localhost:8080").append("\",");
        sb.append("\"headless\": ").append(config.getHeadless() != null ? config.getHeadless() : properties.isDefaultHeadless()).append(",");
        sb.append("\"screenshot\": ").append(config.getScreenshot() != null ? config.getScreenshot() : properties.isDefaultScreenshot()).append(",");
        sb.append("\"delay\": ").append(config.getDelayTime() != null ? config.getDelayTime() : properties.getDefaultDelayTime()).append(",");
        sb.append("\"tasks\": []");
        sb.append("}");
        
        return sb.toString();
    }

    /**
     * 通过HTTP API发送自动化任务到桌面应用
     */
    private String sendAutomationTask(String taskConfig)
    {
        try
        {
            URL url = new URL(getDesktopHttpBaseUrl() + "/api/automation/start");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // 发送请求数据
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream()))
            {
                outputStream.writeBytes(taskConfig);
                outputStream.flush();
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseCode >= 200 && responseCode < 300 
                    ? connection.getInputStream() : connection.getErrorStream())))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    response.append(line);
                }
            }

            connection.disconnect();
            
            log.info("发送自动化任务响应: {}", response.toString());
            return response.toString();
        }
        catch (Exception e)
        {
            log.error("发送自动化任务失败", e);
            return "发送自动化任务失败: " + e.getMessage();
        }
    }

    /**
     * 通过HTTP API发送停止命令到桌面应用
     */
    private String sendStopCommand()
    {
        try
        {
            URL url = new URL(getDesktopHttpBaseUrl() + "/api/automation/stop");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // 发送空请求体
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream()))
            {
                outputStream.writeBytes("{}");
                outputStream.flush();
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseCode >= 200 && responseCode < 300 
                    ? connection.getInputStream() : connection.getErrorStream())))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    response.append(line);
                }
            }

            connection.disconnect();
            
            log.info("发送停止命令响应: {}", response.toString());
            return response.toString();
        }
        catch (Exception e)
        {
            log.error("发送停止命令失败", e);
            return "发送停止命令失败: " + e.getMessage();
        }
    }

    /**
     * 通过HTTP API查询自动化状态
     */
    private String queryAutomationStatus()
    {
        try
        {
            URL url = new URL(getDesktopHttpBaseUrl() + "/api/automation/status");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // 读取响应
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseCode >= 200 && responseCode < 300 
                    ? connection.getInputStream() : connection.getErrorStream())))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    response.append(line);
                }
            }

            connection.disconnect();
            
            log.info("查询状态响应: {}", response.toString());
            return response.toString();
        }
        catch (Exception e)
        {
            log.error("查询状态失败", e);
            return "查询状态失败: " + e.getMessage();
        }
    }
}