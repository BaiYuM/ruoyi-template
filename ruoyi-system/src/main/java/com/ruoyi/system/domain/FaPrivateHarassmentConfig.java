package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 私信追杀配置对象 fa_private_harassment_config
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public class FaPrivateHarassmentConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 系统用户ID（关联sys_user.id） */
    @Excel(name = "系统用户ID", readConverterExp = "关=联sys_user.id")
    private Long sysUserId;

    /** 抖音账号ID */
    @Excel(name = "抖音账号ID")
    private String accountId;

    /** 抖音账号名称 */
    @Excel(name = "抖音账号名称")
    private String accountName;

    /** 是否启用（0=否 1=是） */
    @Excel(name = "是否启用", readConverterExp = "0==否,1==是")
    private Integer isEnabled;

    /** 回复间隔（格式：HH:mm:ss） */
    @Excel(name = "回复间隔", readConverterExp = "格=式：HH:mm:ss")
    private String replyInterval;

    /** 回复间隔（秒） */
    @Excel(name = "回复间隔", readConverterExp = "秒=")
    private Long replyIntervalSeconds;

    /** 回访次数 */
    @Excel(name = "回访次数")
    private Long visitCount;

    /** 回复话术列表（JSON格式存储） */
    @Excel(name = "回复话术列表", readConverterExp = "J=SON格式存储")
    private String phraseList;

    /** 总时间（秒） */
    @Excel(name = "总时间", readConverterExp = "秒=")
    private Long totalTimeSeconds;

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

    public void setIsEnabled(Integer isEnabled) 
    {
        this.isEnabled = isEnabled;
    }

    public Integer getIsEnabled() 
    {
        return isEnabled;
    }

    public void setReplyInterval(String replyInterval) 
    {
        this.replyInterval = replyInterval;
    }

    public String getReplyInterval() 
    {
        return replyInterval;
    }

    public void setReplyIntervalSeconds(Long replyIntervalSeconds) 
    {
        this.replyIntervalSeconds = replyIntervalSeconds;
    }

    public Long getReplyIntervalSeconds() 
    {
        return replyIntervalSeconds;
    }

    public void setVisitCount(Long visitCount) 
    {
        this.visitCount = visitCount;
    }

    public Long getVisitCount() 
    {
        return visitCount;
    }

    public void setPhraseList(String phraseList) 
    {
        this.phraseList = phraseList;
    }

    public String getPhraseList() 
    {
        return phraseList;
    }

    public void setTotalTimeSeconds(Long totalTimeSeconds) 
    {
        this.totalTimeSeconds = totalTimeSeconds;
    }

    public Long getTotalTimeSeconds() 
    {
        return totalTimeSeconds;
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
            .append("sysUserId", getSysUserId())
            .append("accountId", getAccountId())
            .append("accountName", getAccountName())
            .append("isEnabled", getIsEnabled())
            .append("replyInterval", getReplyInterval())
            .append("replyIntervalSeconds", getReplyIntervalSeconds())
            .append("visitCount", getVisitCount())
            .append("phraseList", getPhraseList())
            .append("totalTimeSeconds", getTotalTimeSeconds())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
