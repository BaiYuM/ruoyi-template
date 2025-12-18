package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.FaUser;

/**
 * 抖音用户 服务层
 */
public interface IFaUserService
{
    /**
     * 查询抖音用户
     *
     * @param id 抖音用户主键
     * @return 抖音用户
     */
    public FaUser selectFaUserById(Long id);

    /**
     * 查询抖音用户列表
     *
     * @param faUser 抖音用户
     * @return 抖音用户集合
     */
    public List<FaUser> selectFaUserList(FaUser faUser);

    /**
     * 新增抖音用户
     *
     * @param faUser 抖音用户
     * @return 结果
     */
    public int insertFaUser(FaUser faUser);

    /**
     * 修改抖音用户
     *
     * @param faUser 抖音用户
     * @return 结果
     */
    public int updateFaUser(FaUser faUser);

    /**
     * 批量删除抖音用户
     *
     * @param ids 需要删除的抖音用户主键集合
     * @return 结果
     */
    public int deleteFaUserByIds(Long[] ids);

    /**
     * 删除抖音用户信息
     *
     * @param id 抖音用户主键
     * @return 结果
     */
    public int deleteFaUserById(Long id);

    /**
     * 根据openId查询用户
     *
     * @param openId 开放平台ID
     * @return 用户信息
     */
    public FaUser selectFaUserByOpenId(String openId);

    /**
     * 根据unionId查询用户
     *
     * @param unionId 联合ID
     * @return 用户信息
     */
    public FaUser selectFaUserByUnionId(String unionId);

    /**
     * 根据allFasMuin查询用户
     *
     * @param allFasMuin 全平台唯一标识
     * @return 用户信息
     */
    public FaUser selectFaUserByAllFasMuin(String allFasMuin);
}