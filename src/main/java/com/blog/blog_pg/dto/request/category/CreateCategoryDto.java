package com.blog.blog_pg.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryDto {
    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 3, max = 255, message = "Tên danh mục phải từ 3 đến 255 ký tự")
    private String catName;

    @NotBlank(message = "Mô tả danh mục không được để trống")
    @Size(max = 255, message = "Mô tả danh mục không được quá 255 ký tự")
    private String catDescription;

    @NotNull(message = "Số thứ tự danh mục không được để trống")
    private int catOrder;
}
