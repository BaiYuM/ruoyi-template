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
import com.ruoyi.system.domain.FriendAddRecord;
import com.ruoyi.system.service.IFriendAddRecordService;

@RestController
public class FriendAddRecordController extends BaseController {
    /**
     * 好友添加记录控制器
     * 提供查询记录列表与保存记录的接口，用于好友自动添加模块的操作记录管理
     */

    @Autowired
    private IFriendAddRecordService service;

    @PreAuthorize("@ss.hasPermi('autoworkflow:friendrecord:list')")
    @GetMapping("/auto/friend/record/list")
    /**
     * 查询好友添加记录（支持按计划、状态等过滤，分页）
     * @param filter FriendAddRecord 作为查询过滤条件
     * @return TableDataInfo 分页结果
     */
    public TableDataInfo list(FriendAddRecord filter) {
        try { startPage(); } catch (Exception ignored) {}
        List<FriendAddRecord> list = service.selectRecordList(filter);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('autoworkflow:friendrecord:add')")
    @Log(title = "添加好友记录", businessType = BusinessType.INSERT)
    @PostMapping("/auto/friend/record/save")
    /**
     * 新增或更新好友添加记录
     * @param rec FriendAddRecord 实体
     * @return AjaxResult 操作结果，包含 success 字段
     */
    public AjaxResult save(@Validated @RequestBody FriendAddRecord rec) {
        try { rec.setCreateBy(getUsername()); } catch (Exception ignored) {}
        AjaxResult res;
        if (rec.getId() == null) res = toAjax(service.insertRecord(rec)); else res = toAjax(service.updateRecord(rec));
        res.put("success", res.isSuccess());
        return res;
    }
}
