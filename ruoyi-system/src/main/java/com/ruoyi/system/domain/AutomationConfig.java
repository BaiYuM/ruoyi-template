package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 自动化配置对象 automation_config
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
public class AutomationConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 配置名称 */
    private String configName;

    /** 目标URL */
    private String targetUrl;

    /** 是否无头模式 */
    private Boolean headless;

    /** 是否截图 */
    private Boolean screenshot;

    /** 任务延迟时间 */
    private Long delayTime;

    /** 配置描述 */
    private String description;

    /** 状态（0正常 1停用） */
    private String status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setConfigName(String configName) 
    {
        this.configName = configName;
    }

    public String getConfigName() 
    {
        return configName;
    }
    public void setTargetUrl(String targetUrl) 
    {
        this.targetUrl = targetUrl;
    }

    public String getTargetUrl() 
    {
        return targetUrl;
    }
    public void setHeadless(Boolean headless) 
    {
        this.headless = headless;
    }

    public Boolean getHeadless() 
    {
        return headless;
    }
    public void setScreenshot(Boolean screenshot) 
    {
        this.screenshot = screenshot;
    }

    public Boolean getScreenshot() 
    {
        return screenshot;
    }
    public void setDelayTime(Long delayTime) 
    {
        this.delayTime = delayTime;
    }

    public Long getDelayTime() 
    {
        return delayTime;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("configName", getConfigName())
            .append("targetUrl", getTargetUrl())
            .append("headless", getHeadless())
            .append("screenshot", getScreenshot())
            .append("delayTime", getDelayTime())
            .append("description", getDescription())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}