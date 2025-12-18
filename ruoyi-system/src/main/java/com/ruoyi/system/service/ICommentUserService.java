package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.CommentUser;

/**
 * 评论用户 服务层
 */
public interface ICommentUserService
{
    /**
     * 查询评论用户
     *
     * @param id 评论用户主键
     * @return 评论用户
     */
    public CommentUser selectCommentUserById(Long id);

    /**
     * 查询评论用户列表
     *
     * @param commentUser 评论用户
     * @return 评论用户集合
     */
    public List<CommentUser> selectCommentUserList(CommentUser commentUser);

    /**
     * 新增评论用户
     *
     * @param commentUser 评论用户
     * @return 结果
     */
    public int insertCommentUser(CommentUser commentUser);

    /**
     * 修改评论用户
     *
     * @param commentUser 评论用户
     * @return 结果
     */
    public int updateCommentUser(CommentUser commentUser);

    /**
     * 批量删除评论用户
     *
     * @param ids 需要删除的评论用户主键集合
     * @return 结果
     */
    public int deleteCommentUserByIds(Long[] ids);

    /**
     * 删除评论用户信息
     *
     * @param id 评论用户主键
     * @return 结果
     */
    public int deleteCommentUserById(Long id);

    /**
     * 根据userId查询评论用户
     *
     * @param userId 关联的fa_user ID
     * @return 评论用户信息
     */
    public CommentUser selectCommentUserByUserId(Long userId);

    /**
     * 根据平台账号查询评论用户
     *
     * @param platform 平台
     * @param account 账号
     * @return 评论用户信息
     */
    public CommentUser selectCommentUserByPlatformAndAccount(String platform, String account);
}