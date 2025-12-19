package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.FaGroupChatMsgRead;

/**
 * 群消息已读状态Service接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface IFaGroupChatMsgReadService 
{
    /**
     * 查询群消息已读状态
     * 
     * @param id 群消息已读状态主键
     * @return 群消息已读状态
     */
    public FaGroupChatMsgRead selectFaGroupChatMsgReadById(Long id);

    /**
     * 查询群消息已读状态列表
     * 
     * @param faGroupChatMsgRead 群消息已读状态
     * @return 群消息已读状态集合
     */
    public List<FaGroupChatMsgRead> selectFaGroupChatMsgReadList(FaGroupChatMsgRead faGroupChatMsgRead);

    /**
     * 新增群消息已读状态
     * 
     * @param faGroupChatMsgRead 群消息已读状态
     * @return 结果
     */
    public int insertFaGroupChatMsgRead(FaGroupChatMsgRead faGroupChatMsgRead);

    /**
     * 修改群消息已读状态
     * 
     * @param faGroupChatMsgRead 群消息已读状态
     * @return 结果
     */
    public int updateFaGroupChatMsgRead(FaGroupChatMsgRead faGroupChatMsgRead);

    /**
     * 批量删除群消息已读状态
     * 
     * @param ids 需要删除的群消息已读状态主键集合
     * @return 结果
     */
    public int deleteFaGroupChatMsgReadByIds(Long[] ids);

    /**
     * 删除群消息已读状态信息
     * 
     * @param id 群消息已读状态主键
     * @return 结果
     */
    public int deleteFaGroupChatMsgReadById(Long id);
}
