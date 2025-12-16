package com.ruoyi.web.controller.autoworkflow;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.VideoClipConfig;
import com.ruoyi.system.service.IVideoClipConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 视频剪辑控制器单元测试
 */
@SpringJUnitConfig
class VideoClipControllerTest {

    @InjectMocks
    private VideoClipController controller;

    @Mock
    private IVideoClipConfigService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void list_ShouldReturnTableDataInfo_WhenConfigsExist() {
        VideoClipConfig filter = new VideoClipConfig();
        List<VideoClipConfig> list = new ArrayList<>();
        VideoClipConfig cfg = new VideoClipConfig();
        cfg.setId(1L);
        list.add(cfg);

        when(service.selectVideoClipList(any(VideoClipConfig.class))).thenReturn(list);

        TableDataInfo res = controller.list(filter);
        assertNotNull(res);
        assertEquals(200, res.getCode());
        assertEquals(1, res.getTotal());
        assertEquals(1, res.getRows().size());
        verify(service, times(1)).selectVideoClipList(any(VideoClipConfig.class));
    }

    @Test
    void save_ShouldInsert_WhenIdIsNull() {
        VideoClipConfig cfg = new VideoClipConfig();
        cfg.setId(null);
        when(service.insertVideoClip(any(VideoClipConfig.class))).thenReturn(1);

        AjaxResult r = controller.save(cfg);
        assertNotNull(r);
        assertTrue((Boolean) r.get("success"));
        verify(service, times(1)).insertVideoClip(any(VideoClipConfig.class));
        verify(service, times(0)).updateVideoClip(any(VideoClipConfig.class));
    }

    @Test
    void save_ShouldUpdate_WhenIdNotNull() {
        VideoClipConfig cfg = new VideoClipConfig();
        cfg.setId(2L);
        when(service.updateVideoClip(any(VideoClipConfig.class))).thenReturn(1);

        AjaxResult r = controller.save(cfg);
        assertNotNull(r);
        assertTrue((Boolean) r.get("success"));
        verify(service, times(0)).insertVideoClip(any(VideoClipConfig.class));
        verify(service, times(1)).updateVideoClip(any(VideoClipConfig.class));
    }
}
