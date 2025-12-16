package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.FriendAddRecord;

/**
 * 好友添加记录 Service 接口
 */
public interface IFriendAddRecordService {
    /**
     * 查询好友添加记录列表
     * @param filter 过滤条件
     * @return 记录列表
     */
    public List<FriendAddRecord> selectRecordList(FriendAddRecord filter);

    /**
     * 根据 ID 查询好友添加记录
     * @param id 主键
     * @return FriendAddRecord 实体
     */
    public FriendAddRecord selectRecordById(Long id);

    /**
     * 插入好友添加记录
     * @param obj 实体
     * @return 受影响行数
     */
    public int insertRecord(FriendAddRecord obj);

    /**
     * 更新好友添加记录
     * @param obj 实体
     * @return 受影响行数
     */
    public int updateRecord(FriendAddRecord obj);

    /**
     * 批量删除好友添加记录
     * @param ids 主键数组
     * @return 受影响行数
     */
    public int deleteRecordByIds(Long[] ids);
}
