package com.ruoyi.system.config;

import com.ruoyi.system.handler.PrivateMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import vip.gadfly.tiktok.open.message.TtOpWebhookMessageRouter;

import javax.annotation.PostConstruct;

/**
 * 抖音开放平台 Handler 配置
 */
@Configuration
@RequiredArgsConstructor
public class TtOpHandlerConfig {

    private final TtOpWebhookMessageRouter messageRouter;
    private final PrivateMessageHandler privateMessageHandler;

    @PostConstruct
    public void configureMessageRouter() {

        if (messageRouter != null) {
            // 处理私信接收消息事件
            messageRouter.rule().async(true).event("im.receive_msg").msgType("text").addHandler(privateMessageHandler).end();
        }
    }
}