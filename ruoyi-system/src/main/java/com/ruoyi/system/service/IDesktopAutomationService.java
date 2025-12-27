package com.ruoyi.system.service;

/**
 * 桌面自动化服务接口
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
public interface IDesktopAutomationService
{
    /**
     * 启动桌面自动化应用
     * 
     * @return 操作结果
     */
    public boolean startDesktopApp();

    /**
     * 停止桌面自动化应用
     * 
     * @return 操作结果
     */
    public boolean stopDesktopApp();

    /**
     * 检查桌面应用是否正在运行
     * 
     * @return 是否运行中
     */
    public boolean isDesktopAppRunning();

    /**
     * 重启桌面自动化应用
     * 
     * @return 操作结果
     */
    public boolean restartDesktopApp();
}