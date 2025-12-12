package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.ExposureConfig;
import com.ruoyi.system.domain.ExposureStatsDTO;

/**
 * 曝光配置 数据层
 */
public interface ExposureConfigMapper
{
    public ExposureConfig selectExposureById(Long id);

    public List<ExposureConfig> selectExposureList(ExposureConfig exposure);

    public int insertExposure(ExposureConfig exposure);

    public int updateExposure(ExposureConfig exposure);

    public List<ExposureStatsDTO> selectExposureStats(ExposureConfig filter);

    public int deleteExposureById(Long id);

    public int deleteExposureByIds(Long[] ids);
}
