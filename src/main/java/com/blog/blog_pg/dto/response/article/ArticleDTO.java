package com.blog.blog_pg.dto.response.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO {
    private String atlId;
    private String atlTitle;
    private String atlSlug;
}
