package com.ruoyi.framework.handler;

import org.springframework.stereotype.Service;
import vip.gadfly.tiktok.open.bean.message.TtOpWebhookMessage;
import vip.gadfly.tiktok.open.message.TtOpWebhookMessageHandleResult;

import java.util.Map;

@Service
public class NullHandler extends AbstractHandler {
    @Override
    public TtOpWebhookMessageHandleResult handle(TtOpWebhookMessage ttOpWebhookMessage, Map<String, Object> map) {
        log.info("进入了默认处理");
        return null;
    }
}
