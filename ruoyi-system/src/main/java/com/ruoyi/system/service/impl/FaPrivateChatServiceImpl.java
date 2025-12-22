package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.FaPrivateChat;
import com.ruoyi.system.domain.FaPrivateChatMsg;
import com.ruoyi.system.mapper.FaPrivateChatMapper;
import com.ruoyi.system.service.IFaPrivateChatService;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 私聊会话Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
@Service
public class FaPrivateChatServiceImpl implements IFaPrivateChatService
{
    @Autowired
    private FaPrivateChatMapper faPrivateChatMapper;

    /**
     * 查询私聊会话
     * 
     * @param id 私聊会话主键
     * @return 私聊会话
     */
    @Override
    public FaPrivateChat selectFaPrivateChatById(Long id)
    {
        return faPrivateChatMapper.selectFaPrivateChatById(id);
    }

    /**
     * 查询私聊会话列表
     * 
     * @param faPrivateChat 私聊会话
     * @return 私聊会话
     */
    @Override
    public List<FaPrivateChat> selectFaPrivateChatList(FaPrivateChat faPrivateChat)
    {
        return faPrivateChatMapper.selectFaPrivateChatList(faPrivateChat);
    }

    /**
     * 新增私聊会话
     * 
     * @param faPrivateChat 私聊会话
     * @return 结果
     */
    @Override
    public int insertFaPrivateChat(FaPrivateChat faPrivateChat)
    {
        // 使用雪花算法生成ID
        if (faPrivateChat.getId() == null) {
            faPrivateChat.setId(IdUtils.getSnowflakeId());
        }
        faPrivateChat.setCreateTime(DateUtils.getNowDate());
        return faPrivateChatMapper.insertFaPrivateChat(faPrivateChat);
    }

    /**
     * 修改私聊会话
     * 
     * @param faPrivateChat 私聊会话
     * @return 结果
     */
    @Override
    public int updateFaPrivateChat(FaPrivateChat faPrivateChat)
    {
        faPrivateChat.setUpdateTime(DateUtils.getNowDate());
        return faPrivateChatMapper.updateFaPrivateChat(faPrivateChat);
    }

    /**
     * 批量删除私聊会话
     * 
     * @param ids 需要删除的私聊会话主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatByIds(Long[] ids)
    {
        return faPrivateChatMapper.deleteFaPrivateChatByIds(ids);
    }

    /**
     * 删除私聊会话信息
     * 
     * @param id 私聊会话主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatById(Long id)
    {
        return faPrivateChatMapper.deleteFaPrivateChatById(id);
    }
    
    /**
     * 获取评论用户账号列表
     *
     * @return 抖音账号列表
     */
    @Override
    public List<String> getCommentUserAccounts() {
        return faPrivateChatMapper.selectCommentUserAccounts();
    }

    /**
     * 获取最近会话列表
     *
     * @param account 抖音账号
     * @param hours 时间范围（小时）
     * @param limit 限制条数
     * @return 会话列表
     */
    @Override
    public List<FaPrivateChat> getRecentSessions(String account, int hours, int limit) {
        return faPrivateChatMapper.selectRecentSessions(account, hours, limit);
    }

    /**
     * 获取会话消息列表
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @Override
    public List<FaPrivateChatMsg> getSessionMessages(Long sessionId) {
        return faPrivateChatMapper.selectSessionMessages(sessionId);
    }
}