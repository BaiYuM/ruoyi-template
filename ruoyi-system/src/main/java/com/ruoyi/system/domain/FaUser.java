package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 抖音用户表 fa_user
 */
public class FaUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long id;

    /** 头像 */
    private String avatar;

    /** 昵称 */
    private String nickname;

    /** 平台 */
    private String platform;

    /** 用户类型 */
    private String type;

    /** 开放平台ID */
    private String openId;

    /** 联合ID */
    private String unionId;

    /** 全平台唯一标识 */
    private String allFasMuin;

    /** 关联的系统用户ID */
    private Long sysUserId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getAllFasMuin() {
        return allFasMuin;
    }

    public void setAllFasMuin(String allFasMuin) {
        this.allFasMuin = allFasMuin;
    }

    public Long getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(Long sysUserId) {
        this.sysUserId = sysUserId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("avatar", getAvatar())
            .append("nickname", getNickname())
            .append("platform", getPlatform())
            .append("type", getType())
            .append("openId", getOpenId())
            .append("unionId", getUnionId())
            .append("allFasMuin", getAllFasMuin())
            .append("sysUserId", getSysUserId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}