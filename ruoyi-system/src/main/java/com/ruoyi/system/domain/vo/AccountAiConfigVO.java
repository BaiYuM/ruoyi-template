package com.ruoyi.system.domain.vo;

/**
 * 抖音账号和AI配置信息VO
 * 用于私信消息界面：返回账号列表时附带对应的AI授权配置ID
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
public class AccountAiConfigVO
{
    /** 抖音账号 */
    private String account;
    
    /** AI授权配置ID */
    private Long expirationAiId;

    public AccountAiConfigVO()
    {
    }

    public AccountAiConfigVO(String account, Long expirationAiId)
    {
        this.account = account;
        this.expirationAiId = expirationAiId;
    }

    public String getAccount()
    {
        return account;
    }

    public void setAccount(String account)
    {
        this.account = account;
    }

    public Long getExpirationAiId()
    {
        return expirationAiId;
    }

    public void setExpirationAiId(Long expirationAiId)
    {
        this.expirationAiId = expirationAiId;
    }

    @Override
    public String toString()
    {
        return "AccountAiConfigVO{" +
                "account='" + account + '\'' +
                ", expirationAiId=" + expirationAiId +
                '}';
    }
}
