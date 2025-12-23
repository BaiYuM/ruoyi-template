package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.FaPrivateChat;
import com.ruoyi.system.domain.FaPrivateChatMsg;
import com.ruoyi.system.mapper.FaPrivateChatMsgMapper;
import com.ruoyi.system.service.IFaPrivateChatMsgService;
import com.ruoyi.system.service.IFaPrivateChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 私聊消息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
@Service
public class FaPrivateChatMsgServiceImpl implements IFaPrivateChatMsgService
{
    @Autowired
    private FaPrivateChatMsgMapper faPrivateChatMsgMapper;
    
    @Autowired
    private IFaPrivateChatService faPrivateChatService;

    /**
     * 查询私聊消息
     * 
     * @param id 私聊消息主键
     * @return 私聊消息
     */
    @Override
    public FaPrivateChatMsg selectFaPrivateChatMsgById(Long id)
    {
        return faPrivateChatMsgMapper.selectFaPrivateChatMsgById(id);
    }

    /**
     * 查询私聊消息列表
     * 
     * @param faPrivateChatMsg 私聊消息
     * @return 私聊消息
     */
    @Override
    public List<FaPrivateChatMsg> selectFaPrivateChatMsgList(FaPrivateChatMsg faPrivateChatMsg)
    {
        return faPrivateChatMsgMapper.selectFaPrivateChatMsgList(faPrivateChatMsg);
    }

    /**
     * 新增私聊消息
     * 
     * @param faPrivateChatMsg 私聊消息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertFaPrivateChatMsg(FaPrivateChatMsg faPrivateChatMsg)
    {
        // 使用雪花算法生成ID
        if (faPrivateChatMsg.getId() == null) {
            faPrivateChatMsg.setId(IdUtils.getSnowflakeId());
        }

        
        // 插入消息
        int result = faPrivateChatMsgMapper.insertFaPrivateChatMsg(faPrivateChatMsg);
        
        // 更新会话的最后消息ID
        if (result > 0 && faPrivateChatMsg.getChatId() != null) {
            FaPrivateChat chat = new FaPrivateChat();
            chat.setId(faPrivateChatMsg.getChatId());
            chat.setLastMsgId(faPrivateChatMsg.getId());
            faPrivateChatService.updateFaPrivateChat(chat);
        }
        
        return result;
    }

    /**
     * 修改私聊消息
     * 
     * @param faPrivateChatMsg 私聊消息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateFaPrivateChatMsg(FaPrivateChatMsg faPrivateChatMsg)
    {
        int result = faPrivateChatMsgMapper.updateFaPrivateChatMsg(faPrivateChatMsg);
        // 更新会话的最后消息ID
        if (result > 0 && faPrivateChatMsg.getChatId() != null) {
            FaPrivateChat chat = new FaPrivateChat();
            chat.setId(faPrivateChatMsg.getChatId());
            chat.setLastMsgId(faPrivateChatMsg.getId());
            faPrivateChatService.updateFaPrivateChat(chat);
        }
        return result;
    }

    /**
     * 批量删除私聊消息
     * 
     * @param ids 需要删除的私聊消息主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatMsgByIds(Long[] ids)
    {
        return faPrivateChatMsgMapper.deleteFaPrivateChatMsgByIds(ids);
    }

    /**
     * 删除私聊消息信息
     * 
     * @param id 私聊消息主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatMsgById(Long id)
    {
        return faPrivateChatMsgMapper.deleteFaPrivateChatMsgById(id);
    }
}