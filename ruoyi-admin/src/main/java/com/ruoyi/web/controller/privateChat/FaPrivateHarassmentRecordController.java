package com.ruoyi.web.controller.privateChat;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.FaPrivateHarassmentRecord;
import com.ruoyi.system.service.IFaPrivateHarassmentRecordService;
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
 * 私信追杀执行记录Controller
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/privateChat/private_har_record")
public class FaPrivateHarassmentRecordController extends BaseController
{
    @Autowired
    private IFaPrivateHarassmentRecordService faPrivateHarassmentRecordService;

    /**
     * 查询私信追杀执行记录列表
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaPrivateHarassmentRecord faPrivateHarassmentRecord)
    {
        startPage();
        List<FaPrivateHarassmentRecord> list = faPrivateHarassmentRecordService.selectFaPrivateHarassmentRecordList(faPrivateHarassmentRecord);
        return getDataTable(list);
    }

    /**
     * 导出私信追杀执行记录列表
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_record:export')")
    @Log(title = "私信追杀执行记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaPrivateHarassmentRecord faPrivateHarassmentRecord)
    {
        List<FaPrivateHarassmentRecord> list = faPrivateHarassmentRecordService.selectFaPrivateHarassmentRecordList(faPrivateHarassmentRecord);
        ExcelUtil<FaPrivateHarassmentRecord> util = new ExcelUtil<FaPrivateHarassmentRecord>(FaPrivateHarassmentRecord.class);
        util.exportExcel(response, list, "私信追杀执行记录数据");
    }

    /**
     * 获取私信追杀执行记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faPrivateHarassmentRecordService.selectFaPrivateHarassmentRecordById(id));
    }

    /**
     * 新增私信追杀执行记录
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_record:add')")
    @Log(title = "私信追杀执行记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaPrivateHarassmentRecord faPrivateHarassmentRecord)
    {
        return toAjax(faPrivateHarassmentRecordService.insertFaPrivateHarassmentRecord(faPrivateHarassmentRecord));
    }

    /**
     * 修改私信追杀执行记录
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_record:edit')")
    @Log(title = "私信追杀执行记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaPrivateHarassmentRecord faPrivateHarassmentRecord)
    {
        return toAjax(faPrivateHarassmentRecordService.updateFaPrivateHarassmentRecord(faPrivateHarassmentRecord));
    }

    /**
     * 删除私信追杀执行记录
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_har_record:remove')")
    @Log(title = "私信追杀执行记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faPrivateHarassmentRecordService.deleteFaPrivateHarassmentRecordByIds(ids));
    }
}
