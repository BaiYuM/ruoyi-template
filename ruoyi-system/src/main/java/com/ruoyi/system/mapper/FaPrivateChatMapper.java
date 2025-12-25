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
     * 查询好友列表
     * 
     * @param expirationAiId AI授权配置ID
     * @param isLead 是否留资:0-未留资,1-已留资
     * @param limit 限制条数
     * @return 好友列表
     */
    public List<FaPrivateChat> selectRecentSessions(@Param("expirationAiId") Long expirationAiId,
                                                    @Param("isLead") Integer isLead,
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
     * @return 折音账号列表
     */
    public List<String> selectCommentUserAccounts();
        
    /**
     * 查询账户相关的抠音账号和AI配置
     * 用于私信消息界面：返回账号列表时附带对应的AI授权配置ID
     * 
     * @return 抠音账号和AI認路信息列表
     */
    public List<com.ruoyi.system.domain.vo.AccountAiConfigVO> selectAccountsWithAiConfig();

    /**
     * 根据用户ID查询会话
     * 
     * @param userId1 用户ID1
     * @param userId2 用户ID2
     * @return 会话
     */
    public FaPrivateChat selectChatByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

}