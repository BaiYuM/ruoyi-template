package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.DesktopAutomationTask;
import com.ruoyi.system.service.ITaskExecutionService;
import com.ruoyi.system.service.ITaskConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务执行控制器
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
@RestController
@RequestMapping("/system/task-execution")
public class TaskExecutionController
{
    @Autowired
    private ITaskExecutionService taskExecutionService;
    
    @Autowired
    private ITaskConfigService taskConfigService;

    /**
     * 获取可用任务列表
     */
    @GetMapping("/tasks")
    public AjaxResult getTasks()
    {
        String configPath = taskConfigService.getDefaultConfigPath();
        List<DesktopAutomationTask> tasks = taskConfigService.getEnabledTasks(configPath);
        
        if (tasks == null)
        {
            return AjaxResult.error("无法加载任务配置");
        }
        
        // 转换为前端友好的格式
        List<Map<String, Object>> taskList = new ArrayList<>();
        for (DesktopAutomationTask task : tasks)
        {
            Map<String, Object> taskInfo = new HashMap<>();
            taskInfo.put("id", task.getId());
            taskInfo.put("name", task.getName());
            taskInfo.put("description", task.getDescription());
            taskInfo.put("enabled", task.isEnabled());
            taskInfo.put("taskCount", task.getTasks() != null ? task.getTasks().size() : 0);
            taskList.add(taskInfo);
        }
        
        return AjaxResult.success(taskList);
    }
    
    /**
     * 一键启动并执行任务（推荐使用）
     * 自动启动桌面应用并执行指定任务，支持智能匹配任务ID或名称
     * 
     * @param request 任务执行请求
     * @return 执行结果
     */
    @PostMapping("/execute")
    public AjaxResult startAndExecute(@RequestBody TaskExecuteRequest request)
    {
        String taskIdentifier = request.getTaskIdentifier();
        String url = request.getUrl();
        
        if (taskIdentifier == null || taskIdentifier.isEmpty())
        {
            return AjaxResult.error("任务标识不能为空");
        }
        
        if (url == null || url.isEmpty())
        {
            return AjaxResult.error("目标URL不能为空");
        }
        
        // 启动桌面应用（内部已包含等待HTTP服务就绪的逻辑）
        boolean startResult = taskExecutionService.startDesktopApp();
        if (!startResult)
        {
            return AjaxResult.error("启动桌面自动化应用失败，请检查Electron是否已安装");
        }
        
        // 智能匹配执行任务：先按ID查找，失败则按名称查找
        boolean result = taskExecutionService.executeTaskById(taskIdentifier, url);
        
        if (!result)
        {
            result = taskExecutionService.executeTaskByName(taskIdentifier, url, null);
        }
        
        if (result)
        {
            return AjaxResult.success("任务执行成功");
        }
        else
        {
            return AjaxResult.error("任务执行失败，未找到任务: " + taskIdentifier);
        }
    }

    /**
     * 任务执行请求类
     */
    public static class TaskExecuteRequest
    {
        /**
         * 任务标识（可以是ID或名称）
         */
        private String taskIdentifier;
        
        /**
         * 目标URL
         */
        private String url;

        public String getTaskIdentifier()
        {
            return taskIdentifier;
        }

        public void setTaskIdentifier(String taskIdentifier)
        {
            this.taskIdentifier = taskIdentifier;
        }

        public String getUrl()
        {
            return url;
        }

        public void setUrl(String url)
        {
            this.url = url;
        }
    }
}