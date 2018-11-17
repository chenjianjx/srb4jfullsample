package com.github.chenjianjx.srb4jfullsample.impl.biz.staff;


import com.github.chenjianjx.srb4jfullsample.impl.biz.user.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

/**
 * @author chenjianjx@gmail.com
 */


@Repository
public interface StaffUserRepo {

    @Insert("insert into StaffUser(username, password, lastLoginDate, createdAt, createdBy) "
            + "values (#{username}, #{password}, #{lastLoginDate}, #{createdAt}, #{createdBy})")
    @SelectKey(statement = "select last_insert_id() as id", keyProperty = "id", keyColumn = "id", before = false, resultType = long.class)
    public void saveNewStaffUser(StaffUser staffUser);

    @Select("select * from StaffUser where  username = #{username}")
    public StaffUser getStaffUserByUsername(String username);

    @Select("select * from StaffUser where  id = #{id}")
    public StaffUser getStaffUserById(long id);

    @Update("update StaffUser set password = #{password}, lastLoginDate = #{lastLoginDate}, updatedBy = #{updatedBy}, updatedAt = #{updatedAt}  where id = #{id}")
    public void updateStaffUser(StaffUser newStaffUser);

}
