package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 群聊基础信息对象 fa_group_chat
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public class FaGroupChat extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 群聊ID（雪花算法） */
    private Long id;

    /** 群聊名称 */
    @Excel(name = "群聊名称")
    private String groupName;

    /** 群头像URL */
    @Excel(name = "群头像URL")
    private String groupAvatar;

    /** 创建人ID（关联sys_user.id） */
    @Excel(name = "创建人ID", readConverterExp = "关=联sys_user.id")
    private Long creatorId;

    /** 群聊描述 */
    @Excel(name = "群聊描述")
    private String description;

    /** 最大成员数 */
    @Excel(name = "最大成员数")
    private Long maxMemberCount;

    /** 是否解散（0=否 1=是） */
    @Excel(name = "是否解散", readConverterExp = "0==否,1==是")
    private Long isDisbanded;

    /** 是否全员禁言（0=否 1=是） */
    @Excel(name = "是否全员禁言", readConverterExp = "0==否,1==是")
    private Long isMute;

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

    public void setGroupName(String groupName) 
    {
        this.groupName = groupName;
    }

    public String getGroupName() 
    {
        return groupName;
    }

    public void setGroupAvatar(String groupAvatar) 
    {
        this.groupAvatar = groupAvatar;
    }

    public String getGroupAvatar() 
    {
        return groupAvatar;
    }

    public void setCreatorId(Long creatorId) 
    {
        this.creatorId = creatorId;
    }

    public Long getCreatorId() 
    {
        return creatorId;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setMaxMemberCount(Long maxMemberCount) 
    {
        this.maxMemberCount = maxMemberCount;
    }

    public Long getMaxMemberCount() 
    {
        return maxMemberCount;
    }

    public void setIsDisbanded(Long isDisbanded) 
    {
        this.isDisbanded = isDisbanded;
    }

    public Long getIsDisbanded() 
    {
        return isDisbanded;
    }

    public void setIsMute(Long isMute) 
    {
        this.isMute = isMute;
    }

    public Long getIsMute() 
    {
        return isMute;
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
            .append("groupName", getGroupName())
            .append("groupAvatar", getGroupAvatar())
            .append("creatorId", getCreatorId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("description", getDescription())
            .append("maxMemberCount", getMaxMemberCount())
            .append("isDisbanded", getIsDisbanded())
            .append("isMute", getIsMute())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
