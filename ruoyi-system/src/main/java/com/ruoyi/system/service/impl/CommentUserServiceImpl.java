package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CommentUserMapper;
import com.ruoyi.system.domain.CommentUser;
import com.ruoyi.system.service.ICommentUserService;

/**
 * 平台账户Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@Service
public class CommentUserServiceImpl implements ICommentUserService 
{
    @Autowired
    private CommentUserMapper commentUserMapper;

    /**
     * 查询平台账户
     * 
     * @param id 平台账户主键
     * @return 平台账户
     */
    @Override
    public CommentUser selectCommentUserById(Long id)
    {
        return commentUserMapper.selectCommentUserById(id);
    }

    /**
     * 查询平台账户列表
     * 
     * @param commentUser 平台账户
     * @return 平台账户
     */
    @Override
    public List<CommentUser> selectCommentUserList(CommentUser commentUser)
    {
        return commentUserMapper.selectCommentUserList(commentUser);
    }

    /**
     * 新增平台账户
     * 
     * @param commentUser 平台账户
     * @return 结果
     */
    @Override
    public int insertCommentUser(CommentUser commentUser)
    {
        commentUser.setCreateTime(DateUtils.getNowDate());
        return commentUserMapper.insertCommentUser(commentUser);
    }

    /**
     * 修改平台账户
     * 
     * @param commentUser 平台账户
     * @return 结果
     */
    @Override
    public int updateCommentUser(CommentUser commentUser)
    {
        commentUser.setUpdateTime(DateUtils.getNowDate());
        return commentUserMapper.updateCommentUser(commentUser);
    }

    /**
     * 批量删除平台账户
     * 
     * @param ids 需要删除的平台账户主键
     * @return 结果
     */
    @Override
    public int deleteCommentUserByIds(Long[] ids)
    {
        return commentUserMapper.deleteCommentUserByIds(ids);
    }

    /**
     * 删除平台账户信息
     * 
     * @param id 平台账户主键
     * @return 结果
     */
    @Override
    public int deleteCommentUserById(Long id)
    {
        return commentUserMapper.deleteCommentUserById(id);
    }
}
