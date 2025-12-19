package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.FaPrivateChat;

/**
 * 私聊会话Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface FaPrivateChatMapper 
{
    /**
     * 查询私聊会话
     * 
     * @param id 私聊会话主键
     * @return 私聊会话
     */
    public FaPrivateChat selectFaPrivateChatById(Long id);

    /**
     * 查询私聊会话列表
     * 
     * @param faPrivateChat 私聊会话
     * @return 私聊会话集合
     */
    public List<FaPrivateChat> selectFaPrivateChatList(FaPrivateChat faPrivateChat);

    /**
     * 新增私聊会话
     * 
     * @param faPrivateChat 私聊会话
     * @return 结果
     */
    public int insertFaPrivateChat(FaPrivateChat faPrivateChat);

    /**
     * 修改私聊会话
     * 
     * @param faPrivateChat 私聊会话
     * @return 结果
     */
    public int updateFaPrivateChat(FaPrivateChat faPrivateChat);

    /**
     * 删除私聊会话
     * 
     * @param id 私聊会话主键
     * @return 结果
     */
    public int deleteFaPrivateChatById(Long id);

    /**
     * 批量删除私聊会话
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFaPrivateChatByIds(Long[] ids);
}
