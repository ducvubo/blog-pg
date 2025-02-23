package com.blog.blog_pg.models.ArticleContent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleImageModel {
    private String imageLink;
    private String imageName;
    private String imageDescription;
    private String Id;


}
