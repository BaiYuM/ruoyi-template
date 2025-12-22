package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.FaPrivateChatMsg;

import java.util.List;

/**
 * 私聊消息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
public interface FaPrivateChatMsgMapper 
{
    /**
     * 查询私聊消息
     * 
     * @param id 私聊消息主键
     * @return 私聊消息
     */
    public FaPrivateChatMsg selectFaPrivateChatMsgById(Long id);

    /**
     * 查询私聊消息列表
     * 
     * @param faPrivateChatMsg 私聊消息
     * @return 私聊消息集合
     */
    public List<FaPrivateChatMsg> selectFaPrivateChatMsgList(FaPrivateChatMsg faPrivateChatMsg);

    /**
     * 新增私聊消息
     * 
     * @param faPrivateChatMsg 私聊消息
     * @return 结果
     */
    public int insertFaPrivateChatMsg(FaPrivateChatMsg faPrivateChatMsg);

    /**
     * 修改私聊消息
     * 
     * @param faPrivateChatMsg 私聊消息
     * @return 结果
     */
    public int updateFaPrivateChatMsg(FaPrivateChatMsg faPrivateChatMsg);

    /**
     * 删除私聊消息
     * 
     * @param id 私聊消息主键
     * @return 结果
     */
    public int deleteFaPrivateChatMsgById(Long id);

    /**
     * 批量删除私聊消息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFaPrivateChatMsgByIds(Long[] ids);
}
