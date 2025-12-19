package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.AiConfigMapper;
import com.ruoyi.system.domain.AiConfig;
import com.ruoyi.system.service.IAiConfigService;

/**
 * AI客服配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@Service
public class AiConfigServiceImpl implements IAiConfigService 
{
    @Autowired
    private AiConfigMapper aiConfigMapper;

    /**
     * 查询AI客服配置
     * 
     * @param id AI客服配置主键
     * @return AI客服配置
     */
    @Override
    public AiConfig selectAiConfigById(Long id)
    {
        return aiConfigMapper.selectAiConfigById(id);
    }

    /**
     * 查询AI客服配置列表
     * 
     * @param aiConfig AI客服配置
     * @return AI客服配置
     */
    @Override
    public List<AiConfig> selectAiConfigList(AiConfig aiConfig)
    {
        return aiConfigMapper.selectAiConfigList(aiConfig);
    }

    /**
     * 新增AI客服配置
     * 
     * @param aiConfig AI客服配置
     * @return 结果
     */
    @Override
    public int insertAiConfig(AiConfig aiConfig)
    {
        aiConfig.setCreateTime(DateUtils.getNowDate());
        return aiConfigMapper.insertAiConfig(aiConfig);
    }

    /**
     * 修改AI客服配置
     * 
     * @param aiConfig AI客服配置
     * @return 结果
     */
    @Override
    public int updateAiConfig(AiConfig aiConfig)
    {
        return aiConfigMapper.updateAiConfig(aiConfig);
    }

    /**
     * 批量删除AI客服配置
     * 
     * @param ids 需要删除的AI客服配置主键
     * @return 结果
     */
    @Override
    public int deleteAiConfigByIds(Long[] ids)
    {
        return aiConfigMapper.deleteAiConfigByIds(ids);
    }

    /**
     * 删除AI客服配置信息
     * 
     * @param id AI客服配置主键
     * @return 结果
     */
    @Override
    public int deleteAiConfigById(Long id)
    {
        return aiConfigMapper.deleteAiConfigById(id);
    }
}
