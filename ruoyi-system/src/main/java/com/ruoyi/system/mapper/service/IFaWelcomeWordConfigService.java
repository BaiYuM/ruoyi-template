package com.ruoyi.system.mapper.service;

import com.ruoyi.system.domain.FaWelcomeWordConfig;

import java.util.List;


/**
 * 欢迎词配置Service接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface IFaWelcomeWordConfigService 
{
    /**
     * 查询欢迎词配置
     * 
     * @param id 欢迎词配置主键
     * @return 欢迎词配置
     */
    public FaWelcomeWordConfig selectFaWelcomeWordConfigById(Long id);

    /**
     * 查询欢迎词配置列表
     * 
     * @param faWelcomeWordConfig 欢迎词配置
     * @return 欢迎词配置集合
     */
    public List<FaWelcomeWordConfig> selectFaWelcomeWordConfigList(FaWelcomeWordConfig faWelcomeWordConfig);

    /**
     * 新增欢迎词配置
     * 
     * @param faWelcomeWordConfig 欢迎词配置
     * @return 结果
     */
    public int insertFaWelcomeWordConfig(FaWelcomeWordConfig faWelcomeWordConfig);

    /**
     * 修改欢迎词配置
     * 
     * @param faWelcomeWordConfig 欢迎词配置
     * @return 结果
     */
    public int updateFaWelcomeWordConfig(FaWelcomeWordConfig faWelcomeWordConfig);

    /**
     * 批量删除欢迎词配置
     * 
     * @param ids 需要删除的欢迎词配置主键集合
     * @return 结果
     */
    public int deleteFaWelcomeWordConfigByIds(Long[] ids);

    /**
     * 删除欢迎词配置信息
     * 
     * @param id 欢迎词配置主键
     * @return 结果
     */
    public int deleteFaWelcomeWordConfigById(Long id);
}
