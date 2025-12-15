package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ExposureEvent;

public interface ExposureEventLogMapper {
    /**
     * 插入一条曝光历史记录（单次曝光）
     */
    int insertExposureLog(ExposureEvent event);
}
