package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.ExposureEvent;

public interface ExposureEventMapper {

    int insertExposureEvent(ExposureEvent event);

    int updateExposureEvent(ExposureEvent event);

    /**
     * 原子增量更新曝光次数，返回受影响行数
     */
    int incrementExposureCount(@org.apache.ibatis.annotations.Param("configId") Long configId,
                               @org.apache.ibatis.annotations.Param("account") String account,
                               @org.apache.ibatis.annotations.Param("platform") String platform,
                               @org.apache.ibatis.annotations.Param("delta") Integer delta,
                               @org.apache.ibatis.annotations.Param("exposureTime") java.util.Date exposureTime);

    /**
     * 优化的单行增量更新：优先匹配 `config_id`，若没有则回退匹配 `account`+`platform` 并只更新最小 id 的那一行，返回受影响行数。
     */
    int incrementSingleRowByConfigOrAccountPlatform(@org.apache.ibatis.annotations.Param("configId") Long configId,
                                                    @org.apache.ibatis.annotations.Param("account") String account,
                                                    @org.apache.ibatis.annotations.Param("platform") String platform,
                                                    @org.apache.ibatis.annotations.Param("delta") Integer delta,
                                                    @org.apache.ibatis.annotations.Param("exposureTime") java.util.Date exposureTime);

    /**
     * 原子 upsert：若已存在则累加 exposure_count 并更新 exposure_time，否则插入新行。
     */
    int upsertExposureEvent(ExposureEvent event);

    ExposureEvent selectByConfigAccountPlatform(@Param("configId") Long configId, @Param("account") String account, @Param("platform") String platform);

    java.util.Date selectLastExposureTime(@Param("account") String account, @Param("platform") String platform);
}
