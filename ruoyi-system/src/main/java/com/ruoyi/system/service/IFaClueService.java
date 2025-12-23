package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.FaClue;

/**
 * 关联线索Service接口
 * 
 * @author ruoyi
 * @date 2025-01-06
 */
public interface IFaClueService 
{
    /**
     * 查询关联线索
     * 
     * @param clueId 关联线索ID
     * @return 关联线索
     */
    public FaClue selectFaClueById(Long clueId);

    /**
     * 查询关联线索列表
     * 
     * @param faClue 关联线索
     * @return 关联线索集合
     */
    public List<FaClue> selectFaClueList(FaClue faClue);

    /**
     * 新增关联线索
     * 
     * @param faClue 关联线索
     * @return 结果
     */
    public int insertFaClue(FaClue faClue);

    /**
     * 修改关联线索
     * 
     * @param faClue 关联线索
     * @return 结果
     */
    public int updateFaClue(FaClue faClue);

    /**
     * 批量删除关联线索
     * 
     * @param clueIds 需要删除的关联线索ID
     * @return 结果
     */
    public int deleteFaClueByIds(Long[] clueIds);

    /**
     * 删除关联线索信息
     * 
     * @param clueId 关联线索ID
     * @return 结果
     */
    public int deleteFaClueById(Long clueId);
}
