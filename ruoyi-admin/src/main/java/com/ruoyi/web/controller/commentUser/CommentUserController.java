package com.ruoyi.web.controller.commentUser;

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
import com.ruoyi.system.domain.CommentUser;
import com.ruoyi.system.service.ICommentUserService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 平台账户Controller
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/commentUser/user")
public class CommentUserController extends BaseController
{
    @Autowired
    private ICommentUserService commentUserService;

    /**
     * 查询平台账户列表
     */
    @PreAuthorize("@ss.hasPermi('commentUser:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(CommentUser commentUser)
    {
        startPage();
        List<CommentUser> list = commentUserService.selectCommentUserList(commentUser);
        return getDataTable(list);
    }

    /**
     * 导出平台账户列表
     */
    @PreAuthorize("@ss.hasPermi('commentUser:user:export')")
    @Log(title = "平台账户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CommentUser commentUser)
    {
        List<CommentUser> list = commentUserService.selectCommentUserList(commentUser);
        ExcelUtil<CommentUser> util = new ExcelUtil<CommentUser>(CommentUser.class);
        util.exportExcel(response, list, "平台账户数据");
    }

    /**
     * 获取平台账户详细信息
     */
    @PreAuthorize("@ss.hasPermi('commentUser:user:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(commentUserService.selectCommentUserById(id));
    }

    /**
     * 新增平台账户
     */
    @PreAuthorize("@ss.hasPermi('commentUser:user:add')")
    @Log(title = "平台账户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CommentUser commentUser)
    {
        return toAjax(commentUserService.insertCommentUser(commentUser));
    }

    /**
     * 修改平台账户
     */
    @PreAuthorize("@ss.hasPermi('commentUser:user:edit')")
    @Log(title = "平台账户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CommentUser commentUser)
    {
        return toAjax(commentUserService.updateCommentUser(commentUser));
    }

    /**
     * 删除平台账户
     */
    @PreAuthorize("@ss.hasPermi('commentUser:user:remove')")
    @Log(title = "平台账户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(commentUserService.deleteCommentUserByIds(ids));
    }
}
