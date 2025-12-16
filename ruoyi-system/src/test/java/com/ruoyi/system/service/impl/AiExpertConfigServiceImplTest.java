package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.AiExpertConfig;
import com.ruoyi.system.mapper.AiExpertConfigMapper;
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

class AiExpertConfigServiceImplTest {

    @InjectMocks
    private AiExpertConfigServiceImpl service;

    @Mock
    private AiExpertConfigMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void selectAiList_Delegates() {
        List<AiExpertConfig> list = new ArrayList<>();
        AiExpertConfig cfg = new AiExpertConfig(); cfg.setId(1L); list.add(cfg);
        when(mapper.selectAiList(any(AiExpertConfig.class))).thenReturn(list);

        List<AiExpertConfig> res = service.selectAiList(new AiExpertConfig());
        assertEquals(1, res.size());
        verify(mapper, times(1)).selectAiList(any(AiExpertConfig.class));
    }
}
