package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.FaUser;
import com.ruoyi.system.domain.CommentUser;
import com.ruoyi.system.domain.vo.DouyinUserVO;
import com.ruoyi.system.mapper.FaUserMapper;
import com.ruoyi.system.mapper.CommentUserMapper;
import com.ruoyi.system.service.IDouyinUserService;

/**
 * 抖音用户统一管理 服务层实现
 */
@Service
public class DouyinUserServiceImpl implements IDouyinUserService
{
    @Autowired
    private FaUserMapper faUserMapper;

    @Autowired
    private CommentUserMapper commentUserMapper;

    /**
     * 根据ID查询统一用户视图
     *
     * @param id 用户ID
     * @return 统一用户视图
     */
    @Override
    public DouyinUserVO selectDouyinUserById(Long id)
    {
        FaUser faUser = faUserMapper.selectFaUserById(id);
        if (faUser == null) {
            return null;
        }

        CommentUser commentUser = commentUserMapper.selectCommentUserByUserId(id);

        DouyinUserVO vo = new DouyinUserVO();
        vo.setFaUser(faUser);
        vo.setCommentUser(commentUser);
        vo.setUnifiedId(faUser.getAllFasMuin());

        return vo;
    }

    /**
     * 查询统一用户列表
     *
     * @param douyinUser 查询条件
     * @return 用户列表
     */
    @Override
    public List<DouyinUserVO> selectDouyinUserList(DouyinUserVO douyinUser)
    {
        // 这里需要一个联合查询，暂时使用fa_user表查询，然后补充comment_user数据
        FaUser faUserQuery = new FaUser();
        if (douyinUser != null) {
            faUserQuery.setPlatform(douyinUser.getPlatform());
            faUserQuery.setNickname(douyinUser.getDisplayNickname());
        }

        List<FaUser> faUserList = faUserMapper.selectFaUserList(faUserQuery);
        List<DouyinUserVO> result = new ArrayList<>();

        for (FaUser faUser : faUserList) {
            CommentUser commentUser = commentUserMapper.selectCommentUserByUserId(faUser.getId());

            DouyinUserVO vo = new DouyinUserVO();
            vo.setFaUser(faUser);
            vo.setCommentUser(commentUser);
            vo.setUnifiedId(faUser.getAllFasMuin());

            result.add(vo);
        }

        return result;
    }

    /**
     * 新增抖音用户（同时创建fa_user和comment_user记录）
     *
     * @param douyinUserVO 统一用户视图
     * @return 结果
     */
    @Override
    @Transactional
    public int insertDouyinUser(DouyinUserVO douyinUserVO)
    {
        // 插入fa_user
        FaUser faUser = douyinUserVO.getFaUser();
        if (faUser == null) {
            return 0;
        }

        int result = faUserMapper.insertFaUser(faUser);
        if (result > 0) {
            // 插入comment_user
            CommentUser commentUser = douyinUserVO.getCommentUser();
            if (commentUser != null) {
                commentUser.setUserId(faUser.getId());
                commentUserMapper.insertCommentUser(commentUser);
            }
        }

        return result;
    }

    /**
     * 更新抖音用户
     *
     * @param douyinUserVO 统一用户视图
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDouyinUser(DouyinUserVO douyinUserVO)
    {
        int result = 0;

        // 更新fa_user
        FaUser faUser = douyinUserVO.getFaUser();
        if (faUser != null) {
            result = faUserMapper.updateFaUser(faUser);
        }

        // 更新comment_user
        CommentUser commentUser = douyinUserVO.getCommentUser();
        if (commentUser != null) {
            result += commentUserMapper.updateCommentUser(commentUser);
        }

        return result;
    }

    /**
     * 删除抖音用户
     *
     * @param id 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteDouyinUserById(Long id)
    {
        // 删除comment_user
        commentUserMapper.deleteCommentUserById(id);

        // 删除fa_user
        return faUserMapper.deleteFaUserById(id);
    }

    /**
     * 批量删除抖音用户
     *
     * @param ids 用户ID数组
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteDouyinUserByIds(Long[] ids)
    {
        int result = 0;
        for (Long id : ids) {
            result += deleteDouyinUserById(id);
        }
        return result;
    }

    /**
     * 根据openId查询用户
     *
     * @param openId 开放平台ID
     * @return 统一用户视图
     */
    @Override
    public DouyinUserVO selectDouyinUserByOpenId(String openId)
    {
        FaUser faUser = faUserMapper.selectFaUserByOpenId(openId);
        if (faUser == null) {
            return null;
        }

        CommentUser commentUser = commentUserMapper.selectCommentUserByUserId(faUser.getId());

        DouyinUserVO vo = new DouyinUserVO();
        vo.setFaUser(faUser);
        vo.setCommentUser(commentUser);
        vo.setUnifiedId(faUser.getAllFasMuin());

        return vo;
    }

    /**
     * 根据unionId查询用户
     *
     * @param unionId 联合ID
     * @return 统一用户视图
     */
    @Override
    public DouyinUserVO selectDouyinUserByUnionId(String unionId)
    {
        FaUser faUser = faUserMapper.selectFaUserByUnionId(unionId);
        if (faUser == null) {
            return null;
        }

        CommentUser commentUser = commentUserMapper.selectCommentUserByUserId(faUser.getId());

        DouyinUserVO vo = new DouyinUserVO();
        vo.setFaUser(faUser);
        vo.setCommentUser(commentUser);
        vo.setUnifiedId(faUser.getAllFasMuin());

        return vo;
    }

    /**
     * 根据allFasMuin查询用户
     *
     * @param allFasMuin 全平台唯一标识
     * @return 统一用户视图
     */
    @Override
    public DouyinUserVO selectDouyinUserByAllFasMuin(String allFasMuin)
    {
        FaUser faUser = faUserMapper.selectFaUserByAllFasMuin(allFasMuin);
        if (faUser == null) {
            return null;
        }

        CommentUser commentUser = commentUserMapper.selectCommentUserByUserId(faUser.getId());

        DouyinUserVO vo = new DouyinUserVO();
        vo.setFaUser(faUser);
        vo.setCommentUser(commentUser);
        vo.setUnifiedId(faUser.getAllFasMuin());

        return vo;
    }

    /**
     * 根据平台账号查询用户
     *
     * @param platform 平台
     * @param account 账号
     * @return 统一用户视图
     */
    @Override
    public DouyinUserVO selectDouyinUserByPlatformAndAccount(String platform, String account)
    {
        CommentUser commentUser = commentUserMapper.selectCommentUserByPlatformAndAccount(platform, account);
        if (commentUser == null) {
            return null;
        }

        FaUser faUser = faUserMapper.selectFaUserById(commentUser.getUserId());
        if (faUser == null) {
            return null;
        }

        DouyinUserVO vo = new DouyinUserVO();
        vo.setFaUser(faUser);
        vo.setCommentUser(commentUser);
        vo.setUnifiedId(faUser.getAllFasMuin());

        return vo;
    }

    /**
     * 同步用户数据（从第三方平台更新本地数据）
     *
     * @param openId 开放平台ID
     * @param platformData 平台数据
     * @return 结果
     */
    @Override
    @Transactional
    public int syncUserData(String openId, Object platformData)
    {
        // 这里可以实现从第三方平台同步用户数据的逻辑
        // 暂时返回0，表示未实现
        return 0;
    }
}