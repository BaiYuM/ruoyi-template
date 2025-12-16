package com.ruoyi.web.controller.autoworkflow;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.AutoPublishConfig;
import com.ruoyi.system.service.IAutoPublishConfigService;
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

@SpringJUnitConfig
class AutoPublishControllerTest {

    @InjectMocks
    private AutoPublishController controller;

    @Mock
    private IAutoPublishConfigService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void list_ShouldReturnTableDataInfo_WhenConfigsExist() {
        AutoPublishConfig filter = new AutoPublishConfig();
        List<AutoPublishConfig> list = new ArrayList<>();
        AutoPublishConfig cfg = new AutoPublishConfig();
        cfg.setId(1L);
        list.add(cfg);

        when(service.selectAutoPublishList(any(AutoPublishConfig.class))).thenReturn(list);

        TableDataInfo res = controller.list(filter);
        assertNotNull(res);
        assertEquals(200, res.getCode());
        assertEquals(1, res.getTotal());
        assertEquals(1, res.getRows().size());
        verify(service, times(1)).selectAutoPublishList(any(AutoPublishConfig.class));
    }

    @Test
    void save_ShouldInsert_WhenIdNull() {
        AutoPublishConfig cfg = new AutoPublishConfig();
        cfg.setId(null);
        when(service.insertAutoPublish(any(AutoPublishConfig.class))).thenReturn(1);

        AjaxResult r = controller.save(cfg);
        assertNotNull(r);
        assertTrue((Boolean) r.get("success"));
        verify(service, times(1)).insertAutoPublish(any(AutoPublishConfig.class));
    }

    @Test
    void save_ShouldUpdate_WhenIdNotNull() {
        AutoPublishConfig cfg = new AutoPublishConfig();
        cfg.setId(2L);
        when(service.updateAutoPublish(any(AutoPublishConfig.class))).thenReturn(1);

        AjaxResult r = controller.save(cfg);
        assertNotNull(r);
        assertTrue((Boolean) r.get("success"));
        verify(service, times(1)).updateAutoPublish(any(AutoPublishConfig.class));
    }
}
