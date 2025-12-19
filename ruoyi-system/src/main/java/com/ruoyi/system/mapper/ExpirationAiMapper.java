package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ExpirationAi;

import java.util.List;


/**
 * ai客服配置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface ExpirationAiMapper 
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
     * 删除ai客服配置
     * 
     * @param id ai客服配置主键
     * @return 结果
     */
    public int deleteExpirationAiById(Long id);

    /**
     * 批量删除ai客服配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteExpirationAiByIds(Long[] ids);
}
