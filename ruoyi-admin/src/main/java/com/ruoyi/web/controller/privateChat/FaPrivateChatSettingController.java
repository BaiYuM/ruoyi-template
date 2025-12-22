package com.ruoyi.web.controller.privateChat;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.FaPrivateChatSetting;
import com.ruoyi.system.service.IFaPrivateChatSettingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 私聊设置Controller
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/privateChat/private_chat_setting")
public class FaPrivateChatSettingController extends BaseController
{
    @Autowired
    private IFaPrivateChatSettingService faPrivateChatSettingService;

    /**
     * 查询私聊设置列表
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_setting:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaPrivateChatSetting faPrivateChatSetting)
    {
        startPage();
        List<FaPrivateChatSetting> list = faPrivateChatSettingService.selectFaPrivateChatSettingList(faPrivateChatSetting);
        return getDataTable(list);
    }

    /**
     * 导出私聊设置列表
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_setting:export')")
    @Log(title = "私聊设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaPrivateChatSetting faPrivateChatSetting)
    {
        List<FaPrivateChatSetting> list = faPrivateChatSettingService.selectFaPrivateChatSettingList(faPrivateChatSetting);
        ExcelUtil<FaPrivateChatSetting> util = new ExcelUtil<FaPrivateChatSetting>(FaPrivateChatSetting.class);
        util.exportExcel(response, list, "私聊设置数据");
    }

    /**
     * 获取私聊设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_setting:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faPrivateChatSettingService.selectFaPrivateChatSettingById(id));
    }

    /**
     * 新增私聊设置
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_setting:add')")
    @Log(title = "私聊设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaPrivateChatSetting faPrivateChatSetting)
    {
        return toAjax(faPrivateChatSettingService.insertFaPrivateChatSetting(faPrivateChatSetting));
    }

    /**
     * 修改私聊设置
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_setting:edit')")
    @Log(title = "私聊设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaPrivateChatSetting faPrivateChatSetting)
    {
        return toAjax(faPrivateChatSettingService.updateFaPrivateChatSetting(faPrivateChatSetting));
    }

    /**
     * 删除私聊设置
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_setting:remove')")
    @Log(title = "私聊设置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faPrivateChatSettingService.deleteFaPrivateChatSettingByIds(ids));
    }
}
