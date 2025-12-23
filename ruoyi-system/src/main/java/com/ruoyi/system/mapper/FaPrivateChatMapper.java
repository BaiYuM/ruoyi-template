package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.FaPrivateChat;
import com.ruoyi.system.domain.FaPrivateChatMsg;
import org.apache.ibatis.annotations.Param;


import java.util.List;


/**
 * 私聊会话Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-22
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
     * 查询最近会话
     * 
     * @param account 抖音账号
     * @param hours 时间范围（小时）
     * @param limit 限制条数
     * @return 会话列表
     */
    public List<FaPrivateChat> selectRecentSessions(@Param("account") String account,
                                                    @Param("hours") int hours,
                                                    @Param("funds") Integer funds,
                                                    @Param("limit") int limit);

    /**
     * 查询会话消息
     * 
     * @param sessionId 会话ID
     * @return 消息列表
     */
    public List<FaPrivateChatMsg> selectSessionMessages(Long sessionId);

    /**
     * 查询评论用户账号列表
     * 
     * @return 抖音账号列表
     */
    public List<String> selectCommentUserAccounts();
}