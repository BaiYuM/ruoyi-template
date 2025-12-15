package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 曝光配置表 exposure_config
 */
public class ExposureConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String platform;

    private String account;

    private String configType;

    private String searchKeywords;

    private String commentContent;

    private Integer dailyLimit;

    private String startTime;

    private String sortOrder;

    private Boolean skipRepeat;

    private String status;
    /** 上次停止原因（若自动或手动停止） */
    private String lastStopReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Integer getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(Integer dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getSkipRepeat() {
        return skipRepeat;
    }

    public void setSkipRepeat(Boolean skipRepeat) {
        this.skipRepeat = skipRepeat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastStopReason() {
        return lastStopReason;
    }

    public void setLastStopReason(String lastStopReason) {
        this.lastStopReason = lastStopReason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("platform", getPlatform())
            .append("account", getAccount())
            .append("configType", getConfigType())
            .append("searchKeywords", getSearchKeywords())
            .append("dailyLimit", getDailyLimit())
            .append("startTime", getStartTime())
            .append("sortOrder", getSortOrder())
            .append("skipRepeat", getSkipRepeat())
            .append("status", getStatus())
            .append("lastStopReason", getLastStopReason())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
