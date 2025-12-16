package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.ruoyi.system.domain.FriendAddPlan;

@Mapper
public interface FriendAddPlanMapper {
    @Select("SELECT * FROM friend_add_plan WHERE id = #{id}")
    public FriendAddPlan selectPlanById(Long id);

    @Select("SELECT * FROM friend_add_plan")
    public List<FriendAddPlan> selectPlanList(FriendAddPlan filter);

    @Insert("INSERT INTO friend_add_plan(name, target_count, completed_count, failed_count, status, create_by, create_time) VALUES (#{name}, #{targetCount}, #{completedCount}, #{failedCount}, #{status}, #{createBy}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insertPlan(FriendAddPlan obj);

    @Update("UPDATE friend_add_plan SET name=#{name}, target_count=#{targetCount}, completed_count=#{completedCount}, failed_count=#{failedCount}, status=#{status}, create_by=#{createBy}, create_time=#{createTime} WHERE id=#{id}")
    public int updatePlan(FriendAddPlan obj);

    @Delete("DELETE FROM friend_add_plan WHERE id = #{id}")
    public int deletePlanById(Long id);

    @Delete({"<script>", "DELETE FROM friend_add_plan WHERE id IN", "<foreach collection='array' item='id' open='(' separator=',' close=')'>#{id}</foreach>", "</script>"})
    public int deletePlanByIds(Long[] ids);
}
