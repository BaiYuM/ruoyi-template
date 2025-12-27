package com.ruoyi.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 桌面自动化配置属性
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
@Component
@ConfigurationProperties(prefix = "desktop.automation")
public class DesktopAutomationProperties
{
    /**
     * 桌面应用路径
     */
    private String appPath = System.getProperty("user.dir") + "/ruoyi-desktop";

    /**
     * HTTP服务端口
     */
    private int httpPort = 9876;

    /**
     * 是否启用无头模式（默认）
     */
    private boolean defaultHeadless = false;

    /**
     * 是否启用截图（默认）
     */
    private boolean defaultScreenshot = true;

    /**
     * 默认延迟时间（毫秒）
     */
    private long defaultDelayTime = 1000;

    /**
     * 最大重试次数
     */
    private int maxRetryAttempts = 3;

    /**
     * 任务超时时间（毫秒）
     */
    private long taskTimeout = 300000; // 5分钟

    public String getAppPath()
    {
        return appPath;
    }

    public void setAppPath(String appPath)
    {
        this.appPath = appPath;
    }

    public int getHttpPort()
    {
        return httpPort;
    }

    public void setHttpPort(int httpPort)
    {
        this.httpPort = httpPort;
    }

    public boolean isDefaultHeadless()
    {
        return defaultHeadless;
    }

    public void setDefaultHeadless(boolean defaultHeadless)
    {
        this.defaultHeadless = defaultHeadless;
    }

    public boolean isDefaultScreenshot()
    {
        return defaultScreenshot;
    }

    public void setDefaultScreenshot(boolean defaultScreenshot)
    {
        this.defaultScreenshot = defaultScreenshot;
    }

    public long getDefaultDelayTime()
    {
        return defaultDelayTime;
    }

    public void setDefaultDelayTime(long defaultDelayTime)
    {
        this.defaultDelayTime = defaultDelayTime;
    }

    public int getMaxRetryAttempts()
    {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts)
    {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public long getTaskTimeout()
    {
        return taskTimeout;
    }

    public void setTaskTimeout(long taskTimeout)
    {
        this.taskTimeout = taskTimeout;
    }
}