package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FaGroupChatMapper;
import com.ruoyi.system.domain.FaGroupChat;
import com.ruoyi.system.service.IFaGroupChatService;

/**
 * 群聊基础信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@Service
public class FaGroupChatServiceImpl implements IFaGroupChatService 
{
    @Autowired
    private FaGroupChatMapper faGroupChatMapper;

    /**
     * 查询群聊基础信息
     * 
     * @param id 群聊基础信息主键
     * @return 群聊基础信息
     */
    @Override
    public FaGroupChat selectFaGroupChatById(Long id)
    {
        return faGroupChatMapper.selectFaGroupChatById(id);
    }

    /**
     * 查询群聊基础信息列表
     * 
     * @param faGroupChat 群聊基础信息
     * @return 群聊基础信息
     */
    @Override
    public List<FaGroupChat> selectFaGroupChatList(FaGroupChat faGroupChat)
    {
        return faGroupChatMapper.selectFaGroupChatList(faGroupChat);
    }

    /**
     * 新增群聊基础信息
     * 
     * @param faGroupChat 群聊基础信息
     * @return 结果
     */
    @Override
    public int insertFaGroupChat(FaGroupChat faGroupChat)
    {
        faGroupChat.setCreateTime(DateUtils.getNowDate());
        return faGroupChatMapper.insertFaGroupChat(faGroupChat);
    }

    /**
     * 修改群聊基础信息
     * 
     * @param faGroupChat 群聊基础信息
     * @return 结果
     */
    @Override
    public int updateFaGroupChat(FaGroupChat faGroupChat)
    {
        faGroupChat.setUpdateTime(DateUtils.getNowDate());
        return faGroupChatMapper.updateFaGroupChat(faGroupChat);
    }

    /**
     * 批量删除群聊基础信息
     * 
     * @param ids 需要删除的群聊基础信息主键
     * @return 结果
     */
    @Override
    public int deleteFaGroupChatByIds(Long[] ids)
    {
        return faGroupChatMapper.deleteFaGroupChatByIds(ids);
    }

    /**
     * 删除群聊基础信息信息
     * 
     * @param id 群聊基础信息主键
     * @return 结果
     */
    @Override
    public int deleteFaGroupChatById(Long id)
    {
        return faGroupChatMapper.deleteFaGroupChatById(id);
    }
}
