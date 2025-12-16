package com.ruoyi.web.controller.autoworkflow;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.FriendAutoApprove;
import com.ruoyi.system.service.IFriendAutoApproveService;
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
class FriendAutoApproveControllerTest {

    @InjectMocks
    private FriendAutoApproveController controller;

    @Mock
    private IFriendAutoApproveService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void list_ShouldReturnTableDataInfo_WhenExists() {
        FriendAutoApprove filter = new FriendAutoApprove();
        List<FriendAutoApprove> list = new ArrayList<>();
        FriendAutoApprove cfg = new FriendAutoApprove();
        cfg.setId(1L);
        list.add(cfg);

        when(service.selectAutoApproveList(any(FriendAutoApprove.class))).thenReturn(list);

        TableDataInfo res = controller.list(filter);
        assertNotNull(res);
        assertEquals(200, res.getCode());
        assertEquals(1, res.getTotal());
        verify(service, times(1)).selectAutoApproveList(any(FriendAutoApprove.class));
    }

    @Test
    void save_ShouldInsert_WhenIdNull() {
        FriendAutoApprove cfg = new FriendAutoApprove();
        cfg.setId(null);
        when(service.insertAutoApprove(any(FriendAutoApprove.class))).thenReturn(1);

        AjaxResult r = controller.save(cfg);
        assertNotNull(r);
        assertTrue((Boolean) r.get("success"));
        verify(service, times(1)).insertAutoApprove(any(FriendAutoApprove.class));
    }
}
