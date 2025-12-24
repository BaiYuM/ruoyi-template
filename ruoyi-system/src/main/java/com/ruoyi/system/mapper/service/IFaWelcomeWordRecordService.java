package com.ruoyi.system.mapper.service;

import com.ruoyi.system.domain.FaWelcomeWordRecord;

import java.util.List;


/**
 * 欢迎词执行记录Service接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface IFaWelcomeWordRecordService 
{
    /**
     * 查询欢迎词执行记录
     * 
     * @param id 欢迎词执行记录主键
     * @return 欢迎词执行记录
     */
    public FaWelcomeWordRecord selectFaWelcomeWordRecordById(Long id);

    /**
     * 查询欢迎词执行记录列表
     * 
     * @param faWelcomeWordRecord 欢迎词执行记录
     * @return 欢迎词执行记录集合
     */
    public List<FaWelcomeWordRecord> selectFaWelcomeWordRecordList(FaWelcomeWordRecord faWelcomeWordRecord);

    /**
     * 新增欢迎词执行记录
     * 
     * @param faWelcomeWordRecord 欢迎词执行记录
     * @return 结果
     */
    public int insertFaWelcomeWordRecord(FaWelcomeWordRecord faWelcomeWordRecord);

    /**
     * 修改欢迎词执行记录
     * 
     * @param faWelcomeWordRecord 欢迎词执行记录
     * @return 结果
     */
    public int updateFaWelcomeWordRecord(FaWelcomeWordRecord faWelcomeWordRecord);

    /**
     * 批量删除欢迎词执行记录
     * 
     * @param ids 需要删除的欢迎词执行记录主键集合
     * @return 结果
     */
    public int deleteFaWelcomeWordRecordByIds(Long[] ids);

    /**
     * 删除欢迎词执行记录信息
     * 
     * @param id 欢迎词执行记录主键
     * @return 结果
     */
    public int deleteFaWelcomeWordRecordById(Long id);
}
