package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.FaGroupChat;

/**
 * 群聊基础信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface FaGroupChatMapper 
{
    /**
     * 查询群聊基础信息
     * 
     * @param id 群聊基础信息主键
     * @return 群聊基础信息
     */
    public FaGroupChat selectFaGroupChatById(Long id);

    /**
     * 查询群聊基础信息列表
     * 
     * @param faGroupChat 群聊基础信息
     * @return 群聊基础信息集合
     */
    public List<FaGroupChat> selectFaGroupChatList(FaGroupChat faGroupChat);

    /**
     * 新增群聊基础信息
     * 
     * @param faGroupChat 群聊基础信息
     * @return 结果
     */
    public int insertFaGroupChat(FaGroupChat faGroupChat);

    /**
     * 修改群聊基础信息
     * 
     * @param faGroupChat 群聊基础信息
     * @return 结果
     */
    public int updateFaGroupChat(FaGroupChat faGroupChat);

    /**
     * 删除群聊基础信息
     * 
     * @param id 群聊基础信息主键
     * @return 结果
     */
    public int deleteFaGroupChatById(Long id);

    /**
     * 批量删除群聊基础信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFaGroupChatByIds(Long[] ids);
}
