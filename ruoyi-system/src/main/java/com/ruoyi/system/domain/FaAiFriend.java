package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 抖音授权账号好友对象 fa_ai_friend
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
public class FaAiFriend extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** AI授权配置ID(关联expiration_ai表) */
    @Excel(name = "AI授权配置ID(关联expiration_ai表)")
    private Long expirationAiId;

    /** 好友用户ID(关联comment_user表) */
    @Excel(name = "好友用户ID(关联comment_user表)")
    private Long friendUserId;

    /** 好友抖音账号(冗余,便于查询) */
    @Excel(name = "好友抖音账号(冗余,便于查询)")
    private String friendAccount;

    /** 好友昵称(冗余,便于查询) */
    @Excel(name = "好友昵称(冗余,便于查询)")
    private String friendNickname;

    /** 好友头像(冗余,便于查询) */
    @Excel(name = "好友头像(冗余,便于查询)")
    private String friendAvatar;

    /** 好友备注名 */
    @Excel(name = "好友备注名")
    private String friendRemark;

    /** 是否已留资:0-未留资,1-已留资 */
    @Excel(name = "是否已留资:0-未留资,1-已留资")
    private Integer isLead;

    /** 最后聊天时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后聊天时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastChatTime;

    /** 未读消息数 */
    @Excel(name = "未读消息数")
    private Long unreadCount;

    /** 状态:0-已删除,1-正常 */
    @Excel(name = "状态:0-已删除,1-正常")
    private Integer status;

    /** 删除标志:0-正常,1-删除 */
    private Integer delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setExpirationAiId(Long expirationAiId) 
    {
        this.expirationAiId = expirationAiId;
    }

    public Long getExpirationAiId() 
    {
        return expirationAiId;
    }

    public void setFriendUserId(Long friendUserId) 
    {
        this.friendUserId = friendUserId;
    }

    public Long getFriendUserId() 
    {
        return friendUserId;
    }

    public void setFriendAccount(String friendAccount) 
    {
        this.friendAccount = friendAccount;
    }

    public String getFriendAccount() 
    {
        return friendAccount;
    }

    public void setFriendNickname(String friendNickname) 
    {
        this.friendNickname = friendNickname;
    }

    public String getFriendNickname() 
    {
        return friendNickname;
    }

    public void setFriendAvatar(String friendAvatar) 
    {
        this.friendAvatar = friendAvatar;
    }

    public String getFriendAvatar() 
    {
        return friendAvatar;
    }

    public void setFriendRemark(String friendRemark) 
    {
        this.friendRemark = friendRemark;
    }

    public String getFriendRemark() 
    {
        return friendRemark;
    }

    public void setIsLead(Integer isLead) 
    {
        this.isLead = isLead;
    }

    public Integer getIsLead() 
    {
        return isLead;
    }

    public void setLastChatTime(Date lastChatTime) 
    {
        this.lastChatTime = lastChatTime;
    }

    public Date getLastChatTime() 
    {
        return lastChatTime;
    }

    public void setUnreadCount(Long unreadCount) 
    {
        this.unreadCount = unreadCount;
    }

    public Long getUnreadCount() 
    {
        return unreadCount;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("expirationAiId", getExpirationAiId())
            .append("friendUserId", getFriendUserId())
            .append("friendAccount", getFriendAccount())
            .append("friendNickname", getFriendNickname())
            .append("friendAvatar", getFriendAvatar())
            .append("friendRemark", getFriendRemark())
            .append("isLead", getIsLead())
            .append("lastChatTime", getLastChatTime())
            .append("unreadCount", getUnreadCount())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
