package com.ruoyi.web.controller.autoworkflow;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.AiExpertConfig;
import com.ruoyi.system.service.IAiExpertConfigService;
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
class AiExpertControllerTest {

    @InjectMocks
    private AiExpertController controller;

    @Mock
    private IAiExpertConfigService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void list_ShouldReturnTableDataInfo_WhenExists() {
        AiExpertConfig filter = new AiExpertConfig();
        List<AiExpertConfig> list = new ArrayList<>();
        AiExpertConfig cfg = new AiExpertConfig();
        cfg.setId(1L);
        list.add(cfg);

        when(service.selectAiList(any(AiExpertConfig.class))).thenReturn(list);

        TableDataInfo res = controller.list(filter);
        assertNotNull(res);
        assertEquals(200, res.getCode());
        assertEquals(1, res.getTotal());
        verify(service, times(1)).selectAiList(any(AiExpertConfig.class));
    }

    @Test
    void save_ShouldInsertOrUpdate() {
        AiExpertConfig cfg = new AiExpertConfig();
        cfg.setId(null);
        when(service.insertAi(any(AiExpertConfig.class))).thenReturn(1);

        AjaxResult r = controller.save(cfg);
        assertNotNull(r);
        assertTrue((Boolean) r.get("success"));
        verify(service, times(1)).insertAi(any(AiExpertConfig.class));
    }
}
