package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AiExpertConfig;

/**
 * AI 专家配置 Service 接口
 * 定义对 AiExpertConfig 的 CRUD 操作
 */
public interface IAiExpertConfigService {
    /**
     * 查询 AI 专家配置列表（可按条件筛选）
     * @param filter 过滤条件
     * @return 符合条件的 AiExpertConfig 列表
     */
    public List<AiExpertConfig> selectAiList(AiExpertConfig filter);

    /**
     * 根据主键查询单条记录
     * @param id 主键 ID
     * @return AiExpertConfig 实体或 null
     */
    public AiExpertConfig selectAiById(Long id);

    /**
     * 插入一条新的 AI 专家配置
     * @param obj 要插入的实体
     * @return 受影响的行数（1 表示成功）
     */
    public int insertAi(AiExpertConfig obj);

    /**
     * 更新已有的 AI 专家配置
     * @param obj 要更新的实体（含 id）
     * @return 受影响的行数
     */
    public int updateAi(AiExpertConfig obj);

    /**
     * 批量删除 AI 专家配置
     * @param ids 要删除的主键数组
     * @return 受影响的行数
     */
    public int deleteAiByIds(Long[] ids);
}
