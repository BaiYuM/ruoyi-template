package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ExposureConfigMapper;
import com.ruoyi.system.domain.ExposureConfig;
import com.ruoyi.system.domain.ExposureStatsDTO;
import com.ruoyi.system.service.IExposureConfigService;

@Service
public class ExposureConfigServiceImpl implements IExposureConfigService
{
    @Autowired
    private ExposureConfigMapper exposureConfigMapper;

    @Override
    public List<ExposureConfig> selectExposureList(ExposureConfig exposure)
    {
        return exposureConfigMapper.selectExposureList(exposure);
    }

    @Override
    public ExposureConfig selectExposureById(Long id)
    {
        return exposureConfigMapper.selectExposureById(id);
    }

    @Override
    public int insertExposure(ExposureConfig exposure)
    {
        return exposureConfigMapper.insertExposure(exposure);
    }

    @Override
    public int updateExposure(ExposureConfig exposure)
    {
        return exposureConfigMapper.updateExposure(exposure);
    }

    @Override
    public int deleteExposureByIds(Long[] ids)
    {
        int result = 0;
        for (Long id : ids) {
            result += exposureConfigMapper.deleteExposureById(id);
        }
        return result;
    }

    @Override
    public List<ExposureStatsDTO> selectExposureStats(ExposureConfig filter)
    {
        List<ExposureStatsDTO> list = exposureConfigMapper.selectExposureStats(filter);
        if (list == null) return null;
        for (ExposureStatsDTO dto : list) {
            if ((dto.getPlatform() == null || dto.getPlatform().trim().isEmpty()) && dto.getAccount() != null) {
                dto.setPlatform(detectPlatform(dto.getAccount()));
            }
        }
        return list;
    }

    private String detectPlatform(String account) {
        String a = account.trim();
        if (a.matches("^\\d{8,}$")) return "douyin"; // 长数字视为抖音 ID
        if (a.contains("@")) return "weibo";
        if (a.toLowerCase().startsWith("wx") || a.toLowerCase().startsWith("wechat")) return "weixin";
        if (a.matches("^[a-zA-Z0-9_\\-]+$") && a.length() <= 6) return "shortid";
        return "";
    }
}
