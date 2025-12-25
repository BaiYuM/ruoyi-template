package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.FaAiFriendClue;
import com.ruoyi.system.mapper.FaAiFriendClueMapper;
import com.ruoyi.system.service.IFaAiFriendClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 好友线索明细Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
@Service
public class FaAiFriendClueServiceImpl implements IFaAiFriendClueService
{
    @Autowired
    private FaAiFriendClueMapper faAiFriendClueMapper;

    /**
     * 查询好友线索明细
     * 
     * @param id 好友线索明细主键
     * @return 好友线索明细
     */
    @Override
    public FaAiFriendClue selectFaAiFriendClueById(Long id)
    {
        return faAiFriendClueMapper.selectFaAiFriendClueById(id);
    }

    /**
     * 查询好友线索明细列表
     * 
     * @param faAiFriendClue 好友线索明细
     * @return 好友线索明细
     */
    @Override
    public List<FaAiFriendClue> selectFaAiFriendClueList(FaAiFriendClue faAiFriendClue)
    {
        return faAiFriendClueMapper.selectFaAiFriendClueList(faAiFriendClue);
    }

    /**
     * 新增好友线索明细
     * 
     * @param faAiFriendClue 好友线索明细
     * @return 结果
     */
    @Override
    public int insertFaAiFriendClue(FaAiFriendClue faAiFriendClue)
    {
        faAiFriendClue.setCreateTime(DateUtils.getNowDate());
        return faAiFriendClueMapper.insertFaAiFriendClue(faAiFriendClue);
    }

    /**
     * 修改好友线索明细
     * 
     * @param faAiFriendClue 好友线索明细
     * @return 结果
     */
    @Override
    public int updateFaAiFriendClue(FaAiFriendClue faAiFriendClue)
    {
        faAiFriendClue.setUpdateTime(DateUtils.getNowDate());
        return faAiFriendClueMapper.updateFaAiFriendClue(faAiFriendClue);
    }

    /**
     * 批量删除好友线索明细
     * 
     * @param ids 需要删除的好友线索明细主键
     * @return 结果
     */
    @Override
    public int deleteFaAiFriendClueByIds(Long[] ids)
    {
        return faAiFriendClueMapper.deleteFaAiFriendClueByIds(ids);
    }

    /**
     * 删除好友线索明细信息
     * 
     * @param id 好友线索明细主键
     * @return 结果
     */
    @Override
    public int deleteFaAiFriendClueById(Long id)
    {
        return faAiFriendClueMapper.deleteFaAiFriendClueById(id);
    }
}
