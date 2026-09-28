package com.cshlands.dto;


import com.cshlands.pojo.ArticleState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
public class CreateArticleDTO {
    @NotBlank
    @Pattern(regexp = "^\\S{1,10}", message = "标题必须是1~10个非空字符")
    private String title; //1~10个非空字符

    @NotBlank(message = "文章内容不能为空")
    private String content;

    @NotBlank(message = "封面图片不能为空")
    @URL(message = "封面图片必须是合法的URL地址")
    private String coverImg; //必须是url地址

    @NotNull(message = "发布状态不能为空")
    private ArticleState state; // 已发布 | 草稿

    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;
}
