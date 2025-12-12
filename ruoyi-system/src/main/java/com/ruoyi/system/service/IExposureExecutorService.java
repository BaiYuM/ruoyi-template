package com.ruoyi.system.service;

import com.ruoyi.system.domain.ExposureConfig;

public interface IExposureExecutorService {
    /**
     * 执行一次曝光操作（占位实现）。
     * @param config 曝光配置
     * @return true 表示曝光成功并应记录事件，false 表示曝光未执行或失败
     */
    boolean executeExposure(ExposureConfig config);
}
