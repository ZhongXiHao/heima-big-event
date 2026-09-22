package com.cshlands.mapper;

import com.cshlands.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUserName(String username);

    @Insert("insert into user(username, password, create_time, update_time)" +
            " values(#{username}, #{password}, #{createTime}, #{updateTime})")
//    获取id、created_at、updated_at的值，并设置到user对象中
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void register(User user);
}
