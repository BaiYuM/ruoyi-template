package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AutoPublishConfig;

/**
 * 自动发布配置 Service 接口
 */
public interface IAutoPublishConfigService {
    /**
     * 查询自动发布配置列表
     * @param filter 过滤条件
     * @return 列表
     */
    public List<AutoPublishConfig> selectAutoPublishList(AutoPublishConfig filter);

    /**
     * 根据 ID 查询自动发布配置
     * @param id 主键
     * @return AutoPublishConfig 实体
     */
    public AutoPublishConfig selectAutoPublishById(Long id);

    /**
     * 新增自动发布配置
     * @param obj 实体
     * @return 受影响行数
     */
    public int insertAutoPublish(AutoPublishConfig obj);

    /**
     * 更新自动发布配置
     * @param obj 实体
     * @return 受影响行数
     */
    public int updateAutoPublish(AutoPublishConfig obj);

    /**
     * 批量删除自动发布配置
     * @param ids 主键数组
     * @return 受影响行数
     */
    public int deleteAutoPublishByIds(Long[] ids);
}
