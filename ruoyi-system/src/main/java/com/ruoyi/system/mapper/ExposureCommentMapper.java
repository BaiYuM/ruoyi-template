package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ExposureEvent;
import java.util.List;
import java.util.Map;

public interface ExposureCommentMapper {
    int insertCommentSegment(Map<String, Object> params);

    List<Map<String, Object>> selectCommentsByLogId(Long exposureLogId);
}
