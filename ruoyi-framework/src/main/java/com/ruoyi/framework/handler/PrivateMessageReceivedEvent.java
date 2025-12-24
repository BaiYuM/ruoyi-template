package com.ruoyi.framework.handler;

import org.springframework.context.ApplicationEvent;

/**
 * 私信接收事件
 */
public class PrivateMessageReceivedEvent extends ApplicationEvent {

    private final String fromUserId;
    private final String fromNickname;
    private final String toUserId;
    private final String toNickname;
    private final String messageContent;

    public PrivateMessageReceivedEvent(Object source, String fromUserId, String fromNickname,
                                     String toUserId, String toNickname, String messageContent) {
        super(source);
        this.fromUserId = fromUserId;
        this.fromNickname = fromNickname;
        this.toUserId = toUserId;
        this.toNickname = toNickname;
        this.messageContent = messageContent;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getFromNickname() {
        return fromNickname;
    }

    public String getToUserId() {
        return toUserId;
    }

    public String getToNickname() {
        return toNickname;
    }

    public String getMessageContent() {
        return messageContent;
    }
}