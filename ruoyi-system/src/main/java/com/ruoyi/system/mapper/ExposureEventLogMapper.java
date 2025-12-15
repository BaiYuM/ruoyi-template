package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ExposureEvent;
import java.util.List;
import java.util.Map;

public interface ExposureEventLogMapper {
    /**
     * 插入一条曝光历史记录（单次曝光）
     */
    int insertExposureLog(ExposureEvent event);

    /**
     * 查询指定配置当天的曝光总数
     */
    int selectTodayCountByConfig(Long configId);

    /**
     * 分页查询指定配置的曝光日志，参数 map 包含: configId, offset, limit
     */
    List<ExposureEvent> selectLogsByConfig(Map<String, Object> params);
}
