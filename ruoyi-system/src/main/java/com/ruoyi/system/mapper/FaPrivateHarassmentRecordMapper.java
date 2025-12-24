package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.FaPrivateHarassmentRecord;

import java.util.List;

/**
 * 私信追杀执行记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface FaPrivateHarassmentRecordMapper 
{
    /**
     * 查询私信追杀执行记录
     * 
     * @param id 私信追杀执行记录主键
     * @return 私信追杀执行记录
     */
    public FaPrivateHarassmentRecord selectFaPrivateHarassmentRecordById(Long id);

    /**
     * 查询私信追杀执行记录列表
     * 
     * @param faPrivateHarassmentRecord 私信追杀执行记录
     * @return 私信追杀执行记录集合
     */
    public List<FaPrivateHarassmentRecord> selectFaPrivateHarassmentRecordList(FaPrivateHarassmentRecord faPrivateHarassmentRecord);

    /**
     * 新增私信追杀执行记录
     * 
     * @param faPrivateHarassmentRecord 私信追杀执行记录
     * @return 结果
     */
    public int insertFaPrivateHarassmentRecord(FaPrivateHarassmentRecord faPrivateHarassmentRecord);

    /**
     * 修改私信追杀执行记录
     * 
     * @param faPrivateHarassmentRecord 私信追杀执行记录
     * @return 结果
     */
    public int updateFaPrivateHarassmentRecord(FaPrivateHarassmentRecord faPrivateHarassmentRecord);

    /**
     * 删除私信追杀执行记录
     * 
     * @param id 私信追杀执行记录主键
     * @return 结果
     */
    public int deleteFaPrivateHarassmentRecordById(Long id);

    /**
     * 批量删除私信追杀执行记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFaPrivateHarassmentRecordByIds(Long[] ids);
}
