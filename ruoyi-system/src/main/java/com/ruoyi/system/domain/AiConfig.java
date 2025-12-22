package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI客服配置对象 ai_config
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public class AiConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 授权用户表 */
    @Excel(name = "授权用户表")
    private Long expirationId;

    /** ai名称 */
    @Excel(name = "ai名称")
    private String name;

    /** 提示词 */
    @Excel(name = "提示词")
    private String prompt;

    /** 知识库 */
    @Excel(name = "知识库")
    private String knowledgeBase;

    /** 携带上下文轮数 */
    @Excel(name = "携带上下文轮数")
    private String count;

    /** 严谨程度 */
    @Excel(name = "严谨程度")
    private String level;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;

    /** ai模型 */
    @Excel(name = "ai模型")
    private Long aiModel;
    
    /** 用户昵称 */
    private String nickName;
    
    /** 开始创建时间 */
    private String beginCreateTime;
    
    /** 结束创建时间 */
    private String endCreateTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setExpirationId(Long expirationId) 
    {
        this.expirationId = expirationId;
    }

    public Long getExpirationId() 
    {
        return expirationId;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setPrompt(String prompt) 
    {
        this.prompt = prompt;
    }

    public String getPrompt() 
    {
        return prompt;
    }

    public void setKnowledgeBase(String knowledgeBase) 
    {
        this.knowledgeBase = knowledgeBase;
    }

    public String getKnowledgeBase() 
    {
        return knowledgeBase;
    }

    public void setCount(String count) 
    {
        this.count = count;
    }

    public String getCount() 
    {
        return count;
    }

    public void setLevel(String level) 
    {
        this.level = level;
    }

    public String getLevel() 
    {
        return level;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setAiModel(Long aiModel) 
    {
        this.aiModel = aiModel;
    }

    public Long getAiModel() 
    {
        return aiModel;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getNickName()
    {
        return nickName;
    }
    
    public void setBeginCreateTime(String beginCreateTime)
    {
        this.beginCreateTime = beginCreateTime;
    }
    
    public String getBeginCreateTime()
    {
        return beginCreateTime;
    }
    
    public void setEndCreateTime(String endCreateTime)
    {
        this.endCreateTime = endCreateTime;
    }
    
    public String getEndCreateTime()
    {
        return endCreateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("expirationId", getExpirationId())
            .append("name", getName())
            .append("prompt", getPrompt())
            .append("knowledgeBase", getKnowledgeBase())
            .append("count", getCount())
            .append("level", getLevel())
            .append("status", getStatus())
            .append("aiModel", getAiModel())
            .append("createTime", getCreateTime())
            .append("nickName", getNickName())
            .toString();
    }
}
