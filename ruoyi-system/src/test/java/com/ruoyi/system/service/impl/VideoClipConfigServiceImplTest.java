package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.VideoClipConfig;
import com.ruoyi.system.mapper.VideoClipConfigMapper;
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

class VideoClipConfigServiceImplTest {

    @InjectMocks
    private VideoClipConfigServiceImpl service;

    @Mock
    private VideoClipConfigMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void selectVideoClipList_ShouldDelegateToMapper() {
        List<VideoClipConfig> list = new ArrayList<>();
        VideoClipConfig cfg = new VideoClipConfig(); cfg.setId(1L); list.add(cfg);
        when(mapper.selectVideoClipList(any(VideoClipConfig.class))).thenReturn(list);

        List<VideoClipConfig> res = service.selectVideoClipList(new VideoClipConfig());
        assertNotNull(res);
        assertEquals(1, res.size());
        verify(mapper, times(1)).selectVideoClipList(any(VideoClipConfig.class));
    }

    @Test
    void deleteVideoClipByIds_ShouldSumDeletedRows() {
        when(mapper.deleteVideoClipById(1L)).thenReturn(1);
        when(mapper.deleteVideoClipById(2L)).thenReturn(0);

        int r = service.deleteVideoClipByIds(new Long[]{1L,2L});
        assertEquals(1, r);
        verify(mapper, times(1)).deleteVideoClipById(1L);
        verify(mapper, times(1)).deleteVideoClipById(2L);
    }
}
