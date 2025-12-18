package com.ruoyi.system.domain;

import java.util.Date;

/**
 * 曝光事件表对应 Domain（用于持久化曝光事件）
 */
public class ExposureEvent
{
    private Long id;
    private Long configId;
    private String account;
    private String platform;
    private String exposureType;
    private Date exposureTime;
    private Integer exposureCount;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConfigId() { return configId; }
    public void setConfigId(Long configId) { this.configId = configId; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getExposureType() { return exposureType; }
    public void setExposureType(String exposureType) { this.exposureType = exposureType; }

    public Date getExposureTime() { return exposureTime; }
    public void setExposureTime(Date exposureTime) { this.exposureTime = exposureTime; }

    public Integer getExposureCount() { return exposureCount; }
    public void setExposureCount(Integer exposureCount) { this.exposureCount = exposureCount; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
