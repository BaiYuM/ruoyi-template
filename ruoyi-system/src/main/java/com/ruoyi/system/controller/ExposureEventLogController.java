package com.ruoyi.system.controller;

import com.ruoyi.system.domain.ExposureEvent;
import com.ruoyi.system.service.IExposureEventLogService;
import com.ruoyi.system.mapper.ExposureEventLogMapper;
import com.ruoyi.system.mapper.ExposureEventMapper;
import com.ruoyi.system.service.IExposureConfigService;
import com.ruoyi.system.service.IExposureExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/exposure")
public class ExposureEventLogController {

    @Autowired
    private IExposureEventLogService logService;

    @Autowired
    private IExposureConfigService exposureService;

    @Autowired
    private IExposureExecutorService exposureExecutorService;

    @Autowired
    private ExposureEventLogMapper eventLogMapper;

    @Autowired
    private ExposureEventMapper eventMapper;

    @GetMapping("/todayCount")
    public Map<String, Object> todayCount(@RequestParam Long configId) {
        int cnt = logService.selectTodayCountByConfig(configId);
        Map<String, Object> r = new HashMap<>();
        r.put("configId", configId);
        r.put("todayCount", cnt);
        return r;
    }

    @GetMapping("/logs")
    public Map<String, Object> logs(@RequestParam Long configId,
                                    @RequestParam(defaultValue = "1") int pageNum,
                                    @RequestParam(defaultValue = "20") int pageSize) {
        int offset = (Math.max(1, pageNum) - 1) * pageSize;
        Map<String, Object> params = new HashMap<>();
        params.put("configId", configId);
        params.put("offset", offset);
        params.put("limit", pageSize);
        List<ExposureEvent> rows = logService.selectLogsByConfig(params);
        Map<String, Object> r = new HashMap<>();
        r.put("rows", rows);
        r.put("pageNum", pageNum);
        r.put("pageSize", pageSize);
        return r;
    }

    @PostMapping("/trigger")
    public Map<String, Object> trigger(@RequestParam Long configId) {
        Map<String, Object> r = new HashMap<>();
        try {
            // load config
            com.ruoyi.system.domain.ExposureConfig cfg = exposureService.selectExposureById(configId);
            if (cfg == null) {
                r.put("success", false);
                r.put("message", "config not found");
                return r;
            }
            boolean executed = exposureExecutorService.executeExposure(cfg);
            if (executed) {
                // write log and update cumulative count similar to AutoExposureTask
                Date now = new Date();
                com.ruoyi.system.domain.ExposureEvent logEv = new com.ruoyi.system.domain.ExposureEvent();
                logEv.setConfigId(cfg.getId());
                logEv.setAccount(cfg.getAccount());
                logEv.setPlatform(cfg.getPlatform());
                logEv.setExposureTime(now);
                logEv.setCreateTime(now);
                eventLogMapper.insertExposureLog(logEv);

                int updated = eventMapper.incrementSingleRowByConfigOrAccountPlatform(cfg.getId(), cfg.getAccount(), cfg.getPlatform(), 1, now);
                if (updated == 0) {
                    com.ruoyi.system.domain.ExposureEvent ev = new com.ruoyi.system.domain.ExposureEvent();
                    ev.setConfigId(cfg.getId());
                    ev.setAccount(cfg.getAccount());
                    ev.setPlatform(cfg.getPlatform());
                    ev.setExposureTime(now);
                    ev.setCreateTime(now);
                    ev.setExposureCount(1);
                    eventMapper.upsertExposureEvent(ev);
                }

                r.put("success", true);
            } else {
                r.put("success", false);
                r.put("message", "executor returned false");
            }
        } catch (Exception e) {
            r.put("success", false);
            r.put("message", e.getMessage());
        }
        return r;
    }
}
