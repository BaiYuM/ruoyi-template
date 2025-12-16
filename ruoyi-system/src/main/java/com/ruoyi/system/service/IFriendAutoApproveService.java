package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.FriendAutoApprove;

/**
 * 好友自动通过配置 Service 接口
 */
public interface IFriendAutoApproveService {
    /**
     * 查询自动通过配置列表
     * @param filter 过滤条件
     * @return 列表
     */
    public List<FriendAutoApprove> selectAutoApproveList(FriendAutoApprove filter);

    /**
     * 根据 ID 查询自动通过配置
     * @param id 主键
     * @return FriendAutoApprove 实体
     */
    public FriendAutoApprove selectAutoApproveById(Long id);

    /**
     * 插入自动通过配置
     * @param obj 实体
     * @return 受影响行数
     */
    public int insertAutoApprove(FriendAutoApprove obj);

    /**
     * 更新自动通过配置
     * @param obj 实体
     * @return 受影响行数
     */
    public int updateAutoApprove(FriendAutoApprove obj);

    /**
     * 批量删除自动通过配置
     * @param ids 主键数组
     * @return 受影响行数
     */
    public int deleteAutoApproveByIds(Long[] ids);
}
