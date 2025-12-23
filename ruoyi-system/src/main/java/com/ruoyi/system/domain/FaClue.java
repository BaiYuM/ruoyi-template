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

    /** 主键 */
    private Long id;

    /** 过期AI ID */
    @Excel(name = "过期AI ID")
    private Long expirationAiId;

    /** 线索标签(如:手机号,微信号) */
    @Excel(name = "线索标签(如:手机号,微信号)")
    private String clueLabel;

    /** 线索内容 */
    @Excel(name = "线索内容")
    private String clueValue;

    /** 线索类型 */
    @Excel(name = "线索类型")
    private String clueType;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setExpirationAiId(Long expirationAiId)
    {
        this.expirationAiId = expirationAiId;
    }

    public Long getExpirationAiId()
    {
        return expirationAiId;
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
            .append("id", getId())
            .append("expirationAiId", getExpirationAiId())
            .append("clueLabel", getClueLabel())
            .append("clueValue", getClueValue())
            .append("clueType", getClueType())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
