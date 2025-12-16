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
import com.ruoyi.system.domain.AiExpertConfig;
import com.ruoyi.system.service.IAiExpertConfigService;

/**
 * AI 专家配置控制器
 * 提供 AI 专家相关配置的查询与保存接口
 */
@RestController
public class AiExpertController extends BaseController {
    @Autowired
    private IAiExpertConfigService service;

    /**
     * 查询 AI 专家配置列表（支持分页与筛选）
     * @param filter 查询过滤条件封装为 AiExpertConfig
     * @return TableDataInfo 分页表格数据
     */
    @PreAuthorize("@ss.hasPermi('autoworkflow:ai:list')")
    @GetMapping("/auto/ai/list")
    public TableDataInfo list(AiExpertConfig filter) {
        try { startPage(); } catch (Exception ignored) {}
        List<AiExpertConfig> list = service.selectAiList(filter);
        return getDataTable(list);
    }

    /**
     * 新增或更新 AI 专家配置（含创建者信息写入）
     * @param cfg 要保存的 AiExpertConfig 对象
     * @return AjaxResult 操作结果，包含 success 字段
     */
    @PreAuthorize("@ss.hasPermi('autoworkflow:ai:add')")
    @Log(title = "AI 专家配置", businessType = BusinessType.INSERT)
    @PostMapping("/auto/ai/save")
    public AjaxResult save(@Validated @RequestBody AiExpertConfig cfg) {
        try { cfg.setCreateBy(getUsername()); } catch (Exception ignored) {}
        AjaxResult res;
        if (cfg.getId() == null) res = toAjax(service.insertAi(cfg)); else res = toAjax(service.updateAi(cfg));
        res.put("success", res.isSuccess());
        return res;
    }
}
