package com.blog.blog_pg.dto.request.article;

import com.blog.blog_pg.models.ArticleContent.ArticleDocumentModel;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateArticleDocumentDto {
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

    @NotBlank(message = "Nội dung không được để trống")
    private ArticleDocumentModel articleDocumentModel;
}
