package com.blog.blog_pg.repository;

import com.blog.blog_pg.dto.response.article.ArticleName;
import com.blog.blog_pg.entities.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, UUID> {
    Boolean existsByAtlSlugAndAtlResId(String atl_slug, String atl_res_id);

    @Query("SELECT l FROM ArticleEntity l WHERE (l.atlSlug = :atlSlug) AND (l.atlResId = :atlResId)")
    List<ArticleEntity> findByAtlSlug(@Param("atlSlug") String atlSlug, @Param("atlResId") String atlResId);

    @Query("SELECT l FROM ArticleEntity l WHERE (l.atlTitle LIKE %:atlTitle%) AND (l.atlResId = :atlResId) AND (l.isDeleted = :isDeleted) ORDER BY l.updatedAt DESC")
    Page<ArticleEntity> findByFilters(@Param("atlTitle") String atlTitle, @Param("atlResId") String atlResId, @Param("isDeleted") int isDeleted, Pageable pageable);

    @Query("SELECT new com.blog.blog_pg.dto.response.article.ArticleName(l.atlId,l.atlTitle,l.atlImage) FROM ArticleEntity l WHERE (l.atlResId = :atlResId) AND (l.isDeleted = :isDeleted)")
    List<ArticleName> findByAtlResId(@Param("atlResId") String atlResId, @Param("isDeleted") int isDeleted);

    @Transactional
    @Modifying
    @Query("UPDATE ArticleEntity l SET l.atlView = l.atlView + 1 WHERE l.atlId = :atlId")
    void updateView(@Param("atlId") UUID atlId);

//    countTotalViewByRestaurantId
    @Query("SELECT SUM(l.atlView) FROM ArticleEntity l WHERE l.atlResId = :atlResId AND l.isDeleted = 0")
    Integer countTotalViewByRestaurantId(@Param("atlResId") String atlResId);

    @Query("SELECT l FROM ArticleEntity l WHERE l.atlResId = :atlResId AND l.isDeleted = 0 ORDER BY l.atlView DESC")
    List<ArticleEntity> findTop5ByAtlResIdOrderByAtlViewDesc(@Param("atlResId") String atlResId, Pageable pageable);
}
