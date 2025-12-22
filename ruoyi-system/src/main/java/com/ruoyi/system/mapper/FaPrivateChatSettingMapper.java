package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.FaPrivateChatSetting;

import java.util.List;

/**
 * 私聊设置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-22
 */
public interface FaPrivateChatSettingMapper 
{
    /**
     * 查询私聊设置
     * 
     * @param id 私聊设置主键
     * @return 私聊设置
     */
    public FaPrivateChatSetting selectFaPrivateChatSettingById(Long id);

    /**
     * 查询私聊设置列表
     * 
     * @param faPrivateChatSetting 私聊设置
     * @return 私聊设置集合
     */
    public List<FaPrivateChatSetting> selectFaPrivateChatSettingList(FaPrivateChatSetting faPrivateChatSetting);

    /**
     * 新增私聊设置
     * 
     * @param faPrivateChatSetting 私聊设置
     * @return 结果
     */
    public int insertFaPrivateChatSetting(FaPrivateChatSetting faPrivateChatSetting);

    /**
     * 修改私聊设置
     * 
     * @param faPrivateChatSetting 私聊设置
     * @return 结果
     */
    public int updateFaPrivateChatSetting(FaPrivateChatSetting faPrivateChatSetting);

    /**
     * 删除私聊设置
     * 
     * @param id 私聊设置主键
     * @return 结果
     */
    public int deleteFaPrivateChatSettingById(Long id);

    /**
     * 批量删除私聊设置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFaPrivateChatSettingByIds(Long[] ids);
}
