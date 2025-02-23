package com.blog.blog_pg.controller;

import com.blog.blog_pg.dto.response.ApiResponse;
import com.blog.blog_pg.exception.BadRequestError;
import com.blog.blog_pg.middleware.Account;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Test {

    @GetMapping("/no-authen")
    public ApiResponse<String> testNoAuthen() {
        return ApiResponse.<String>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Hello World No Authen")
                .data("no-authen")
                .build();
    }

    @GetMapping("/authen")
    public ApiResponse<Account> testAuthen() {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<Account>builder()
                .statusCode(10001)
                .message("Hello World Authen")
                .data(account)
                .build();
    }

    @GetMapping("/test-exception")
    public ApiResponse<Account> testException() {
        throw new BadRequestError("Test exception");
    }
}
