package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 平台账户对象 comment_user
 *
 * @author ruoyi
 * @date 2025-12-19
 */
public class CommentUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 平台 */
    @Excel(name = "平台")
    private String platform;

    /** 用户open_id */
    @Excel(name = "用户open_id")
    private String openId;

    /** 你的应用编号，防止多 App 混用 */
    @Excel(name = "你的应用编号，防止多 App 混用")
    private String appId;

    /** 抖音 union_id（保留） */
    @Excel(name = "抖音 union_id", readConverterExp = "保=留")
    private String unionId;

    /** 关联fa_user的ID */
    @Excel(name = "关联fa_user的ID")
    private Long userId;

    /** 用户类型 */
    @Excel(name = "用户类型")
    private String type;

    /** 账号 */
    @Excel(name = "账号")
    private String account;

    /** 昵称，各平台通用 */
    @Excel(name = "昵称，各平台通用")
    private String nickname;

    /** 头像 URL，各平台通用 */
    @Excel(name = "头像 URL，各平台通用")
    private String avatar;

    /** 0 未知 1 男 2 女，通用 */
    @Excel(name = "0 未知 1 男 2 女，通用")
    private Integer gender;

    /** 国家，通用 */
    @Excel(name = "国家，通用")
    private String country;

    /** 省份，通用 */
    @Excel(name = "省份，通用")
    private String province;

    /** 城市，通用 */
    @Excel(name = "城市，通用")
    private String city;

    /** 位置 */
    @Excel(name = "位置")
    private String location;

    /** 生日，通用 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生日，通用", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthday;

    /** 年龄 */
    @Excel(name = "年龄")
    private Long age;

    /** 用户分类：active活跃用户, commenter评论用户, lurker潜水者, fan粉丝 */
    @Excel(name = "用户分类：active活跃用户, commenter评论用户, lurker潜水者, fan粉丝")
    private String userCategory;

    /** 分类置信度 */
    @Excel(name = "分类置信度")
    private BigDecimal categoryConfidence;

    /** 分类理由 */
    @Excel(name = "分类理由")
    private String categoryReason;

    /** 活跃度：high高活跃, medium中活跃, low低活跃 */
    @Excel(name = "活跃度：high高活跃, medium中活跃, low低活跃")
    private String activityLevel;

    /** 评论数 */
    @Excel(name = "评论数")
    private Long commentCount;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 分享数 */
    @Excel(name = "分享数")
    private Long shareCount;

    /** 互动评分 */
    @Excel(name = "互动评分")
    private Long interactionScore;

    /** 最后分类时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后分类时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastClassifiedTime;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setOpenId(String openId)
    {
        this.openId = openId;
    }

    public String getOpenId()
    {
        return openId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    public String getAppId()
    {
        return appId;
    }

    public void setUnionId(String unionId)
    {
        this.unionId = unionId;
    }

    public String getUnionId()
    {
        return unionId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setAccount(String account)
    {
        this.account = account;
    }

    public String getAccount()
    {
        return account;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setGender(Integer gender)
    {
        this.gender = gender;
    }

    public Integer getGender()
    {
        return gender;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getCountry()
    {
        return country;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getProvince()
    {
        return province;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCity()
    {
        return city;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation()
    {
        return location;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setAge(Long age)
    {
        this.age = age;
    }

    public Long getAge()
    {
        return age;
    }

    public void setUserCategory(String userCategory)
    {
        this.userCategory = userCategory;
    }

    public String getUserCategory()
    {
        return userCategory;
    }

    public void setCategoryConfidence(BigDecimal categoryConfidence)
    {
        this.categoryConfidence = categoryConfidence;
    }

    public BigDecimal getCategoryConfidence()
    {
        return categoryConfidence;
    }

    public void setCategoryReason(String categoryReason)
    {
        this.categoryReason = categoryReason;
    }

    public String getCategoryReason()
    {
        return categoryReason;
    }

    public void setActivityLevel(String activityLevel)
    {
        this.activityLevel = activityLevel;
    }

    public String getActivityLevel()
    {
        return activityLevel;
    }

    public void setCommentCount(Long commentCount)
    {
        this.commentCount = commentCount;
    }

    public Long getCommentCount()
    {
        return commentCount;
    }

    public void setLikeCount(Long likeCount)
    {
        this.likeCount = likeCount;
    }

    public Long getLikeCount()
    {
        return likeCount;
    }

    public void setShareCount(Long shareCount)
    {
        this.shareCount = shareCount;
    }

    public Long getShareCount()
    {
        return shareCount;
    }

    public void setInteractionScore(Long interactionScore)
    {
        this.interactionScore = interactionScore;
    }

    public Long getInteractionScore()
    {
        return interactionScore;
    }

    public void setLastClassifiedTime(Date lastClassifiedTime)
    {
        this.lastClassifiedTime = lastClassifiedTime;
    }

    public Date getLastClassifiedTime()
    {
        return lastClassifiedTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("platform", getPlatform())
                .append("openId", getOpenId())
                .append("appId", getAppId())
                .append("unionId", getUnionId())
                .append("userId", getUserId())
                .append("type", getType())
                .append("account", getAccount())
                .append("nickname", getNickname())
                .append("avatar", getAvatar())
                .append("gender", getGender())
                .append("country", getCountry())
                .append("province", getProvince())
                .append("city", getCity())
                .append("location", getLocation())
                .append("birthday", getBirthday())
                .append("age", getAge())
                .append("createBy", getCreateBy())
                .append("updateBy", getUpdateBy())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("userCategory", getUserCategory())
                .append("categoryConfidence", getCategoryConfidence())
                .append("categoryReason", getCategoryReason())
                .append("activityLevel", getActivityLevel())
                .append("commentCount", getCommentCount())
                .append("likeCount", getLikeCount())
                .append("shareCount", getShareCount())
                .append("interactionScore", getInteractionScore())
                .append("lastClassifiedTime", getLastClassifiedTime())
                .toString();
    }
}
