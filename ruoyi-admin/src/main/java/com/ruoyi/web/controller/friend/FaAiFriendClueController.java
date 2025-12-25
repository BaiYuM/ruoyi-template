package com.ruoyi.web.controller.friend;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.FaAiFriendClue;
import com.ruoyi.system.service.IFaAiFriendClueService;
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
 * 好友线索明细Controller
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
@RestController
@RequestMapping("/aiFriendClue/ai_friend_clue")
public class FaAiFriendClueController extends BaseController
{
    @Autowired
    private IFaAiFriendClueService faAiFriendClueService;

    /**
     * 查询好友线索明细列表
     */
    @PreAuthorize("@ss.hasPermi('aiFriendClue:ai_friend_clue:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaAiFriendClue faAiFriendClue)
    {
        startPage();
        List<FaAiFriendClue> list = faAiFriendClueService.selectFaAiFriendClueList(faAiFriendClue);
        return getDataTable(list);
    }

    /**
     * 导出好友线索明细列表
     */
    @PreAuthorize("@ss.hasPermi('aiFriendClue:ai_friend_clue:export')")
    @Log(title = "好友线索明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaAiFriendClue faAiFriendClue)
    {
        List<FaAiFriendClue> list = faAiFriendClueService.selectFaAiFriendClueList(faAiFriendClue);
        ExcelUtil<FaAiFriendClue> util = new ExcelUtil<FaAiFriendClue>(FaAiFriendClue.class);
        util.exportExcel(response, list, "好友线索明细数据");
    }

    /**
     * 获取好友线索明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('aiFriendClue:ai_friend_clue:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faAiFriendClueService.selectFaAiFriendClueById(id));
    }

    /**
     * 新增好友线索明细
     */
    @PreAuthorize("@ss.hasPermi('aiFriendClue:ai_friend_clue:add')")
    @Log(title = "好友线索明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaAiFriendClue faAiFriendClue)
    {
        return toAjax(faAiFriendClueService.insertFaAiFriendClue(faAiFriendClue));
    }

    /**
     * 修改好友线索明细
     */
    @PreAuthorize("@ss.hasPermi('aiFriendClue:ai_friend_clue:edit')")
    @Log(title = "好友线索明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaAiFriendClue faAiFriendClue)
    {
        return toAjax(faAiFriendClueService.updateFaAiFriendClue(faAiFriendClue));
    }

    /**
     * 删除好友线索明细
     */
    @PreAuthorize("@ss.hasPermi('aiFriendClue:ai_friend_clue:remove')")
    @Log(title = "好友线索明细", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faAiFriendClueService.deleteFaAiFriendClueByIds(ids));
    }

    /**
     * 根据好友ID查询线索列表
     * 用于私信消息界面：选择好友后，展示该好友的所有线索
     * 
     * @param friendId 好友ID
     * @return 线索列表
     */
    @PreAuthorize("@ss.hasPermi('aiFriendClue:ai_friend_clue:list')")
    @GetMapping("/byFriendId/{friendId}")
    public TableDataInfo getCluesByFriendId(@PathVariable("friendId") Long friendId)
    {
        startPage();
        FaAiFriendClue query = new FaAiFriendClue();
        query.setFriendId(friendId);
        List<FaAiFriendClue> list = faAiFriendClueService.selectFaAiFriendClueList(query);
        return getDataTable(list);
    }

    /**
     * 根据好友用户ID查询线索列表
     * 用于私信消息界面：根据comment_user的ID查询线索
     * 
     * @param friendUserId 好友用户ID (comment_user.id)
     * @return 线索列表
     */
    @PreAuthorize("@ss.hasPermi('aiFriendClue:ai_friend_clue:list')")
    @GetMapping("/byUserId/{friendUserId}")
    public TableDataInfo getCluesByUserId(@PathVariable("friendUserId") Long friendUserId)
    {
        startPage();
        FaAiFriendClue query = new FaAiFriendClue();
        query.setFriendUserId(friendUserId);
        List<FaAiFriendClue> list = faAiFriendClueService.selectFaAiFriendClueList(query);
        return getDataTable(list);
    }
}
