package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 好友线索明细对象 fa_ai_friend_clue
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
public class FaAiFriendClue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 好友ID(关联fa_ai_friend表) */
    @Excel(name = "好友ID(关联fa_ai_friend表)")
    private Long friendId;

    /** AI授权配置ID(冗余,便于查询) */
    @Excel(name = "AI授权配置ID(冗余,便于查询)")
    private Long expirationAiId;

    /** 好友用户ID(冗余,便于查询) */
    @Excel(name = "好友用户ID(冗余,便于查询)")
    private Long friendUserId;

    /** 线索标签(如:手机号、微信号、邮箱、咨询产品、预算范围) */
    @Excel(name = "线索标签(如:手机号、微信号、邮箱、咨询产品、预算范围)")
    private String clueLabel;

    /** 线索内容 */
    @Excel(name = "线索内容")
    private String clueValue;

    /** 线索来源:manual-手动添加,ai-AI提取,chat-聊天记录 */
    @Excel(name = "线索来源:manual-手动添加,ai-AI提取,chat-聊天记录")
    private String clueSource;

    /** 状态:0-无效,1-有效 */
    @Excel(name = "状态:0-无效,1-有效")
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

    public void setFriendId(Long friendId) 
    {
        this.friendId = friendId;
    }

    public Long getFriendId() 
    {
        return friendId;
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

    public void setClueLabel(String clueLabel) 
    {
        this.clueLabel = clueLabel;
    }

    public String getClueLabel() 
    {
        return clueLabel;
    }

    public void setClueValue(String clueValue) 
    {
        this.clueValue = clueValue;
    }

    public String getClueValue() 
    {
        return clueValue;
    }

    public void setClueSource(String clueSource) 
    {
        this.clueSource = clueSource;
    }

    public String getClueSource() 
    {
        return clueSource;
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
            .append("friendId", getFriendId())
            .append("expirationAiId", getExpirationAiId())
            .append("friendUserId", getFriendUserId())
            .append("clueLabel", getClueLabel())
            .append("clueValue", getClueValue())
            .append("clueSource", getClueSource())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
