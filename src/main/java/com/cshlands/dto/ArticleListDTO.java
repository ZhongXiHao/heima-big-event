package com.cshlands.dto;

import com.cshlands.pojo.ArticleState;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArticleListDTO {
    private Integer pageNum;
    private Integer pageSize;
    private Integer categoryId;
    private ArticleState articleState;
}
