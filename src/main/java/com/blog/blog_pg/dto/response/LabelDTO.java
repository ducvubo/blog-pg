package com.blog.blog_pg.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelDTO {
    private UUID lb_id;
    private String lb_name;
    private String lb_color;
}
