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
import com.ruoyi.system.domain.FaGroupChatMember;
import com.ruoyi.system.service.IFaGroupChatMemberService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 群聊成员关联Controller
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/group/member")
public class FaGroupChatMemberController extends BaseController
{
    @Autowired
    private IFaGroupChatMemberService faGroupChatMemberService;

    /**
     * 查询群聊成员关联列表
     */
    @PreAuthorize("@ss.hasPermi('system:member:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaGroupChatMember faGroupChatMember)
    {
        startPage();
        List<FaGroupChatMember> list = faGroupChatMemberService.selectFaGroupChatMemberList(faGroupChatMember);
        return getDataTable(list);
    }

    /**
     * 导出群聊成员关联列表
     */
    @PreAuthorize("@ss.hasPermi('system:member:export')")
    @Log(title = "群聊成员关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaGroupChatMember faGroupChatMember)
    {
        List<FaGroupChatMember> list = faGroupChatMemberService.selectFaGroupChatMemberList(faGroupChatMember);
        ExcelUtil<FaGroupChatMember> util = new ExcelUtil<FaGroupChatMember>(FaGroupChatMember.class);
        util.exportExcel(response, list, "群聊成员关联数据");
    }

    /**
     * 获取群聊成员关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:member:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faGroupChatMemberService.selectFaGroupChatMemberById(id));
    }

    /**
     * 新增群聊成员关联
     */
    @PreAuthorize("@ss.hasPermi('system:member:add')")
    @Log(title = "群聊成员关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaGroupChatMember faGroupChatMember)
    {
        return toAjax(faGroupChatMemberService.insertFaGroupChatMember(faGroupChatMember));
    }

    /**
     * 修改群聊成员关联
     */
    @PreAuthorize("@ss.hasPermi('system:member:edit')")
    @Log(title = "群聊成员关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaGroupChatMember faGroupChatMember)
    {
        return toAjax(faGroupChatMemberService.updateFaGroupChatMember(faGroupChatMember));
    }

    /**
     * 删除群聊成员关联
     */
    @PreAuthorize("@ss.hasPermi('system:member:remove')")
    @Log(title = "群聊成员关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faGroupChatMemberService.deleteFaGroupChatMemberByIds(ids));
    }
}
