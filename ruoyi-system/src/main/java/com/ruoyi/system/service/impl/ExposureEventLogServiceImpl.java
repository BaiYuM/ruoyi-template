package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ExposureEvent;
import com.ruoyi.system.mapper.ExposureEventLogMapper;
import com.ruoyi.system.service.IExposureEventLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExposureEventLogServiceImpl implements IExposureEventLogService {

    @Autowired
    private ExposureEventLogMapper logMapper;

    @Override
    public int selectTodayCountByConfig(Long configId) {
        return logMapper.selectTodayCountByConfig(configId);
    }

    @Override
    public List<ExposureEvent> selectLogsByConfig(Map<String, Object> params) {
        return logMapper.selectLogsByConfig(params);
    }
}
