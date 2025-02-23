package com.blog.blog_pg.dto.request.article;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArticleDefaultDto extends CreateArticleDefaultDto {
    @NotBlank(message = "ID bài viết không được để trống")
    private String atlId;
}
