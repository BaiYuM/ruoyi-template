package com.ruoyi.web.controller.friend;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.FaAiFriend;
import com.ruoyi.system.service.IFaAiFriendService;
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
 * 抖音授权账号好友Controller
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
@RestController
@RequestMapping("/aiFriend/ai_friend")
public class FaAiFriendController extends BaseController
{
    @Autowired
    private IFaAiFriendService faAiFriendService;

    /**
     * 查询抖音授权账号好友列表
     */
    @PreAuthorize("@ss.hasPermi('aiFriend:ai_friend:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaAiFriend faAiFriend)
    {
        startPage();
        List<FaAiFriend> list = faAiFriendService.selectFaAiFriendList(faAiFriend);
        return getDataTable(list);
    }

    /**
     * 导出抖音授权账号好友列表
     */
    @PreAuthorize("@ss.hasPermi('aiFriend:ai_friend:export')")
    @Log(title = "抖音授权账号好友", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaAiFriend faAiFriend)
    {
        List<FaAiFriend> list = faAiFriendService.selectFaAiFriendList(faAiFriend);
        ExcelUtil<FaAiFriend> util = new ExcelUtil<FaAiFriend>(FaAiFriend.class);
        util.exportExcel(response, list, "抖音授权账号好友数据");
    }

    /**
     * 获取抖音授权账号好友详细信息
     */
    @PreAuthorize("@ss.hasPermi('aiFriend:ai_friend:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faAiFriendService.selectFaAiFriendById(id));
    }

    /**
     * 新增抖音授权账号好友
     */
    @PreAuthorize("@ss.hasPermi('aiFriend:ai_friend:add')")
    @Log(title = "抖音授权账号好友", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaAiFriend faAiFriend)
    {
        return toAjax(faAiFriendService.insertFaAiFriend(faAiFriend));
    }

    /**
     * 修改抖音授权账号好友
     */
    @PreAuthorize("@ss.hasPermi('aiFriend:ai_friend:edit')")
    @Log(title = "抖音授权账号好友", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaAiFriend faAiFriend)
    {
        return toAjax(faAiFriendService.updateFaAiFriend(faAiFriend));
    }

    /**
     * 删除抖音授权账号好友
     */
    @PreAuthorize("@ss.hasPermi('aiFriend:ai_friend:remove')")
    @Log(title = "抖音授权账号好友", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {        return toAjax(faAiFriendService.deleteFaAiFriendByIds(ids));
    }

    /**
     * 根据AI授权配置ID查询好友列表
     * 用于私信消息界面：选择授权账号后，获取该账号的好友列表
     * 
     * @param expirationAiId AI授权配置ID
     * @return 好友列表
     */
    @PreAuthorize("@ss.hasPermi('aiFriend:ai_friend:list')")
    @GetMapping("/byAiId/{expirationAiId}")
    public TableDataInfo getFriendsByAiId(@PathVariable("expirationAiId") Long expirationAiId)
    {
        startPage();
        FaAiFriend query = new FaAiFriend();
        query.setExpirationAiId(expirationAiId);
        List<FaAiFriend> list = faAiFriendService.selectFaAiFriendList(query);
        return getDataTable(list);
    }

    /**
     * 根据好友用户ID查询好友信息
     * 用于私信消息界面：选择好友后，获取好友详细信息
     * 
     * @param expirationAiId AI授权配置ID
     * @param friendUserId 好友用户ID
     * @return 好友信息
     */
    @PreAuthorize("@ss.hasPermi('aiFriend:ai_friend:query')")
    @GetMapping("/byUserId")
    public AjaxResult getFriendByUserId(
            @RequestParam("expirationAiId") Long expirationAiId,
            @RequestParam("friendUserId") Long friendUserId)
    {
        FaAiFriend query = new FaAiFriend();
        query.setExpirationAiId(expirationAiId);
        query.setFriendUserId(friendUserId);
        List<FaAiFriend> list = faAiFriendService.selectFaAiFriendList(query);
        if (list != null && !list.isEmpty()) {
            return success(list.get(0));
        }
        return success(null);
    }
}
