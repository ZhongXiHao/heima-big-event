package com.cshlands.service;

import com.cshlands.dto.CreateArticleDTO;
import com.cshlands.pojo.Article;
import com.cshlands.pojo.ArticleState;
import com.cshlands.vo.ArticleVO;
import com.cshlands.vo.PageArticleVO;

import java.util.List;

public interface ArticleService {

    ArticleVO addArticle(CreateArticleDTO dto);

    PageArticleVO getArticles(Integer pageNum, Integer pageSize, Integer categoryId, ArticleState state);
}
