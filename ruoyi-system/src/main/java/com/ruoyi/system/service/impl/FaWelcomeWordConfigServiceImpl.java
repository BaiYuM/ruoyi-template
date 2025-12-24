package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.FaWelcomeWordConfig;
import com.ruoyi.system.mapper.FaWelcomeWordConfigMapper;

import com.ruoyi.system.service.IFaWelcomeWordConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 欢迎词配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@Service
public class FaWelcomeWordConfigServiceImpl implements IFaWelcomeWordConfigService
{
    @Autowired
    private FaWelcomeWordConfigMapper faWelcomeWordConfigMapper;

    /**
     * 查询欢迎词配置
     * 
     * @param id 欢迎词配置主键
     * @return 欢迎词配置
     */
    @Override
    public FaWelcomeWordConfig selectFaWelcomeWordConfigById(Long id)
    {
        return faWelcomeWordConfigMapper.selectFaWelcomeWordConfigById(id);
    }

    /**
     * 查询欢迎词配置列表
     * 
     * @param faWelcomeWordConfig 欢迎词配置
     * @return 欢迎词配置
     */
    @Override
    public List<FaWelcomeWordConfig> selectFaWelcomeWordConfigList(FaWelcomeWordConfig faWelcomeWordConfig)
    {
        return faWelcomeWordConfigMapper.selectFaWelcomeWordConfigList(faWelcomeWordConfig);
    }

    /**
     * 新增欢迎词配置
     * 
     * @param faWelcomeWordConfig 欢迎词配置
     * @return 结果
     */
    @Override
    public int insertFaWelcomeWordConfig(FaWelcomeWordConfig faWelcomeWordConfig)
    {
        // 使用雪花算法生成ID
        faWelcomeWordConfig.setId(IdUtils.getSnowflakeId());
        // 设置当前系统用户ID
        faWelcomeWordConfig.setSysUserId(SecurityUtils.getUserId());
        faWelcomeWordConfig.setCreateTime(DateUtils.getNowDate());
        return faWelcomeWordConfigMapper.insertFaWelcomeWordConfig(faWelcomeWordConfig);
    }

    /**
     * 修改欢迎词配置
     * 
     * @param faWelcomeWordConfig 欢迎词配置
     * @return 结果
     */
    @Override
    public int updateFaWelcomeWordConfig(FaWelcomeWordConfig faWelcomeWordConfig)
    {
        faWelcomeWordConfig.setUpdateTime(DateUtils.getNowDate());
        return faWelcomeWordConfigMapper.updateFaWelcomeWordConfig(faWelcomeWordConfig);
    }

    /**
     * 批量删除欢迎词配置
     * 
     * @param ids 需要删除的欢迎词配置主键
     * @return 结果
     */
    @Override
    public int deleteFaWelcomeWordConfigByIds(Long[] ids)
    {
        return faWelcomeWordConfigMapper.deleteFaWelcomeWordConfigByIds(ids);
    }

    /**
     * 删除欢迎词配置信息
     * 
     * @param id 欢迎词配置主键
     * @return 结果
     */
    @Override
    public int deleteFaWelcomeWordConfigById(Long id)
    {
        return faWelcomeWordConfigMapper.deleteFaWelcomeWordConfigById(id);
    }
}
