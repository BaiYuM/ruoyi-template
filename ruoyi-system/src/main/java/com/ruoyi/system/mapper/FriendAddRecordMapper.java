package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.ruoyi.system.domain.FriendAddRecord;

@Mapper
public interface FriendAddRecordMapper {
    @Select("SELECT * FROM friend_add_record WHERE id = #{id}")
    public FriendAddRecord selectRecordById(Long id);

    @Select("SELECT * FROM friend_add_record")
    public List<FriendAddRecord> selectRecordList(FriendAddRecord filter);

    @Insert("INSERT INTO friend_add_record(plan_id, target_account, result, create_by, create_time) VALUES (#{planId}, #{targetAccount}, #{result}, #{createBy}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insertRecord(FriendAddRecord obj);

    @Update("UPDATE friend_add_record SET plan_id=#{planId}, target_account=#{targetAccount}, result=#{result}, create_by=#{createBy}, create_time=#{createTime} WHERE id=#{id}")
    public int updateRecord(FriendAddRecord obj);

    @Delete("DELETE FROM friend_add_record WHERE id = #{id}")
    public int deleteRecordById(Long id);

    @Delete({"<script>", "DELETE FROM friend_add_record WHERE id IN", "<foreach collection='array' item='id' open='(' separator=',' close=')'>#{id}</foreach>", "</script>"})
    public int deleteRecordByIds(Long[] ids);
}
