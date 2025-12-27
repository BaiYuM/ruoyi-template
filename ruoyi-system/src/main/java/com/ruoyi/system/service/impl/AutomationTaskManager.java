package com.ruoyi.system.service.impl;

import com.ruoyi.system.config.DesktopAutomationProperties;
import com.ruoyi.system.domain.AutomationConfig;
import com.ruoyi.system.service.IDesktopAutomationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * 自动化任务管理器
 * 支持后台运行模式和任务调度
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
@Service
public class AutomationTaskManager
{
    private static final Logger log = LoggerFactory.getLogger(AutomationTaskManager.class);

    @Autowired
    private DesktopAutomationProperties properties;

    @Autowired
    private IDesktopAutomationService desktopAutomationService;

    // 任务执行器
    private final ExecutorService taskExecutor = Executors.newFixedThreadPool(5);

    // 定时任务调度器
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // 后台任务标识
    private volatile boolean backgroundModeEnabled = false;

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init()
    {
        log.info("自动化任务管理器初始化完成");
    }

    /**
     * 销毁方法
     */
    @PreDestroy
    public void destroy()
    {
        taskExecutor.shutdown();
        scheduler.shutdown();
        try
        {
            if (!taskExecutor.awaitTermination(5, TimeUnit.SECONDS) || 
                !scheduler.awaitTermination(5, TimeUnit.SECONDS))
            {
                taskExecutor.shutdownNow();
                scheduler.shutdownNow();
            }
        }
        catch (InterruptedException e)
        {
            taskExecutor.shutdownNow();
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("自动化任务管理器已关闭");
    }

    /**
     * 启动后台自动化任务
     * 
     * @param config 自动化配置
     * @return 任务ID
     */
    public String startBackgroundTask(AutomationConfig config)
    {
        backgroundModeEnabled = true;
        String taskId = "task_" + System.currentTimeMillis();
        
        // 提交后台任务
        taskExecutor.submit(() -> {
            try
            {
                log.info("开始执行后台自动化任务: {}", taskId);
                
                // 设置为无头模式（后台运行）
                if (config.getHeadless() == null)
                {
                    config.setHeadless(true);
                }
                
                String result = desktopAutomationService.startAutomationTask(config);
                log.info("后台自动化任务完成: {}，结果: {}", taskId, result);
            }
            catch (Exception e)
            {
                log.error("后台自动化任务执行失败: {}", taskId, e);
            }
        });
        
        return taskId;
    }

    /**
     * 计划定时自动化任务
     * 
     * @param config 自动化配置
     * @param delay 延迟时间（毫秒）
     * @return 任务ID
     */
    public String scheduleTask(AutomationConfig config, long delay)
    {
        String taskId = "scheduled_task_" + System.currentTimeMillis();
        
        scheduler.schedule(() -> {
            try
            {
                log.info("开始执行计划自动化任务: {}", taskId);
                
                String result = desktopAutomationService.startAutomationTask(config);
                log.info("计划自动化任务完成: {}，结果: {}", taskId, result);
            }
            catch (Exception e)
            {
                log.error("计划自动化任务执行失败: {}", taskId, e);
            }
        }, delay, TimeUnit.MILLISECONDS);
        
        return taskId;
    }

    /**
     * 循环执行自动化任务
     * 
     * @param config 自动化配置
     * @param initialDelay 初始延迟（毫秒）
     * @param period 执行间隔（毫秒）
     * @return 任务ID
     */
    public String scheduleRepeatingTask(AutomationConfig config, long initialDelay, long period)
    {
        String taskId = "repeating_task_" + System.currentTimeMillis();
        
        scheduler.scheduleAtFixedRate(() -> {
            try
            {
                log.info("开始执行循环自动化任务: {}", taskId);
                
                String result = desktopAutomationService.startAutomationTask(config);
                log.info("循环自动化任务完成: {}，结果: {}", taskId, result);
            }
            catch (Exception e)
            {
                log.error("循环自动化任务执行失败: {}", taskId, e);
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);
        
        return taskId;
    }

    /**
     * 停止后台模式
     */
    public void stopBackgroundMode()
    {
        backgroundModeEnabled = false;
        log.info("已停止后台模式");
    }

    /**
     * 检查后台模式是否启用
     */
    public boolean isBackgroundModeEnabled()
    {
        return backgroundModeEnabled;
    }
}