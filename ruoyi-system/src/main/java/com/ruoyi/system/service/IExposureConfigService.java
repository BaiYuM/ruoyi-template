package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.ExposureConfig;
import com.ruoyi.system.domain.ExposureStatsDTO;

public interface IExposureConfigService
{
    public List<ExposureConfig> selectExposureList(ExposureConfig exposure);

    public ExposureConfig selectExposureById(Long id);

    public int insertExposure(ExposureConfig exposure);

    public int updateExposure(ExposureConfig exposure);

    public int deleteExposureByIds(Long[] ids);

    public List<ExposureStatsDTO> selectExposureStats(ExposureConfig filter);
}
