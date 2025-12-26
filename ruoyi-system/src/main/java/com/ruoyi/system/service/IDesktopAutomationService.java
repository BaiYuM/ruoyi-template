package com.ruoyi.system.service;

import com.ruoyi.system.domain.AutomationConfig;

/**
 * 桌面自动化服务接口
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
public interface IDesktopAutomationService
{
    /**
     * 启动桌面自动化任务
     * 
     * @param config 自动化配置
     * @return 任务执行结果
     */
    public String startAutomationTask(AutomationConfig config);

    /**
     * 停止自动化任务
     * 
     * @return 操作结果
     */
    public String stopAutomationTask();

    /**
     * 获取自动化任务状态
     * 
     * @return 任务状态信息
     */
    public String getAutomationStatus();

    /**
     * 启动桌面应用
     * 
     * @return 操作结果
     */
    public String startDesktopApp();

    /**
     * 关闭桌面应用
     * 
     * @return 操作结果
     */
    public String stopDesktopApp();

    /**
     * 检查桌面应用是否运行
     * 
     * @return 是否运行
     */
    public boolean isDesktopAppRunning();
}