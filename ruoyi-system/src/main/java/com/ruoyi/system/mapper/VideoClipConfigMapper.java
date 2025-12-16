package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.ruoyi.system.domain.VideoClipConfig;

@Mapper
public interface VideoClipConfigMapper {
    @Select("SELECT * FROM video_clip_config WHERE id = #{id}")
    public VideoClipConfig selectVideoClipById(Long id);

    @Select("SELECT * FROM video_clip_config")
    public List<VideoClipConfig> selectVideoClipList(VideoClipConfig filter);

    @Insert("INSERT INTO video_clip_config(enabled, material_list, product_name, product_feature, clip_count, video_length_min, video_length_max, clip_time, aspect_ratio, resolution, status, create_by, create_time) VALUES (#{enabled}, #{materialList}, #{productName}, #{productFeature}, #{clipCount}, #{videoLengthMin}, #{videoLengthMax}, #{clipTime}, #{aspectRatio}, #{resolution}, #{status}, #{createBy}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insertVideoClip(VideoClipConfig obj);

    @Update("UPDATE video_clip_config SET enabled=#{enabled}, material_list=#{materialList}, product_name=#{productName}, product_feature=#{productFeature}, clip_count=#{clipCount}, video_length_min=#{videoLengthMin}, video_length_max=#{videoLengthMax}, clip_time=#{clipTime}, aspect_ratio=#{aspectRatio}, resolution=#{resolution}, status=#{status}, create_by=#{createBy}, create_time=#{createTime} WHERE id=#{id}")
    public int updateVideoClip(VideoClipConfig obj);

    @Delete("DELETE FROM video_clip_config WHERE id = #{id}")
    public int deleteVideoClipById(Long id);

    @Delete({"<script>", "DELETE FROM video_clip_config WHERE id IN", "<foreach collection='array' item='id' open='(' separator=',' close=')'>#{id}</foreach>", "</script>"})
    public int deleteVideoClipByIds(Long[] ids);
}
