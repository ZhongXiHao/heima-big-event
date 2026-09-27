package com.cshlands.mapper;

import com.cshlands.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUserName(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Integer id);

    @Insert("insert into user(username, password, create_time, update_time)" +
            " values(#{username}, #{password}, #{createTime}, #{updateTime})")
//    获取id、created_at、updated_at的值，并设置到user对象中
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void register(User user);

    @Update("update user " +
            "set nickname = #{nickname}, email = #{email}, user_pic = #{userPic}, update_time = #{updateTime} " +
            "where username = #{username}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void updateUserInfo(User user);

    @Update("update user set user_pic = #{userPic}, update_time = #{updateTime}" +
            " where username = #{username}")
    void updateUserAvatar(User user);

    @Update("update user set password = #{password}, update_time = #{updateTime}" +
            " where username = #{username}")
    void updateUserPassword(User user);
}
