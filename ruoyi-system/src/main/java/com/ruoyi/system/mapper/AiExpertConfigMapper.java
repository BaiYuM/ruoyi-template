package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.ruoyi.system.domain.AiExpertConfig;

@Mapper
public interface AiExpertConfigMapper {
    @Select("SELECT * FROM ai_expert_config WHERE id = #{id}")
    public AiExpertConfig selectAiById(Long id);

    @Select("SELECT * FROM ai_expert_config")
    public List<AiExpertConfig> selectAiList(AiExpertConfig filter);

    @Insert("INSERT INTO ai_expert_config(enabled, robot_list, remark, create_by, create_time) VALUES (#{enabled}, #{robotList}, #{remark}, #{createBy}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insertAi(AiExpertConfig obj);

    @Update("UPDATE ai_expert_config SET enabled=#{enabled}, robot_list=#{robotList}, remark=#{remark}, create_by=#{createBy}, create_time=#{createTime} WHERE id=#{id}")
    public int updateAi(AiExpertConfig obj);

    @Delete("DELETE FROM ai_expert_config WHERE id = #{id}")
    public int deleteAiById(Long id);

    @Delete({"<script>", "DELETE FROM ai_expert_config WHERE id IN", "<foreach collection='array' item='id' open='(' separator=',' close=')'>#{id}</foreach>", "</script>"})
    public int deleteAiByIds(Long[] ids);
}
