package com.blog.blog_pg.dto.request.article;

import com.blog.blog_pg.models.ArticleContent.ArticleVideoModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateArticleVideoDto {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String atlTitle;

    @NotBlank(message = "Danh mục không được để trống")
    private String catId;

    @NotBlank(message = "Slug không được để trống")
    private String atlSlug;

    private String atlDescription;

    private String atlImage;


    private List<String> listArticleRelated;

    private List<String> listArticleNote;

    @NotNull(message = "Nội dung không được để trống")
    private ArticleVideoModel articleVideoModel;
}
