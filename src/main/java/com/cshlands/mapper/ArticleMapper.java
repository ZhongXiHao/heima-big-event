package com.cshlands.mapper;

import com.cshlands.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper {
    @Select("")
    List<Article> selectAll();
}
