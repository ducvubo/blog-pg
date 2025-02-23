package com.blog.blog_pg.models.ArticleContent;

import com.blog.blog_pg.enums.VideoArticleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVideoModel {
    private VideoArticleType videoArticleType;
    private String contentVideo;
    private String description;
}
