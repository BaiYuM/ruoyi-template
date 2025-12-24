package com.ruoyi.framework.handler;

import com.ruoyi.framework.handler.AbstractHandler;
import org.springframework.stereotype.Service;
import vip.gadfly.tiktok.open.bean.message.TtOpWebhookMessage;
import vip.gadfly.tiktok.open.message.TtOpWebhookMessageHandleResult;

import java.util.Map;

@Service
public class VerifyWebhookHandler extends AbstractHandler {
    @Override
    public TtOpWebhookMessageHandleResult handle(TtOpWebhookMessage message, Map<String, Object> map) {
        log.info("VerifyWebhookChallenge为：{}", message.getContent().getChallenge());
        TtOpWebhookMessageHandleResult result = new TtOpWebhookMessageHandleResult();
        result.setHandleResult(message.getContent());
        return result;
    }
}
