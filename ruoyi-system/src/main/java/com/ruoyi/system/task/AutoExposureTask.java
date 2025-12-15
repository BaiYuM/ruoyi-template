package com.ruoyi.system.task;

import java.util.Date;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.system.domain.ExposureConfig;
import com.ruoyi.system.domain.ExposureEvent;
import com.ruoyi.system.mapper.ExposureEventMapper;
import com.ruoyi.system.service.IExposureConfigService;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 简单自动曝光任务：周期检查所有自动曝光配置并在满足间隔时写入一次曝光事件（占位实现）
 */
@Component
public class AutoExposureTask {
    private static final Logger log = LoggerFactory.getLogger(AutoExposureTask.class);
    @Autowired
    private IExposureConfigService exposureService;

    @Autowired
    private ExposureEventMapper eventMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private com.ruoyi.system.service.IExposureExecutorService exposureExecutorService;

    private final Random random = new Random();

    // 每 30 秒检查一次（可调）
    @Scheduled(fixedDelay = 30000)
    public void run() {
        try {
            int min = parseConfig("exposure.auto.min", 1);
            int max = parseConfig("exposure.auto.max", 10);
            if (min <= 0) min = 1;
            if (max < min) max = min;

            // 获取所有曝光配置（可根据需求筛选启用/类型）
            List<ExposureConfig> configs = exposureService.selectExposureList(new ExposureConfig());
            Date now = new Date();
            for (ExposureConfig cfg : configs) {
                try {
                    // 跳过未启用的配置（status != 0 表示禁用）
                    if (!StringUtils.equals(UserConstants.NORMAL, cfg.getStatus())) {
                        continue;
                    }
                    String account = cfg.getAccount();
                    String platform = cfg.getPlatform();
                    Date last = eventMapper.selectLastExposureTime(account, platform);
                    int interval = random.nextInt(max - min + 1) + min; // 秒
                    boolean should = false;
                    if (last == null) {
                        should = true;
                    } else {
                        long diff = (now.getTime() - last.getTime()) / 1000L;
                        if (diff >= interval) should = true;
                    }
                    if (should) {
                        // 调用执行器进行实际曝光（占位实现）——只有执行成功才写入事件
                        try {
                            boolean executed = exposureExecutorService.executeExposure(cfg);
                            if (executed) {
                                // 先尝试增量更新，若未匹配到任何行再执行 upsert（降低重复行风险）
                                int updated = eventMapper.incrementSingleRowByConfigOrAccountPlatform(cfg.getId(), account, platform, 1, now);
                                if (updated == 0) {
                                    ExposureEvent ev = new ExposureEvent();
                                    ev.setConfigId(cfg.getId());
                                    ev.setAccount(account);
                                    ev.setPlatform(platform);
                                    ev.setExposureTime(now);
                                    ev.setCreateTime(now);
                                    ev.setExposureCount(1);
                                    eventMapper.upsertExposureEvent(ev);
                                }
                                // 检查当天累计次数，若达到或超过 dailyLimit，则禁用该配置并写入停止原因
                                try {
                                    Integer limit = cfg.getDailyLimit();
                                    if (limit != null && cfg.getId() != null) {
                                        int today = eventMapper.selectTodayExposureCountByConfig(cfg.getId());
                                        if (today >= limit) {
                                            cfg.setStatus("1"); // 禁用
                                            cfg.setLastStopReason("达到每日次数上限");
                                            try {
                                                exposureService.updateExposure(cfg);
                                            } catch (Exception e) {
                                                log.warn("failed to update config status for {}", cfg.getId(), e);
                                            }
                                        }
                                    }
                                } catch (Exception ex) {
                                    log.warn("failed to evaluate daily limit for config {}", cfg.getId(), ex);
                                }
                            } else {
                                log.info("exposureExecutorService reported not executed for config {}", cfg.getId());
                            }
                        } catch (Exception execEx) {
                            log.error("error while executing exposure for config {}", cfg.getId(), execEx);
                        }
                    }
                } catch (Exception inner) {
                    // 单个配置异常不影响整体
                    log.error("auto exposure failed for config {}", cfg.getId(), inner);
                }
            }
        } catch (Exception e) {
            log.error("auto exposure task failed", e);
        }
    }

    private int parseConfig(String key, int def) {
        try {
            String v = configService.selectConfigByKey(key);
            if (StringUtils.isEmpty(v)) return def;
            return Integer.parseInt(v);
        } catch (Exception e) {
            return def;
        }
    }
}
