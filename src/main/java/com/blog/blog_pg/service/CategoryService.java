package com.blog.blog_pg.service;

import com.blog.blog_pg.dto.request.category.CreateCategoryDto;
import com.blog.blog_pg.dto.request.category.UpdateCategoryDto;
import com.blog.blog_pg.dto.request.category.UpdateStatusCategoryDto;
import com.blog.blog_pg.dto.response.ResPagination;
import com.blog.blog_pg.dto.response.category.CategoryDTO;
import com.blog.blog_pg.dto.response.category.CategoryName;
import com.blog.blog_pg.entities.CategoryEntity;
import com.blog.blog_pg.middleware.Account;

import java.util.List;

public interface CategoryService {
    CategoryEntity createCategory(CreateCategoryDto createCategoryDto, Account account);
    CategoryEntity getCategory(String id);
    CategoryEntity updateCategory(UpdateCategoryDto updateCategoryDto, Account account);
    CategoryEntity deleteCategory(String id, Account account);
    CategoryEntity restoreCategory(String id, Account account);
    CategoryEntity updateStatus(UpdateStatusCategoryDto updateStatusCategoryDto, Account account);
    ResPagination<CategoryEntity> getAllCategory(int pageIndex , int pageSize, String catName, Account account);
    ResPagination<CategoryEntity> getAllCategoryRecycleBin(int pageIndex , int pageSize, String catName, Account account);
    List<CategoryName> getAllCategoryByRestaurantId(Account account);

    List<CategoryDTO> getCategoryAllView(String catResId);
}
