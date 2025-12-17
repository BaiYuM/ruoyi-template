package com.ruoyi.framework.handler;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import vip.gadfly.tiktok.open.bean.message.TtOpWebhookMessage;
import vip.gadfly.tiktok.open.message.TtOpWebhookMessageHandleResult;

import java.util.Map;

@Service
public class LogHandler extends AbstractHandler {
    @Override
    public TtOpWebhookMessageHandleResult handle(TtOpWebhookMessage ttOpWebhookMessage, Map<String, Object> map) {
        log.info("接收到抖音webhook请求消息，内容：{}", JSONObject.toJSONString(ttOpWebhookMessage));
        return null;
    }
}
