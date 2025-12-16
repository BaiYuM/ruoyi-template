package com.ruoyi.web.controller.autoworkflow;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.FriendAddRecord;
import com.ruoyi.system.service.IFriendAddRecordService;
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
class FriendAddRecordControllerTest {

    @InjectMocks
    private FriendAddRecordController controller;

    @Mock
    private IFriendAddRecordService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void list_ShouldReturnTableDataInfo_WhenRecordsExist() {
        FriendAddRecord filter = new FriendAddRecord();
        List<FriendAddRecord> list = new ArrayList<>();
        FriendAddRecord r = new FriendAddRecord();
        r.setId(1L);
        list.add(r);

        when(service.selectRecordList(any(FriendAddRecord.class))).thenReturn(list);

        TableDataInfo res = controller.list(filter);
        assertNotNull(res);
        assertEquals(200, res.getCode());
        assertEquals(1, res.getTotal());
        verify(service, times(1)).selectRecordList(any(FriendAddRecord.class));
    }

    @Test
    void save_ShouldInsert_WhenIdNull() {
        FriendAddRecord r = new FriendAddRecord();
        r.setId(null);
        when(service.insertRecord(any(FriendAddRecord.class))).thenReturn(1);

        AjaxResult res = controller.save(r);
        assertNotNull(res);
        assertTrue((Boolean) res.get("success"));
        verify(service, times(1)).insertRecord(any(FriendAddRecord.class));
    }
}
