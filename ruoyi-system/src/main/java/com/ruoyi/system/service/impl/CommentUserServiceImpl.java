package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CommentUserMapper;
import com.ruoyi.system.domain.CommentUser;
import com.ruoyi.system.service.ICommentUserService;

/**
 * 评论用户 服务层实现
 */
@Service
public class CommentUserServiceImpl implements ICommentUserService
{
    @Autowired
    private CommentUserMapper commentUserMapper;

    /**
     * 查询评论用户
     *
     * @param id 评论用户主键
     * @return 评论用户
     */
    @Override
    public CommentUser selectCommentUserById(Long id)
    {
        return commentUserMapper.selectCommentUserById(id);
    }

    /**
     * 查询评论用户列表
     *
     * @param commentUser 评论用户
     * @return 评论用户
     */
    @Override
    public List<CommentUser> selectCommentUserList(CommentUser commentUser)
    {
        return commentUserMapper.selectCommentUserList(commentUser);
    }

    /**
     * 新增评论用户
     *
     * @param commentUser 评论用户
     * @return 结果
     */
    @Override
    public int insertCommentUser(CommentUser commentUser)
    {
        return commentUserMapper.insertCommentUser(commentUser);
    }

    /**
     * 修改评论用户
     *
     * @param commentUser 评论用户
     * @return 结果
     */
    @Override
    public int updateCommentUser(CommentUser commentUser)
    {
        return commentUserMapper.updateCommentUser(commentUser);
    }

    /**
     * 批量删除评论用户
     *
     * @param ids 需要删除的评论用户主键
     * @return 结果
     */
    @Override
    public int deleteCommentUserByIds(Long[] ids)
    {
        return commentUserMapper.deleteCommentUserByIds(ids);
    }

    /**
     * 删除评论用户信息
     *
     * @param id 评论用户主键
     * @return 结果
     */
    @Override
    public int deleteCommentUserById(Long id)
    {
        return commentUserMapper.deleteCommentUserById(id);
    }

    /**
     * 根据userId查询评论用户
     *
     * @param userId 关联的fa_user ID
     * @return 评论用户信息
     */
    @Override
    public CommentUser selectCommentUserByUserId(Long userId)
    {
        return commentUserMapper.selectCommentUserByUserId(userId);
    }

    /**
     * 根据平台账号查询评论用户
     *
     * @param platform 平台
     * @param account 账号
     * @return 评论用户信息
     */
    @Override
    public CommentUser selectCommentUserByPlatformAndAccount(String platform, String account)
    {
        return commentUserMapper.selectCommentUserByPlatformAndAccount(platform, account);
    }
}