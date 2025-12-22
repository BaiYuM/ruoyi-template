package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 私聊会话对象 fa_private_chat
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
public class FaPrivateChat extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会话ID（雪花算法） */
    private Long id;

    /** 用户1 ID（关联sys_user.id，约定user_id1 &lt; user_id2 避免重复会话） */
    @Excel(name = "用户1 ID", readConverterExp = "关=联sys_user.id，约定user_id1,&=lt;,u=ser_id2,避=免重复会话")
    private Long userId1;

    /** 用户2 ID（关联sys_user.id） */
    @Excel(name = "用户2 ID", readConverterExp = "关=联sys_user.id")
    private Long userId2;

    /** 最后一条消息ID（关联fa_private_chat_msg.id） */
    @Excel(name = "最后一条消息ID", readConverterExp = "关=联fa_private_chat_msg.id")
    private Long lastMsgId;

    /** 是否拉黑（0=否 1=user1拉黑user2 2=user2拉黑user1） */
    @Excel(name = "是否拉黑", readConverterExp = "0==否,1==user1拉黑user2,2==user2拉黑user1")
    private Long isBlocked;

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

    public void setUserId1(Long userId1) 
    {
        this.userId1 = userId1;
    }

    public Long getUserId1() 
    {
        return userId1;
    }

    public void setUserId2(Long userId2) 
    {
        this.userId2 = userId2;
    }

    public Long getUserId2() 
    {
        return userId2;
    }

    public void setLastMsgId(Long lastMsgId) 
    {
        this.lastMsgId = lastMsgId;
    }

    public Long getLastMsgId() 
    {
        return lastMsgId;
    }

    public void setIsBlocked(Long isBlocked) 
    {
        this.isBlocked = isBlocked;
    }

    public Long getIsBlocked() 
    {
        return isBlocked;
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
            .append("userId1", getUserId1())
            .append("userId2", getUserId2())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("lastMsgId", getLastMsgId())
            .append("isBlocked", getIsBlocked())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
