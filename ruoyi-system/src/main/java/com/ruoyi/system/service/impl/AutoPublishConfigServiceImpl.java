package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.AutoPublishConfigMapper;
import com.ruoyi.system.domain.AutoPublishConfig;
import com.ruoyi.system.service.IAutoPublishConfigService;

@Service
public class AutoPublishConfigServiceImpl implements IAutoPublishConfigService {
    @Autowired
    private AutoPublishConfigMapper mapper;

    @Override
    public List<AutoPublishConfig> selectAutoPublishList(AutoPublishConfig filter) {
        return mapper.selectAutoPublishList(filter);
    }

    @Override
    public AutoPublishConfig selectAutoPublishById(Long id) {
        return mapper.selectAutoPublishById(id);
    }

    @Override
    public int insertAutoPublish(AutoPublishConfig obj) {
        return mapper.insertAutoPublish(obj);
    }

    @Override
    public int updateAutoPublish(AutoPublishConfig obj) {
        return mapper.updateAutoPublish(obj);
    }

    @Override
    public int deleteAutoPublishByIds(Long[] ids) {
        int r = 0; for (Long id : ids) r += mapper.deleteAutoPublishById(id); return r;
    }
}
