package com.ruoyi.web.controller.expirationAi;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.ExpirationAi;
import com.ruoyi.system.service.IExpirationAiService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 授权账号配置Controller
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/aiCuServer/expirationAi")
public class ExpirationAiController extends BaseController
{
    @Autowired
    private IExpirationAiService expirationAiService;

    /**
     * 查询授权账号配置列表
     */
    @PreAuthorize("@ss.hasPermi('tikTok:expirationAi:list')")
    @GetMapping("/list")
    public TableDataInfo list(ExpirationAi expirationAi, @RequestParam(value = "createTime", required = false) String[] createTime)
    {
        // 兼容前端传入的 createTime 范围（数组：[begin, end]），转换并设置到实体的 beginCreateTime/endCreateTime
        if (createTime != null && createTime.length == 2) {
            expirationAi.setBeginCreateTime(DateUtils.parseDate(createTime[0]));
            expirationAi.setEndCreateTime(DateUtils.parseDate(createTime[1]));
        }
        startPage();
        List<ExpirationAi> list = expirationAiService.selectExpirationAiList(expirationAi);
        return getDataTable(list);
    }

    /**
     * 查询授权账户头像，昵称及账户配置信息
     */
    @PreAuthorize("@ss.hasPermi('tikTok:expirationAiWithUser:list')")
    @GetMapping("/getExpirationAiInfo")
    @PostMapping("/getExpirationAiInfo")
    public TableDataInfo expirationAiWithUserList(ExpirationAi expirationAi,
                                                  @RequestParam(value = "createTime", required = false) String[] createTime,
                                                  @RequestBody(required = false) Map<String, Object> body)
    {
        // 优先使用 query param，如果没有则尝试从 JSON body 中读取 createTime 字段（数组：[begin, end]）
        if (createTime != null && createTime.length == 2) {
            expirationAi.setBeginCreateTime(DateUtils.parseDate(createTime[0]));
            expirationAi.setEndCreateTime(DateUtils.parseDate(createTime[1]));
        } else if (body != null && body.get("createTime") != null) {
            Object ct = body.get("createTime");
            if (ct instanceof List) {
                List<?> arr = (List<?>) ct;
                if (arr.size() == 2) {
                    expirationAi.setBeginCreateTime(DateUtils.parseDate(String.valueOf(arr.get(0))));
                    expirationAi.setEndCreateTime(DateUtils.parseDate(String.valueOf(arr.get(1))));
                }
            } else if (ct instanceof String[]) {
                String[] arr = (String[]) ct;
                if (arr.length == 2) {
                    expirationAi.setBeginCreateTime(DateUtils.parseDate(arr[0]));
                    expirationAi.setEndCreateTime(DateUtils.parseDate(arr[1]));
                }
            }
        }

        startPage();
        List<ExpirationAi> list = expirationAiService.selectExpirationAiWithUserList(expirationAi);
        return getDataTable(list);
    }

    /**
     * 导出授权账号配置列表
     */
    @PreAuthorize("@ss.hasPermi('tikTok:expirationAi:export')")
    @Log(title = "授权账号配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ExpirationAi expirationAi)
    {
        List<ExpirationAi> list = expirationAiService.selectExpirationAiList(expirationAi);
        ExcelUtil<ExpirationAi> util = new ExcelUtil<ExpirationAi>(ExpirationAi.class);
        util.exportExcel(response, list, "授权账号配置数据");
    }

    /**
     * 获取授权账号配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('tikTok:expirationAi:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(expirationAiService.selectExpirationAiById(id));
    }

    /**
     * 新增授权账号配置
     */
    @PreAuthorize("@ss.hasPermi('tikTok:expirationAi:add')")
    @Log(title = "授权账号配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ExpirationAi expirationAi)
    {
        return toAjax(expirationAiService.insertExpirationAi(expirationAi));
    }

    /**
     * 修改授权账号配置
     */
    @PreAuthorize("@ss.hasPermi('tikTok:expirationAi:edit')")
    @Log(title = "授权账号配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ExpirationAi expirationAi)
    {
        return toAjax(expirationAiService.updateExpirationAi(expirationAi));
    }

    /**
     * 删除授权账号配置
     */
    @PreAuthorize("@ss.hasPermi('tikTok:expirationAi:remove')")
    @Log(title = "授权账号配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(expirationAiService.deleteExpirationAiByIds(ids));
    }
}
