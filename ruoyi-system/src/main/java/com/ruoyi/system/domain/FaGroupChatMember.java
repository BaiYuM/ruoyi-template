package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 群聊成员关联对象 fa_group_chat_member
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public class FaGroupChatMember extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 关联ID（雪花算法） */
    private Long id;

    /** 群聊ID（关联fa_group_chat.id） */
    @Excel(name = "群聊ID", readConverterExp = "关=联fa_group_chat.id")
    private Long groupId;

    /** 成员用户ID（关联sys_user.id） */
    @Excel(name = "成员用户ID", readConverterExp = "关=联sys_user.id")
    private Long userId;

    /** 成员角色（0=普通 1=群主 2=管理员） */
    @Excel(name = "成员角色", readConverterExp = "0==普通,1==群主,2==管理员")
    private Long roleType;

    /** 加入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "加入时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date joinTime;

    /** 退出时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "退出时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date quitTime;

    /** 个人禁言状态（0=否 1=是） */
    @Excel(name = "个人禁言状态", readConverterExp = "0==否,1==是")
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

    public void setGroupId(Long groupId) 
    {
        this.groupId = groupId;
    }

    public Long getGroupId() 
    {
        return groupId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setRoleType(Long roleType) 
    {
        this.roleType = roleType;
    }

    public Long getRoleType() 
    {
        return roleType;
    }

    public void setJoinTime(Date joinTime) 
    {
        this.joinTime = joinTime;
    }

    public Date getJoinTime() 
    {
        return joinTime;
    }

    public void setQuitTime(Date quitTime) 
    {
        this.quitTime = quitTime;
    }

    public Date getQuitTime() 
    {
        return quitTime;
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
            .append("groupId", getGroupId())
            .append("userId", getUserId())
            .append("roleType", getRoleType())
            .append("joinTime", getJoinTime())
            .append("quitTime", getQuitTime())
            .append("isMute", getIsMute())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
