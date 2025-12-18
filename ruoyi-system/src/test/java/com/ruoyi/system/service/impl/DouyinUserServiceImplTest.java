package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.FaUser;
import com.ruoyi.system.domain.CommentUser;
import com.ruoyi.system.domain.vo.DouyinUserVO;
import com.ruoyi.system.mapper.FaUserMapper;
import com.ruoyi.system.mapper.CommentUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 抖音用户统一管理 服务层测试
 */
class DouyinUserServiceImplTest {

    @InjectMocks
    private DouyinUserServiceImpl service;

    @Mock
    private FaUserMapper faUserMapper;

    @Mock
    private CommentUserMapper commentUserMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void selectDouyinUserById_ShouldReturnUnifiedUser() {
        // 准备测试数据
        FaUser faUser = new FaUser();
        faUser.setId(1L);
        faUser.setNickname("测试用户");
        faUser.setPlatform("douyin");
        faUser.setOpenId("open123");

        CommentUser commentUser = new CommentUser();
        commentUser.setId(1L);
        commentUser.setUserId(1L);
        commentUser.setFollowerCount(100);
        commentUser.setFollowingCount(50);

        // 设置Mock行为
        when(faUserMapper.selectFaUserById(1L)).thenReturn(faUser);
        when(commentUserMapper.selectCommentUserByUserId(1L)).thenReturn(commentUser);

        // 执行测试
        DouyinUserVO result = service.selectDouyinUserById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(faUser, result.getFaUser());
        assertEquals(commentUser, result.getCommentUser());
        assertEquals("测试用户", result.getDisplayNickname());
        assertEquals(100, result.getFollowerCount());
        assertEquals("douyin", result.getPlatform());

        // 验证方法调用
        verify(faUserMapper, times(1)).selectFaUserById(1L);
        verify(commentUserMapper, times(1)).selectCommentUserByUserId(1L);
    }

    @Test
    void selectDouyinUserByOpenId_ShouldReturnUnifiedUser() {
        // 准备测试数据
        FaUser faUser = new FaUser();
        faUser.setId(1L);
        faUser.setOpenId("open123");

        CommentUser commentUser = new CommentUser();
        commentUser.setUserId(1L);

        // 设置Mock行为
        when(faUserMapper.selectFaUserByOpenId("open123")).thenReturn(faUser);
        when(commentUserMapper.selectCommentUserByUserId(1L)).thenReturn(commentUser);

        // 执行测试
        DouyinUserVO result = service.selectDouyinUserByOpenId("open123");

        // 验证结果
        assertNotNull(result);
        assertEquals(faUser, result.getFaUser());
        assertEquals(commentUser, result.getCommentUser());
    }

    @Test
    void insertDouyinUser_ShouldInsertBothTables() {
        // 准备测试数据
        FaUser faUser = new FaUser();
        faUser.setNickname("新用户");

        CommentUser commentUser = new CommentUser();
        commentUser.setFollowerCount(0);

        DouyinUserVO vo = new DouyinUserVO();
        vo.setFaUser(faUser);
        vo.setCommentUser(commentUser);

        // 设置Mock行为
        when(faUserMapper.insertFaUser(any(FaUser.class))).thenReturn(1);
        when(commentUserMapper.insertCommentUser(any(CommentUser.class))).thenReturn(1);

        // 执行测试
        int result = service.insertDouyinUser(vo);

        // 验证结果
        assertEquals(1, result);

        // 验证方法调用
        verify(faUserMapper, times(1)).insertFaUser(any(FaUser.class));
        verify(commentUserMapper, times(1)).insertCommentUser(any(CommentUser.class));
    }

    @Test
    void deleteDouyinUserById_ShouldDeleteFromBothTables() {
        // 设置Mock行为
        when(faUserMapper.deleteFaUserById(1L)).thenReturn(1);

        // 执行测试
        int result = service.deleteDouyinUserById(1L);

        // 验证结果
        assertEquals(1, result);

        // 验证方法调用
        verify(commentUserMapper, times(1)).deleteCommentUserById(1L);
        verify(faUserMapper, times(1)).deleteFaUserById(1L);
    }
}