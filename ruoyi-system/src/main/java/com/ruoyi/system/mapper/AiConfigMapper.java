package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.AiConfig;

/**
 * AI客服配置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface AiConfigMapper 
{
    /**
     * 查询AI客服配置
     * 
     * @param id AI客服配置主键
     * @return AI客服配置
     */
    public AiConfig selectAiConfigById(Long id);

    /**
     * 查询AI客服配置列表
     * 
     * @param aiConfig AI客服配置
     * @return AI客服配置集合
     */
    public List<AiConfig> selectAiConfigList(AiConfig aiConfig);
    
    /**
     * 查询AI客服配置列表（关联comment_user）
     * 
     * @param aiConfig AI客服配置
     * @return AI客服配置集合
     */
    public List<AiConfig> selectAiConfigWithCommentUserList(AiConfig aiConfig);

    /**
     * 新增AI客服配置
     * 
     * @param aiConfig AI客服配置
     * @return 结果
     */
    public int insertAiConfig(AiConfig aiConfig);

    /**
     * 修改AI客服配置
     * 
     * @param aiConfig AI客服配置
     * @return 结果
     */
    public int updateAiConfig(AiConfig aiConfig);

    /**
     * 删除AI客服配置
     * 
     * @param id AI客服配置主键
     * @return 结果
     */
    public int deleteAiConfigById(Long id);

    /**
     * 批量删除AI客服配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAiConfigByIds(Long[] ids);

    /**
     * 根据授权账号ID查询AI配置
     * 
     * @param expirationId 授权账号ID
     * @return AI客服配置
     */
    public AiConfig selectAiConfigByExpirationId(Long expirationId);
}
