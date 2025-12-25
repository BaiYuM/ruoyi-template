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

    /** 会话参与方1（关联 comment_user.id；约定 user_id1 < user_id2 避免重复会话） */
    @Excel(name = "参与方1ID", readConverterExp = "关=联comment_user.id，约定user_id1<user_id2避免重复会话")
    private Long userId1;

    /** 会话参与方2（关联 comment_user.id） */
    @Excel(name = "参与方2ID", readConverterExp = "关=联comment_user.id")
    private Long userId2;

    /** 最后一条消息ID（关联fa_private_chat_msg.id） */
    @Excel(name = "最后一条消息ID", readConverterExp = "关=联fa_private_chat_msg.id")
    private Long lastMsgId;

    /** 最后一条消息内容 */
    @Excel(name = "最后一条消息内容")
    private String lastMsgContent;

    /** 是否拉黑（0=否 1=user_id1拉黑user_id2 2=user_id2拉黑user_id1） */
    @Excel(name = "是否拉黑", readConverterExp = "0==否,1==user_id1拉黑user_id2,2==user_id2拉黑user_id1")
    private Long isBlocked;

    /** 删除标志（0=正常 1=删除） */
    private Long delFlag;

    /**
     * 会话列表展示：对方抖音用户ID（comment_user.id）
     *
     * <p>当按“授权抖音号(account)”筛选会话时，后端可推导出该会话的“对方”，并回填以下字段。
     */
    private Long peerUserId;

    /** 会话列表展示：对方抖音号/账号（comment_user.account） */
    private String peerAccount;

    /** 会话列表展示：对方昵称（comment_user.nickname） */
    private String peerNickname;

    /** 会话列表展示：对方头像（comment_user.avatar） */
    private String peerAvatar;

    /** 会话列表展示：对方留资状态（来源 expiration_ai.funds；0/1；可能为空表示未知） */
    private Integer peerFunds;

    /** 会话列表展示：对方留资状态（来源 fa_ai_friend.is_lead；0/1；可能为空表示未知） */
    private Integer peerLead;

    /** 会话列表展示：该会话最近一条消息时间 */
    private java.util.Date lastSendTime;

    /**
     * 前端会话列表兼容字段：当前会话所属授权抖音号（= account）
     *
     * <p>用于左侧列表按抖音号筛选（截图中的 douyinId）。</p>
     */
    private String douyinId;

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

    public void setLastMsgContent(String lastMsgContent) 
    {
        this.lastMsgContent = lastMsgContent;
    }

    public String getLastMsgContent() 
    {
        return lastMsgContent;
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

    public Long getPeerUserId()
    {
        return peerUserId;
    }

    public void setPeerUserId(Long peerUserId)
    {
        this.peerUserId = peerUserId;
    }

    public String getPeerAccount()
    {
        return peerAccount;
    }

    public void setPeerAccount(String peerAccount)
    {
        this.peerAccount = peerAccount;
    }

    public String getPeerNickname()
    {
        return peerNickname;
    }

    public void setPeerNickname(String peerNickname)
    {
        this.peerNickname = peerNickname;
    }

    public String getPeerAvatar()
    {
        return peerAvatar;
    }

    public void setPeerAvatar(String peerAvatar)
    {
        this.peerAvatar = peerAvatar;
    }

    public Integer getPeerFunds()
    {
        return peerFunds;
    }

    public void setPeerFunds(Integer peerFunds)
    {
        this.peerFunds = peerFunds;
    }

    public java.util.Date getLastSendTime()
    {
        return lastSendTime;
    }

    public void setLastSendTime(java.util.Date lastSendTime)
    {
        this.lastSendTime = lastSendTime;
    }

    public String getDouyinId()
    {
        return douyinId;
    }

    public void setDouyinId(String douyinId)
    {
        this.douyinId = douyinId;
    }

    public Integer getPeerLead()
    {
        return peerLead;
    }
    public void setPeerLead(Integer peerLead)
    {
        this.peerLead = peerLead;
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
            .append("lastMsgContent", getLastMsgContent())
            .append("isBlocked", getIsBlocked())
            .append("delFlag", getDelFlag())
            .append("peerUserId", getPeerUserId())
            .append("peerAccount", getPeerAccount())
            .append("peerNickname", getPeerNickname())
            .append("peerAvatar", getPeerAvatar())
            .append("peerFunds", getPeerFunds())
            .append("peerLead", getPeerLead())
            .append("lastSendTime", getLastSendTime())
            .append("douyinId", getDouyinId())
            .toString();
    }
}