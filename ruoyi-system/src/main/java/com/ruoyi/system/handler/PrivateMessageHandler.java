package com.ruoyi.system.handler;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.CommentUser;
import com.ruoyi.system.domain.FaPrivateChat;
import com.ruoyi.system.domain.FaPrivateChatMsg;
import com.ruoyi.system.service.ICommentUserService;
import com.ruoyi.system.service.IFaPrivateChatService;
import com.ruoyi.system.service.IFaPrivateHarassmentRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vip.gadfly.tiktok.open.bean.message.TtOpWebhookMessage;
import vip.gadfly.tiktok.open.message.ITtOpWebhookMessageHandler;
import vip.gadfly.tiktok.open.message.TtOpWebhookMessageHandleResult;

import java.util.Date;
import java.util.List;

/**
 * 抖音私信消息处理器
 */
@Slf4j
@Component
public class PrivateMessageHandler implements ITtOpWebhookMessageHandler {

    @Autowired
    private IFaPrivateChatService chatService;

    @Autowired
    private ICommentUserService commentUserService;

    @Autowired
    private IFaPrivateHarassmentRecordService harassmentRecordService;

    @Override
    public TtOpWebhookMessageHandleResult handle(TtOpWebhookMessage message, java.util.Map<String, Object> context) {
        log.info("收到抖音私信消息: {}", JSONObject.toJSONString(message));

        try {
            // 获取clientKey
            String clientKey = (String) context.get("clientKey");
            if (clientKey == null) {
                log.warn("无法获取clientKey，跳过消息处理");
                return new TtOpWebhookMessageHandleResult();
            }

            // 解析消息内容
            String contentStr = message.getContent().toString();
            JSONObject content = JSONObject.parseObject(contentStr);
            if (content == null) {
                return new TtOpWebhookMessageHandleResult();
            }

            // 获取消息类型
            String msgType = content.getString("messageType");
            if (!"text".equals(msgType)) {
                // 只处理文本消息
                return new TtOpWebhookMessageHandleResult();
            }

            // 获取发送者和接收者信息
            JSONObject fromUser = content.getJSONObject("fromUser");
            JSONObject toUser = content.getJSONObject("toUser");

            if (fromUser == null || toUser == null) {
                return new TtOpWebhookMessageHandleResult();
            }

            String fromUserId = fromUser.getString("userId");
            String toUserId = toUser.getString("userId");
            String messageContent = content.getString("content");

            if (fromUserId == null || toUserId == null || messageContent == null) {
                return new TtOpWebhookMessageHandleResult();
            }

            // 查找或创建用户记录
            CommentUser fromUserRecord = getOrCreateUser(fromUserId, fromUser.getString("nickname"));
            CommentUser toUserRecord = getOrCreateUser(toUserId, toUser.getString("nickname"));
            if (toUserRecord != null) {
                // 设置接收者账号为clientKey
                toUserRecord.setAccount(clientKey);
            }

            if (fromUserRecord == null || toUserRecord == null) {
                log.warn("无法创建用户记录，跳过消息处理");
                return new TtOpWebhookMessageHandleResult();
            }

            // 查找或创建会话
            FaPrivateChat chat = findOrCreateChat(fromUserRecord.getId(), toUserRecord.getId(), clientKey);
            if (chat == null) {
                log.warn("无法创建会话，跳过消息处理");
                return new TtOpWebhookMessageHandleResult();
            }

            // 创建消息记录
            FaPrivateChatMsg msg = new FaPrivateChatMsg();
            msg.setChatId(chat.getId());
            msg.setSenderId(fromUserRecord.getId());
            msg.setReceiverId(toUserRecord.getId());
            msg.setMsgType(0L); // 文本消息
            msg.setMsgContent(messageContent);
            msg.setSendTime(new Date());

            // 保存消息
            chatService.sendMessage(msg);

            // 触发私信追杀（如果接收者是我们的账号）
            harassmentRecordService.startHarassment(chat.getId(), fromUserRecord.getId(), clientKey);

            return new TtOpWebhookMessageHandleResult();

        } catch (Exception e) {
            log.error("处理私信消息失败", e);
            return new TtOpWebhookMessageHandleResult(); // 返回成功避免重试
        }
    }

    /**
     * 获取或创建用户记录
     */
    private CommentUser getOrCreateUser(String openId, String nickname) {
        // 先查找是否存在
        CommentUser query = new CommentUser();
        query.setOpenId(openId);
        List<CommentUser> users = commentUserService.selectCommentUserList(query);

        if (!users.isEmpty()) {
            return users.get(0);
        }

        // 创建新用户
        CommentUser newUser = new CommentUser();
        newUser.setOpenId(openId);
        newUser.setNickname(nickname);
        newUser.setPlatform("douyin");
        newUser.setType("user");

        try {
            commentUserService.insertCommentUser(newUser);
            return newUser;
        } catch (Exception e) {
            log.error("创建用户失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 查找或创建会话
     */
    private FaPrivateChat findOrCreateChat(Long userId1, Long userId2, String accountId) {
        // 查找现有会话
        FaPrivateChat query = new FaPrivateChat();
        query.setUserId1(Math.min(userId1, userId2));
        query.setUserId2(Math.max(userId1, userId2));

        List<FaPrivateChat> chats = chatService.selectFaPrivateChatList(query);
        if (!chats.isEmpty()) {
            return chats.get(0);
        }

        // 创建新会话
        FaPrivateChat newChat = new FaPrivateChat();
        newChat.setUserId1(Math.min(userId1, userId2));
        newChat.setUserId2(Math.max(userId1, userId2));
        newChat.setDouyinId(accountId);

        try {
            chatService.insertFaPrivateChat(newChat);
            return newChat;
        } catch (Exception e) {
            log.error("创建会话失败: {}", e.getMessage());
            return null;
        }
    }
}