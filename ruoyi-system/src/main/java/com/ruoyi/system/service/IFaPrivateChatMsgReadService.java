package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.FaPrivateChatMsgRead;

/**
 * 私聊消息已读Service接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface IFaPrivateChatMsgReadService 
{
    /**
     * 查询私聊消息已读
     * 
     * @param id 私聊消息已读主键
     * @return 私聊消息已读
     */
    public FaPrivateChatMsgRead selectFaPrivateChatMsgReadById(Long id);

    /**
     * 查询私聊消息已读列表
     * 
     * @param faPrivateChatMsgRead 私聊消息已读
     * @return 私聊消息已读集合
     */
    public List<FaPrivateChatMsgRead> selectFaPrivateChatMsgReadList(FaPrivateChatMsgRead faPrivateChatMsgRead);

    /**
     * 新增私聊消息已读
     * 
     * @param faPrivateChatMsgRead 私聊消息已读
     * @return 结果
     */
    public int insertFaPrivateChatMsgRead(FaPrivateChatMsgRead faPrivateChatMsgRead);

    /**
     * 修改私聊消息已读
     * 
     * @param faPrivateChatMsgRead 私聊消息已读
     * @return 结果
     */
    public int updateFaPrivateChatMsgRead(FaPrivateChatMsgRead faPrivateChatMsgRead);

    /**
     * 批量删除私聊消息已读
     * 
     * @param ids 需要删除的私聊消息已读主键集合
     * @return 结果
     */
    public int deleteFaPrivateChatMsgReadByIds(Long[] ids);

    /**
     * 删除私聊消息已读信息
     * 
     * @param id 私聊消息已读主键
     * @return 结果
     */
    public int deleteFaPrivateChatMsgReadById(Long id);
}
