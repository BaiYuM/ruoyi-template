package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.system.config.DesktopAutomationProperties;
import com.ruoyi.system.domain.DesktopAutomationTask;
import com.ruoyi.system.service.IDesktopAutomationService;
import com.ruoyi.system.service.ITaskConfigService;
import com.ruoyi.system.service.ITaskExecutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务执行服务实现
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
@Service
public class TaskExecutionServiceImpl implements ITaskExecutionService
{
    private static final Logger log = LoggerFactory.getLogger(TaskExecutionServiceImpl.class);

    @Autowired
    private DesktopAutomationProperties automationProperties;
    
    @Autowired
    private ITaskConfigService taskConfigService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private IDesktopAutomationService desktopAutomationService;

    /**
     * 执行指定的任务
     */
    @Override
    public boolean executeTask(DesktopAutomationTask task, String url)
    {
        try
        {
            String apiUrl = String.format("http://localhost:%d/api/automation/start", 
                automationProperties.getHttpPort());

            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("url", url);
            requestParams.put("headless", automationProperties.isDefaultHeadless());
            requestParams.put("screenshot", automationProperties.isDefaultScreenshot());
            requestParams.put("delay", automationProperties.getDefaultDelayTime());
            requestParams.put("tasks", task.getTasks());

            log.info("执行任务: {} -> {}", task.getName(), url);

            String jsonParams = objectMapper.writeValueAsString(requestParams);
            log.debug("请求参数: {}", jsonParams);
            
            String response = HttpUtils.sendPost(apiUrl, jsonParams);
            log.info("响应结果: {}", response);

            return response != null && response.contains("\"success\":true");
        }
        catch (JsonProcessingException e)
        {
            log.error("转换JSON失败: {}", e.getMessage());
            return false;
        }
        catch (Exception e)
        {
            log.error("执行任务失败: {}", task.getName(), e);
            return false;
        }
    }

    /**
     * 执行登录任务
     */
    @Override
    public boolean executeLoginTask(String url)
    {
        return executeTaskById("login_task", url);
    }
    
    /**
     * 根据ID执行任务
     */
    @Override
    public boolean executeTaskById(String taskId, String url)
    {
        List<DesktopAutomationTask> allTasks = loadTasks();
        
        if (allTasks == null || allTasks.isEmpty())
        {
            return false;
        }
        
        for (DesktopAutomationTask task : allTasks)
        {
            if (taskId.equals(task.getId()) && task.isEnabled())
            {
                return executeTask(task, url);
            }
        }
        
        log.warn("未找到ID为 [{}] 的启用任务", taskId);
        return false;
    }
    
    /**
     * 根据名称执行任务
     */
    @Override
    public boolean executeTaskByName(String taskName, String url, String configPath)
    {
        List<DesktopAutomationTask> allTasks = loadTasks();
        
        if (allTasks == null || allTasks.isEmpty())
        {
            return false;
        }
        
        for (DesktopAutomationTask task : allTasks)
        {
            if (taskName.equals(task.getName()) && task.isEnabled())
            {
                return executeTask(task, url);
            }
        }
        
        log.warn("未找到名称为 [{}] 的启用任务", taskName);
        return false;
    }

    /**
     * 启动桌面应用
     */
    @Override
    public boolean startDesktopApp()
    {
        return desktopAutomationService.startDesktopApp();
    }
    
    /**
     * 加载任务配置
     */
    private List<DesktopAutomationTask> loadTasks()
    {
        String configPath = taskConfigService.getDefaultConfigPath();
        List<DesktopAutomationTask> tasks = taskConfigService.loadTasksFromJson(configPath);
        
        if (tasks == null || tasks.isEmpty())
        {
            log.error("无法加载任务配置: {}", configPath);
        }
        
        return tasks;
    }
}