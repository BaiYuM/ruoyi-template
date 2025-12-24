package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 欢迎词配置对象 fa_welcome_word_config
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public class FaWelcomeWordConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long id;

    /** 系统用户ID */
    @Excel(name = "系统用户ID")
    private Long sysUserId;

    /** 抖音账号ID */
    @Excel(name = "抖音账号ID")
    private String accountId;

    /** 抖音账号名称 */
    @Excel(name = "抖音账号名称")
    private String accountName;

    /** 配置名称 */
    @Excel(name = "配置名称")
    private String configName;

    /** 欢迎词内容 */
    @Excel(name = "欢迎词内容")
    private String welcomeContent;

    /** 触发类型（auto/keyword/target） */
    @Excel(name = "触发类型", readConverterExp = "a=uto/keyword/target")
    private String triggerType;

    /** 关键词列表（JSON） */
    @Excel(name = "关键词列表", readConverterExp = "J=SON")
    private String keywordList;

    /** 目标账号列表（JSON） */
    @Excel(name = "目标账号列表", readConverterExp = "J=SON")
    private String targetAccounts;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 每日限制次数 */
    @Excel(name = "每日限制次数")
    private Long dailyLimit;

    /** 最小延迟（秒） */
    @Excel(name = "最小延迟", readConverterExp = "秒=")
    private Long minDelaySeconds;

    /** 最大延迟（秒） */
    @Excel(name = "最大延迟", readConverterExp = "秒=")
    private Long maxDelaySeconds;

    /** 是否启用 */
    @Excel(name = "是否启用")
    private Long isEnabled;

    /** 优先级 */
    @Excel(name = "优先级")
    private Long priority;

    /** 删除标志 */
    private Long delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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

    public void setAccountName(String accountName) 
    {
        this.accountName = accountName;
    }

    public String getAccountName() 
    {
        return accountName;
    }

    public void setConfigName(String configName) 
    {
        this.configName = configName;
    }

    public String getConfigName() 
    {
        return configName;
    }

    public void setWelcomeContent(String welcomeContent) 
    {
        this.welcomeContent = welcomeContent;
    }

    public String getWelcomeContent() 
    {
        return welcomeContent;
    }

    public void setTriggerType(String triggerType) 
    {
        this.triggerType = triggerType;
    }

    public String getTriggerType() 
    {
        return triggerType;
    }

    public void setKeywordList(String keywordList) 
    {
        this.keywordList = keywordList;
    }

    public String getKeywordList() 
    {
        return keywordList;
    }

    public void setTargetAccounts(String targetAccounts) 
    {
        this.targetAccounts = targetAccounts;
    }

    public String getTargetAccounts() 
    {
        return targetAccounts;
    }

    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }

    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }

    public void setDailyLimit(Long dailyLimit) 
    {
        this.dailyLimit = dailyLimit;
    }

    public Long getDailyLimit() 
    {
        return dailyLimit;
    }

    public void setMinDelaySeconds(Long minDelaySeconds) 
    {
        this.minDelaySeconds = minDelaySeconds;
    }

    public Long getMinDelaySeconds() 
    {
        return minDelaySeconds;
    }

    public void setMaxDelaySeconds(Long maxDelaySeconds) 
    {
        this.maxDelaySeconds = maxDelaySeconds;
    }

    public Long getMaxDelaySeconds() 
    {
        return maxDelaySeconds;
    }

    public void setIsEnabled(Long isEnabled) 
    {
        this.isEnabled = isEnabled;
    }

    public Long getIsEnabled() 
    {
        return isEnabled;
    }

    public void setPriority(Long priority) 
    {
        this.priority = priority;
    }

    public Long getPriority() 
    {
        return priority;
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
            .append("sysUserId", getSysUserId())
            .append("accountId", getAccountId())
            .append("accountName", getAccountName())
            .append("configName", getConfigName())
            .append("welcomeContent", getWelcomeContent())
            .append("triggerType", getTriggerType())
            .append("keywordList", getKeywordList())
            .append("targetAccounts", getTargetAccounts())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("dailyLimit", getDailyLimit())
            .append("minDelaySeconds", getMinDelaySeconds())
            .append("maxDelaySeconds", getMaxDelaySeconds())
            .append("isEnabled", getIsEnabled())
            .append("priority", getPriority())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
