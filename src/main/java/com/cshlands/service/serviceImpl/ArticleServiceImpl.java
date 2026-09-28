package com.cshlands.service.serviceImpl;

import com.cshlands.dto.ArticleDTO;
import com.cshlands.exception.BusinessException;
import com.cshlands.mapper.ArticleMapper;
import com.cshlands.mapper.CategoryMapper;
import com.cshlands.pojo.Article;
import com.cshlands.pojo.ArticleState;
import com.cshlands.pojo.Category;
import com.cshlands.service.ArticleService;
import com.cshlands.utils.ThreadLocalUtil;
import com.cshlands.vo.ArticleVO;
import com.cshlands.vo.PageArticleVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper, CategoryMapper categoryMapper) {
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public ArticleVO addArticle(ArticleDTO dto) {
        Integer userId = getCurrentUserId();
        Integer categoryId = dto.getCategoryId();
        getOwnedCategoryOrThrow(categoryId, userId);

        // 使用 DTO 中的内容创建一个新的 Article 实例
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setCoverImg(dto.getCoverImg());
        article.setCategoryId(categoryId);
        article.setCreateUser(userId);
        article.setCreateTime(LocalDateTime.now().withNano(0));
        article.setUpdateTime(LocalDateTime.now().withNano(0));
        article.setState(dto.getState());
        articleMapper.insert(article);
        return toVO(article);
    }


    @Override
    public PageArticleVO getArticles(Integer pageNum, Integer pageSize, Integer categoryId, ArticleState state) {
        Integer userId = getCurrentUserId();
        PageArticleVO articlesVO = new PageArticleVO();
        try (Page<Object> p = PageHelper.startPage(pageNum, pageSize)) {
            List<Article> articles = articleMapper.selectArticles(userId, categoryId, state);

            PageInfo<Article> pageInfo = new PageInfo<>(articles);
            articlesVO.setTotal(pageInfo.getTotal());
            articlesVO.setItems(pageInfo.getList().stream().map(this::toVO).toList());

            return articlesVO;
        }
    }

    @Override
    public ArticleVO getArticle(Integer articleId) {
        Integer userId = getCurrentUserId();
        Article article = getOwnedArticleOrThrow(articleId, userId);
        return toVO(article);
    }

    @Override
    public ArticleVO updateArticle(Integer articleId, ArticleDTO dto) {
        Integer userId = getCurrentUserId();
        Article article = getOwnedArticleOrThrow(articleId, userId);
        article.setCreateUser(userId);
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setCoverImg(dto.getCoverImg());
        article.setUpdateTime(LocalDateTime.now().withNano(0));
        article.setState(dto.getState());
        articleMapper.updateArticle(article);
        return toVO(article);
    }

    @Override
    public void deleteArticle(Integer articleId) {
        Integer userId = getCurrentUserId();
        getOwnedArticleOrThrow(articleId, userId);
        articleMapper.deleteByIdAndUser(articleId, userId);
    }

    private Integer getCurrentUserId() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        return ((Number) claims.get("id")).intValue();
    }

    private Category getOwnedCategoryOrThrow(Integer categoryId, Integer userId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null || !category.getCreateUser().equals(userId)) {
            throw BusinessException.notFound("分类不存在");
        }
        return category;
    }

    private ArticleVO toVO(Article article) {
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        return articleVO;
    }

    private Article getOwnedArticleOrThrow(Integer articleId, Integer userId) {
        Article article = articleMapper.selectByIdAndUser(articleId, userId);
        if (article == null) {
            throw BusinessException.notFound("文章不存在");
        }
        return article;
    }


}
