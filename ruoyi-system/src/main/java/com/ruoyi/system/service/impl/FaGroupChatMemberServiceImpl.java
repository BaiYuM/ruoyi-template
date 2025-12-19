package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FaGroupChatMemberMapper;
import com.ruoyi.system.domain.FaGroupChatMember;
import com.ruoyi.system.service.IFaGroupChatMemberService;

/**
 * 群聊成员关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@Service
public class FaGroupChatMemberServiceImpl implements IFaGroupChatMemberService 
{
    @Autowired
    private FaGroupChatMemberMapper faGroupChatMemberMapper;

    /**
     * 查询群聊成员关联
     * 
     * @param id 群聊成员关联主键
     * @return 群聊成员关联
     */
    @Override
    public FaGroupChatMember selectFaGroupChatMemberById(Long id)
    {
        return faGroupChatMemberMapper.selectFaGroupChatMemberById(id);
    }

    /**
     * 查询群聊成员关联列表
     * 
     * @param faGroupChatMember 群聊成员关联
     * @return 群聊成员关联
     */
    @Override
    public List<FaGroupChatMember> selectFaGroupChatMemberList(FaGroupChatMember faGroupChatMember)
    {
        return faGroupChatMemberMapper.selectFaGroupChatMemberList(faGroupChatMember);
    }

    /**
     * 新增群聊成员关联
     * 
     * @param faGroupChatMember 群聊成员关联
     * @return 结果
     */
    @Override
    public int insertFaGroupChatMember(FaGroupChatMember faGroupChatMember)
    {
        return faGroupChatMemberMapper.insertFaGroupChatMember(faGroupChatMember);
    }

    /**
     * 修改群聊成员关联
     * 
     * @param faGroupChatMember 群聊成员关联
     * @return 结果
     */
    @Override
    public int updateFaGroupChatMember(FaGroupChatMember faGroupChatMember)
    {
        return faGroupChatMemberMapper.updateFaGroupChatMember(faGroupChatMember);
    }

    /**
     * 批量删除群聊成员关联
     * 
     * @param ids 需要删除的群聊成员关联主键
     * @return 结果
     */
    @Override
    public int deleteFaGroupChatMemberByIds(Long[] ids)
    {
        return faGroupChatMemberMapper.deleteFaGroupChatMemberByIds(ids);
    }

    /**
     * 删除群聊成员关联信息
     * 
     * @param id 群聊成员关联主键
     * @return 结果
     */
    @Override
    public int deleteFaGroupChatMemberById(Long id)
    {
        return faGroupChatMemberMapper.deleteFaGroupChatMemberById(id);
    }
}
