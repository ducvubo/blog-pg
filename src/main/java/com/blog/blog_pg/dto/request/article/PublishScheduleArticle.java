package com.blog.blog_pg.dto.request.article;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishScheduleArticle {
    @NotBlank(message = "ID bài viết không được để trống")
    private String atl_id;

    @NotBlank(message = "Thời gian không được để trống")
    private String atl_publish_schedule;
}
