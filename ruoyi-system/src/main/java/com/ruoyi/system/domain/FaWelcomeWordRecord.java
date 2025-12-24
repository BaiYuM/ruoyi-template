package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 欢迎词执行记录对象 fa_welcome_word_record
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public class FaWelcomeWordRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

    /** 配置ID */
    @Excel(name = "配置ID")
    private Long configId;

    /** 系统用户ID */
    @Excel(name = "系统用户ID")
    private Long sysUserId;

    /** 抖音账号ID */
    @Excel(name = "抖音账号ID")
    private String accountId;

    /** 被触发的用户ID */
    @Excel(name = "被触发的用户ID")
    private String triggerUserId;

    /** 被触发的用户名 */
    @Excel(name = "被触发的用户名")
    private String triggerUserName;

    /** 发送的内容 */
    @Excel(name = "发送的内容")
    private String sentContent;

    /** 执行状态（success/failed/pending） */
    @Excel(name = "执行状态", readConverterExp = "s=uccess/failed/pending")
    private String executionStatus;

    /** 执行时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "执行时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date executionTime;

    /** 错误信息 */
    @Excel(name = "错误信息")
    private String errorMessage;

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

    public void setSysUserId(Long sysUserId) 
    {
        this.sysUserId = sysUserId;
    }

    public Long getSysUserId() 
    {
        return sysUserId;
    }

    public void setAccountId(String accountId) 
    {
        this.accountId = accountId;
    }

    public String getAccountId() 
    {
        return accountId;
    }

    public void setTriggerUserId(String triggerUserId) 
    {
        this.triggerUserId = triggerUserId;
    }

    public String getTriggerUserId() 
    {
        return triggerUserId;
    }

    public void setTriggerUserName(String triggerUserName) 
    {
        this.triggerUserName = triggerUserName;
    }

    public String getTriggerUserName() 
    {
        return triggerUserName;
    }

    public void setSentContent(String sentContent) 
    {
        this.sentContent = sentContent;
    }

    public String getSentContent() 
    {
        return sentContent;
    }

    public void setExecutionStatus(String executionStatus) 
    {
        this.executionStatus = executionStatus;
    }

    public String getExecutionStatus() 
    {
        return executionStatus;
    }

    public void setExecutionTime(Date executionTime) 
    {
        this.executionTime = executionTime;
    }

    public Date getExecutionTime() 
    {
        return executionTime;
    }

    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("configId", getConfigId())
            .append("sysUserId", getSysUserId())
            .append("accountId", getAccountId())
            .append("triggerUserId", getTriggerUserId())
            .append("triggerUserName", getTriggerUserName())
            .append("sentContent", getSentContent())
            .append("executionStatus", getExecutionStatus())
            .append("executionTime", getExecutionTime())
            .append("errorMessage", getErrorMessage())
            .append("createTime", getCreateTime())
            .toString();
    }
}
