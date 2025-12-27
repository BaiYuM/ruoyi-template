package com.ruoyi.web.controller.DeskTop;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IDesktopAutomationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 桌面自动化控制器
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
@RestController
@RequestMapping("/system/desktop-automation")
public class DesktopAutomationController
{
    @Autowired
    private IDesktopAutomationService desktopAutomationService;

    /**
     * 启动桌面自动化应用
     */
    @PostMapping("/start")
    public AjaxResult startDesktopApp()
    {
        boolean result = desktopAutomationService.startDesktopApp();
        if (result)
        {
            return AjaxResult.success("桌面自动化应用启动成功");
        }
        else
        {
            return AjaxResult.error("桌面自动化应用启动失败");
        }
    }

    /**
     * 停止桌面自动化应用
     */
    @PostMapping("/stop")
    public AjaxResult stopDesktopApp()
    {
        boolean result = desktopAutomationService.stopDesktopApp();
        if (result)
        {
            return AjaxResult.success("桌面自动化应用停止成功");
        }
        else
        {
            return AjaxResult.error("桌面自动化应用停止失败");
        }
    }

    /**
     * 重启桌面自动化应用
     */
    @PostMapping("/restart")
    public AjaxResult restartDesktopApp()
    {
        boolean result = desktopAutomationService.restartDesktopApp();
        if (result)
        {
            return AjaxResult.success("桌面自动化应用重启成功");
        }
        else
        {
            return AjaxResult.error("桌面自动化应用重启失败");
        }
    }

    /**
     * 检查桌面应用运行状态
     */
    @GetMapping("/status")
    public AjaxResult checkStatus()
    {
        boolean isRunning = desktopAutomationService.isDesktopAppRunning();
        return AjaxResult.success("桌面自动化应用运行状态", isRunning);
    }
}