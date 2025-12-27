package com.ruoyi.web.controller.automation;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.AutomationConfig;
import com.ruoyi.system.service.IDesktopAutomationService;
import com.ruoyi.system.service.impl.AutomationTaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 桌面自动化控制
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
@RestController
@RequestMapping("/automation/desktop")
public class DesktopAutomationController
{
    @Autowired
    private IDesktopAutomationService desktopAutomationService;

    @Autowired
    private AutomationTaskManager taskManager;

    /**
     * 启动桌面应用
     */
    @PostMapping("/startApp")
    public AjaxResult startDesktopApp()
    {
        try
        {
            String result = desktopAutomationService.startDesktopApp();
            return AjaxResult.success(result);
        }
        catch (Exception e)
        {
            return AjaxResult.error("启动桌面应用失败: " + e.getMessage());
        }
    }

    /**
     * 停止桌面应用
     */
    @PostMapping("/stopApp")
    public AjaxResult stopDesktopApp()
    {
        try
        {
            String result = desktopAutomationService.stopDesktopApp();
            return AjaxResult.success(result);
        }
        catch (Exception e)
        {
            return AjaxResult.error("停止桌面应用失败: " + e.getMessage());
        }
    }

    /**
     * 检查桌面应用运行状态
     */
    @GetMapping("/status")
    public AjaxResult checkDesktopAppStatus()
    {
        try
        {
            boolean isRunning = desktopAutomationService.isDesktopAppRunning();
            return AjaxResult.success("桌面应用运行状态", isRunning);
        }
        catch (Exception e)
        {
            return AjaxResult.error("检查桌面应用状态失败: " + e.getMessage());
        }
    }

    /**
     * 启动自动化任务
     */
    @PostMapping("/startTask")
    public AjaxResult startAutomationTask(@RequestBody AutomationConfig config)
    {
        try
        {
            // 验证必要参数
            if (config == null)
            {
                return AjaxResult.error("配置参数不能为空");
            }
            
            if (StringUtils.isEmpty(config.getTargetUrl()))
            {
                return AjaxResult.error("目标URL不能为空");
            }

            String result = desktopAutomationService.startAutomationTask(config);
            return AjaxResult.success(result);
        }
        catch (Exception e)
        {
            return AjaxResult.error("启动自动化任务失败: " + e.getMessage());
        }
    }

    /**
     * 启动后台自动化任务
     */
    @PostMapping("/startBackgroundTask")
    public AjaxResult startBackgroundTask(@RequestBody AutomationConfig config)
    {
        try
        {
            // 验证必要参数
            if (config == null)
            {
                return AjaxResult.error("配置参数不能为空");
            }
            
            if (StringUtils.isEmpty(config.getTargetUrl()))
            {
                return AjaxResult.error("目标URL不能为空");
            }

            String taskId = taskManager.startBackgroundTask(config);
            return AjaxResult.success("后台自动化任务已启动", taskId);
        }
        catch (Exception e)
        {
            return AjaxResult.error("启动后台自动化任务失败: " + e.getMessage());
        }
    }

    /**
     * 计划定时自动化任务
     */
    @PostMapping("/scheduleTask")
    public AjaxResult scheduleTask(@RequestBody ScheduleTaskRequest request)
    {
        try
        {
            // 验证必要参数
            if (request == null || request.getConfig() == null)
            {
                return AjaxResult.error("配置参数不能为空");
            }
            
            if (StringUtils.isEmpty(request.getConfig().getTargetUrl()))
            {
                return AjaxResult.error("目标URL不能为空");
            }

            if (request.getDelay() == null || request.getDelay() <= 0)
            {
                return AjaxResult.error("延迟时间必须大于0");
            }

            String taskId = taskManager.scheduleTask(request.getConfig(), request.getDelay());
            return AjaxResult.success("定时自动化任务已计划", taskId);
        }
        catch (Exception e)
        {
            return AjaxResult.error("计划定时自动化任务失败: " + e.getMessage());
        }
    }

    /**
     * 计划循环自动化任务
     */
    @PostMapping("/scheduleRepeatingTask")
    public AjaxResult scheduleRepeatingTask(@RequestBody RepeatingTaskRequest request)
    {
        try
        {
            // 验证必要参数
            if (request == null || request.getConfig() == null)
            {
                return AjaxResult.error("配置参数不能为空");
            }
            
            if (StringUtils.isEmpty(request.getConfig().getTargetUrl()))
            {
                return AjaxResult.error("目标URL不能为空");
            }

            if (request.getInitialDelay() == null || request.getInitialDelay() <= 0)
            {
                return AjaxResult.error("初始延迟时间必须大于0");
            }

            if (request.getPeriod() == null || request.getPeriod() <= 0)
            {
                return AjaxResult.error("执行间隔时间必须大于0");
            }

            String taskId = taskManager.scheduleRepeatingTask(request.getConfig(), request.getInitialDelay(), request.getPeriod());
            return AjaxResult.success("循环自动化任务已计划", taskId);
        }
        catch (Exception e)
        {
            return AjaxResult.error("计划循环自动化任务失败: " + e.getMessage());
        }
    }

    /**
     * 停止自动化任务
     */
    @PostMapping("/stopTask")
    public AjaxResult stopAutomationTask()
    {
        try
        {
            String result = desktopAutomationService.stopAutomationTask();
            return AjaxResult.success(result);
        }
        catch (Exception e)
        {
            return AjaxResult.error("停止自动化任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取自动化任务状态
     */
    @GetMapping("/taskStatus")
    public AjaxResult getAutomationStatus()
    {
        try
        {
            String status = desktopAutomationService.getAutomationStatus();
            return AjaxResult.success("自动化任务状态", status);
        }
        catch (Exception e)
        {
            return AjaxResult.error("获取自动化状态失败: " + e.getMessage());
        }
    }

    /**
     * 检查后台模式状态
     */
    @GetMapping("/backgroundStatus")
    public AjaxResult checkBackgroundStatus()
    {
        try
        {
            boolean isEnabled = taskManager.isBackgroundModeEnabled();
            return AjaxResult.success("后台模式状态", isEnabled);
        }
        catch (Exception e)
        {
            return AjaxResult.error("检查后台模式状态失败: " + e.getMessage());
        }
    }

    /**
     * 停止后台模式
     */
    @PostMapping("/stopBackgroundMode")
    public AjaxResult stopBackgroundMode()
    {
        try
        {
            taskManager.stopBackgroundMode();
            return AjaxResult.success("已停止后台模式");
        }
        catch (Exception e)
        {
            return AjaxResult.error("停止后台模式失败: " + e.getMessage());
        }
    }

    /**
     * 计划任务请求封装类
     */
    public static class ScheduleTaskRequest
    {
        private AutomationConfig config;
        private Long delay; // 延迟时间（毫秒）

        public AutomationConfig getConfig()
        {
            return config;
        }

        public void setConfig(AutomationConfig config)
        {
            this.config = config;
        }

        public Long getDelay()
        {
            return delay;
        }

        public void setDelay(Long delay)
        {
            this.delay = delay;
        }
    }

    /**
     * 循环任务请求封装类
     */
    public static class RepeatingTaskRequest
    {
        private AutomationConfig config;
        private Long initialDelay; // 初始延迟（毫秒）
        private Long period; // 执行间隔（毫秒）

        public AutomationConfig getConfig()
        {
            return config;
        }

        public void setConfig(AutomationConfig config)
        {
            this.config = config;
        }

        public Long getInitialDelay()
        {
            return initialDelay;
        }

        public void setInitialDelay(Long initialDelay)
        {
            this.initialDelay = initialDelay;
        }

        public Long getPeriod()
        {
            return period;
        }

        public void setPeriod(Long period)
        {
            this.period = period;
        }
    }
}