package com.cshlands.mapper;

import com.cshlands.pojo.Article;
import com.cshlands.pojo.ArticleState;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper {

    List<Article> selectArticles(Integer userId, Integer categoryId, ArticleState state);

    @Insert("insert into article (title, content, cover_img, state, category_id, create_user, create_time, update_time) " +
            "values (#{title}, #{content}, #{coverImg}, #{state}, #{categoryId}, #{createUser}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Article article);

}
