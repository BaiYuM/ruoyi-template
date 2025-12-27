package com.ruoyi.system.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.system.domain.DesktopAutomationTask;
import com.ruoyi.system.service.ITaskConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 任务配置服务实现
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
@Service
public class TaskConfigServiceImpl implements ITaskConfigService
{
    private static final Logger log = LoggerFactory.getLogger(TaskConfigServiceImpl.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 从JSON文件读取任务配置
     * 
     * @param filePath 配置文件路径
     * @return 任务配置列表
     */
    @Override
    public List<DesktopAutomationTask> loadTasksFromJson(String filePath)
    {
        try
        {
            File configFile = new File(filePath);
            if (!configFile.exists())
            {
                log.error("配置文件不存在: {}", filePath);
                return null;
            }

            List<DesktopAutomationTask> tasks = objectMapper.readValue(configFile, 
                new TypeReference<List<DesktopAutomationTask>>() {});

            log.info("成功从 {} 读取了 {} 个任务配置", filePath, tasks.size());
            return tasks;
        }
        catch (IOException e)
        {
            log.error("读取任务配置文件失败: {}", filePath, e);
            return null;
        }
    }

    /**
     * 获取默认任务配置文件路径
     * 
     * @return 默认配置文件路径
     */
    @Override
    public String getDefaultConfigPath()
    {
        return System.getProperty("user.dir") + "/ruoyi-desktop/tasks-config.json";
    }

    /**
     * 获取启用的任务
     * 
     * @param filePath 配置文件路径
     * @return 启用的任务列表
     */
    @Override
    public List<DesktopAutomationTask> getEnabledTasks(String filePath)
    {
        List<DesktopAutomationTask> allTasks = loadTasksFromJson(filePath);
        if (allTasks == null)
        {
            return null;
        }

        return allTasks.stream()
            .filter(DesktopAutomationTask::isEnabled)
            .toList();
    }
}