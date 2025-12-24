package com.ruoyi.framework.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.gadfly.tiktok.open.message.ITtOpWebhookMessageHandler;

public abstract class AbstractHandler implements ITtOpWebhookMessageHandler {
    protected static final Logger log = LoggerFactory.getLogger(AbstractHandler.class);
}
