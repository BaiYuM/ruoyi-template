package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FriendAutoApproveMapper;
import com.ruoyi.system.domain.FriendAutoApprove;
import com.ruoyi.system.service.IFriendAutoApproveService;

@Service
public class FriendAutoApproveServiceImpl implements IFriendAutoApproveService {
    @Autowired
    private FriendAutoApproveMapper mapper;

    @Override
    public List<FriendAutoApprove> selectAutoApproveList(FriendAutoApprove filter) {
        return mapper.selectAutoApproveList(filter);
    }

    @Override
    public FriendAutoApprove selectAutoApproveById(Long id) { return mapper.selectAutoApproveById(id); }

    @Override
    public int insertAutoApprove(FriendAutoApprove obj) { return mapper.insertAutoApprove(obj); }

    @Override
    public int updateAutoApprove(FriendAutoApprove obj) { return mapper.updateAutoApprove(obj); }

    @Override
    public int deleteAutoApproveByIds(Long[] ids) { int r=0; for(Long id:ids) r+=mapper.deleteAutoApproveById(id); return r; }
}
