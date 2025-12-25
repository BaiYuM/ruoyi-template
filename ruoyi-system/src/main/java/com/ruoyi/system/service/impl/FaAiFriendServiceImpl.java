package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.FaAiFriend;
import com.ruoyi.system.mapper.FaAiFriendMapper;
import com.ruoyi.system.service.IFaAiFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 抖音授权账号好友Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
@Service
public class FaAiFriendServiceImpl implements IFaAiFriendService
{
    @Autowired
    private FaAiFriendMapper faAiFriendMapper;

    /**
     * 查询抖音授权账号好友
     * 
     * @param id 抖音授权账号好友主键
     * @return 抖音授权账号好友
     */
    @Override
    public FaAiFriend selectFaAiFriendById(Long id)
    {
        return faAiFriendMapper.selectFaAiFriendById(id);
    }

    /**
     * 查询抖音授权账号好友列表
     * 
     * @param faAiFriend 抖音授权账号好友
     * @return 抖音授权账号好友
     */
    @Override
    public List<FaAiFriend> selectFaAiFriendList(FaAiFriend faAiFriend)
    {
        return faAiFriendMapper.selectFaAiFriendList(faAiFriend);
    }

    /**
     * 新增抖音授权账号好友
     * 
     * @param faAiFriend 抖音授权账号好友
     * @return 结果
     */
    @Override
    public int insertFaAiFriend(FaAiFriend faAiFriend)
    {
        faAiFriend.setCreateTime(DateUtils.getNowDate());
        return faAiFriendMapper.insertFaAiFriend(faAiFriend);
    }

    /**
     * 修改抖音授权账号好友
     * 
     * @param faAiFriend 抖音授权账号好友
     * @return 结果
     */
    @Override
    public int updateFaAiFriend(FaAiFriend faAiFriend)
    {
        faAiFriend.setUpdateTime(DateUtils.getNowDate());
        return faAiFriendMapper.updateFaAiFriend(faAiFriend);
    }

    /**
     * 批量删除抖音授权账号好友
     * 
     * @param ids 需要删除的抖音授权账号好友主键
     * @return 结果
     */
    @Override
    public int deleteFaAiFriendByIds(Long[] ids)
    {
        return faAiFriendMapper.deleteFaAiFriendByIds(ids);
    }

    /**
     * 删除抖音授权账号好友信息
     * 
     * @param id 抖音授权账号好友主键
     * @return 结果
     */
    @Override
    public int deleteFaAiFriendById(Long id)
    {
        return faAiFriendMapper.deleteFaAiFriendById(id);
    }
}
