package com.ruoyi.system.vo;

import com.ruoyi.common.annotation.Excel;
import java.io.Serializable;

/**
 * AI客服配置中授权账户视图对象
 * 
 * @author ruoyi
 * @date 2025-12-23
 */
public class AiCustomerServiceConfigVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 用户账号 */
    @Excel(name = "用户账号")
    private String account;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    private String nickName;

    public Long getId() 
    {
        return id;
    }

    public void setId(Long id) 
    {
        this.id = id;
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