package com.ruoyi.web.controller.privateChat;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.FaPrivateChat;
import com.ruoyi.system.domain.FaPrivateChatMsg;
import com.ruoyi.system.domain.FaClue;
import com.ruoyi.system.service.IFaPrivateChatService;
import com.ruoyi.system.service.IFaClueService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 抖音私信会话Controller
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/privateChat/private_chat")
public class FaPrivateChatController extends BaseController
{
    @Autowired
    private IFaPrivateChatService faPrivateChatService;

    @Autowired
    private IFaClueService faClueService;

    /**
     * 查询私聊会话列表
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat:list')")
    @GetMapping("/list")
    public TableDataInfo list(FaPrivateChat faPrivateChat)
    {
        startPage();
        List<FaPrivateChat> list = faPrivateChatService.selectFaPrivateChatList(faPrivateChat);
        return getDataTable(list);
    }

    /**
     * 导出私聊会话列表
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat:export')")
    @Log(title = "私聊会话", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FaPrivateChat faPrivateChat)
    {
        List<FaPrivateChat> list = faPrivateChatService.selectFaPrivateChatList(faPrivateChat);
        ExcelUtil<FaPrivateChat> util = new ExcelUtil<FaPrivateChat>(FaPrivateChat.class);
        util.exportExcel(response, list, "私聊会话数据");
    }

    /**
     * 获取私聊会话详细信息
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(faPrivateChatService.selectFaPrivateChatById(id));
    }

    /**
     * 新增私聊会话
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat:add')")
    @Log(title = "私聊会话", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FaPrivateChat faPrivateChat)
    {
        return toAjax(faPrivateChatService.insertFaPrivateChat(faPrivateChat));
    }

    /**
     * 修改私聊会话
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat:edit')")
    @Log(title = "私聊会话", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FaPrivateChat faPrivateChat)
    {
        return toAjax(faPrivateChatService.updateFaPrivateChat(faPrivateChat));
    }

    /**
     * 删除私聊会话
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat:remove')")
    @Log(title = "私聊会话", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(faPrivateChatService.deleteFaPrivateChatByIds(ids));
    }

    /**
     * 获取抖音号列表
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat:accounts')")
    @GetMapping("/accounts")
    public AjaxResult getAccounts() {
        List<String> accounts = faPrivateChatService.getCommentUserAccounts();
        return success(accounts);
    }

    /**
     * 获取会话列表
        *
        * <p>说明：本系统以 comment_user 表表示抖音侧用户/账号。
        * account 参数表示“授权抖音号”（comment_user.account），用于确定用哪个抖音号视角查看会话。</p>
        *
        * @param account 授权抖音号（comment_user.account）
        * @param funds 对方用户留资状态：0未留资，1已留资，不传则返回所有
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat:sessions')")
    @GetMapping("/sessions")
    public AjaxResult getSessions(@RequestParam(required = false) String account, @RequestParam(required = false) Integer funds) {
        List<FaPrivateChat> sessions = faPrivateChatService.getRecentSessions(account, 72, funds, 2000);
        return success(sessions);
    }

    /**
     * 获取聊天信息
     * @param sessionId 会话ID
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat:messages')")
    @GetMapping("/messages")
    public AjaxResult getMessages(@RequestParam Long sessionId) {
        List<FaPrivateChatMsg> messages = faPrivateChatService.getSessionMessages(sessionId);
        return success(messages);
    }
    /**
     * 获取关联线索
     * @param userId 用户ID (对应 comment_user.id)
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat:query')")
    @GetMapping("/clues")
    public AjaxResult getClues(@RequestParam Long userId) {
        FaClue query = new FaClue();
        query.setExpirationAiId(userId);
        List<FaClue> clues = faClueService.selectFaClueList(query);
        return success(clues);
    }

    /**
     * 发送私信消息
     */
    @PreAuthorize("@ss.hasPermi('privateChat:private_chat:add')")
    @Log(title = "发送私信", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public AjaxResult send(@RequestBody FaPrivateChatMsg msg)
    {
        return toAjax(faPrivateChatService.sendMessage(msg));
    }
}
