package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 私聊消息已读对象 fa_private_chat_msg_read
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public class FaPrivateChatMsgRead extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 已读记录ID（雪花算法） */
    private Long id;

    /** 消息ID（关联fa_private_chat_msg.id） */
    @Excel(name = "消息ID", readConverterExp = "关=联fa_private_chat_msg.id")
    private Long msgId;

    /** 已读用户ID（接收人ID） */
    @Excel(name = "已读用户ID", readConverterExp = "接=收人ID")
    private Long userId;

    /** 已读时间（未读为null） */
    @Excel(name = "已读时间", readConverterExp = "未=读为null")
    private Date readTime;

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

    public void setMsgId(Long msgId) 
    {
        this.msgId = msgId;
    }

    public Long getMsgId() 
    {
        return msgId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setReadTime(Date readTime) 
    {
        this.readTime = readTime;
    }

    public Date getReadTime() 
    {
        return readTime;
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
            .append("msgId", getMsgId())
            .append("userId", getUserId())
            .append("readTime", getReadTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
