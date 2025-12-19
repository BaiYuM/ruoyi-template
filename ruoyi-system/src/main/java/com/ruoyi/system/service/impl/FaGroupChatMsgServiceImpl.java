package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FaGroupChatMsgMapper;
import com.ruoyi.system.domain.FaGroupChatMsg;
import com.ruoyi.system.service.IFaGroupChatMsgService;

/**
 * 群聊消息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@Service
public class FaGroupChatMsgServiceImpl implements IFaGroupChatMsgService 
{
    @Autowired
    private FaGroupChatMsgMapper faGroupChatMsgMapper;

    /**
     * 查询群聊消息
     * 
     * @param id 群聊消息主键
     * @return 群聊消息
     */
    @Override
    public FaGroupChatMsg selectFaGroupChatMsgById(Long id)
    {
        return faGroupChatMsgMapper.selectFaGroupChatMsgById(id);
    }

    /**
     * 查询群聊消息列表
     * 
     * @param faGroupChatMsg 群聊消息
     * @return 群聊消息
     */
    @Override
    public List<FaGroupChatMsg> selectFaGroupChatMsgList(FaGroupChatMsg faGroupChatMsg)
    {
        return faGroupChatMsgMapper.selectFaGroupChatMsgList(faGroupChatMsg);
    }

    /**
     * 新增群聊消息
     * 
     * @param faGroupChatMsg 群聊消息
     * @return 结果
     */
    @Override
    public int insertFaGroupChatMsg(FaGroupChatMsg faGroupChatMsg)
    {
        return faGroupChatMsgMapper.insertFaGroupChatMsg(faGroupChatMsg);
    }

    /**
     * 修改群聊消息
     * 
     * @param faGroupChatMsg 群聊消息
     * @return 结果
     */
    @Override
    public int updateFaGroupChatMsg(FaGroupChatMsg faGroupChatMsg)
    {
        return faGroupChatMsgMapper.updateFaGroupChatMsg(faGroupChatMsg);
    }

    /**
     * 批量删除群聊消息
     * 
     * @param ids 需要删除的群聊消息主键
     * @return 结果
     */
    @Override
    public int deleteFaGroupChatMsgByIds(Long[] ids)
    {
        return faGroupChatMsgMapper.deleteFaGroupChatMsgByIds(ids);
    }

    /**
     * 删除群聊消息信息
     * 
     * @param id 群聊消息主键
     * @return 结果
     */
    @Override
    public int deleteFaGroupChatMsgById(Long id)
    {
        return faGroupChatMsgMapper.deleteFaGroupChatMsgById(id);
    }
}
