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
import com.ruoyi.system.domain.FaPrivateChatMsgRead;
import com.ruoyi.system.service.IFaPrivateChatMsgReadService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 私聊消息已读Controller
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/private/read")
public class FaPrivateChatMsgReadController extends BaseController
{
    @Autowired
    private IFaPrivateChatMsgReadService faPrivateChatMsgReadService;

    /**
     * 查询私聊消息已读列表
     */
    @PreAuthorize("@ss.hasPermi('system:read:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaPrivateChatMsgRead faPrivateChatMsgRead)
    {
        startPage();
        List<FaPrivateChatMsgRead> list = faPrivateChatMsgReadService.selectFaPrivateChatMsgReadList(faPrivateChatMsgRead);
        return getDataTable(list);
    }

    /**
     * 导出私聊消息已读列表
     */
    @PreAuthorize("@ss.hasPermi('system:read:export')")
    @Log(title = "私聊消息已读", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaPrivateChatMsgRead faPrivateChatMsgRead)
    {
        List<FaPrivateChatMsgRead> list = faPrivateChatMsgReadService.selectFaPrivateChatMsgReadList(faPrivateChatMsgRead);
        ExcelUtil<FaPrivateChatMsgRead> util = new ExcelUtil<FaPrivateChatMsgRead>(FaPrivateChatMsgRead.class);
        util.exportExcel(response, list, "私聊消息已读数据");
    }

    /**
     * 获取私聊消息已读详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:read:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faPrivateChatMsgReadService.selectFaPrivateChatMsgReadById(id));
    }

    /**
     * 新增私聊消息已读
     */
    @PreAuthorize("@ss.hasPermi('system:read:add')")
    @Log(title = "私聊消息已读", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaPrivateChatMsgRead faPrivateChatMsgRead)
    {
        return toAjax(faPrivateChatMsgReadService.insertFaPrivateChatMsgRead(faPrivateChatMsgRead));
    }

    /**
     * 修改私聊消息已读
     */
    @PreAuthorize("@ss.hasPermi('system:read:edit')")
    @Log(title = "私聊消息已读", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaPrivateChatMsgRead faPrivateChatMsgRead)
    {
        return toAjax(faPrivateChatMsgReadService.updateFaPrivateChatMsgRead(faPrivateChatMsgRead));
    }

    /**
     * 删除私聊消息已读
     */
    @PreAuthorize("@ss.hasPermi('system:read:remove')")
    @Log(title = "私聊消息已读", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faPrivateChatMsgReadService.deleteFaPrivateChatMsgReadByIds(ids));
    }
}
