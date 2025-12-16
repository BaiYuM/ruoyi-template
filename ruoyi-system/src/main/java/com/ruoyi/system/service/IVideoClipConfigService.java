package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.VideoClipConfig;

/**
 * 视频剪辑配置 Service 接口
 */
public interface IVideoClipConfigService {
    /**
     * 查询视频剪辑配置列表
     * @param filter 过滤条件
     * @return 列表
     */
    public List<VideoClipConfig> selectVideoClipList(VideoClipConfig filter);

    /**
     * 根据 ID 查询视频剪辑配置
     * @param id 主键
     * @return VideoClipConfig 或 null
     */
    public VideoClipConfig selectVideoClipById(Long id);

    /**
     * 新增视频剪辑配置
     * @param obj 实体
     * @return 受影响行数
     */
    public int insertVideoClip(VideoClipConfig obj);

    /**
     * 更新视频剪辑配置
     * @param obj 实体（含 id）
     * @return 受影响行数
     */
    public int updateVideoClip(VideoClipConfig obj);

    /**
     * 批量删除视频剪辑配置
     * @param ids 主键数组
     * @return 受影响行数
     */
    public int deleteVideoClipByIds(Long[] ids);
}
