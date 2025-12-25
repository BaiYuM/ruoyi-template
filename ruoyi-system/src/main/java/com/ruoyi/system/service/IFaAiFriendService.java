package com.ruoyi.system.service;

import com.ruoyi.system.domain.FaAiFriend;

import java.util.List;

/**
 * 抖音授权账号好友Service接口
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
public interface IFaAiFriendService 
{
    /**
     * 查询抖音授权账号好友
     * 
     * @param id 抖音授权账号好友主键
     * @return 抖音授权账号好友
     */
    public FaAiFriend selectFaAiFriendById(Long id);

    /**
     * 查询抖音授权账号好友列表
     * 
     * @param faAiFriend 抖音授权账号好友
     * @return 抖音授权账号好友集合
     */
    public List<FaAiFriend> selectFaAiFriendList(FaAiFriend faAiFriend);

    /**
     * 新增抖音授权账号好友
     * 
     * @param faAiFriend 抖音授权账号好友
     * @return 结果
     */
    public int insertFaAiFriend(FaAiFriend faAiFriend);

    /**
     * 修改抖音授权账号好友
     * 
     * @param faAiFriend 抖音授权账号好友
     * @return 结果
     */
    public int updateFaAiFriend(FaAiFriend faAiFriend);

    /**
     * 批量删除抖音授权账号好友
     * 
     * @param ids 需要删除的抖音授权账号好友主键集合
     * @return 结果
     */
    public int deleteFaAiFriendByIds(Long[] ids);

    /**
     * 删除抖音授权账号好友信息
     * 
     * @param id 抖音授权账号好友主键
     * @return 结果
     */
    public int deleteFaAiFriendById(Long id);
}
