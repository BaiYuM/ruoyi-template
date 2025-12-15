package com.ruoyi.system.task;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
import com.ruoyi.system.mapper.ExposureEventLogMapper;

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
    private ExposureEventLogMapper eventLogMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private com.ruoyi.system.service.IExposureExecutorService exposureExecutorService;

    private final Random random = new Random();

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

                    // 如果配置了每天上限且今天已达到，则跳过当天的执行（但不修改 status），并持久化上次停止原因
                    try {
                        Integer limitCheck = cfg.getDailyLimit();
                        if (limitCheck != null && cfg.getId() != null) {
                            int todayCount = eventMapper.selectTodayExposureCountByConfig(cfg.getId());
                            if (todayCount >= limitCheck) {
                                try {
                                    // 仅写入停止原因以便前端展示，但保留 status=0（启用），以便隔日自动恢复
                                    cfg.setLastStopReason("达到每日次数上限");
                                    exposureService.updateExposure(cfg);
                                } catch (Exception e) {
                                    log.debug("failed to persist lastStopReason for config {}", cfg.getId(), e);
                                }
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        log.debug("failed to check daily limit for config {}", cfg.getId(), e);
                    }
                    int interval = random.nextInt(max - min + 1) + min; // 秒
                    boolean should = false;

                    // 如果配置了 startTime，则在每天的 startTime 到达且当天尚未触发过曝光时，触发一次
                    String startTime = cfg.getStartTime();
                    if (startTime != null && !startTime.trim().isEmpty()) {
                        try {
                            LocalTime lt = LocalTime.parse(startTime);
                            LocalDateTime startOfToday = LocalDate.now().atTime(lt);
                            Date startDate = Date.from(startOfToday.atZone(ZoneId.systemDefault()).toInstant());
                            if (now.compareTo(startDate) >= 0) {
                                // 若 last 为 null 或 last 在今天 startTime 之前，则应触发一次
                                if (last == null || last.before(startDate)) {
                                    should = true;
                                }
                            }
                        } catch (Exception parseEx) {
                            // 解析失败则回退到间隔逻辑
                            log.debug("failed to parse startTime {} for config {}, fallback to interval", startTime, cfg.getId());
                        }
                    }

                    // 若尚未因为 startTime 决定触发，则使用原有的随机间隔逻辑
                    if (!should) {
                        if (last == null) {
                            should = true;
                        } else {
                            long diff = (now.getTime() - last.getTime()) / 1000L;
                            if (diff >= interval) should = true;
                        }
                    }
                    if (should) {
                        // 调用执行器进行实际曝光（占位实现）——只有执行成功才写入事件
                        try {
                            boolean executed = exposureExecutorService.executeExposure(cfg);
                            if (executed) {
                                // 先写入历史日志（每次曝光一条），便于按日统计
                                try {
                                    ExposureEvent logEv = new ExposureEvent();
                                    logEv.setConfigId(cfg.getId());
                                    logEv.setAccount(account);
                                    logEv.setPlatform(platform);
                                    logEv.setExposureTime(now);
                                    logEv.setCreateTime(now);
                                    int inserted = eventLogMapper.insertExposureLog(logEv);
                                    if (inserted <= 0) {
                                        log.warn("no rows inserted into exposure_event_log for config {} account {} platform {}", cfg.getId(), account, platform);
                                    } else {
                                        log.info("exposure_event_log inserted for config {} account {} platform {}, rows={}", cfg.getId(), account, platform, inserted);
                                    }
                                } catch (Exception logEx) {
                                    log.warn("failed to insert exposure log for config {} account {} platform {}", cfg.getId(), account, platform, logEx);
                                }

                                // 累加总曝光计数（exposure_event 保留为累计表）
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
                                // 检查当天累计次数，若达到或超过 dailyLimit，则记录停止原因（不修改 status）以便前端显示并阻止当天后续执行
                                try {
                                    Integer limit = cfg.getDailyLimit();
                                    if (limit != null && cfg.getId() != null) {
                                        int today = eventMapper.selectTodayExposureCountByConfig(cfg.getId());
                                        if (today >= limit) {
                                            cfg.setLastStopReason("达到每日次数上限");
                                            try {
                                                exposureService.updateExposure(cfg);
                                            } catch (Exception e) {
                                                log.warn("failed to persist lastStopReason for {}", cfg.getId(), e);
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
