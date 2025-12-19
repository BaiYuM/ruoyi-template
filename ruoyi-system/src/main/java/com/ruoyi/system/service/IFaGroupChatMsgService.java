package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.FaGroupChatMsg;

/**
 * 群聊消息Service接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface IFaGroupChatMsgService 
{
    /**
     * 查询群聊消息
     * 
     * @param id 群聊消息主键
     * @return 群聊消息
     */
    public FaGroupChatMsg selectFaGroupChatMsgById(Long id);

    /**
     * 查询群聊消息列表
     * 
     * @param faGroupChatMsg 群聊消息
     * @return 群聊消息集合
     */
    public List<FaGroupChatMsg> selectFaGroupChatMsgList(FaGroupChatMsg faGroupChatMsg);

    /**
     * 新增群聊消息
     * 
     * @param faGroupChatMsg 群聊消息
     * @return 结果
     */
    public int insertFaGroupChatMsg(FaGroupChatMsg faGroupChatMsg);

    /**
     * 修改群聊消息
     * 
     * @param faGroupChatMsg 群聊消息
     * @return 结果
     */
    public int updateFaGroupChatMsg(FaGroupChatMsg faGroupChatMsg);

    /**
     * 批量删除群聊消息
     * 
     * @param ids 需要删除的群聊消息主键集合
     * @return 结果
     */
    public int deleteFaGroupChatMsgByIds(Long[] ids);

    /**
     * 删除群聊消息信息
     * 
     * @param id 群聊消息主键
     * @return 结果
     */
    public int deleteFaGroupChatMsgById(Long id);
}
