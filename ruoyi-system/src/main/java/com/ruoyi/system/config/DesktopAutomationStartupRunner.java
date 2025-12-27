package com.ruoyi.system.config;

import com.ruoyi.system.service.IDesktopAutomationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 桌面自动化启动器
 * 在Spring Boot应用启动时自动启动桌面自动化应用
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
@Component
public class DesktopAutomationStartupRunner implements ApplicationRunner
{
    private static final Logger log = LoggerFactory.getLogger(DesktopAutomationStartupRunner.class);

    @Autowired
    private IDesktopAutomationService desktopAutomationService;

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        log.info("桌面自动化应用启动器已初始化");
        // 注释掉自动启动功能，改为通过接口控制
        // boolean result = desktopAutomationService.startDesktopApp();
        // 
        // if (result)
        // {
        //     log.info("桌面自动化应用启动成功");
        // }
        // else
        // {
        //     log.error("桌面自动化应用启动失败");
        // }
    }
}