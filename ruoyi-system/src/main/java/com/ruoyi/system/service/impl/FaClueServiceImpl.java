package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FaClueMapper;
import com.ruoyi.system.domain.FaClue;
import com.ruoyi.system.service.IFaClueService;

/**
 * 关联线索Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-06
 */
@Service
public class FaClueServiceImpl implements IFaClueService 
{
    @Autowired
    private FaClueMapper faClueMapper;

    /**
     * 查询关联线索
     * 
     * @param clueId 关联线索ID
     * @return 关联线索
     */
    @Override
    public FaClue selectFaClueById(Long clueId)
    {
        return faClueMapper.selectFaClueById(clueId);
    }

    /**
     * 查询关联线索列表
     * 
     * @param faClue 关联线索
     * @return 关联线索
     */
    @Override
    public List<FaClue> selectFaClueList(FaClue faClue)
    {
        return faClueMapper.selectFaClueList(faClue);
    }

    /**
     * 新增关联线索
     * 
     * @param faClue 关联线索
     * @return 结果
     */
    @Override
    public int insertFaClue(FaClue faClue)
    {
        faClue.setCreateTime(DateUtils.getNowDate());
        return faClueMapper.insertFaClue(faClue);
    }

    /**
     * 修改关联线索
     * 
     * @param faClue 关联线索
     * @return 结果
     */
    @Override
    public int updateFaClue(FaClue faClue)
    {
        faClue.setUpdateTime(DateUtils.getNowDate());
        return faClueMapper.updateFaClue(faClue);
    }

    /**
     * 批量删除关联线索
     * 
     * @param clueIds 需要删除的关联线索ID
     * @return 结果
     */
    @Override
    public int deleteFaClueByIds(Long[] clueIds)
    {
        return faClueMapper.deleteFaClueByIds(clueIds);
    }

    /**
     * 删除关联线索信息
     * 
     * @param clueId 关联线索ID
     * @return 结果
     */
    @Override
    public int deleteFaClueById(Long clueId)
    {
        return faClueMapper.deleteFaClueById(clueId);
    }
}
