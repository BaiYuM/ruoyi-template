package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.AiExpertConfigMapper;
import com.ruoyi.system.domain.AiExpertConfig;
import com.ruoyi.system.service.IAiExpertConfigService;

@Service
public class AiExpertConfigServiceImpl implements IAiExpertConfigService {
    @Autowired
    private AiExpertConfigMapper mapper;

    @Override
    public List<AiExpertConfig> selectAiList(AiExpertConfig filter) {
        return mapper.selectAiList(filter);
    }

    @Override
    public AiExpertConfig selectAiById(Long id) { return mapper.selectAiById(id); }

    @Override
    public int insertAi(AiExpertConfig obj) { return mapper.insertAi(obj); }

    @Override
    public int updateAi(AiExpertConfig obj) { return mapper.updateAi(obj); }

    @Override
    public int deleteAiByIds(Long[] ids) { int r=0; for(Long id:ids) r+=mapper.deleteAiById(id); return r; }
}
