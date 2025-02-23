package com.blog.blog_pg.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryDto extends  CreateCategoryDto{
    @NotBlank(message = "ID danh mục không được để trống")
    private String catId;
}
