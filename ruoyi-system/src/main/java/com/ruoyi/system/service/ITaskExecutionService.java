package com.ruoyi.system.service;

import com.ruoyi.system.domain.DesktopAutomationTask;

/**
 * 任务执行服务接口
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
public interface ITaskExecutionService
{
    /**
     * 执行指定的任务
     * 
     * @param task 任务配置
     * @param url 目标URL
     * @return 执行结果
     */
    public boolean executeTask(DesktopAutomationTask task, String url);

    /**
     * 执行登录任务
     * 
     * @param url 目标URL
     * @return 执行结果
     */
    public boolean executeLoginTask(String url);
    
    /**
     * 执行指定ID的任务
     * 
     * @param taskId 任务ID
     * @param url 目标URL
     * @return 执行结果
     */
    public boolean executeTaskById(String taskId, String url);
    
    /**
     * 启动桌面自动化应用
     * 
     * @return 操作结果
     */
    public boolean startDesktopApp();
    
    /**
     * 根据任务名称执行任务
     * 
     * @param taskName 任务名称
     * @param url 目标URL
     * @param configPath 配置文件路径
     * @return 执行结果
     */
    public boolean executeTaskByName(String taskName, String url, String configPath);
}