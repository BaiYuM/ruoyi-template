package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 私聊消息对象 fa_private_chat_msg
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
public class FaPrivateChatMsg extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID（雪花算法） */
    private Long id;

    /** 会话ID（关联fa_private_chat.id） */
    @Excel(name = "会话ID", readConverterExp = "关=联fa_private_chat.id")
    private Long chatId;

    /** 发送人ID（关联sys_user.id） */
    @Excel(name = "发送人ID", readConverterExp = "关=联sys_user.id")
    private Long senderId;

    /** 接收人ID（关联sys_user.id） */
    @Excel(name = "接收人ID", readConverterExp = "关=联sys_user.id")
    private Long receiverId;

    /** 消息类型（0=文本 1=图片 2=文件 3=语音） */
    @Excel(name = "消息类型", readConverterExp = "0==文本,1==图片,2==文件,3==语音")
    private Long msgType;

    /** 消息内容（文本存内容，文件/图片存URL） */
    @Excel(name = "消息内容", readConverterExp = "文=本存内容，文件/图片存URL")
    private String msgContent;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date sendTime;

    /** 是否撤回（0=否 1=是） */
    @Excel(name = "是否撤回", readConverterExp = "0==否,1==是")
    private Long isRecalled;

    /** 撤回时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "撤回时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date recallTime;

    /** 删除标志（0=正常 1=删除） */
    private Long delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setChatId(Long chatId) 
    {
        this.chatId = chatId;
    }

    public Long getChatId() 
    {
        return chatId;
    }

    public void setSenderId(Long senderId) 
    {
        this.senderId = senderId;
    }

    public Long getSenderId() 
    {
        return senderId;
    }

    public void setReceiverId(Long receiverId) 
    {
        this.receiverId = receiverId;
    }

    public Long getReceiverId() 
    {
        return receiverId;
    }

    public void setMsgType(Long msgType) 
    {
        this.msgType = msgType;
    }

    public Long getMsgType() 
    {
        return msgType;
    }

    public void setMsgContent(String msgContent) 
    {
        this.msgContent = msgContent;
    }

    public String getMsgContent() 
    {
        return msgContent;
    }

    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }

    public void setIsRecalled(Long isRecalled) 
    {
        this.isRecalled = isRecalled;
    }

    public Long getIsRecalled() 
    {
        return isRecalled;
    }

    public void setRecallTime(Date recallTime) 
    {
        this.recallTime = recallTime;
    }

    public Date getRecallTime() 
    {
        return recallTime;
    }

    public void setDelFlag(Long delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Long getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("chatId", getChatId())
            .append("senderId", getSenderId())
            .append("receiverId", getReceiverId())
            .append("msgType", getMsgType())
            .append("msgContent", getMsgContent())
            .append("sendTime", getSendTime())
            .append("isRecalled", getIsRecalled())
            .append("recallTime", getRecallTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
