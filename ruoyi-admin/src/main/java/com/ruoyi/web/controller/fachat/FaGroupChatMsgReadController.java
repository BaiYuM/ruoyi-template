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
import com.ruoyi.system.domain.FaGroupChatMsgRead;
import com.ruoyi.system.service.IFaGroupChatMsgReadService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 群消息已读状态Controller
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/group/read")
public class FaGroupChatMsgReadController extends BaseController
{
    @Autowired
    private IFaGroupChatMsgReadService faGroupChatMsgReadService;

    /**
     * 查询群消息已读状态列表
     */
    @PreAuthorize("@ss.hasPermi('system:read:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaGroupChatMsgRead faGroupChatMsgRead)
    {
        startPage();
        List<FaGroupChatMsgRead> list = faGroupChatMsgReadService.selectFaGroupChatMsgReadList(faGroupChatMsgRead);
        return getDataTable(list);
    }

    /**
     * 导出群消息已读状态列表
     */
    @PreAuthorize("@ss.hasPermi('system:read:export')")
    @Log(title = "群消息已读状态", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaGroupChatMsgRead faGroupChatMsgRead)
    {
        List<FaGroupChatMsgRead> list = faGroupChatMsgReadService.selectFaGroupChatMsgReadList(faGroupChatMsgRead);
        ExcelUtil<FaGroupChatMsgRead> util = new ExcelUtil<FaGroupChatMsgRead>(FaGroupChatMsgRead.class);
        util.exportExcel(response, list, "群消息已读状态数据");
    }

    /**
     * 获取群消息已读状态详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:read:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faGroupChatMsgReadService.selectFaGroupChatMsgReadById(id));
    }

    /**
     * 新增群消息已读状态
     */
    @PreAuthorize("@ss.hasPermi('system:read:add')")
    @Log(title = "群消息已读状态", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaGroupChatMsgRead faGroupChatMsgRead)
    {
        return toAjax(faGroupChatMsgReadService.insertFaGroupChatMsgRead(faGroupChatMsgRead));
    }

    /**
     * 修改群消息已读状态
     */
    @PreAuthorize("@ss.hasPermi('system:read:edit')")
    @Log(title = "群消息已读状态", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaGroupChatMsgRead faGroupChatMsgRead)
    {
        return toAjax(faGroupChatMsgReadService.updateFaGroupChatMsgRead(faGroupChatMsgRead));
    }

    /**
     * 删除群消息已读状态
     */
    @PreAuthorize("@ss.hasPermi('system:read:remove')")
    @Log(title = "群消息已读状态", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faGroupChatMsgReadService.deleteFaGroupChatMsgReadByIds(ids));
    }
}
