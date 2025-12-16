package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.FriendAddRecord;
import com.ruoyi.system.mapper.FriendAddRecordMapper;
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

class FriendAddRecordServiceImplTest {

    @InjectMocks
    private FriendAddRecordServiceImpl service;

    @Mock
    private FriendAddRecordMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void selectRecordList_Delegates() {
        List<FriendAddRecord> list = new ArrayList<>();
        FriendAddRecord r = new FriendAddRecord(); r.setId(1L); list.add(r);
        when(mapper.selectRecordList(any(FriendAddRecord.class))).thenReturn(list);

        List<FriendAddRecord> res = service.selectRecordList(new FriendAddRecord());
        assertEquals(1, res.size());
        verify(mapper, times(1)).selectRecordList(any(FriendAddRecord.class));
    }

    @Test
    void deleteRecordByIds_Sums() {
        when(mapper.deleteRecordById(1L)).thenReturn(1);
        when(mapper.deleteRecordById(2L)).thenReturn(0);

        int r = service.deleteRecordByIds(new Long[]{1L,2L});
        assertEquals(1, r);
    }
}
