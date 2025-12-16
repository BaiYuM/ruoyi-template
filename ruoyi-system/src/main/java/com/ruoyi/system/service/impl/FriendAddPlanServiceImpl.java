package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.FriendAddPlanMapper;
import com.ruoyi.system.domain.FriendAddPlan;
import com.ruoyi.system.service.IFriendAddPlanService;

@Service
public class FriendAddPlanServiceImpl implements IFriendAddPlanService {
    @Autowired
    private FriendAddPlanMapper mapper;

    @Override
    public List<FriendAddPlan> selectPlanList(FriendAddPlan filter) {
        return mapper.selectPlanList(filter);
    }

    @Override
    public FriendAddPlan selectPlanById(Long id) {
        return mapper.selectPlanById(id);
    }

    @Override
    public int insertPlan(FriendAddPlan obj) {
        return mapper.insertPlan(obj);
    }

    @Override
    public int updatePlan(FriendAddPlan obj) { return mapper.updatePlan(obj); }

    @Override
    public int deletePlanByIds(Long[] ids) { int r=0; for(Long id:ids) r+=mapper.deletePlanById(id); return r; }
}
