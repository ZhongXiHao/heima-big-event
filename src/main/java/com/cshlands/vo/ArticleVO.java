package com.cshlands.vo;

import com.cshlands.pojo.ArticleState;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ArticleVO {
    private Integer id;//主键ID
    private String title;//文章标题
    private String content;//文章内容
    private String coverImg;//封面图像
    private ArticleState state;//发布状态 已发布|草稿
    private Integer categoryId;//文章分类id
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
}
