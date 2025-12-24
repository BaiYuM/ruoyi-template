package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.FaPrivateHarassmentConfig;
import com.ruoyi.system.mapper.FaPrivateHarassmentConfigMapper;
import com.ruoyi.system.service.IFaPrivateHarassmentConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 私信追杀配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@Service
public class FaPrivateHarassmentConfigServiceImpl implements IFaPrivateHarassmentConfigService
{
    @Autowired
    private FaPrivateHarassmentConfigMapper faPrivateHarassmentConfigMapper;

    /**
     * 查询私信追杀配置
     * 
     * @param id 私信追杀配置主键
     * @return 私信追杀配置
     */
    @Override
    public FaPrivateHarassmentConfig selectFaPrivateHarassmentConfigById(Long id)
    {
        return faPrivateHarassmentConfigMapper.selectFaPrivateHarassmentConfigById(id);
    }

    /**
     * 查询私信追杀配置列表
     * 
     * @param faPrivateHarassmentConfig 私信追杀配置
     * @return 私信追杀配置
     */
    @Override
    public List<FaPrivateHarassmentConfig> selectFaPrivateHarassmentConfigList(FaPrivateHarassmentConfig faPrivateHarassmentConfig)
    {
        return faPrivateHarassmentConfigMapper.selectFaPrivateHarassmentConfigList(faPrivateHarassmentConfig);
    }

    /**
     * 新增私信追杀配置
     * 
     * @param faPrivateHarassmentConfig 私信追杀配置
     * @return 结果
     */
    @Override
    public int insertFaPrivateHarassmentConfig(FaPrivateHarassmentConfig faPrivateHarassmentConfig)
    {
        faPrivateHarassmentConfig.setCreateTime(DateUtils.getNowDate());
        syncInterval(faPrivateHarassmentConfig);
        return faPrivateHarassmentConfigMapper.insertFaPrivateHarassmentConfig(faPrivateHarassmentConfig);
    }

    /**
     * 修改私信追杀配置
     * 
     * @param faPrivateHarassmentConfig 私信追杀配置
     * @return 结果
     */
    @Override
    public int updateFaPrivateHarassmentConfig(FaPrivateHarassmentConfig faPrivateHarassmentConfig)
    {
        faPrivateHarassmentConfig.setUpdateTime(DateUtils.getNowDate());
        syncInterval(faPrivateHarassmentConfig);
        return faPrivateHarassmentConfigMapper.updateFaPrivateHarassmentConfig(faPrivateHarassmentConfig);
    }

    /**
     * 同步时间间隔
     */
    private void syncInterval(FaPrivateHarassmentConfig config) {
        if (config.getReplyInterval() != null && config.getReplyInterval().contains(":")) {
            try {
                String[] parts = config.getReplyInterval().split(":");
                if (parts.length == 3) {
                    long seconds = Long.parseLong(parts[0]) * 3600 + Long.parseLong(parts[1]) * 60 + Long.parseLong(parts[2]);
                    config.setReplyIntervalSeconds(seconds);
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
        }
    }

    /**
     * 批量删除私信追杀配置
     * 
     * @param ids 需要删除的私信追杀配置主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateHarassmentConfigByIds(Long[] ids)
    {
        return faPrivateHarassmentConfigMapper.deleteFaPrivateHarassmentConfigByIds(ids);
    }

    /**
     * 删除私信追杀配置信息
     * 
     * @param id 私信追杀配置主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateHarassmentConfigById(Long id)
    {
        return faPrivateHarassmentConfigMapper.deleteFaPrivateHarassmentConfigById(id);
    }
}
