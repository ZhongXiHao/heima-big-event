package com.cshlands.controller;

import com.cshlands.dto.ArticleListDTO;
import com.cshlands.dto.CreateArticleDTO;
import com.cshlands.pojo.Article;
import com.cshlands.pojo.ArticleState;
import com.cshlands.pojo.Result;
import com.cshlands.service.ArticleService;
import com.cshlands.vo.ArticleVO;
import com.cshlands.vo.PageArticleVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    public ResponseEntity<ArticleVO> addArticle(@RequestBody @Validated CreateArticleDTO dto) {
        ArticleVO articleVO = articleService.addArticle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(articleVO);
    }

    @GetMapping
    public Result<PageArticleVO> getArticles(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) ArticleState state) {
        PageArticleVO articles = articleService.getArticles(pageNum, pageSize, categoryId, state);
        return Result.success(articles);
    }
}
