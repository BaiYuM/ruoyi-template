package com.ruoyi.web.controller.privateChat;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.FaPrivateChatMsg;
import com.ruoyi.system.service.IFaPrivateChatMsgService;
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
 * 私聊消息Controller
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/privateChat/private_chat_msg")
public class FaPrivateChatMsgController extends BaseController
{
    @Autowired
    private IFaPrivateChatMsgService faPrivateChatMsgService;

    /**
     * 查询私聊消息列表
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_msg:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaPrivateChatMsg faPrivateChatMsg)
    {
        startPage();
        List<FaPrivateChatMsg> list = faPrivateChatMsgService.selectFaPrivateChatMsgList(faPrivateChatMsg);
        return getDataTable(list);
    }

    /**
     * 导出私聊消息列表
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_msg:export')")
    @Log(title = "私聊消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaPrivateChatMsg faPrivateChatMsg)
    {
        List<FaPrivateChatMsg> list = faPrivateChatMsgService.selectFaPrivateChatMsgList(faPrivateChatMsg);
        ExcelUtil<FaPrivateChatMsg> util = new ExcelUtil<FaPrivateChatMsg>(FaPrivateChatMsg.class);
        util.exportExcel(response, list, "私聊消息数据");
    }

    /**
     * 获取私聊消息详细信息
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_msg:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faPrivateChatMsgService.selectFaPrivateChatMsgById(id));
    }

    /**
     * 新增私聊消息
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_msg:add')")
    @Log(title = "私聊消息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaPrivateChatMsg faPrivateChatMsg)
    {
        return toAjax(faPrivateChatMsgService.insertFaPrivateChatMsg(faPrivateChatMsg));
    }

    /**
     * 修改私聊消息
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_msg:edit')")
    @Log(title = "私聊消息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaPrivateChatMsg faPrivateChatMsg)
    {
        return toAjax(faPrivateChatMsgService.updateFaPrivateChatMsg(faPrivateChatMsg));
    }

    /**
     * 删除私聊消息
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat_msg:remove')")
    @Log(title = "私聊消息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faPrivateChatMsgService.deleteFaPrivateChatMsgByIds(ids));
    }
}
