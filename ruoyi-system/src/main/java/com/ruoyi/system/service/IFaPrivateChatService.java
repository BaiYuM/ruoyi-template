package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.FaPrivateChat;

/**
 * 私聊会话Service接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface IFaPrivateChatService 
{
    /**
     * 查询私聊会话
     * 
     * @param id 私聊会话主键
     * @return 私聊会话
     */
    public FaPrivateChat selectFaPrivateChatById(Long id);

    /**
     * 查询私聊会话列表
     * 
     * @param faPrivateChat 私聊会话
     * @return 私聊会话集合
     */
    public List<FaPrivateChat> selectFaPrivateChatList(FaPrivateChat faPrivateChat);

    /**
     * 新增私聊会话
     * 
     * @param faPrivateChat 私聊会话
     * @return 结果
     */
    public int insertFaPrivateChat(FaPrivateChat faPrivateChat);

    /**
     * 修改私聊会话
     * 
     * @param faPrivateChat 私聊会话
     * @return 结果
     */
    public int updateFaPrivateChat(FaPrivateChat faPrivateChat);

    /**
     * 批量删除私聊会话
     * 
     * @param ids 需要删除的私聊会话主键集合
     * @return 结果
     */
    public int deleteFaPrivateChatByIds(Long[] ids);

    /**
     * 删除私聊会话信息
     * 
     * @param id 私聊会话主键
     * @return 结果
     */
    public int deleteFaPrivateChatById(Long id);

    /**
     * 获取抖音号列表
     * @return 抖音号集合
     */
    List<String> getCommentUserAccounts();

    /**
     * 获取72小时内的会话列表
     * @param account 抖音号
     * @param hours 时间范围（小时）
     * @param limit 最大条数
     * @return 会话集合
     */
    List<FaPrivateChat> getRecentSessions(String account, int hours, int limit);

    /**
     * 获取会话的聊天信息
     * @param sessionId 会话ID
     * @return 聊天信息集合
     */
    List<Map<String, Object>> getSessionMessages(Long sessionId);
}
