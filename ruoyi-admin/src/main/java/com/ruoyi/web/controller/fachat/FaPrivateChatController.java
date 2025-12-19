package com.ruoyi.web.controller.fachat;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.system.domain.FaPrivateChat;
import com.ruoyi.system.service.IFaPrivateChatService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 私聊会话Controller
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/private/chat")
public class FaPrivateChatController extends BaseController
{
    @Autowired
    private IFaPrivateChatService faPrivateChatService;

    /**
     * 查询私聊会话列表
     */
    @PreAuthorize("@ss.hasPermi('system:chat:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaPrivateChat faPrivateChat)
    {
        startPage();
        List<FaPrivateChat> list = faPrivateChatService.selectFaPrivateChatList(faPrivateChat);
        return getDataTable(list);
    }

    /**
     * 导出私聊会话列表
     */
    @PreAuthorize("@ss.hasPermi('system:chat:export')")
    @Log(title = "私聊会话", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaPrivateChat faPrivateChat)
    {
        List<FaPrivateChat> list = faPrivateChatService.selectFaPrivateChatList(faPrivateChat);
        ExcelUtil<FaPrivateChat> util = new ExcelUtil<FaPrivateChat>(FaPrivateChat.class);
        util.exportExcel(response, list, "私聊会话数据");
    }

    /**
     * 获取私聊会话详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:chat:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faPrivateChatService.selectFaPrivateChatById(id));
    }

    /**
     * 新增私聊会话
     */
    @PreAuthorize("@ss.hasPermi('system:chat:add')")
    @Log(title = "私聊会话", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaPrivateChat faPrivateChat)
    {
        return toAjax(faPrivateChatService.insertFaPrivateChat(faPrivateChat));
    }

    /**
     * 修改私聊会话
     */
    @PreAuthorize("@ss.hasPermi('system:chat:edit')")
    @Log(title = "私聊会话", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaPrivateChat faPrivateChat)
    {
        return toAjax(faPrivateChatService.updateFaPrivateChat(faPrivateChat));
    }

    /**
     * 删除私聊会话
     */
    @PreAuthorize("@ss.hasPermi('system:chat:remove')")
    @Log(title = "私聊会话", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faPrivateChatService.deleteFaPrivateChatByIds(ids));
    }
}
