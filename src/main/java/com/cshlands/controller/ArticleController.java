package com.cshlands.controller;

import com.cshlands.pojo.Article;
import com.cshlands.pojo.Result;
import com.cshlands.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    ArticleService articleService;

    @GetMapping
    public Result<List<Article>> list() {
        return Result.success(articleService.list());
    }
}
