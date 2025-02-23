package com.blog.blog_pg.dto.request.category;

import com.blog.blog_pg.enums.EnumStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusCategoryDto {
    @NotBlank(message = "ID danh mục không được để trống")
    private String catId;

    @NotNull(message = "Trạng thái danh mục không được để trống")
    private EnumStatus catStatus;
}
