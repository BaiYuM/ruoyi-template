package com.ruoyi.system.domain.vo;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.system.domain.ExpirationAi;

/**
 * AI客服配置中授权账户视图对象
 * 
 * @author ruoyi
 * @date 2025-12-23
 */
public class AiCustomerServiceConfigVo extends ExpirationAi
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "用户ID")
    private Long userId;

    /** 用户账号 */
    @Excel(name = "用户账号")
    private String account;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    public Long getUserId()
    {
        return userId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getAccount() 
    {
        return account;
    }

    public void setAccount(String account) 
    {
        this.account = account;
    }

    public String getNickName() 
    {
        return nickName;
    }

    public void setNickName(String nickName) 
    {
        this.nickName = nickName;
    }
}