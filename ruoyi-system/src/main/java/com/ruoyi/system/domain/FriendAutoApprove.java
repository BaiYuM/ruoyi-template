package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 自动通过好友配置 friend_auto_approve
 */
public class FriendAutoApprove extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Boolean enabled;

    private String remark;

    private Boolean sendWelcome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getSendWelcome() {
        return sendWelcome;
    }

    public void setSendWelcome(Boolean sendWelcome) {
        this.sendWelcome = sendWelcome;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("enabled", getEnabled())
            .append("remark", getRemark())
            .append("sendWelcome", getSendWelcome())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
