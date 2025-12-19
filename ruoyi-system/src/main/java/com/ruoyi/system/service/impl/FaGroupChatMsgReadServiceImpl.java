package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FaGroupChatMsgReadMapper;
import com.ruoyi.system.domain.FaGroupChatMsgRead;
import com.ruoyi.system.service.IFaGroupChatMsgReadService;

/**
 * 群消息已读状态Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@Service
public class FaGroupChatMsgReadServiceImpl implements IFaGroupChatMsgReadService 
{
    @Autowired
    private FaGroupChatMsgReadMapper faGroupChatMsgReadMapper;

    /**
     * 查询群消息已读状态
     * 
     * @param id 群消息已读状态主键
     * @return 群消息已读状态
     */
    @Override
    public FaGroupChatMsgRead selectFaGroupChatMsgReadById(Long id)
    {
        return faGroupChatMsgReadMapper.selectFaGroupChatMsgReadById(id);
    }

    /**
     * 查询群消息已读状态列表
     * 
     * @param faGroupChatMsgRead 群消息已读状态
     * @return 群消息已读状态
     */
    @Override
    public List<FaGroupChatMsgRead> selectFaGroupChatMsgReadList(FaGroupChatMsgRead faGroupChatMsgRead)
    {
        return faGroupChatMsgReadMapper.selectFaGroupChatMsgReadList(faGroupChatMsgRead);
    }

    /**
     * 新增群消息已读状态
     * 
     * @param faGroupChatMsgRead 群消息已读状态
     * @return 结果
     */
    @Override
    public int insertFaGroupChatMsgRead(FaGroupChatMsgRead faGroupChatMsgRead)
    {
        return faGroupChatMsgReadMapper.insertFaGroupChatMsgRead(faGroupChatMsgRead);
    }

    /**
     * 修改群消息已读状态
     * 
     * @param faGroupChatMsgRead 群消息已读状态
     * @return 结果
     */
    @Override
    public int updateFaGroupChatMsgRead(FaGroupChatMsgRead faGroupChatMsgRead)
    {
        return faGroupChatMsgReadMapper.updateFaGroupChatMsgRead(faGroupChatMsgRead);
    }

    /**
     * 批量删除群消息已读状态
     * 
     * @param ids 需要删除的群消息已读状态主键
     * @return 结果
     */
    @Override
    public int deleteFaGroupChatMsgReadByIds(Long[] ids)
    {
        return faGroupChatMsgReadMapper.deleteFaGroupChatMsgReadByIds(ids);
    }

    /**
     * 删除群消息已读状态信息
     * 
     * @param id 群消息已读状态主键
     * @return 结果
     */
    @Override
    public int deleteFaGroupChatMsgReadById(Long id)
    {
        return faGroupChatMsgReadMapper.deleteFaGroupChatMsgReadById(id);
    }
}
