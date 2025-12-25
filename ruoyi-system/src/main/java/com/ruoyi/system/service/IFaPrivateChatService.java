package com.ruoyi.system.service;

import com.ruoyi.system.domain.FaPrivateChat;
import com.ruoyi.system.domain.FaPrivateChatMsg;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * 私聊会话Service接口
 * 
 * @author ruoyi
 * @date 2025-12-22
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
     * 获取账户相关的抠音账号和AI配置
     * 用于私信消息界面：返回账号列表时附带对应的AI授权配置ID
     *
     * @return 抠音账号和AI配置列表
     */
    public List<com.ruoyi.system.domain.vo.AccountAiConfigVO> getAccountsWithAiConfig();
    
    /**
     * 获取好友列表
     * 
     * @param expirationAiId AI授权配置ID
     * @param isLead 是否留资:0-未留资,1-已留资
     * @param limit 限制条数
     * @return 好友列表
     */
    public List<FaPrivateChat> getRecentSessions(Long expirationAiId, Integer isLead, int limit);
    
    /**
     * 根据用户ID查询会话
     * 
     * @param userId1 用户ID1
     * @param userId2 用户ID2
     * @return 会话
     */
    public FaPrivateChat selectChatByUserIds(Long userId1, Long userId2);
    
    /**
     * 获取会话消息列表
     * 
     * @param sessionId 会话ID
     * @return 消息列表
     */
    public List<FaPrivateChatMsg> getSessionMessages(Long sessionId);

    /**
     * 发送消息
     * 
     * @param msg 消息对象
     * @return 结果
     */
    public int sendMessage(FaPrivateChatMsg msg);
}