package com.blog.blog_pg.models.ArticleContent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListArticleImage {
    private List<ArticleImageModel> listArticleImage;
}
