package com.cshlands.controller;

import com.cshlands.dto.ArticleDTO;
import com.cshlands.pojo.ArticleState;
import com.cshlands.pojo.Result;
import com.cshlands.service.ArticleService;
import com.cshlands.vo.ArticleVO;
import com.cshlands.vo.PageArticleVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    public ResponseEntity<ArticleVO> addArticle(@RequestBody @Validated ArticleDTO dto) {
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

    @GetMapping("/{id}")
    public Result<ArticleVO> getArticleById(@PathVariable Integer id) {
        ArticleVO article = articleService.getArticle(id);
        return Result.success(article);
    }

    @PutMapping("/{id}")
    public Result<ArticleVO> updateArticle(@PathVariable Integer id, @RequestBody @Validated ArticleDTO dto) {
        ArticleVO article = articleService.updateArticle(id, dto);
        return Result.success(article);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticleById(@PathVariable Integer id) {
        articleService.deleteArticle(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
