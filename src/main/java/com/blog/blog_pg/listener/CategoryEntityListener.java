package com.blog.blog_pg.listener;

import com.blog.blog_pg.entities.CategoryEntity;
import com.blog.blog_pg.utils.ElasticsearchUtils;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityListener {

    private static final Logger log = LoggerFactory.getLogger(CategoryEntityListener.class);
    private static final String CATEGORY_BLOG_PG_ELASTICSEARCH_INDEX = "category-blog-pg";

    private static ElasticsearchUtils elasticsearchUtils;

    @Autowired
    public void setElasticsearchUtils(ElasticsearchUtils elasticsearchUtils) {
        CategoryEntityListener.elasticsearchUtils = elasticsearchUtils;
    }

    @PostPersist
    public void afterInsert(CategoryEntity category) {
        try {
            if (elasticsearchUtils != null) {
                elasticsearchUtils.addDocToElasticsearch(CATEGORY_BLOG_PG_ELASTICSEARCH_INDEX, category.getCatId().toString(), category);
                log.info("Inserted CategoryEntity with ID {} synced to Elasticsearch", category.getCatId());
            }
        } catch (Exception e) {
            log.error("Failed to sync inserted CategoryEntity to Elasticsearch: ", e);
        }
    }

    @PostUpdate
    public void afterUpdate(CategoryEntity category) {
        try {
            if (elasticsearchUtils != null) {
                    elasticsearchUtils.updateDocByElasticsearch(CATEGORY_BLOG_PG_ELASTICSEARCH_INDEX, category.getCatId().toString(), category);
                    log.info("Updated CategoryEntity with ID {} synced to Elasticsearch", category.getCatId());
            }
        } catch (Exception e) {
            log.error("Failed to sync updated CategoryEntity to Elasticsearch: ", e);
        }
    }
}