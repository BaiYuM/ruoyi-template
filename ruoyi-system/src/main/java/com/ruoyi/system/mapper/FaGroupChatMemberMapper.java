package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.FaGroupChatMember;

/**
 * 群聊成员关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
public interface FaGroupChatMemberMapper 
{
    /**
     * 查询群聊成员关联
     * 
     * @param id 群聊成员关联主键
     * @return 群聊成员关联
     */
    public FaGroupChatMember selectFaGroupChatMemberById(Long id);

    /**
     * 查询群聊成员关联列表
     * 
     * @param faGroupChatMember 群聊成员关联
     * @return 群聊成员关联集合
     */
    public List<FaGroupChatMember> selectFaGroupChatMemberList(FaGroupChatMember faGroupChatMember);

    /**
     * 新增群聊成员关联
     * 
     * @param faGroupChatMember 群聊成员关联
     * @return 结果
     */
    public int insertFaGroupChatMember(FaGroupChatMember faGroupChatMember);

    /**
     * 修改群聊成员关联
     * 
     * @param faGroupChatMember 群聊成员关联
     * @return 结果
     */
    public int updateFaGroupChatMember(FaGroupChatMember faGroupChatMember);

    /**
     * 删除群聊成员关联
     * 
     * @param id 群聊成员关联主键
     * @return 结果
     */
    public int deleteFaGroupChatMemberById(Long id);

    /**
     * 批量删除群聊成员关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFaGroupChatMemberByIds(Long[] ids);
}
