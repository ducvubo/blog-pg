package com.blog.blog_pg.controller;


import com.blog.blog_pg.dto.request.category.CreateCategoryDto;
import com.blog.blog_pg.dto.request.category.UpdateCategoryDto;
import com.blog.blog_pg.dto.request.category.UpdateStatusCategoryDto;
import com.blog.blog_pg.dto.response.ApiResponse;
import com.blog.blog_pg.dto.response.ResPagination;
import com.blog.blog_pg.dto.response.category.CategoryName;
import com.blog.blog_pg.entities.CategoryEntity;
import com.blog.blog_pg.middleware.Account;
import com.blog.blog_pg.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    ApiResponse<CategoryEntity> createCategory(@Valid @RequestBody CreateCategoryDto createCategoryDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<CategoryEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Create category successfully")
                .data(categoryService.createCategory(createCategoryDto,account))
                .build();
    }

    @PatchMapping
    ApiResponse<CategoryEntity> updateCategory(@Valid @RequestBody UpdateCategoryDto updateCategoryDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<CategoryEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Update category successfully")
                .data(categoryService.updateCategory(updateCategoryDto,account))
                .build();
    }

    @GetMapping
    ApiResponse<ResPagination<CategoryEntity>> getCategorys(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                      @RequestParam(value = "catName", required = false) String catName) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ResPagination<CategoryEntity>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get categorys successfully")
                .data(categoryService.getAllCategory(pageIndex, pageSize, catName, account))
                .build();
    }

    @GetMapping("all")
    ApiResponse<List<CategoryName>> getAllCategorys() {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<List<CategoryName>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get all categorys successfully")
                .data(categoryService.getAllCategoryByRestaurantId(account))
                .build();
    }

    @GetMapping("recycle")
    ApiResponse<ResPagination<CategoryEntity>> getRecycleCategorys(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                             @RequestParam(value = "catName", required = false) String catName) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ResPagination<CategoryEntity>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get recycle categorys successfully")
                .data(categoryService.getAllCategoryRecycleBin( pageIndex, pageSize, catName, account))
                .build();
    }

    @PatchMapping("/update-status")
    ApiResponse<CategoryEntity> updateStatus(@Valid @RequestBody UpdateStatusCategoryDto updateStatusCategoryDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<CategoryEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Update status category successfully")
                .data(categoryService.updateStatus(updateStatusCategoryDto, account))
                .build();
    }

    @PatchMapping("/restore/{catId}")
    ApiResponse<CategoryEntity> restoreCategory(@PathVariable String catId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<CategoryEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Restore category successfully")
                .data(categoryService.restoreCategory(catId, account))
                .build();
    }

    @DeleteMapping("/{catId}")
    ApiResponse<CategoryEntity> deleteCategory(@PathVariable String catId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<CategoryEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Delete category successfully")
                .data(categoryService.deleteCategory(catId, account))
                .build();
    }

    @GetMapping("/{catId}")
    ApiResponse<CategoryEntity> getCategory(@PathVariable String catId) {
        return ApiResponse.<CategoryEntity>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get category successfully")
                .data(categoryService.getCategory(catId))
                .build();
    }
}
