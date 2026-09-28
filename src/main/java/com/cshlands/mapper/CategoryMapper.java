package com.cshlands.mapper;

import com.cshlands.pojo.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Insert("insert into category (category_name,category_alias,create_user,create_time,update_time) " +
            "values (#{categoryName},#{categoryAlias},#{createUser},#{createTime},#{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    @Select("select * from category where create_user = #{userId}")
    List<Category> selectAll(Integer userId);

    @Select("select * from category where id = #{categoryId}")
    Category selectById(Integer categoryId);

    @Update("update category set category_name = #{categoryName}, category_alias = #{categoryAlias}, update_time = #{updateTime} where id = #{id}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void updateById(Category category);

    @Delete("delete from category where id = #{categoryId}")
    void deleteById(Integer categoryId);
}

