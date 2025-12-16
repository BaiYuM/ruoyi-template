package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.ruoyi.system.domain.FriendAutoApprove;

@Mapper
public interface FriendAutoApproveMapper {
    @Select("SELECT * FROM friend_auto_approve WHERE id = #{id}")
    public FriendAutoApprove selectAutoApproveById(Long id);

    @Select("SELECT * FROM friend_auto_approve")
    public List<FriendAutoApprove> selectAutoApproveList(FriendAutoApprove filter);

    @Insert("INSERT INTO friend_auto_approve(enabled, remark, send_welcome, create_by, create_time) VALUES (#{enabled}, #{remark}, #{sendWelcome}, #{createBy}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insertAutoApprove(FriendAutoApprove obj);

    @Update("UPDATE friend_auto_approve SET enabled=#{enabled}, remark=#{remark}, send_welcome=#{sendWelcome}, create_by=#{createBy}, create_time=#{createTime} WHERE id=#{id}")
    public int updateAutoApprove(FriendAutoApprove obj);

    @Delete("DELETE FROM friend_auto_approve WHERE id = #{id}")
    public int deleteAutoApproveById(Long id);

    @Delete({"<script>", "DELETE FROM friend_auto_approve WHERE id IN", "<foreach collection='array' item='id' open='(' separator=',' close=')'>#{id}</foreach>", "</script>"})
    public int deleteAutoApproveByIds(Long[] ids);
}
