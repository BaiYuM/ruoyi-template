package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FriendAddRecordMapper;
import com.ruoyi.system.domain.FriendAddRecord;
import com.ruoyi.system.service.IFriendAddRecordService;

@Service
public class FriendAddRecordServiceImpl implements IFriendAddRecordService {
    @Autowired
    private FriendAddRecordMapper mapper;

    @Override
    public List<FriendAddRecord> selectRecordList(FriendAddRecord filter) {
        return mapper.selectRecordList(filter);
    }

    @Override
    public FriendAddRecord selectRecordById(Long id) { return mapper.selectRecordById(id); }

    @Override
    public int insertRecord(FriendAddRecord obj) { return mapper.insertRecord(obj); }

    @Override
    public int updateRecord(FriendAddRecord obj) { return mapper.updateRecord(obj); }

    @Override
    public int deleteRecordByIds(Long[] ids) { int r=0; for(Long id:ids) r+=mapper.deleteRecordById(id); return r; }
}
