package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.FaPrivateHarassmentRecord;
import com.ruoyi.system.mapper.FaPrivateHarassmentRecordMapper;
import com.ruoyi.system.service.IFaPrivateHarassmentRecordService;
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
}
