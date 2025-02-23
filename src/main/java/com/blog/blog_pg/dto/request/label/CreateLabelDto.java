package com.blog.blog_pg.dto.request.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLabelDto {
    @NotBlank(message = "Tên nhãn không được để trống")
    @Size(min = 1, max = 255, message = "Tên nhãn phải từ 1 đến 255 ký tự")
    private String lb_name;

    @NotBlank(message = "Mô tả nhãn không được để trống")
    @Size(max = 255, message = "Mô tả nhãn không quá 255 ký tự")
    private String lb_description;

    @NotBlank(message = "Màu nhãn không được để trống")
    @Size(max = 50, message = "Màu nhãn không quá 50 ký tự")
    private String lb_color;
}
