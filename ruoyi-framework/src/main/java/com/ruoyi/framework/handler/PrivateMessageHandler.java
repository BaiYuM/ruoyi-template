package com.ruoyi.framework.handler;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.framework.handler.AbstractHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import vip.gadfly.tiktok.open.bean.message.TtOpWebhookMessage;
import vip.gadfly.tiktok.open.message.TtOpWebhookMessageHandleResult;

/**
 * 抖音私信消息处理器
 */
@Slf4j
public class PrivateMessageHandler extends AbstractHandler {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public TtOpWebhookMessageHandleResult handle(TtOpWebhookMessage message, java.util.Map<String, Object> context) {
        log.info("收到抖音私信消息: {}", JSONObject.toJSONString(message));

        try {
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
            String fromNickname = fromUser.getString("nickname");
            String toNickname = toUser.getString("nickname");

            if (fromUserId == null || toUserId == null || messageContent == null) {
                return new TtOpWebhookMessageHandleResult();
            }

            // 发布私信接收事件
            PrivateMessageReceivedEvent event = new PrivateMessageReceivedEvent(this,
                fromUserId, fromNickname, toUserId, toNickname, messageContent);
            eventPublisher.publishEvent(event);

            return new TtOpWebhookMessageHandleResult();

        } catch (Exception e) {
            log.error("处理私信消息失败", e);
            return new TtOpWebhookMessageHandleResult(); // 返回成功避免重试
        }
    }
}