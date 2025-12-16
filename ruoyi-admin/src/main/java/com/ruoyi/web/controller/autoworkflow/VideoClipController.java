package com.ruoyi.web.controller.autoworkflow;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.VideoClipConfig;
import com.ruoyi.system.service.IVideoClipConfigService;

/**
 * 视频剪辑配置接口控制器
 * 提供列表查询与保存（创建/更新）接口，路由前缀：/auto/video
 */
@RestController
public class VideoClipController extends BaseController {
    @Autowired
    private IVideoClipConfigService service;

    /**
     * 查询视频剪辑配置列表（分页/筛选）
     * @param filter 过滤条件封装为 VideoClipConfig
     * @return 分页表格数据 TableDataInfo
     */
    @PreAuthorize("@ss.hasPermi('autoworkflow:video:list')")
    @GetMapping("/auto/video/list")
    public TableDataInfo list(VideoClipConfig filter) {
        try { startPage(); } catch (Exception ignored) {}
        List<VideoClipConfig> list = service.selectVideoClipList(filter);
        return getDataTable(list);
    }

    /**
     * 创建或更新视频剪辑配置（若包含 id 则为更新，否则为创建）
     * @param cfg 要保存的 VideoClipConfig 对象
     * @return 操作结果 AjaxResult，包含 success 字段
     */
    @PreAuthorize("@ss.hasPermi('autoworkflow:video:add')")
    @Log(title = "视频剪辑", businessType = BusinessType.INSERT)
    @PostMapping("/auto/video/save")
    public AjaxResult save(@Validated @RequestBody VideoClipConfig cfg) {
        try { cfg.setCreateBy(getUsername()); } catch (Exception ignored) {}
        AjaxResult res;
        if (cfg.getId() == null) res = toAjax(service.insertVideoClip(cfg)); else res = toAjax(service.updateVideoClip(cfg));
        res.put("success", res.isSuccess());
        return res;
    }
}
