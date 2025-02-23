package com.blog.blog_pg.models.ArticleContent;

import com.blog.blog_pg.enums.DocumentArticleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDocumentModel {
    private DocumentArticleType documentType;
    private String documentUrl;
    private String documentName;
    private String documentDescription;
}
