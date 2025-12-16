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
import com.ruoyi.system.domain.AutoPublishConfig;
import com.ruoyi.system.service.IAutoPublishConfigService;

/**
 * 自动发布配置控制器
 * 提供自动发布规则的查询与保存接口
 */
@RestController
public class AutoPublishController extends BaseController {
    @Autowired
    private IAutoPublishConfigService service;

    /**
     * 查询自动发布配置列表（支持分页）
     * @param filter 过滤条件
     * @return TableDataInfo 分页数据
     */
    @PreAuthorize("@ss.hasPermi('autoworkflow:publish:list')")
    @GetMapping("/auto/publish/list")
    public TableDataInfo list(AutoPublishConfig filter) {
        try { startPage(); } catch (Exception ignored) {}
        List<AutoPublishConfig> list = service.selectAutoPublishList(filter);
        return getDataTable(list);
    }

    /**
     * 新增或更新自动发布配置
     * @param cfg AutoPublishConfig 实体
     * @return AjaxResult 操作结果
     */
    @PreAuthorize("@ss.hasPermi('autoworkflow:publish:add')")
    @Log(title = "自动发布", businessType = BusinessType.INSERT)
    @PostMapping("/auto/publish/save")
    public AjaxResult save(@Validated @RequestBody AutoPublishConfig cfg) {
        try { cfg.setCreateBy(getUsername()); } catch (Exception ignored) {}
        AjaxResult res;
        if (cfg.getId() == null) res = toAjax(service.insertAutoPublish(cfg)); else res = toAjax(service.updateAutoPublish(cfg));
        res.put("success", res.isSuccess());
        return res;
    }
}
