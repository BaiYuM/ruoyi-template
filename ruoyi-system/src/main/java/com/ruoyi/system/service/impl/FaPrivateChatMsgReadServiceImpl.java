package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FaPrivateChatMsgReadMapper;
import com.ruoyi.system.domain.FaPrivateChatMsgRead;
import com.ruoyi.system.service.IFaPrivateChatMsgReadService;

/**
 * 私聊消息已读Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@Service
public class FaPrivateChatMsgReadServiceImpl implements IFaPrivateChatMsgReadService 
{
    @Autowired
    private FaPrivateChatMsgReadMapper faPrivateChatMsgReadMapper;

    /**
     * 查询私聊消息已读
     * 
     * @param id 私聊消息已读主键
     * @return 私聊消息已读
     */
    @Override
    public FaPrivateChatMsgRead selectFaPrivateChatMsgReadById(Long id)
    {
        return faPrivateChatMsgReadMapper.selectFaPrivateChatMsgReadById(id);
    }

    /**
     * 查询私聊消息已读列表
     * 
     * @param faPrivateChatMsgRead 私聊消息已读
     * @return 私聊消息已读
     */
    @Override
    public List<FaPrivateChatMsgRead> selectFaPrivateChatMsgReadList(FaPrivateChatMsgRead faPrivateChatMsgRead)
    {
        return faPrivateChatMsgReadMapper.selectFaPrivateChatMsgReadList(faPrivateChatMsgRead);
    }

    /**
     * 新增私聊消息已读
     * 
     * @param faPrivateChatMsgRead 私聊消息已读
     * @return 结果
     */
    @Override
    public int insertFaPrivateChatMsgRead(FaPrivateChatMsgRead faPrivateChatMsgRead)
    {
        return faPrivateChatMsgReadMapper.insertFaPrivateChatMsgRead(faPrivateChatMsgRead);
    }

    /**
     * 修改私聊消息已读
     * 
     * @param faPrivateChatMsgRead 私聊消息已读
     * @return 结果
     */
    @Override
    public int updateFaPrivateChatMsgRead(FaPrivateChatMsgRead faPrivateChatMsgRead)
    {
        return faPrivateChatMsgReadMapper.updateFaPrivateChatMsgRead(faPrivateChatMsgRead);
    }

    /**
     * 批量删除私聊消息已读
     * 
     * @param ids 需要删除的私聊消息已读主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatMsgReadByIds(Long[] ids)
    {
        return faPrivateChatMsgReadMapper.deleteFaPrivateChatMsgReadByIds(ids);
    }

    /**
     * 删除私聊消息已读信息
     * 
     * @param id 私聊消息已读主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatMsgReadById(Long id)
    {
        return faPrivateChatMsgReadMapper.deleteFaPrivateChatMsgReadById(id);
    }
}
