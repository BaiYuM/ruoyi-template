package com.ruoyi.web.controller.autoworkflow;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.FriendAddPlan;
import com.ruoyi.system.service.IFriendAddPlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 添加好友计划控制器测试类
 */
class FriendAddPlanControllerTest {

    @InjectMocks
    private FriendAddPlanController friendAddPlanController;

    @Mock
    private IFriendAddPlanService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 设置安全上下文以避免getUsername()方法出错
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken("testUser", null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 设置请求上下文
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void list() {
        // 准备测试数据
        FriendAddPlan filter = new FriendAddPlan();
        filter.setName("test plan");
        
        List<FriendAddPlan> mockPlans = new ArrayList<>();
        FriendAddPlan plan1 = new FriendAddPlan();
        plan1.setId(1L);
        plan1.setName("plan 1");
        mockPlans.add(plan1);
        
        FriendAddPlan plan2 = new FriendAddPlan();
        plan2.setId(2L);
        plan2.setName("plan 2");
        mockPlans.add(plan2);
        
        // 模拟服务调用
        when(service.selectPlanList(any(FriendAddPlan.class))).thenReturn(mockPlans);
        
        // 调用被测试的方法
        TableDataInfo result = friendAddPlanController.list(filter);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMsg());
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getRows().size());
        
        List<FriendAddPlan> rows = (List<FriendAddPlan>) result.getRows();
        assertEquals("plan 1", rows.get(0).getName());
        assertEquals("plan 2", rows.get(1).getName());
    }
}