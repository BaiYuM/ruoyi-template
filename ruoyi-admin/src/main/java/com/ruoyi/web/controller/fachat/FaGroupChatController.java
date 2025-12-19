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
import com.ruoyi.system.domain.FaGroupChat;
import com.ruoyi.system.service.IFaGroupChatService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 群聊基础信息Controller
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/group/chat")
public class FaGroupChatController extends BaseController
{
    @Autowired
    private IFaGroupChatService faGroupChatService;

    /**
     * 查询群聊基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('group:chat:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaGroupChat faGroupChat)
    {
        startPage();
        List<FaGroupChat> list = faGroupChatService.selectFaGroupChatList(faGroupChat);
        return getDataTable(list);
    }

    /**
     * 导出群聊基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('groupMember:chat:export')")
    @Log(title = "群聊基础信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaGroupChat faGroupChat)
    {
        List<FaGroupChat> list = faGroupChatService.selectFaGroupChatList(faGroupChat);
        ExcelUtil<FaGroupChat> util = new ExcelUtil<FaGroupChat>(FaGroupChat.class);
        util.exportExcel(response, list, "群聊基础信息数据");
    }

    /**
     * 获取群聊基础信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('group:chat:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faGroupChatService.selectFaGroupChatById(id));
    }

    /**
     * 新增群聊基础信息
     */
    @PreAuthorize("@ss.hasPermi('system:chat:add')")
    @Log(title = "群聊基础信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaGroupChat faGroupChat)
    {
        return toAjax(faGroupChatService.insertFaGroupChat(faGroupChat));
    }

    /**
     * 修改群聊基础信息
     */
    @PreAuthorize("@ss.hasPermi('system:chat:edit')")
    @Log(title = "群聊基础信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaGroupChat faGroupChat)
    {
        return toAjax(faGroupChatService.updateFaGroupChat(faGroupChat));
    }

    /**
     * 删除群聊基础信息
     */
    @PreAuthorize("@ss.hasPermi('system:chat:remove')")
    @Log(title = "群聊基础信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faGroupChatService.deleteFaGroupChatByIds(ids));
    }
}
