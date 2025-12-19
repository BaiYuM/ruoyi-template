package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.ExpirationAi;
import com.ruoyi.system.mapper.ExpirationAiMapper;
import com.ruoyi.system.service.IExpirationAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 授权账号配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-19
 */
@Service
public class ExpirationAiServiceImpl implements IExpirationAiService
{
    @Autowired
    private ExpirationAiMapper expirationAiMapper;

    /**
     * 查询授权账号配置
     * 
     * @param id 授权账号配置主键
     * @return 授权账号配置
     */
    @Override
    public ExpirationAi selectExpirationAiById(Long id)
    {
        return expirationAiMapper.selectExpirationAiById(id);
    }

    /**
     * 查询授权账号配置列表
     * 
     * @param expirationAi 授权账号配置
     * @return 授权账号配置
     */
    @Override
    public List<ExpirationAi> selectExpirationAiList(ExpirationAi expirationAi)
    {
        return expirationAiMapper.selectExpirationAiList(expirationAi);
    }

    /**
     * 新增授权账号配置
     * 
     * @param expirationAi 授权账号配置
     * @return 结果
     */
    @Override
    public int insertExpirationAi(ExpirationAi expirationAi)
    {
        expirationAi.setCreateTime(DateUtils.getNowDate());
        return expirationAiMapper.insertExpirationAi(expirationAi);
    }

    /**
     * 修改授权账号配置
     * 
     * @param expirationAi 授权账号配置
     * @return 结果
     */
    @Override
    public int updateExpirationAi(ExpirationAi expirationAi)
    {
        expirationAi.setUpdateTime(DateUtils.getNowDate());
        return expirationAiMapper.updateExpirationAi(expirationAi);
    }

    /**
     * 批量删除授权账号配置
     * 
     * @param ids 需要删除的授权账号配置主键
     * @return 结果
     */
    @Override
    public int deleteExpirationAiByIds(Long[] ids)
    {
        return expirationAiMapper.deleteExpirationAiByIds(ids);
    }

    /**
     * 删除授权账号配置信息
     * 
     * @param id 授权账号配置主键
     * @return 结果
     */
    @Override
    public int deleteExpirationAiById(Long id)
    {
        return expirationAiMapper.deleteExpirationAiById(id);
    }
}
