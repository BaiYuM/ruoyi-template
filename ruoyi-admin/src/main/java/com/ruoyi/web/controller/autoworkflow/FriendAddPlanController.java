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
import com.ruoyi.system.domain.FriendAddPlan;
import com.ruoyi.system.service.IFriendAddPlanService;

@RestController
public class FriendAddPlanController extends BaseController {
    @Autowired
    private IFriendAddPlanService service;

    @PreAuthorize("@ss.hasPermi('autoworkflow:friendplan:list')")
    @GetMapping("/auto/friend/plan/list")
    public TableDataInfo list(FriendAddPlan filter) {
        try { startPage(); } catch (Exception ignored) {}
        List<FriendAddPlan> list = service.selectPlanList(filter);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('autoworkflow:friendplan:add')")
    @Log(title = "添加好友计划", businessType = BusinessType.INSERT)
    @PostMapping("/auto/friend/plan/save")
    public AjaxResult save(@Validated @RequestBody FriendAddPlan cfg) {
        try { cfg.setCreateBy(getUsername()); } catch (Exception ignored) {}
        AjaxResult res;
        if (cfg.getId() == null) res = toAjax(service.insertPlan(cfg)); else res = toAjax(service.updatePlan(cfg));
        res.put("success", res.isSuccess());
        return res;
    }
}
