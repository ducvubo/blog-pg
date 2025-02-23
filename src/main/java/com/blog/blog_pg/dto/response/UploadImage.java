package com.blog.blog_pg.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadImage {
    private String image_cloud;
    private String image_custom;
}
