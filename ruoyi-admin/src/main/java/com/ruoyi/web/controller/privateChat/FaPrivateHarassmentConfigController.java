package com.ruoyi.web.controller.privateChat;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.FaPrivateHarassmentConfig;
import com.ruoyi.system.service.IFaPrivateHarassmentConfigService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 私信追杀配置Controller
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/privateChat/private_har_config")
public class FaPrivateHarassmentConfigController extends BaseController
{
    @Autowired
    private IFaPrivateHarassmentConfigService faPrivateHarassmentConfigService;

    /**
     * 查询私信追杀配置列表
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_config:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "accountId", required = false) String accountId,
                              @RequestParam(value = "isEnabled", required = false) Integer isEnabled,
                              @RequestParam(value = "accountName", required = false) String accountName)
    {
        FaPrivateHarassmentConfig faPrivateHarassmentConfig = new FaPrivateHarassmentConfig();
        faPrivateHarassmentConfig.setAccountId(accountId);
        faPrivateHarassmentConfig.setIsEnabled(isEnabled);
        faPrivateHarassmentConfig.setAccountName(accountName);
        startPage();
        List<FaPrivateHarassmentConfig> list = faPrivateHarassmentConfigService.selectFaPrivateHarassmentConfigList(faPrivateHarassmentConfig);
        return getDataTable(list);
    }

    /**
     * 导出私信追杀配置列表
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_config:export')")
    @Log(title = "私信追杀配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaPrivateHarassmentConfig faPrivateHarassmentConfig)
    {
        List<FaPrivateHarassmentConfig> list = faPrivateHarassmentConfigService.selectFaPrivateHarassmentConfigList(faPrivateHarassmentConfig);
        ExcelUtil<FaPrivateHarassmentConfig> util = new ExcelUtil<FaPrivateHarassmentConfig>(FaPrivateHarassmentConfig.class);
        util.exportExcel(response, list, "私信追杀配置数据");
    }

    /**
     * 获取私信追杀配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_config:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faPrivateHarassmentConfigService.selectFaPrivateHarassmentConfigById(id));
    }

    /**
     * 新增私信追杀配置
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_config:add')")
    @Log(title = "私信追杀配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaPrivateHarassmentConfig faPrivateHarassmentConfig)
    {
        return toAjax(faPrivateHarassmentConfigService.insertFaPrivateHarassmentConfig(faPrivateHarassmentConfig));
    }

    /**
     * 修改私信追杀配置
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_config:edit')")
    @Log(title = "私信追杀配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaPrivateHarassmentConfig faPrivateHarassmentConfig)
    {
        return toAjax(faPrivateHarassmentConfigService.updateFaPrivateHarassmentConfig(faPrivateHarassmentConfig));
    }

    /**
     * 删除私信追杀配置
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_config:remove')")
    @Log(title = "私信追杀配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faPrivateHarassmentConfigService.deleteFaPrivateHarassmentConfigByIds(ids));
    }
}
