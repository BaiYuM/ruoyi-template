package com.ruoyi.system.service;

import com.ruoyi.system.domain.DesktopAutomationTask;
import java.util.List;

/**
 * 任务配置服务接口
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
public interface ITaskConfigService
{
    /**
     * 从JSON文件读取任务配置
     * 
     * @param filePath 配置文件路径
     * @return 任务配置列表
     */
    public List<DesktopAutomationTask> loadTasksFromJson(String filePath);

    /**
     * 获取默认任务配置文件路径
     * 
     * @return 默认配置文件路径
     */
    public String getDefaultConfigPath();

    /**
     * 获取启用的任务
     * 
     * @param filePath 配置文件路径
     * @return 启用的任务列表
     */
    public List<DesktopAutomationTask> getEnabledTasks(String filePath);
}