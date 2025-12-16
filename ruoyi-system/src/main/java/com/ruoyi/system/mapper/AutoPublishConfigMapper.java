package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.ruoyi.system.domain.AutoPublishConfig;

@Mapper
public interface AutoPublishConfigMapper {
    @Select("SELECT * FROM auto_publish_config WHERE id = #{id}")
    public AutoPublishConfig selectAutoPublishById(Long id);

    @Select("SELECT * FROM auto_publish_config")
    public List<AutoPublishConfig> selectAutoPublishList(AutoPublishConfig filter);

    @Insert("INSERT INTO auto_publish_config(enabled, account_list, product_intro, schedule, status, create_by, create_time) VALUES (#{enabled}, #{accountList}, #{productIntro}, #{schedule}, #{status}, #{createBy}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insertAutoPublish(AutoPublishConfig obj);

    @Update("UPDATE auto_publish_config SET enabled=#{enabled}, account_list=#{accountList}, product_intro=#{productIntro}, schedule=#{schedule}, status=#{status}, create_by=#{createBy}, create_time=#{createTime} WHERE id=#{id}")
    public int updateAutoPublish(AutoPublishConfig obj);

    @Delete("DELETE FROM auto_publish_config WHERE id = #{id}")
    public int deleteAutoPublishById(Long id);

    @Delete({"<script>", "DELETE FROM auto_publish_config WHERE id IN", "<foreach collection='array' item='id' open='(' separator=',' close=')'>#{id}</foreach>", "</script>"})
    public int deleteAutoPublishByIds(Long[] ids);
}
