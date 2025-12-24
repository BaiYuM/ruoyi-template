package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 私信追杀执行记录对象 fa_private_harassment_record
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public class FaPrivateHarassmentRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 配置ID（关联fa_private_harassment_config.id） */
    @Excel(name = "配置ID", readConverterExp = "关=联fa_private_harassment_config.id")
    private Long configId;

    /** 会话ID（关联fa_private_chat.id） */
    @Excel(name = "会话ID", readConverterExp = "关=联fa_private_chat.id")
    private Long chatId;

    /** 抖音用户ID（关联comment_user.id） */
    @Excel(name = "抖音用户ID", readConverterExp = "关=联comment_user.id")
    private Long userId;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String userNickname;

    /** 用户账号 */
    @Excel(name = "用户账号")
    private String userAccount;

    /** 当前回访次数 */
    @Excel(name = "当前回访次数")
    private Long currentVisitCount;

    /** 总应回访次数 */
    @Excel(name = "总应回访次数")
    private Long totalVisitCount;

    /** 最后发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastSendTime;

    /** 下次发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "下次发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date nextSendTime;

    /** 状态（0=进行中 1=已完成 2=已停止 3=已留资） */
    @Excel(name = "状态", readConverterExp = "0==进行中,1==已完成,2==已停止,3==已留资")
    private Integer status;

    /** 删除标志（0=正常 1=删除） */
    private Integer delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setConfigId(Long configId) 
    {
        this.configId = configId;
    }

    public Long getConfigId() 
    {
        return configId;
    }

    public void setChatId(Long chatId) 
    {
        this.chatId = chatId;
    }

    public Long getChatId() 
    {
        return chatId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setUserNickname(String userNickname) 
    {
        this.userNickname = userNickname;
    }

    public String getUserNickname() 
    {
        return userNickname;
    }

    public void setUserAccount(String userAccount) 
    {
        this.userAccount = userAccount;
    }

    public String getUserAccount() 
    {
        return userAccount;
    }

    public void setCurrentVisitCount(Long currentVisitCount) 
    {
        this.currentVisitCount = currentVisitCount;
    }

    public Long getCurrentVisitCount() 
    {
        return currentVisitCount;
    }

    public void setTotalVisitCount(Long totalVisitCount) 
    {
        this.totalVisitCount = totalVisitCount;
    }

    public Long getTotalVisitCount() 
    {
        return totalVisitCount;
    }

    public void setLastSendTime(Date lastSendTime) 
    {
        this.lastSendTime = lastSendTime;
    }

    public Date getLastSendTime() 
    {
        return lastSendTime;
    }

    public void setNextSendTime(Date nextSendTime) 
    {
        this.nextSendTime = nextSendTime;
    }

    public Date getNextSendTime() 
    {
        return nextSendTime;
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
            .append("configId", getConfigId())
            .append("chatId", getChatId())
            .append("userId", getUserId())
            .append("userNickname", getUserNickname())
            .append("userAccount", getUserAccount())
            .append("currentVisitCount", getCurrentVisitCount())
            .append("totalVisitCount", getTotalVisitCount())
            .append("lastSendTime", getLastSendTime())
            .append("nextSendTime", getNextSendTime())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
