package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.VideoClipConfigMapper;
import com.ruoyi.system.domain.VideoClipConfig;
import com.ruoyi.system.service.IVideoClipConfigService;

@Service
public class VideoClipConfigServiceImpl implements IVideoClipConfigService {
    @Autowired
    private VideoClipConfigMapper mapper;

    @Override
    public List<VideoClipConfig> selectVideoClipList(VideoClipConfig filter) {
        return mapper.selectVideoClipList(filter);
    }

    @Override
    public VideoClipConfig selectVideoClipById(Long id) {
        return mapper.selectVideoClipById(id);
    }

    @Override
    public int insertVideoClip(VideoClipConfig obj) {
        return mapper.insertVideoClip(obj);
    }

    @Override
    public int updateVideoClip(VideoClipConfig obj) {
        return mapper.updateVideoClip(obj);
    }

    @Override
    public int deleteVideoClipByIds(Long[] ids) {
        int r = 0;
        for (Long id : ids) r += mapper.deleteVideoClipById(id);
        return r;
    }
}
