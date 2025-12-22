package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.FaPrivateChatSetting;
import com.ruoyi.system.mapper.FaPrivateChatSettingMapper;
import com.ruoyi.system.service.IFaPrivateChatSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 私聊设置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
@Service
public class FaPrivateChatSettingServiceImpl implements IFaPrivateChatSettingService
{
    @Autowired
    private FaPrivateChatSettingMapper faPrivateChatSettingMapper;

    /**
     * 查询私聊设置
     * 
     * @param id 私聊设置主键
     * @return 私聊设置
     */
    @Override
    public FaPrivateChatSetting selectFaPrivateChatSettingById(Long id)
    {
        return faPrivateChatSettingMapper.selectFaPrivateChatSettingById(id);
    }

    /**
     * 查询私聊设置列表
     * 
     * @param faPrivateChatSetting 私聊设置
     * @return 私聊设置
     */
    @Override
    public List<FaPrivateChatSetting> selectFaPrivateChatSettingList(FaPrivateChatSetting faPrivateChatSetting)
    {
        return faPrivateChatSettingMapper.selectFaPrivateChatSettingList(faPrivateChatSetting);
    }

    /**
     * 新增私聊设置
     * 
     * @param faPrivateChatSetting 私聊设置
     * @return 结果
     */
    @Override
    public int insertFaPrivateChatSetting(FaPrivateChatSetting faPrivateChatSetting)
    {
        return faPrivateChatSettingMapper.insertFaPrivateChatSetting(faPrivateChatSetting);
    }

    /**
     * 修改私聊设置
     * 
     * @param faPrivateChatSetting 私聊设置
     * @return 结果
     */
    @Override
    public int updateFaPrivateChatSetting(FaPrivateChatSetting faPrivateChatSetting)
    {
        faPrivateChatSetting.setUpdateTime(DateUtils.getNowDate());
        return faPrivateChatSettingMapper.updateFaPrivateChatSetting(faPrivateChatSetting);
    }

    /**
     * 批量删除私聊设置
     * 
     * @param ids 需要删除的私聊设置主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatSettingByIds(Long[] ids)
    {
        return faPrivateChatSettingMapper.deleteFaPrivateChatSettingByIds(ids);
    }

    /**
     * 删除私聊设置信息
     * 
     * @param id 私聊设置主键
     * @return 结果
     */
    @Override
    public int deleteFaPrivateChatSettingById(Long id)
    {
        return faPrivateChatSettingMapper.deleteFaPrivateChatSettingById(id);
    }
}
