package com.ruoyi.web.controller.welWord;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.FaWelcomeWordConfig;
import com.ruoyi.system.mapper.service.IFaWelcomeWordConfigService;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 欢迎词配置Controller
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/welWord/wel_word_config")
public class FaWelcomeWordConfigController extends BaseController
{
    @Autowired
    private IFaWelcomeWordConfigService faWelcomeWordConfigService;

    /**
     * 查询欢迎词配置列表
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_config:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaWelcomeWordConfig faWelcomeWordConfig)
    {
        startPage();
        List<FaWelcomeWordConfig> list = faWelcomeWordConfigService.selectFaWelcomeWordConfigList(faWelcomeWordConfig);
        return getDataTable(list);
    }

    /**
     * 导出欢迎词配置列表
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_config:export')")
    @Log(title = "欢迎词配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaWelcomeWordConfig faWelcomeWordConfig)
    {
        List<FaWelcomeWordConfig> list = faWelcomeWordConfigService.selectFaWelcomeWordConfigList(faWelcomeWordConfig);
        ExcelUtil<FaWelcomeWordConfig> util = new ExcelUtil<FaWelcomeWordConfig>(FaWelcomeWordConfig.class);
        util.exportExcel(response, list, "欢迎词配置数据");
    }

    /**
     * 获取欢迎词配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_config:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faWelcomeWordConfigService.selectFaWelcomeWordConfigById(id));
    }

    /**
     * 新增欢迎词配置
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_config:add')")
    @Log(title = "欢迎词配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaWelcomeWordConfig faWelcomeWordConfig)
    {
        return toAjax(faWelcomeWordConfigService.insertFaWelcomeWordConfig(faWelcomeWordConfig));
    }

    /**
     * 修改欢迎词配置
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_config:edit')")
    @Log(title = "欢迎词配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaWelcomeWordConfig faWelcomeWordConfig)
    {
        return toAjax(faWelcomeWordConfigService.updateFaWelcomeWordConfig(faWelcomeWordConfig));
    }

    /**
     * 删除欢迎词配置
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_config:remove')")
    @Log(title = "欢迎词配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faWelcomeWordConfigService.deleteFaWelcomeWordConfigByIds(ids));
    }
}
