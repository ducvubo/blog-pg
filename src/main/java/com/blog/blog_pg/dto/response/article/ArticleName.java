package com.blog.blog_pg.dto.response.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleName {
    private UUID atlId;
    private String atlTitle;
    private String atlImage;
}
