package com.ruoyi.web.controller.welWord;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.FaWelcomeWordRecord;
import com.ruoyi.system.service.IFaWelcomeWordRecordService;
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
 * 欢迎词执行记录Controller
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/welWord/wel_word_record")
public class FaWelcomeWordRecordController extends BaseController
{
    @Autowired
    private IFaWelcomeWordRecordService faWelcomeWordRecordService;

    /**
     * 查询欢迎词执行记录列表
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaWelcomeWordRecord faWelcomeWordRecord)
    {
        startPage();
        List<FaWelcomeWordRecord> list = faWelcomeWordRecordService.selectFaWelcomeWordRecordList(faWelcomeWordRecord);
        return getDataTable(list);
    }

    /**
     * 导出欢迎词执行记录列表
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_record:export')")
    @Log(title = "欢迎词执行记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaWelcomeWordRecord faWelcomeWordRecord)
    {
        List<FaWelcomeWordRecord> list = faWelcomeWordRecordService.selectFaWelcomeWordRecordList(faWelcomeWordRecord);
        ExcelUtil<FaWelcomeWordRecord> util = new ExcelUtil<FaWelcomeWordRecord>(FaWelcomeWordRecord.class);
        util.exportExcel(response, list, "欢迎词执行记录数据");
    }

    /**
     * 获取欢迎词执行记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faWelcomeWordRecordService.selectFaWelcomeWordRecordById(id));
    }

    /**
     * 新增欢迎词执行记录
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_record:add')")
    @Log(title = "欢迎词执行记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaWelcomeWordRecord faWelcomeWordRecord)
    {
        return toAjax(faWelcomeWordRecordService.insertFaWelcomeWordRecord(faWelcomeWordRecord));
    }

    /**
     * 修改欢迎词执行记录
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_record:edit')")
    @Log(title = "欢迎词执行记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaWelcomeWordRecord faWelcomeWordRecord)
    {
        return toAjax(faWelcomeWordRecordService.updateFaWelcomeWordRecord(faWelcomeWordRecord));
    }

    /**
     * 删除欢迎词执行记录
     */
    @PreAuthorize("@ss.hasPermi('welWord:wel_word_record:remove')")
    @Log(title = "欢迎词执行记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faWelcomeWordRecordService.deleteFaWelcomeWordRecordByIds(ids));
    }
}
