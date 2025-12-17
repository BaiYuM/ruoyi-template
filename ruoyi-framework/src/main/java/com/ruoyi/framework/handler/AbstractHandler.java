package com.ruoyi.framework.handler;

import org.slf4j.Logger;
import vip.gadfly.tiktok.open.message.ITtOpWebhookMessageHandler;

public abstract class AbstractHandler implements ITtOpWebhookMessageHandler {
    protected static final Logger log = org.slf4j.LoggerFactory.getLogger(AbstractHandler.class);
}
