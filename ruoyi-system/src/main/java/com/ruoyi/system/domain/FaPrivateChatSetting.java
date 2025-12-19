package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 私聊设置对象 fa_private_chat_setting
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public class FaPrivateChatSetting extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 设置ID（雪花算法） */
    private Long id;

    /** 会话ID（关联fa_private_chat.id） */
    @Excel(name = "会话ID", readConverterExp = "关=联fa_private_chat.id")
    private Long chatId;

    /** 设置所属用户ID（关联sys_user.id） */
    @Excel(name = "设置所属用户ID", readConverterExp = "关=联sys_user.id")
    private Long userId;

    /** 是否置顶（0=否 1=是） */
    @Excel(name = "是否置顶", readConverterExp = "0==否,1==是")
    private Long isTop;

    /** 是否免打扰（0=否 1=是） */
    @Excel(name = "是否免打扰", readConverterExp = "0==否,1==是")
    private Long isMute;

    /** 是否隐藏会话（0=否 1=是） */
    @Excel(name = "是否隐藏会话", readConverterExp = "0==否,1==是")
    private Long isHide;

    /** 删除标志（0=正常 1=删除） */
    private Long delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setChatId(Long chatId) 
    {
        this.chatId = chatId;
    }

    public Long getChatId() 
    {
        return chatId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setIsTop(Long isTop) 
    {
        this.isTop = isTop;
    }

    public Long getIsTop() 
    {
        return isTop;
    }

    public void setIsMute(Long isMute) 
    {
        this.isMute = isMute;
    }

    public Long getIsMute() 
    {
        return isMute;
    }

    public void setIsHide(Long isHide) 
    {
        this.isHide = isHide;
    }

    public Long getIsHide() 
    {
        return isHide;
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
            .append("chatId", getChatId())
            .append("userId", getUserId())
            .append("isTop", getIsTop())
            .append("isMute", getIsMute())
            .append("isHide", getIsHide())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
