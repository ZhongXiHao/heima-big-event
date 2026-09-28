package com.cshlands.service;

import com.cshlands.dto.ArticleDTO;
import com.cshlands.pojo.ArticleState;
import com.cshlands.vo.ArticleVO;
import com.cshlands.vo.PageArticleVO;

public interface ArticleService {

    ArticleVO addArticle(ArticleDTO dto);

    PageArticleVO getArticles(Integer pageNum, Integer pageSize, Integer categoryId, ArticleState state);

    ArticleVO getArticle(Integer articleId);

    ArticleVO updateArticle(Integer articleId, ArticleDTO dto);

    void deleteArticle(Integer articleId);
}
