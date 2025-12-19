package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * ai客服配置对象 expiration_ai
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public class ExpirationAi extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 主表外键 */
    @Excel(name = "主表外键")
    private Long userId;

    /** 授权到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "授权到期时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expirationDate;

    /** 是否配置ai客服 */
    @Excel(name = "是否配置ai客服")
    private Integer openAi;

    /** 留资提取 */
    @Excel(name = "留资提取")
    private Long funds;

    /** 工作时段类型：0=全天,1=时间区间 */
    @Excel(name = "工作时段类型：0=全天,1=时间区间")
    private Integer workPeriodType;

    /** 工作时间段开始 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "工作时间段开始", width = 30, dateFormat = "yyyy-MM-dd")
    private Date workPeriodStart;

    /** 工作时间段结束 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "工作时间段结束", width = 30, dateFormat = "yyyy-MM-dd")
    private Date workPeriodEnd;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setExpirationDate(Date expirationDate) 
    {
        this.expirationDate = expirationDate;
    }

    public Date getExpirationDate() 
    {
        return expirationDate;
    }

    public void setOpenAi(Integer openAi) 
    {
        this.openAi = openAi;
    }

    public Integer getOpenAi() 
    {
        return openAi;
    }

    public void setFunds(Long funds) 
    {
        this.funds = funds;
    }

    public Long getFunds() 
    {
        return funds;
    }

    public void setWorkPeriodType(Integer workPeriodType) 
    {
        this.workPeriodType = workPeriodType;
    }

    public Integer getWorkPeriodType() 
    {
        return workPeriodType;
    }

    public void setWorkPeriodStart(Date workPeriodStart) 
    {
        this.workPeriodStart = workPeriodStart;
    }

    public Date getWorkPeriodStart() 
    {
        return workPeriodStart;
    }

    public void setWorkPeriodEnd(Date workPeriodEnd) 
    {
        this.workPeriodEnd = workPeriodEnd;
    }

    public Date getWorkPeriodEnd() 
    {
        return workPeriodEnd;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("expirationDate", getExpirationDate())
            .append("openAi", getOpenAi())
            .append("funds", getFunds())
            .append("workPeriodType", getWorkPeriodType())
            .append("workPeriodStart", getWorkPeriodStart())
            .append("workPeriodEnd", getWorkPeriodEnd())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
