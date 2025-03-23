package com.blog.blog_pg.listener;


import com.blog.blog_pg.entities.ArticleEntity;
import com.blog.blog_pg.utils.ElasticsearchUtils;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArticleEntityListener {

    private static final Logger log = LoggerFactory.getLogger(ArticleEntityListener.class);
    private static final String ARTICLE_ELASTICSEARCH_INDEX = "article-blog-pg";

    private static ElasticsearchUtils elasticsearchUtils;

    @Autowired
    public void setElasticsearchUtils(ElasticsearchUtils elasticsearchUtils) {
        ArticleEntityListener.elasticsearchUtils = elasticsearchUtils;
    }

    @PostPersist
    public void afterInsert(ArticleEntity article) {
        try {
            if (elasticsearchUtils != null) {
                elasticsearchUtils.addDocToElasticsearch(ARTICLE_ELASTICSEARCH_INDEX, article.getAtlId().toString(), article);
                log.info("Inserted ArticleEntity with ID {} synced to Elasticsearch", article.getAtlId());
            }
        } catch (Exception e) {
            log.error("Failed to sync inserted ArticleEntity to Elasticsearch: ", e);
        }
    }

    @PostUpdate
    public void afterUpdate(ArticleEntity article) {
        try {
            if (elasticsearchUtils != null) {
                elasticsearchUtils.updateDocByElasticsearch(ARTICLE_ELASTICSEARCH_INDEX, article.getAtlId().toString(), article);
                log.info("Updated ArticleEntity with ID {} synced to Elasticsearch", article.getAtlId());
            }
        } catch (Exception e) {
            log.error("Failed to sync updated ArticleEntity to Elasticsearch: ", e);
        }
    }
}
