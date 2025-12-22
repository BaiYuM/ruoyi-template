package com.ruoyi.web.controller.aiSerConfig;

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
import com.ruoyi.system.domain.AiConfig;
import com.ruoyi.system.service.IAiConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AI客服配置Controller
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/aiSerConfig/aiconfig")
public class AiConfigController extends BaseController
{
    @Autowired
    private IAiConfigService aiConfigService;

    /**
     * 查询AI客服配置列表
     */
    @PreAuthorize("@ss.hasPermi('tikTok:aiSerConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiConfig aiConfig)
    {
        startPage();
        List<AiConfig> list = aiConfigService.selectAiConfigList(aiConfig);
        return getDataTable(list);
    }
    
    /**
     * 查询AI客服配置列表（关联comment_user）
     */
    @PreAuthorize("@ss.hasPermi('tikTok:aiSerConfig:list')")
    @GetMapping("/listWithCommentUser")
    public TableDataInfo listWithCommentUser(AiConfig aiConfig)
    {
        startPage();
        List<AiConfig> list = aiConfigService.selectAiConfigWithCommentUserList(aiConfig);
        return getDataTable(list);
    }

    /**
     * 导出AI客服配置列表
     */
    @PreAuthorize("@ss.hasPermi('tikTok:aiSerConfig:export')")
    @Log(title = "AI客服配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AiConfig aiConfig)
    {
        List<AiConfig> list = aiConfigService.selectAiConfigList(aiConfig);
        ExcelUtil<AiConfig> util = new ExcelUtil<AiConfig>(AiConfig.class);
        util.exportExcel(response, list, "AI客服配置数据");
    }

    /**
     * 获取AI客服配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('tikTok:aiSerConfig:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aiConfigService.selectAiConfigById(id));
    }

    /**
     * 新增AI客服配置
     */
    @PreAuthorize("@ss.hasPermi('tikTok:aiSerConfig:add')")
    @Log(title = "AI客服配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiConfig aiConfig)
    {
        return toAjax(aiConfigService.insertAiConfig(aiConfig));
    }

    /**
     * 修改AI客服配置
     */
    @PreAuthorize("@ss.hasPermi('tikTok:aiSerConfig:edit')")
    @Log(title = "AI客服配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiConfig aiConfig)
    {
        return toAjax(aiConfigService.updateAiConfig(aiConfig));
    }

    /**
     * 删除AI客服配置
     */
    @PreAuthorize("@ss.hasPermi('tikTok:aiSerConfig:remove')")
    @Log(title = "AI客服配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aiConfigService.deleteAiConfigByIds(ids));
    }
}
