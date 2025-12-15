package com.ruoyi.system.service.impl;

import com.ruoyi.system.mapper.ExposureCommentMapper;
import com.ruoyi.system.service.IExposureCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExposureCommentServiceImpl implements IExposureCommentService {

    @Autowired
    private ExposureCommentMapper commentMapper;

    @Override
    public int addCommentSegment(Long exposureLogId, int segmentIndex, String content) {
        Map<String, Object> params = new HashMap<>();
        params.put("exposureLogId", exposureLogId);
        params.put("segmentIndex", segmentIndex);
        params.put("content", content);
        params.put("createTime", new java.util.Date());
        return commentMapper.insertCommentSegment(params);
    }

    @Override
    public List<Map<String, Object>> getComments(Long exposureLogId) {
        return commentMapper.selectCommentsByLogId(exposureLogId);
    }
}
