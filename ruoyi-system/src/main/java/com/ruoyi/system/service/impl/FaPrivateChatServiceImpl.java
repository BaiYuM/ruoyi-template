package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.FaPrivateChat;
import com.ruoyi.system.domain.FaPrivateChatMsg;
import com.ruoyi.system.mapper.FaPrivateChatMapper;
import com.ruoyi.system.mapper.FaPrivateChatMsgMapper;
import com.ruoyi.system.service.IFaPrivateChatService;
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
    
    @Autowired
    private FaPrivateChatMsgMapper faPrivateChatMsgMapper;

    /**
     * 查询私聊会话
     * 
     * @param id 私聊会话主键
     * @return 私聊会话
     */
    @Override
    public FaPrivateChat selectFaPrivateChatById(Long id)
    {
        FaPrivateChat faPrivateChat = faPrivateChatMapper.selectFaPrivateChatById(id);
        // 加载最后一条消息的内容
        if (faPrivateChat != null && faPrivateChat.getLastMsgId() != null) {
            FaPrivateChatMsg msg = faPrivateChatMsgMapper.selectFaPrivateChatMsgById(faPrivateChat.getLastMsgId());
            if (msg != null) {
                faPrivateChat.setLastMsgContent(msg.getMsgContent());
            }
        }
        return faPrivateChat;
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
        List<FaPrivateChat> list = faPrivateChatMapper.selectFaPrivateChatList(faPrivateChat);
        for (FaPrivateChat chat : list) {
            if (chat.getLastMsgId() != null) {
                FaPrivateChatMsg msg = faPrivateChatMsgMapper.selectFaPrivateChatMsgById(chat.getLastMsgId());
                if (msg != null) {
                    chat.setLastMsgContent(msg.getMsgContent());
                }
            }
        }
        return list;
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
        // 强制约定 userId1 < userId2，防止 A-B 和 B-A 产生重复会话记录
        if (faPrivateChat.getUserId1() != null && faPrivateChat.getUserId2() != null) {
            if (faPrivateChat.getUserId1() > faPrivateChat.getUserId2()) {
                Long temp = faPrivateChat.getUserId1();
                faPrivateChat.setUserId1(faPrivateChat.getUserId2());
                faPrivateChat.setUserId2(temp);
            }
        }
        // 使用雪花算法生成ID
        if (faPrivateChat.getId() == null) {
            faPrivateChat.setId(IdUtils.getSnowflakeId());
        }
        faPrivateChat.setCreateBy(SecurityUtils.getUsername());
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
     * <p>说明：本系统以 comment_user 表表示抖音侧用户/账号。
     * account 参数表示"授权抖音号"（comment_user.account），用于确定用哪个抖音号视角查看会话。</p>
     *
     * @param account 授权抖音号（comment_user.account）
     * @param hours 时间范围（小时）
     * @param limit 限制条数
     * @return 会话列表
     */
    @Override
    public List<FaPrivateChat> getRecentSessions(String account, int hours, int limit) {
        List<FaPrivateChat> list = faPrivateChatMapper.selectRecentSessions(account, hours, null, limit);
        // 为每个会话加载最后一条消息的内容
        for (FaPrivateChat chat : list) {
            if (chat.getLastMsgId() != null) {
                FaPrivateChatMsg msg = faPrivateChatMsgMapper.selectFaPrivateChatMsgById(chat.getLastMsgId());
                if (msg != null) {
                    chat.setLastMsgContent(msg.getMsgContent());
                }
            }
        }
        return list;
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

    /**
     * 发送消息
     * 
     * @param msg 消息对象
     * @return 结果
     */
    @Override
    public int sendMessage(FaPrivateChatMsg msg) {
        msg.setCreateTime(DateUtils.getNowDate());
        // 1. 插入消息记录
        int rows = faPrivateChatMsgMapper.insertFaPrivateChatMsg(msg);
        
        // 2. 更新会话的最后一条消息ID和最后发送时间
        if (rows > 0 && msg.getChatId() != null) {
            FaPrivateChat chat = new FaPrivateChat();
            chat.setId(msg.getChatId());
            chat.setLastMsgId(msg.getId());
            chat.setLastSendTime(msg.getCreateTime());
            faPrivateChatMapper.updateFaPrivateChat(chat);
            
            // 3. 调用抖音API发送消息 (此处为占位，实际需根据SDK或API实现)
            System.out.println("正在调用抖音API发送消息给用户 " + msg.getReceiverId() + ": " + msg.getMsgContent());
            // TODO: 接入抖音私信API
        }
        return rows;
    }
}