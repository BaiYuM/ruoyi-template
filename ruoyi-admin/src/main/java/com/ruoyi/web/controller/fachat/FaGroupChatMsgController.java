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
import com.ruoyi.system.domain.FaGroupChatMsg;
import com.ruoyi.system.service.IFaGroupChatMsgService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 群聊消息Controller
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/group/msg")
public class FaGroupChatMsgController extends BaseController
{
    @Autowired
    private IFaGroupChatMsgService faGroupChatMsgService;

    /**
     * 查询群聊消息列表
     */
    @PreAuthorize("@ss.hasPermi('system:msg:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaGroupChatMsg faGroupChatMsg)
    {
        startPage();
        List<FaGroupChatMsg> list = faGroupChatMsgService.selectFaGroupChatMsgList(faGroupChatMsg);
        return getDataTable(list);
    }

    /**
     * 导出群聊消息列表
     */
    @PreAuthorize("@ss.hasPermi('system:msg:export')")
    @Log(title = "群聊消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaGroupChatMsg faGroupChatMsg)
    {
        List<FaGroupChatMsg> list = faGroupChatMsgService.selectFaGroupChatMsgList(faGroupChatMsg);
        ExcelUtil<FaGroupChatMsg> util = new ExcelUtil<FaGroupChatMsg>(FaGroupChatMsg.class);
        util.exportExcel(response, list, "群聊消息数据");
    }

    /**
     * 获取群聊消息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:msg:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faGroupChatMsgService.selectFaGroupChatMsgById(id));
    }

    /**
     * 新增群聊消息
     */
    @PreAuthorize("@ss.hasPermi('system:msg:add')")
    @Log(title = "群聊消息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaGroupChatMsg faGroupChatMsg)
    {
        return toAjax(faGroupChatMsgService.insertFaGroupChatMsg(faGroupChatMsg));
    }

    /**
     * 修改群聊消息
     */
    @PreAuthorize("@ss.hasPermi('system:msg:edit')")
    @Log(title = "群聊消息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaGroupChatMsg faGroupChatMsg)
    {
        return toAjax(faGroupChatMsgService.updateFaGroupChatMsg(faGroupChatMsg));
    }

    /**
     * 删除群聊消息
     */
    @PreAuthorize("@ss.hasPermi('system:msg:remove')")
    @Log(title = "群聊消息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faGroupChatMsgService.deleteFaGroupChatMsgByIds(ids));
    }
}
