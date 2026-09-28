package com.cshlands.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PageArticleVO {
    private Long total;
    private List<ArticleVO> items;

}
