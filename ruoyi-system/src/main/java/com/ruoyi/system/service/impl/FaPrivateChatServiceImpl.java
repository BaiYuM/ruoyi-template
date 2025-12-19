package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FaPrivateChatMapper;
import com.ruoyi.system.domain.FaPrivateChat;
import com.ruoyi.system.service.IFaPrivateChatService;

/**
 * 私聊会话Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@Service
public class FaPrivateChatServiceImpl implements IFaPrivateChatService 
{
    @Autowired
    private FaPrivateChatMapper faPrivateChatMapper;

    /**
     * 查询私聊会话
     * 
     * @param id 私聊会话主键
     * @return 私聊会话
     */
    @Override
    public FaPrivateChat selectFaPrivateChatById(Long id)
    {
        return faPrivateChatMapper.selectFaPrivateChatById(id);
    }

    /**
     * 查询私聊会话列表
     * 
     * @param faPrivateChat 私聊会话
     * @return 私聊会话
     */
    @Override
    public List<FaPrivateChat> selectFaPrivateChatList(FaPrivateChat faPrivateChat)
    {
        return faPrivateChatMapper.selectFaPrivateChatList(faPrivateChat);
    }

    /**
     * 新增私聊会话
     * 
     * @param faPrivateChat 私聊会话
     * @return 结果
     */
    @Override
    public int insertFaPrivateChat(FaPrivateChat faPrivateChat)
    {
        faPrivateChat.setCreateTime(DateUtils.getNowDate());
        return faPrivateChatMapper.insertFaPrivateChat(faPrivateChat);
    }

    /**
     * 修改私聊会话
     * 
     * @param faPrivateChat 私聊会话
     * @return 结果
     */
    @Override
    public int updateFaPrivateChat(FaPrivateChat faPrivateChat)
    {
        faPrivateChat.setUpdateTime(DateUtils.getNowDate());
        return faPrivateChatMapper.updateFaPrivateChat(faPrivateChat);
    }

    /**
     * 批量删除私聊会话
     * 
     * @param ids 需要删除的私聊会话主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatByIds(Long[] ids)
    {
        return faPrivateChatMapper.deleteFaPrivateChatByIds(ids);
    }

    /**
     * 删除私聊会话信息
     * 
     * @param id 私聊会话主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatById(Long id)
    {
        return faPrivateChatMapper.deleteFaPrivateChatById(id);
    }
}
