package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.AutoPublishConfig;
import com.ruoyi.system.mapper.AutoPublishConfigMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AutoPublishConfigServiceImplTest {

    @InjectMocks
    private AutoPublishConfigServiceImpl service;

    @Mock
    private AutoPublishConfigMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void selectAutoPublishList_Delegates() {
        List<AutoPublishConfig> list = new ArrayList<>();
        AutoPublishConfig cfg = new AutoPublishConfig(); cfg.setId(1L); list.add(cfg);
        when(mapper.selectAutoPublishList(any(AutoPublishConfig.class))).thenReturn(list);

        List<AutoPublishConfig> res = service.selectAutoPublishList(new AutoPublishConfig());
        assertEquals(1, res.size());
        verify(mapper, times(1)).selectAutoPublishList(any(AutoPublishConfig.class));
    }

    @Test
    void deleteAutoPublishByIds_SumsResults() {
        when(mapper.deleteAutoPublishById(1L)).thenReturn(1);
        when(mapper.deleteAutoPublishById(2L)).thenReturn(1);

        int r = service.deleteAutoPublishByIds(new Long[]{1L,2L});
        assertEquals(2, r);
    }
}
