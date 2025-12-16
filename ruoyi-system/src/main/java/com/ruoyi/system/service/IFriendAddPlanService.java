package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.FriendAddPlan;

/**
 * 好友添加计划 Service 接口
 */
public interface IFriendAddPlanService {
    /**
     * 查询添加计划列表
     * @param filter 过滤条件
     * @return 计划列表
     */
    public List<FriendAddPlan> selectPlanList(FriendAddPlan filter);

    /**
     * 根据 ID 查询计划
     * @param id 主键
     * @return FriendAddPlan 实体
     */
    public FriendAddPlan selectPlanById(Long id);

    /**
     * 新增计划
     * @param obj 实体
     * @return 受影响行数
     */
    public int insertPlan(FriendAddPlan obj);

    /**
     * 更新计划
     * @param obj 实体
     * @return 受影响行数
     */
    public int updatePlan(FriendAddPlan obj);

    /**
     * 批量删除计划
     * @param ids 主键数组
     * @return 受影响行数
     */
    public int deletePlanByIds(Long[] ids);
}
