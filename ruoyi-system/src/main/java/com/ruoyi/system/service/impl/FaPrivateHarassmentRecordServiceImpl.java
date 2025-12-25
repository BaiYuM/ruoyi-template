package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.FaPrivateChat;
import com.ruoyi.system.domain.FaPrivateChatMsg;
import com.ruoyi.system.domain.FaPrivateHarassmentConfig;
import com.ruoyi.system.domain.FaPrivateHarassmentRecord;
import com.ruoyi.system.mapper.FaPrivateHarassmentRecordMapper;
import com.ruoyi.system.service.ICommentUserService;
import com.ruoyi.system.service.IFaPrivateChatService;
import com.ruoyi.system.service.IFaPrivateHarassmentConfigService;
import com.ruoyi.system.service.IFaPrivateHarassmentRecordService;
import com.ruoyi.system.domain.CommentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 私信追杀执行记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@Service
public class FaPrivateHarassmentRecordServiceImpl implements IFaPrivateHarassmentRecordService
{
    @Autowired
    private FaPrivateHarassmentRecordMapper faPrivateHarassmentRecordMapper;

    @Autowired
    private IFaPrivateHarassmentConfigService configService;

    @Autowired
    private IFaPrivateChatService chatService;

    @Autowired
    private ICommentUserService commentUserService;
    
    @Autowired
    private com.ruoyi.system.service.IFaAiFriendService faAiFriendService;

    /**
     * 查询私信追杀执行记录
     * 
     * @param id 私信追杀执行记录主键
     * @return 私信追杀执行记录
     */
    @Override
    public FaPrivateHarassmentRecord selectFaPrivateHarassmentRecordById(Long id)
    {
        return faPrivateHarassmentRecordMapper.selectFaPrivateHarassmentRecordById(id);
    }

    /**
     * 查询私信追杀执行记录列表
     * 
     * @param faPrivateHarassmentRecord 私信追杀执行记录
     * @return 私信追杀执行记录
     */
    @Override
    public List<FaPrivateHarassmentRecord> selectFaPrivateHarassmentRecordList(FaPrivateHarassmentRecord faPrivateHarassmentRecord)
    {
        return faPrivateHarassmentRecordMapper.selectFaPrivateHarassmentRecordList(faPrivateHarassmentRecord);
    }

    /**
     * 新增私信追杀执行记录
     * 
     * @param faPrivateHarassmentRecord 私信追杀执行记录
     * @return 结果
     */
    @Override
    public int insertFaPrivateHarassmentRecord(FaPrivateHarassmentRecord faPrivateHarassmentRecord)
    {
        faPrivateHarassmentRecord.setCreateTime(DateUtils.getNowDate());
        return faPrivateHarassmentRecordMapper.insertFaPrivateHarassmentRecord(faPrivateHarassmentRecord);
    }

    /**
     * 修改私信追杀执行记录
     * 
     * @param faPrivateHarassmentRecord 私信追杀执行记录
     * @return 结果
     */
    @Override
    public int updateFaPrivateHarassmentRecord(FaPrivateHarassmentRecord faPrivateHarassmentRecord)
    {
        faPrivateHarassmentRecord.setUpdateTime(DateUtils.getNowDate());
        return faPrivateHarassmentRecordMapper.updateFaPrivateHarassmentRecord(faPrivateHarassmentRecord);
    }

    /**
     * 批量删除私信追杀执行记录
     * 
     * @param ids 需要删除的私信追杀执行记录主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateHarassmentRecordByIds(Long[] ids)
    {
        return faPrivateHarassmentRecordMapper.deleteFaPrivateHarassmentRecordByIds(ids);
    }

    /**
     * 删除私信追杀执行记录信息
     * 
     * @param id 私信追杀执行记录主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateHarassmentRecordById(Long id)
    {
        return faPrivateHarassmentRecordMapper.deleteFaPrivateHarassmentRecordById(id);
    }

    /**
     * 开启私信追杀
     */
    @Override
    public int startHarassment(Long chatId, Long userId, String accountId) {
        // 1. 查找该账号的配置
        FaPrivateHarassmentConfig queryConfig = new FaPrivateHarassmentConfig();
        queryConfig.setAccountId(accountId);
        queryConfig.setIsEnabled(1);
        List<FaPrivateHarassmentConfig> configs = configService.selectFaPrivateHarassmentConfigList(queryConfig);
        
        if (configs.isEmpty()) {
            return 0;
        }
        
        FaPrivateHarassmentConfig config = configs.get(0);
        
        // 2. 检查是否已经存在该会话的追杀记录
        FaPrivateHarassmentRecord queryRecord = new FaPrivateHarassmentRecord();
        queryRecord.setChatId(chatId);
        List<FaPrivateHarassmentRecord> existingRecords = faPrivateHarassmentRecordMapper.selectFaPrivateHarassmentRecordList(queryRecord);
        if (!existingRecords.isEmpty()) {
            // 如果已经存在且是进行中，则不重复创建
            for (FaPrivateHarassmentRecord r : existingRecords) {
                if (r.getStatus() == 0) return 0;
            }
        }
        
        // 3. 创建追杀记录
        FaPrivateHarassmentRecord record = new FaPrivateHarassmentRecord();
        record.setConfigId(config.getId());
        record.setChatId(chatId);
        record.setUserId(userId);
        record.setCurrentVisitCount(0L);
        record.setTotalVisitCount(config.getVisitCount());
        record.setStatus(0); // 进行中
        record.setCreateTime(DateUtils.getNowDate());
        
        // 设置总时间（秒），默认为24小时
        long totalTime = config.getTotalTimeSeconds() != null ? config.getTotalTimeSeconds() : 86400;
        record.setTotalTimeSeconds(totalTime);

        // 计算下次发送时间
        long interval = config.getReplyIntervalSeconds() != null ? config.getReplyIntervalSeconds() : 3600;
        record.setNextSendTime(new Date(System.currentTimeMillis() + interval * 1000));
        
        return faPrivateHarassmentRecordMapper.insertFaPrivateHarassmentRecord(record);
    }

    /**
     * 处理私信追杀任务
     */
    @Override
    public void processHarassmentTask() {
        // 1. 获取所有进行中的追杀记录
        FaPrivateHarassmentRecord queryRecord = new FaPrivateHarassmentRecord();
        queryRecord.setStatus(0); // 进行中
        List<FaPrivateHarassmentRecord> records = faPrivateHarassmentRecordMapper.selectFaPrivateHarassmentRecordList(queryRecord);

        Date now = new Date();
        for (FaPrivateHarassmentRecord record : records) {
            // 检查是否超时（24小时内未留资）
            if (record.getTotalTimeSeconds() != null &&
                now.after(new Date(record.getCreateTime().getTime() + record.getTotalTimeSeconds() * 1000))) {
                record.setStatus(2); // 超时
                faPrivateHarassmentRecordMapper.updateFaPrivateHarassmentRecord(record);
                continue;
            }

            // 2. 检查是否到达发送时间
            if (record.getNextSendTime() != null && now.after(record.getNextSendTime())) {
                // 3. 获取配置
                FaPrivateHarassmentConfig config = configService.selectFaPrivateHarassmentConfigById(record.getConfigId());
                if (config == null || config.getIsEnabled() == 0) {
                    continue;
                }

                // 4. 检查留资状态
                FaPrivateChat chat = chatService.selectFaPrivateChatById(record.getChatId());
                if (chat != null && chat.getPeerUserId() != null) {
                    // 根据好友用户ID和AI授权配置查询好友的is_lead状态
                    // 注：这里需要先获取该会话对应的AI授权ID，从配置中获取
                    String accountId = config.getAccountId();
                    com.ruoyi.system.domain.CommentUser authUser = new com.ruoyi.system.domain.CommentUser();
                    authUser.setAccount(accountId);
                    List<com.ruoyi.system.domain.CommentUser> authUsers = commentUserService.selectCommentUserList(authUser);
                    
                    if (!authUsers.isEmpty()) {
                        Long authUserId = authUsers.get(0).getId();
                        
                        // 查询是否有对应的AI配置
                        com.ruoyi.system.domain.FaAiFriend queryFriend = new com.ruoyi.system.domain.FaAiFriend();
                        queryFriend.setFriendUserId(chat.getPeerUserId());
                        List<com.ruoyi.system.domain.FaAiFriend> friends = faAiFriendService.selectFaAiFriendList(queryFriend);
                        
                        if (!friends.isEmpty()) {
                            com.ruoyi.system.domain.FaAiFriend friend = friends.get(0);
                            // 检查是否已留资
                            if (friend.getIsLead() != null && friend.getIsLead() == 1) {
                                record.setStatus(3); // 已留资
                                faPrivateHarassmentRecordMapper.updateFaPrivateHarassmentRecord(record);
                                continue;
                            }
                        }
                    }
                }

                // 5. 获取话术
                JSONArray phrases = JSON.parseArray(config.getPhraseList());
                if (phrases == null || phrases.isEmpty()) {
                    continue;
                }

                int nextIndex = record.getCurrentVisitCount().intValue();
                if (nextIndex >= phrases.size()) {
                    record.setStatus(1); // 已完成
                    faPrivateHarassmentRecordMapper.updateFaPrivateHarassmentRecord(record);
                    continue;
                }

                String content = phrases.getString(nextIndex);

                // 6. 发送消息
                FaPrivateChatMsg msg = new FaPrivateChatMsg();
                msg.setChatId(record.getChatId());
                msg.setMsgContent(content);
                
                // 查找发送方ID (抖音账号对应的 comment_user.id)
                CommentUser queryUser = new CommentUser();
                queryUser.setAccount(config.getAccountId());
                List<CommentUser> users = commentUserService.selectCommentUserList(queryUser);
                if (!users.isEmpty()) {
                    msg.setSenderId(users.get(0).getId());
                } else {
                    msg.setSenderId(0L); // 兜底
                }
                
                msg.setReceiverId(record.getUserId());
                msg.setMsgType(0L); // 文本
                chatService.sendMessage(msg);

                // 7. 更新记录
                record.setCurrentVisitCount(record.getCurrentVisitCount() + 1);
                record.setLastSendTime(new Date());
                
                if (record.getCurrentVisitCount() >= record.getTotalVisitCount()) {
                    record.setStatus(1); // 已完成
                } else {
                    // 计算下次发送时间
                    long interval = config.getReplyIntervalSeconds() != null ? config.getReplyIntervalSeconds() : 3600;
                    record.setNextSendTime(new Date(System.currentTimeMillis() + interval * 1000));
                }
                
                faPrivateHarassmentRecordMapper.updateFaPrivateHarassmentRecord(record);
            }
        }
    }
}
