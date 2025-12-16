package com.ruoyi.web.controller.autoworkflow;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.FriendAutoApprove;
import com.ruoyi.system.service.IFriendAutoApproveService;

/**
 * 好友自动通过配置控制器
 * 提供自动通过规则的查询与保存接口
 */
@RestController
public class FriendAutoApproveController extends BaseController {
    @Autowired
    private IFriendAutoApproveService service;

    /**
     * 查询自动通过配置（分页）
     * @param filter 过滤条件
     * @return TableDataInfo 分页结果
     */
    @PreAuthorize("@ss.hasPermi('autoworkflow:autoapprove:list')")
    @GetMapping("/auto/friend/autoapprove/list")
    public TableDataInfo list(FriendAutoApprove filter) {
        try { startPage(); } catch (Exception ignored) {}
        List<FriendAutoApprove> list = service.selectAutoApproveList(filter);
        return getDataTable(list);
    }

    /**
     * 新增或更新自动通过配置
     * @param cfg FriendAutoApprove 实体
     * @return AjaxResult 操作结果
     */
    @PreAuthorize("@ss.hasPermi('autoworkflow:autoapprove:add')")
    @Log(title = "自动通过好友", businessType = BusinessType.INSERT)
    @PostMapping("/auto/friend/autoapprove/save")
    public AjaxResult save(@Validated @RequestBody FriendAutoApprove cfg) {
        try { cfg.setCreateBy(getUsername()); } catch (Exception ignored) {}
        AjaxResult res;
        if (cfg.getId() == null) res = toAjax(service.insertAutoApprove(cfg)); else res = toAjax(service.updateAutoApprove(cfg));
        res.put("success", res.isSuccess());
        return res;
    }
}
