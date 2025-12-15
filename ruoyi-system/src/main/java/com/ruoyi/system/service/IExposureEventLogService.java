package com.ruoyi.system.service;

import com.ruoyi.system.domain.ExposureEvent;
import java.util.List;
import java.util.Map;

public interface IExposureEventLogService {
    int selectTodayCountByConfig(Long configId);

    List<ExposureEvent> selectLogsByConfig(Map<String, Object> params);
}
