package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.FaPrivateHarassmentConfig;

import java.util.List;

/**
 * 私信追杀配置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface FaPrivateHarassmentConfigMapper 
{
    /**
     * 查询私信追杀配置
     * 
     * @param id 私信追杀配置主键
     * @return 私信追杀配置
     */
    public FaPrivateHarassmentConfig selectFaPrivateHarassmentConfigById(Long id);

    /**
     * 查询私信追杀配置列表
     * 
     * @param faPrivateHarassmentConfig 私信追杀配置
     * @return 私信追杀配置集合
     */
    public List<FaPrivateHarassmentConfig> selectFaPrivateHarassmentConfigList(FaPrivateHarassmentConfig faPrivateHarassmentConfig);

    /**
     * 新增私信追杀配置
     * 
     * @param faPrivateHarassmentConfig 私信追杀配置
     * @return 结果
     */
    public int insertFaPrivateHarassmentConfig(FaPrivateHarassmentConfig faPrivateHarassmentConfig);

    /**
     * 修改私信追杀配置
     * 
     * @param faPrivateHarassmentConfig 私信追杀配置
     * @return 结果
     */
    public int updateFaPrivateHarassmentConfig(FaPrivateHarassmentConfig faPrivateHarassmentConfig);

    /**
     * 删除私信追杀配置
     * 
     * @param id 私信追杀配置主键
     * @return 结果
     */
    public int deleteFaPrivateHarassmentConfigById(Long id);

    /**
     * 批量删除私信追杀配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFaPrivateHarassmentConfigByIds(Long[] ids);
}
