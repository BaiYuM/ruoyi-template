package com.ruoyi.web.controller.automation;

import com.ruoyi.system.service.IDesktopAutomationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 自动化服务启动配置
 * 应用启动时初始化桌面自动化服务
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
@Component
public class AutomationStartupConfig
{
    private static final Logger log = LoggerFactory.getLogger(AutomationStartupConfig.class);

    @Autowired
    private IDesktopAutomationService desktopAutomationService;

    /**
     * 应用启动完成事件监听
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady()
    {
        log.info("应用启动完成，正在初始化桌面自动化服务...");
        
        try
        {
            // 检查桌面应用是否运行，如果未运行则启动
            if (!desktopAutomationService.isDesktopAppRunning())
            {
                log.info("桌面应用未运行，正在启动...");
                String result = desktopAutomationService.startDesktopApp();
                log.info("桌面应用启动结果: {}", result);
            }
            else
            {
                log.info("桌面应用已在运行中");
            }
        }
        catch (Exception e)
        {
            log.error("初始化桌面自动化服务失败", e);
        }
        
        log.info("桌面自动化服务初始化完成");
    }
}