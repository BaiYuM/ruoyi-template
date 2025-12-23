package com.ruoyi.system.service;

import com.ruoyi.system.domain.ExpirationAi;
import com.ruoyi.system.domain.vo.AiCustomerServiceConfigVo;

import java.util.List;

/**
 * ai客服配置Service接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface IExpirationAiService 
{
    /**
     * 查询ai客服配置
     * 
     * @param id ai客服配置主键
     * @return ai客服配置
     */
    public ExpirationAi selectExpirationAiById(Long id);

    /**
     * 查询ai客服配置列表
     * 
     * @param expirationAi ai客服配置
     * @return ai客服配置集合
     */
    public List<ExpirationAi> selectExpirationAiList(ExpirationAi expirationAi);

    /**
     * 查询ai客服账号配置列表
     */
    public List<ExpirationAi> selectExpirationAiWithUserList(ExpirationAi expirationAi);

    /**
     * 查询AI客服配置中授权账户（关联comment_user表）
     * 
     * @param expirationAi ai客服配置
     * @return ai客服配置集合
     */
    public List<ExpirationAi> selectExpirationAiWithCommentUserList(ExpirationAi expirationAi);

    /**
     * 查询AI客服配置中授权账户（只返回account和nickName）
     *
     * @param expirationAi ai客服配置
     * @return ai客服配置视图集合
     */
    public List<AiCustomerServiceConfigVo> selectAiCustomerServiceConfigList(ExpirationAi expirationAi);
    /**
     * 新增ai客服配置
     * 
     * @param expirationAi ai客服配置
     * @return 结果
     */
    public int insertExpirationAi(ExpirationAi expirationAi);

    /**
     * 修改ai客服配置
     * 
     * @param expirationAi ai客服配置
     * @return 结果
     */
    public int updateExpirationAi(ExpirationAi expirationAi);

    /**
     * 批量删除ai客服配置
     * 
     * @param ids 需要删除的ai客服配置主键集合
     * @return 结果
     */
    public int deleteExpirationAiByIds(Long[] ids);

    /**
     * 删除ai客服配置信息
     * 
     * @param id ai客服配置主键
     * @return 结果
     */
    public int deleteExpirationAiById(Long id);

}
