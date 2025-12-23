package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 关联线索对象 fa_clue
 * 
 * @author ruoyi
 * @date 2025-01-06
 */
public class FaClue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 线索ID */
    private Long clueId;

    /** 用户ID(对应comment_user) */
    @Excel(name = "用户ID(对应comment_user)")
    private Long userId;

    /** 线索标签(如:手机号,微信号) */
    @Excel(name = "线索标签(如:手机号,微信号)")
    private String clueLabel;

    /** 线索内容 */
    @Excel(name = "线索内容")
    private String clueValue;

    /** 线索类型 */
    @Excel(name = "线索类型")
    private String clueType;

    public void setClueId(Long clueId) 
    {
        this.clueId = clueId;
    }

    public Long getClueId() 
    {
        return clueId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setClueLabel(String clueLabel) 
    {
        this.clueLabel = clueLabel;
    }

    public String getClueLabel() 
    {
        return clueLabel;
    }
    public void setClueValue(String clueValue) 
    {
        this.clueValue = clueValue;
    }

    public String getClueValue() 
    {
        return clueValue;
    }
    public void setClueType(String clueType) 
    {
        this.clueType = clueType;
    }

    public String getClueType() 
    {
        return clueType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("clueId", getClueId())
            .append("userId", getUserId())
            .append("clueLabel", getClueLabel())
            .append("clueValue", getClueValue())
            .append("clueType", getClueType())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
