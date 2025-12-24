package com.ruoyi.system.mapper.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.FaWelcomeWordRecord;
import com.ruoyi.system.mapper.FaWelcomeWordRecordMapper;
import com.ruoyi.system.mapper.service.IFaWelcomeWordRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 欢迎词执行记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@Service
public class FaWelcomeWordRecordServiceImpl implements IFaWelcomeWordRecordService
{
    @Autowired
    private FaWelcomeWordRecordMapper faWelcomeWordRecordMapper;

    /**
     * 查询欢迎词执行记录
     * 
     * @param id 欢迎词执行记录主键
     * @return 欢迎词执行记录
     */
    @Override
    public FaWelcomeWordRecord selectFaWelcomeWordRecordById(Long id)
    {
        return faWelcomeWordRecordMapper.selectFaWelcomeWordRecordById(id);
    }

    /**
     * 查询欢迎词执行记录列表
     * 
     * @param faWelcomeWordRecord 欢迎词执行记录
     * @return 欢迎词执行记录
     */
    @Override
    public List<FaWelcomeWordRecord> selectFaWelcomeWordRecordList(FaWelcomeWordRecord faWelcomeWordRecord)
    {
        return faWelcomeWordRecordMapper.selectFaWelcomeWordRecordList(faWelcomeWordRecord);
    }

    /**
     * 新增欢迎词执行记录
     * 
     * @param faWelcomeWordRecord 欢迎词执行记录
     * @return 结果
     */
    @Override
    public int insertFaWelcomeWordRecord(FaWelcomeWordRecord faWelcomeWordRecord)
    {
        faWelcomeWordRecord.setCreateTime(DateUtils.getNowDate());
        return faWelcomeWordRecordMapper.insertFaWelcomeWordRecord(faWelcomeWordRecord);
    }

    /**
     * 修改欢迎词执行记录
     * 
     * @param faWelcomeWordRecord 欢迎词执行记录
     * @return 结果
     */
    @Override
    public int updateFaWelcomeWordRecord(FaWelcomeWordRecord faWelcomeWordRecord)
    {
        return faWelcomeWordRecordMapper.updateFaWelcomeWordRecord(faWelcomeWordRecord);
    }

    /**
     * 批量删除欢迎词执行记录
     * 
     * @param ids 需要删除的欢迎词执行记录主键
     * @return 结果
     */
    @Override
    public int deleteFaWelcomeWordRecordByIds(Long[] ids)
    {
        return faWelcomeWordRecordMapper.deleteFaWelcomeWordRecordByIds(ids);
    }

    /**
     * 删除欢迎词执行记录信息
     * 
     * @param id 欢迎词执行记录主键
     * @return 结果
     */
    @Override
    public int deleteFaWelcomeWordRecordById(Long id)
    {
        return faWelcomeWordRecordMapper.deleteFaWelcomeWordRecordById(id);
    }
}
