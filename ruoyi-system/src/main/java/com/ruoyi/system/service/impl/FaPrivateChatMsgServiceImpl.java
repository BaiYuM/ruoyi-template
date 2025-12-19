package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FaPrivateChatMsgMapper;
import com.ruoyi.system.domain.FaPrivateChatMsg;
import com.ruoyi.system.service.IFaPrivateChatMsgService;

/**
 * 私聊消息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@Service
public class FaPrivateChatMsgServiceImpl implements IFaPrivateChatMsgService 
{
    @Autowired
    private FaPrivateChatMsgMapper faPrivateChatMsgMapper;

    /**
     * 查询私聊消息
     * 
     * @param id 私聊消息主键
     * @return 私聊消息
     */
    @Override
    public FaPrivateChatMsg selectFaPrivateChatMsgById(Long id)
    {
        return faPrivateChatMsgMapper.selectFaPrivateChatMsgById(id);
    }

    /**
     * 查询私聊消息列表
     * 
     * @param faPrivateChatMsg 私聊消息
     * @return 私聊消息
     */
    @Override
    public List<FaPrivateChatMsg> selectFaPrivateChatMsgList(FaPrivateChatMsg faPrivateChatMsg)
    {
        return faPrivateChatMsgMapper.selectFaPrivateChatMsgList(faPrivateChatMsg);
    }

    /**
     * 新增私聊消息
     * 
     * @param faPrivateChatMsg 私聊消息
     * @return 结果
     */
    @Override
    public int insertFaPrivateChatMsg(FaPrivateChatMsg faPrivateChatMsg)
    {
        return faPrivateChatMsgMapper.insertFaPrivateChatMsg(faPrivateChatMsg);
    }

    /**
     * 修改私聊消息
     * 
     * @param faPrivateChatMsg 私聊消息
     * @return 结果
     */
    @Override
    public int updateFaPrivateChatMsg(FaPrivateChatMsg faPrivateChatMsg)
    {
        return faPrivateChatMsgMapper.updateFaPrivateChatMsg(faPrivateChatMsg);
    }

    /**
     * 批量删除私聊消息
     * 
     * @param ids 需要删除的私聊消息主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatMsgByIds(Long[] ids)
    {
        return faPrivateChatMsgMapper.deleteFaPrivateChatMsgByIds(ids);
    }

    /**
     * 删除私聊消息信息
     * 
     * @param id 私聊消息主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatMsgById(Long id)
    {
        return faPrivateChatMsgMapper.deleteFaPrivateChatMsgById(id);
    }
}
