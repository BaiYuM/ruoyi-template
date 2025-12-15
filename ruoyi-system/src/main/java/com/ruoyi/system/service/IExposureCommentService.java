package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;

public interface IExposureCommentService {
    int addCommentSegment(Long exposureLogId, int segmentIndex, String content);

    List<Map<String, Object>> getComments(Long exposureLogId);
}
