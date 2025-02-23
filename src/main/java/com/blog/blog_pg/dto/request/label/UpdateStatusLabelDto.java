package com.blog.blog_pg.dto.request.label;

import com.blog.blog_pg.enums.EnumStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusLabelDto {
    @NotBlank(message = "ID nhãn không được để trống")
    private String lb_id;

    @NotNull(message = "Trạng thái nhãn không được để trống")
    private EnumStatus lb_status;
}
