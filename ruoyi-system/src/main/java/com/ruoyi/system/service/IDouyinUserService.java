package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.FaUser;
import com.ruoyi.system.domain.CommentUser;
import com.ruoyi.system.domain.vo.DouyinUserVO;

/**
 * 抖音用户统一管理 服务层
 */
public interface IDouyinUserService
{
    /**
     * 根据ID查询统一用户视图
     *
     * @param id 用户ID
     * @return 统一用户视图
     */
    public DouyinUserVO selectDouyinUserById(Long id);

    /**
     * 查询统一用户列表
     *
     * @param douyinUser 查询条件
     * @return 用户列表
     */
    public List<DouyinUserVO> selectDouyinUserList(DouyinUserVO douyinUser);

    /**
     * 新增抖音用户（同时创建fa_user和comment_user记录）
     *
     * @param douyinUserVO 统一用户视图
     * @return 结果
     */
    public int insertDouyinUser(DouyinUserVO douyinUserVO);

    /**
     * 更新抖音用户
     *
     * @param douyinUserVO 统一用户视图
     * @return 结果
     */
    public int updateDouyinUser(DouyinUserVO douyinUserVO);

    /**
     * 删除抖音用户
     *
     * @param id 用户ID
     * @return 结果
     */
    public int deleteDouyinUserById(Long id);

    /**
     * 批量删除抖音用户
     *
     * @param ids 用户ID数组
     * @return 结果
     */
    public int deleteDouyinUserByIds(Long[] ids);

    /**
     * 根据openId查询用户
     *
     * @param openId 开放平台ID
     * @return 统一用户视图
     */
    public DouyinUserVO selectDouyinUserByOpenId(String openId);

    /**
     * 根据unionId查询用户
     *
     * @param unionId 联合ID
     * @return 统一用户视图
     */
    public DouyinUserVO selectDouyinUserByUnionId(String unionId);

    /**
     * 根据allFasMuin查询用户
     *
     * @param allFasMuin 全平台唯一标识
     * @return 统一用户视图
     */
    public DouyinUserVO selectDouyinUserByAllFasMuin(String allFasMuin);

    /**
     * 根据平台账号查询用户
     *
     * @param platform 平台
     * @param account 账号
     * @return 统一用户视图
     */
    public DouyinUserVO selectDouyinUserByPlatformAndAccount(String platform, String account);

    /**
     * 同步用户数据（从第三方平台更新本地数据）
     *
     * @param openId 开放平台ID
     * @param platformData 平台数据
     * @return 结果
     */
    public int syncUserData(String openId, Object platformData);
}