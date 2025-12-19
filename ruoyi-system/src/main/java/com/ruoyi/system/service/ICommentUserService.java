package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.CommentUser;

/**
 * 平台账户Service接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface ICommentUserService 
{
    /**
     * 查询平台账户
     * 
     * @param id 平台账户主键
     * @return 平台账户
     */
    public CommentUser selectCommentUserById(Long id);

    /**
     * 查询平台账户列表
     * 
     * @param commentUser 平台账户
     * @return 平台账户集合
     */
    public List<CommentUser> selectCommentUserList(CommentUser commentUser);

    /**
     * 新增平台账户
     * 
     * @param commentUser 平台账户
     * @return 结果
     */
    public int insertCommentUser(CommentUser commentUser);

    /**
     * 修改平台账户
     * 
     * @param commentUser 平台账户
     * @return 结果
     */
    public int updateCommentUser(CommentUser commentUser);

    /**
     * 批量删除平台账户
     * 
     * @param ids 需要删除的平台账户主键集合
     * @return 结果
     */
    public int deleteCommentUserByIds(Long[] ids);

    /**
     * 删除平台账户信息
     * 
     * @param id 平台账户主键
     * @return 结果
     */
    public int deleteCommentUserById(Long id);
}
