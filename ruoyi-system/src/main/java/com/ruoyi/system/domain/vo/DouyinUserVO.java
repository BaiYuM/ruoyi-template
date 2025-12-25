package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.CommentUser;

/**
 * 抖音用户统一视图对象
 */
public class DouyinUserVO
{


    /** 详细用户信息 */
    private CommentUser commentUser;

    /** 全平台唯一标识 */
    private String unifiedId;

    /** 用户状态 */
    private String userStatus;

    /** 最后活跃时间 */
    private String lastActiveTime;


    public CommentUser getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(CommentUser commentUser) {
        this.commentUser = commentUser;
    }

    public String getUnifiedId() {
        return unifiedId;
    }

    public void setUnifiedId(String unifiedId) {
        this.unifiedId = unifiedId;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(String lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    /**
     * 获取用户昵称（优先使用commentUser的昵称）
     */
    public String getDisplayNickname() {
        if (commentUser != null && commentUser.getNickname() != null) {
            return commentUser.getNickname();
        }
        return "";
    }

    /**
     * 获取用户头像（优先使用commentUser的头像）
     */
    public String getDisplayAvatar() {
        if (commentUser != null && commentUser.getAvatar() != null) {
            return commentUser.getAvatar();
        }
        return "";
    }

    /**
     * 获取粉丝数
     */
    public long getFollowerCount() {
        return commentUser != null ? commentUser.getFollowerCount() : 0;
    }

    /**
     * 获取关注数
     */
    public long getFollowingCount() {
        return commentUser != null ? commentUser.getFollowingCount() : 0;
    }

    /**
     * 获取获赞数
     */
    public long getLikeCount() {
        return commentUser != null ? commentUser.getLikeCount() : 0;
    }

    /**
     * 获取视频数
     */
    public long getVideoCount() {
        return commentUser != null ? commentUser.getVideoCount() : 0;
    }

    /**
     * 获取平台信息
     */
    public String getPlatform() {
        if (commentUser != null && commentUser.getPlatform() != null) {
            return commentUser.getPlatform();
        }
        return "";
    }

    /**
     * 获取账号信息
     */
    public String getAccount() {
        return commentUser != null ? commentUser.getAccount() : "";
    }

    /**
     * 获取位置信息
     */
    public String getLocation() {
        return commentUser != null ? commentUser.getLocation() : "";
    }

    /**
     * 获取签名
     */
    public String getSignature() {
        return commentUser != null ? commentUser.getSignature() : "";
    }

    /**
     * 获取性别
     */
    public Integer getGender() {
        return commentUser != null ? commentUser.getGender() : null;
    }

    /**
     * 获取年龄
     */
    public long getAge() {
        return commentUser != null ? commentUser.getAge() : null;
    }

    /**
     * 获取认证状态
     */
    public String getVerified() {
        return commentUser != null ? commentUser.getVerified() : "";
    }

    /**
     * 获取用户等级
     */
    public String getLevel() {
        return commentUser != null ? commentUser.getLevel() : "";
    }
}