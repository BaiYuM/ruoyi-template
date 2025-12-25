package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.FaAiFriendClue;

import java.util.List;
/**
 * 好友线索明细Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-25
 */
public interface FaAiFriendClueMapper 
{
    /**
     * 查询好友线索明细
     * 
     * @param id 好友线索明细主键
     * @return 好友线索明细
     */
    public FaAiFriendClue selectFaAiFriendClueById(Long id);

    /**
     * 查询好友线索明细列表
     * 
     * @param faAiFriendClue 好友线索明细
     * @return 好友线索明细集合
     */
    public List<FaAiFriendClue> selectFaAiFriendClueList(FaAiFriendClue faAiFriendClue);

    /**
     * 新增好友线索明细
     * 
     * @param faAiFriendClue 好友线索明细
     * @return 结果
     */
    public int insertFaAiFriendClue(FaAiFriendClue faAiFriendClue);

    /**
     * 修改好友线索明细
     * 
     * @param faAiFriendClue 好友线索明细
     * @return 结果
     */
    public int updateFaAiFriendClue(FaAiFriendClue faAiFriendClue);

    /**
     * 删除好友线索明细
     * 
     * @param id 好友线索明细主键
     * @return 结果
     */
    public int deleteFaAiFriendClueById(Long id);

    /**
     * 批量删除好友线索明细
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFaAiFriendClueByIds(Long[] ids);
}
