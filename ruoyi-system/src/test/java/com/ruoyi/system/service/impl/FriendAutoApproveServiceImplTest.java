package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.FriendAutoApprove;
import com.ruoyi.system.mapper.FriendAutoApproveMapper;
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

class FriendAutoApproveServiceImplTest {

    @InjectMocks
    private FriendAutoApproveServiceImpl service;

    @Mock
    private FriendAutoApproveMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void selectAutoApproveList_Delegates() {
        List<FriendAutoApprove> list = new ArrayList<>();
        FriendAutoApprove f = new FriendAutoApprove(); f.setId(1L); list.add(f);
        when(mapper.selectAutoApproveList(any(FriendAutoApprove.class))).thenReturn(list);

        List<FriendAutoApprove> res = service.selectAutoApproveList(new FriendAutoApprove());
        assertEquals(1, res.size());
        verify(mapper, times(1)).selectAutoApproveList(any(FriendAutoApprove.class));
    }
}
