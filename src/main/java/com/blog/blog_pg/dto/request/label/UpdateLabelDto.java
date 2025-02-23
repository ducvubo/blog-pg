package com.blog.blog_pg.dto.request.label;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLabelDto extends CreateLabelDto {
    @NotBlank(message = "ID nhãn không được để trống")
    private String lb_id;
}
