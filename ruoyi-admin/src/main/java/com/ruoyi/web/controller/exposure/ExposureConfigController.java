package com.ruoyi.web.controller.exposure;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.ruoyi.system.service.impl.ExposureParseService;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.common.utils.StringUtils;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.IExposureConfigService;
import com.ruoyi.system.mapper.ExposureEventMapper;
import com.ruoyi.system.service.IExposureExecutorService;
import com.ruoyi.system.domain.ExposureConfig;
/**
 * 曝光配置Controller
 */
@RestController
@Api(tags = "曝光配置")
public class ExposureConfigController extends BaseController
{
    @Autowired
    private IExposureConfigService exposureService;

    @Autowired
    private ExposureParseService exposureParseService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ExposureEventMapper eventMapper;

    @Autowired
    private IExposureExecutorService exposureExecutorService;

    // 统一的曝光配置API接口
    @PreAuthorize("@ss.hasPermi('exposure:config:list')")
    @ApiOperation("获取曝光配置列表（分页/筛选）- 统一接口")
    @GetMapping("/exposure/config/list")
    public TableDataInfo getExposureList(ExposureConfig exposure)
    {
        try {
            startPage();
        } catch (Exception ignored) {
        }
        List<ExposureConfig> list = exposureService.selectExposureList(exposure);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('exposure:config:add')")
    @Log(title = "曝光配置", businessType = BusinessType.INSERT)
    @ApiOperation("创建或更新曝光配置 - 统一接口")
    @PostMapping("/exposure/config/save")
    public AjaxResult saveExposure(@Validated @RequestBody ExposureConfig exposure)
    {
        try {
            exposure.setCreateBy(getUsername());
        } catch (Exception ignored) {
        }
        AjaxResult result;
        if (exposure.getId() == null) {
            int r = exposureService.insertExposure(exposure);
            result = toAjax(r);
        } else {
            int r = exposureService.updateExposure(exposure);
            result = toAjax(r);
        }
        result.put("success", result.isSuccess());
        return result;
    }

    @PreAuthorize("@ss.hasPermi('exposure:auto:list')")
    @ApiOperation("获取自动曝光配置列表（分页/筛选）")
    @GetMapping("/exposure/auto/list")
    public TableDataInfo list(ExposureConfig exposure)
    {
        try {
            startPage();
        } catch (Exception ignored) {
        }
        List<ExposureConfig> list = exposureService.selectExposureList(exposure);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('exposure:auto:stats')")
    @ApiOperation("曝光统计（按账号汇总）")
    @GetMapping("/exposure/auto/stats")
    public TableDataInfo stats(ExposureConfig filter) {
        try { startPage(); } catch (Exception ignored) {}
        List<com.ruoyi.system.domain.ExposureStatsDTO> list = exposureService.selectExposureStats(filter);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('exposure:auto:add')")
    @Log(title = "自动曝光", businessType = BusinessType.INSERT)
    @ApiOperation("创建或更新自动曝光配置")
    @PostMapping("/exposure/auto/save")
    public AjaxResult add(@Validated @RequestBody ExposureConfig exposure)
    {
        try {
            exposure.setCreateBy(getUsername());
        } catch (Exception ignored) {
        }
        AjaxResult result;
        if (exposure.getId() == null) {
            int r = exposureService.insertExposure(exposure);
            result = toAjax(r);
        } else {
            int r = exposureService.updateExposure(exposure);
            result = toAjax(r);
        }
        result.put("success", result.isSuccess());
        return result;
    }

    @PreAuthorize("@ss.hasPermi('exposure:dupload')")
    @ApiOperation("获取定向/链接曝光配置列表（分页/筛选）")
    @GetMapping("/exposure/directional/list")
    public TableDataInfo directionalList(ExposureConfig exposure) {
        try {
            startPage();
        } catch (Exception ignored) {
        }
        List<ExposureConfig> list = exposureService.selectExposureList(exposure);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('exposure:dupload')")
    @Log(title = "定向曝光", businessType = BusinessType.INSERT)
    @ApiOperation("创建或更新定向/链接曝光配置")
    @PostMapping("/exposure/directional/save")
    public AjaxResult saveDirectional(@Validated @RequestBody ExposureConfig exposure) {
        try {
            exposure.setCreateBy(getUsername());
        } catch (Exception ignored) {
        }
        AjaxResult result;
        if (exposure.getId() == null) {
            int r = exposureService.insertExposure(exposure);
            result = toAjax(r);
        } else {
            int r = exposureService.updateExposure(exposure);
            result = toAjax(r);
        }
        result.put("success", result.isSuccess());
        return result;
    }

    @PreAuthorize("@ss.hasPermi('exposure:dupload')")
    @ApiOperation("上传目标账号文件并解析（支持 csv, xls, xlsx），建议使用异步接口处理大文件）")
    @PostMapping("/exposure/directional/upload")
    public AjaxResult upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            AjaxResult err = AjaxResult.error("文件为空");
            err.put("success", err.isSuccess());
            return err;
        }
        try {
            ExposureParseService.ParseResult r = exposureParseService.parseFile(file);
            Map<String, Object> data = new HashMap<>();
            data.put("parsed", r.parsed);
            data.put("errors", r.errors);
            AjaxResult ok = AjaxResult.success("解析完成", data);
            ok.put("success", ok.isSuccess());
            return ok;
        } catch (Exception e) {
            AjaxResult err = AjaxResult.error("解析文件失败：" + e.getMessage());
            err.put("success", err.isSuccess());
            return err;
        }
    }

    @PreAuthorize("@ss.hasPermi('exposure:dupload')")
    @ApiOperation("异步上传目标账号文件，返回 taskId，可轮询结果")
    @PostMapping("/exposure/directional/upload/async")
    public AjaxResult uploadAsync(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            AjaxResult err = AjaxResult.error("文件为空");
            err.put("success", err.isSuccess());
            return err;
        }
        try {
            String taskId = ExposureParseTaskManager.submit(file, exposureParseService);
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", taskId);
            AjaxResult ok = AjaxResult.success("任务已提交", data);
            ok.put("success", ok.isSuccess());
            return ok;
        } catch (Exception e) {
            AjaxResult err = AjaxResult.error("提交解析任务失败：" + e.getMessage());
            err.put("success", err.isSuccess());
            return err;
        }
    }

    @PreAuthorize("@ss.hasPermi('exposure:dupload')")
    @ApiOperation("查询异步解析结果（taskId）")
    @GetMapping("/exposure/parse/result")
    public AjaxResult parseResult(String taskId) {
        if (taskId == null || taskId.isEmpty()) {
            AjaxResult err = AjaxResult.error("taskId 为空");
            err.put("success", err.isSuccess());
            return err;
        }
        com.ruoyi.system.service.impl.ExposureParseService.ParseResult r = ExposureParseTaskManager.getResult(taskId);
        if (r == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "processing");
            AjaxResult ok = AjaxResult.success("处理中", data);
            ok.put("success", ok.isSuccess());
            return ok;
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "done");
            data.put("parsed", r.parsed);
            data.put("errors", r.errors);
            AjaxResult ok = AjaxResult.success("完成", data);
            ok.put("success", ok.isSuccess());
            return ok;
        }
    }

    @PreAuthorize("@ss.hasPermi('exposure:auto:settings')")
    @ApiOperation("获取自动曝光设置")
    @GetMapping("/exposure/auto/settings")
    public AjaxResult getAutoSettings() {
        String min = configService.selectConfigByKey("exposure.auto.min");
        String max = configService.selectConfigByKey("exposure.auto.max");
        Map<String, Object> data = new HashMap<>();
        data.put("min", StringUtils.isEmpty(min) ? 1 : Integer.valueOf(min));
        data.put("max", StringUtils.isEmpty(max) ? 10 : Integer.valueOf(max));
        AjaxResult ok = AjaxResult.success(data);
        ok.put("success", ok.isSuccess());
        return ok;
    }

    @PreAuthorize("@ss.hasPermi('exposure:auto:settings')")
    @ApiOperation("保存自动曝光设置")
    @PostMapping("/exposure/auto/settings")
    public AjaxResult saveAutoSettings(@RequestBody Map<String, Object> payload) {
        Integer min = payload.get("min") == null ? 1 : Integer.valueOf(payload.get("min").toString());
        Integer max = payload.get("max") == null ? 10 : Integer.valueOf(payload.get("max").toString());
        // upsert via SysConfig
        try {
            SysConfig q = new SysConfig();
            q.setConfigKey("exposure.auto.min");
            List<SysConfig> existsMin = configService.selectConfigList(q);
            if (existsMin != null && !existsMin.isEmpty()) {
                SysConfig s = existsMin.get(0);
                s.setConfigValue(String.valueOf(min));
                s.setUpdateBy(getUsername());
                configService.updateConfig(s);
            } else {
                SysConfig s = new SysConfig();
                s.setConfigName("自动曝光-最小间隔");
                s.setConfigKey("exposure.auto.min");
                s.setConfigValue(String.valueOf(min));
                s.setConfigType("N");
                s.setCreateBy(getUsername());
                configService.insertConfig(s);
            }

            q = new SysConfig();
            q.setConfigKey("exposure.auto.max");
            List<SysConfig> existsMax = configService.selectConfigList(q);
            if (existsMax != null && !existsMax.isEmpty()) {
                SysConfig s = existsMax.get(0);
                s.setConfigValue(String.valueOf(max));
                s.setUpdateBy(getUsername());
                configService.updateConfig(s);
            } else {
                SysConfig s = new SysConfig();
                s.setConfigName("自动曝光-最大间隔");
                s.setConfigKey("exposure.auto.max");
                s.setConfigValue(String.valueOf(max));
                s.setConfigType("N");
                s.setCreateBy(getUsername());
                configService.insertConfig(s);
            }
            AjaxResult ok = AjaxResult.success();
            ok.put("success", ok.isSuccess());
            return ok;
        } catch (Exception e) {
            AjaxResult err = AjaxResult.error("保存失败: " + e.getMessage());
            err.put("success", err.isSuccess());
            return err;
        }
    }

    @PreAuthorize("@ss.hasPermi('exposure:config:trigger')")
    @ApiOperation("手动触发曝光配置")
    @PostMapping("/exposure/config/trigger/{configId}")
    public AjaxResult triggerExposure(@PathVariable Long configId) {
        try {
            // 获取配置信息
            ExposureConfig config = exposureService.selectExposureById(configId);
            if (config == null) {
                AjaxResult err = AjaxResult.error("配置不存在");
                err.put("success", err.isSuccess());
                return err;
            }

            // 执行曝光逻辑（复用自动曝光的执行器）
            boolean executed = exposureExecutorService.executeExposure(config);
            if (executed) {
                // 记录曝光事件
                ExposureEvent event = new ExposureEvent();
                event.setConfigId(configId);
                event.setAccount(config.getAccount());
                event.setPlatform(config.getPlatform());
                event.setExposureType(config.getExposureType()); // 从配置中获取曝光类型
                event.setExposureTime(new Date());
                event.setCreateTime(new Date());
                event.setExposureCount(1);

                // 先尝试增量更新，若未匹配到任何行再执行 upsert
                int updated = eventMapper.incrementSingleRowByConfigOrAccountPlatform(configId, config.getAccount(), config.getPlatform(), 1, new Date());
                if (updated == 0) {
                    eventMapper.upsertExposureEvent(event);
                }

                AjaxResult ok = AjaxResult.success("曝光执行成功");
                ok.put("success", ok.isSuccess());
                return ok;
            } else {
                AjaxResult err = AjaxResult.error("曝光执行失败");
                err.put("success", err.isSuccess());
                return err;
            }
        } catch (Exception e) {
            AjaxResult err = AjaxResult.error("触发曝光失败：" + e.getMessage());
            err.put("success", err.isSuccess());
            return err;
        }
    }
}
