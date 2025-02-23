package com.blog.blog_pg.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IBackendRes<T> {
    private int statusCode;
    private String message;
    private T data;
}