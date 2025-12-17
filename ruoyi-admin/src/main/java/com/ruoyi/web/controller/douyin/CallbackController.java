package com.ruoyi.web.controller.douyin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import vip.gadfly.tiktok.core.enums.TtOpConst;
import vip.gadfly.tiktok.open.bean.message.TtOpWebhookMessage;
import vip.gadfly.tiktok.open.common.ITtOpBaseService;
import vip.gadfly.tiktok.open.message.TtOpWebhookMessageResult;
import vip.gadfly.tiktok.open.message.TtOpWebhookMessageRouter;

@RestController
@Slf4j
public class CallbackController {
    @Autowired
    private ITtOpBaseService ttOpBaseService;
    @Autowired
    private TtOpWebhookMessageRouter messageRouter;

    @PostMapping("/tiktok_open_webhook/{clientKey}")
    public Object handlePostTtOpWebhook(@RequestBody String body, @RequestHeader HttpHeaders headers,
                                        @PathVariable String clientKey) {
        if (!ttOpBaseService.switchover(clientKey)) {
            throw new IllegalArgumentException(String.format("未找到对应clientKey=[%s]的配置，请核实！", clientKey));
        }
        if (!ttOpBaseService.checkWebhookSignature(headers.getFirst("X-Douyin-Signature"), body)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        TtOpWebhookMessage message = TtOpWebhookMessage.fromJson(body);
        // 抖音webhook的消息id放在了请求头中，因此sdk不能直接读取，需要自行传入
        message.setMsgId(headers.getFirst("Msg-Id"));
        // 抖音的webhook验证要求返回的内容是一个包含challenge的json，相比于走路由器，直接处理消息后做个if直接return更简单
        if (message.getEvent().equalsIgnoreCase(TtOpConst.WebhookEventType.VERIFY_WEBHOOK)) {
            return message.getContent();
        }
        TtOpWebhookMessageResult result = messageRouter.route(message);
        log.info("result:{}", result);
        return result.getDefaultResult();
    }
}
