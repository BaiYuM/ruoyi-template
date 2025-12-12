package com.ruoyi.system.domain;

import java.util.Date;

/**
 * 曝光统计 DTO
 */
public class ExposureStatsDTO
{
    private String account;
    private String platform;
    private Integer exposureCount;
    private Date lastExposureTime;
    private Date createTime;

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public Integer getExposureCount() { return exposureCount; }
    public void setExposureCount(Integer exposureCount) { this.exposureCount = exposureCount; }

    public Date getLastExposureTime() { return lastExposureTime; }
    public void setLastExposureTime(Date lastExposureTime) { this.lastExposureTime = lastExposureTime; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
