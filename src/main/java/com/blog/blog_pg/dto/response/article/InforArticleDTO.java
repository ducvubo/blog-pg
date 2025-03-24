package com.blog.blog_pg.dto.response.article;


import com.blog.blog_pg.enums.ArticleStatus;
import com.blog.blog_pg.enums.ArticleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InforArticleDTO {
    private String atlId;

    private String catId;

    private String atlTitle;

    private String atlDescription;

    private String atlSlug;

    private String atlImage;

    private ArticleType atlType;

    private String atlContent;

    private Number atlPublishedTime;

    private int atlView;

    private List<String> listArticleRelated;
}
