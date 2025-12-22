package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.FaPrivateChat;

/**
 * 私聊会话Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface FaPrivateChatMapper 
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
     * 删除私聊会话
     * 
     * @param id 私聊会话主键
     * @return 结果
     */
    public int deleteFaPrivateChatById(Long id);

    /**
     * 批量删除私聊会话
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFaPrivateChatByIds(Long[] ids);

    /**
     * 查询72小时内的会话列表
     * @param account 抖音号
     * @param hours 时间范围（小时）
     * @param limit 最大条数
     * @return 会话集合
     */
    List<FaPrivateChat> selectRecentSessions(String account, int hours, int limit);

    /**
     * 查询会话的聊天信息
     * @param sessionId 会话ID
     * @return 聊天信息集合
     */
    List<Map<String, Object>> selectSessionMessages(Long sessionId);

    /**
     * 查询所有评论用户的抖音号
     * @return 抖音号列表
     */
    List<String> selectCommentUserAccounts();
}
