package com.cshlands.mapper;

import com.cshlands.pojo.Article;
import com.cshlands.pojo.ArticleState;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {

    List<Article> selectArticles(Integer userId, Integer categoryId, ArticleState state);

    @Insert("insert into article (title, content, cover_img, state, category_id, create_user, create_time, update_time) " +
            "values (#{title}, #{content}, #{coverImg}, #{state}, #{categoryId}, #{createUser}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Article article);

    @Select("select id, title, content, cover_img, state, category_id, create_time, update_time " +
            "from article where id = #{id} and create_user = #{userId}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Article selectByIdAndUser(Integer id, Integer userId);

    @Update("update article set title = #{title}, content = #{content}, cover_img = #{coverImg}, state = #{state}, category_id = #{categoryId}, update_time = #{updateTime} " +
            "where id = #{id} and create_user = #{createUser}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void updateArticle(Article article);

    @Select("select count(*) from article where category_id = #{categoryId}")
    Integer countByCategoryId(Integer categoryId);

    @Delete("delete from article where id = #{id} and create_user = #{userId}")
    void deleteByIdAndUser(Integer id, Integer userId);
}
