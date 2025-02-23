package com.blog.blog_pg.repository;

import com.blog.blog_pg.dto.response.category.CategoryName;
import com.blog.blog_pg.entities.CategoryEntity;
import com.blog.blog_pg.enums.EnumStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    @Query("SELECT l FROM CategoryEntity l WHERE (l.catName LIKE %:catName%) AND (l.catResId = :catResId) AND (l.isDeleted = :isDeleted)")
    Page<CategoryEntity> findByFilters(@Param("catName") String catName, @Param("catResId") String catResId, @Param("isDeleted") int isDeleted, Pageable pageable);

    @Query("SELECT new com.blog.blog_pg.dto.response.category.CategoryName(l.catName, l.catId) FROM CategoryEntity l WHERE (l.catResId = :catResId) AND (l.isDeleted = :isDeleted) AND (l.catStatus = :catStatus)")
    List<CategoryName> findByCatResIdAndIsDeletedAndCatStatus(@Param("catResId") String catResId, @Param("isDeleted") int isDeleted, @Param("catStatus") EnumStatus catStatus);
}
