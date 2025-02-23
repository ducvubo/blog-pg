package com.blog.blog_pg.repository;

import com.blog.blog_pg.entities.LabelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabelRepository extends JpaRepository<LabelEntity, UUID> {
    @Query("SELECT l FROM LabelEntity l WHERE (l.lb_name LIKE %:lbName%) AND (l.lb_res_id = :lbResId) AND (l.isDeleted = :isDeleted)")
    Page<LabelEntity> findByFilters(@Param("lbName") String lbName, @Param("lbResId") String lbResId,@Param("isDeleted") int isDeleted, Pageable pageable);

    //lấy tất cả các nhãn của một nhà hàng với điều kiện isDeleted = 0 và lb_res_id = :lbResId và lb_status = ENABLED
    @Query("SELECT l FROM LabelEntity l WHERE (l.lb_res_id = :lbResId) AND (l.isDeleted = 0) AND (l.lb_status = 'ENABLED')")
    List<LabelEntity> findAllByRestaurantId(@Param("lbResId") String lbResId);
}
