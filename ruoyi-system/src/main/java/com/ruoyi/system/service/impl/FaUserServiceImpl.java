package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FaUserMapper;
import com.ruoyi.system.domain.FaUser;
import com.ruoyi.system.service.IFaUserService;

/**
 * 抖音用户 服务层实现
 */
@Service
public class FaUserServiceImpl implements IFaUserService
{
    @Autowired
    private FaUserMapper faUserMapper;

    /**
     * 查询抖音用户
     *
     * @param id 抖音用户主键
     * @return 抖音用户
     */
    @Override
    public FaUser selectFaUserById(Long id)
    {
        return faUserMapper.selectFaUserById(id);
    }

    /**
     * 查询抖音用户列表
     *
     * @param faUser 抖音用户
     * @return 抖音用户
     */
    @Override
    public List<FaUser> selectFaUserList(FaUser faUser)
    {
        return faUserMapper.selectFaUserList(faUser);
    }

    /**
     * 新增抖音用户
     *
     * @param faUser 抖音用户
     * @return 结果
     */
    @Override
    public int insertFaUser(FaUser faUser)
    {
        return faUserMapper.insertFaUser(faUser);
    }

    /**
     * 修改抖音用户
     *
     * @param faUser 抖音用户
     * @return 结果
     */
    @Override
    public int updateFaUser(FaUser faUser)
    {
        return faUserMapper.updateFaUser(faUser);
    }

    /**
     * 批量删除抖音用户
     *
     * @param ids 需要删除的抖音用户主键
     * @return 结果
     */
    @Override
    public int deleteFaUserByIds(Long[] ids)
    {
        return faUserMapper.deleteFaUserByIds(ids);
    }

    /**
     * 删除抖音用户信息
     *
     * @param id 抖音用户主键
     * @return 结果
     */
    @Override
    public int deleteFaUserById(Long id)
    {
        return faUserMapper.deleteFaUserById(id);
    }

    /**
     * 根据openId查询用户
     *
     * @param openId 开放平台ID
     * @return 用户信息
     */
    @Override
    public FaUser selectFaUserByOpenId(String openId)
    {
        return faUserMapper.selectFaUserByOpenId(openId);
    }

    /**
     * 根据unionId查询用户
     *
     * @param unionId 联合ID
     * @return 用户信息
     */
    @Override
    public FaUser selectFaUserByUnionId(String unionId)
    {
        return faUserMapper.selectFaUserByUnionId(unionId);
    }

    /**
     * 根据allFasMuin查询用户
     *
     * @param allFasMuin 全平台唯一标识
     * @return 用户信息
     */
    @Override
    public FaUser selectFaUserByAllFasMuin(String allFasMuin)
    {
        return faUserMapper.selectFaUserByAllFasMuin(allFasMuin);
    }
}