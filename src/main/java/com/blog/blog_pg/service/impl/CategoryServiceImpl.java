package com.blog.blog_pg.service.impl;


import com.blog.blog_pg.dto.request.category.CreateCategoryDto;
import com.blog.blog_pg.dto.request.category.UpdateCategoryDto;
import com.blog.blog_pg.dto.request.category.UpdateStatusCategoryDto;
import com.blog.blog_pg.dto.response.MetaPagination;
import com.blog.blog_pg.dto.response.ResPagination;
import com.blog.blog_pg.dto.response.category.CategoryName;
import com.blog.blog_pg.entities.CategoryEntity;
import com.blog.blog_pg.enums.EnumStatus;
import com.blog.blog_pg.exception.BadRequestError;
import com.blog.blog_pg.middleware.Account;
import com.blog.blog_pg.repository.CategoryRepository;
import com.blog.blog_pg.service.CategoryService;
import com.blog.blog_pg.utils.AccountUtils;
import com.blog.blog_pg.utils.Slug;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.*;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryEntity createCategory(CreateCategoryDto createCategoryDto, Account account) {
        try{
            Random random = new Random();
            int number = 100000 + random.nextInt(900000);
            String slug = Slug.createSlug(createCategoryDto.getCatName()) + "-" + number;
            CategoryEntity categoryEntity = CategoryEntity.builder()
                    .catName(createCategoryDto.getCatName())
                    .catDescription(createCategoryDto.getCatDescription())
                    .catSlug(slug)
                    .catOrder(createCategoryDto.getCatOrder())
                    .catResId(account.getAccountRestaurantId())
                    .catStatus(EnumStatus.ENABLED)
                    .createdBy(AccountUtils.convertAccountToJson(account))
                    .updatedBy(AccountUtils.convertAccountToJson(account))
                    .isDeleted(0)
                    .build();

            categoryRepository.save(categoryEntity);
            return categoryEntity;
        }
        catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity getCategory(String id) {
        try {
            UUID cat_id = UUID.fromString(id);
            Optional<CategoryEntity> categoryEntity = categoryRepository.findById(cat_id);
            if(categoryEntity.isEmpty()){
                throw  new BadRequestError("Danh mục không tồn tại");
            }
            return categoryEntity.get();
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity updateCategory(UpdateCategoryDto updateCategoryDto, Account account) {
        UUID cat_id = UUID.fromString(updateCategoryDto.getCatId());
        Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(cat_id);
        if(categoryEntityOptional.isEmpty()){
            throw new BadRequestError("Danh mục không tồn tại");
        }
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        String slug = Slug.createSlug(updateCategoryDto.getCatName()) + "-" + number;
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        categoryEntity.setCatSlug(slug);
        categoryEntity.setCatOrder(updateCategoryDto.getCatOrder());
        categoryEntity.setCatName(updateCategoryDto.getCatName());
        categoryEntity.setCatDescription(updateCategoryDto.getCatDescription());
        categoryEntity.setCatOrder(updateCategoryDto.getCatOrder());
        categoryEntity.setUpdatedBy(AccountUtils.convertAccountToJson(account));
        categoryRepository.save(categoryEntity);
        return categoryEntity;
    }

    @Override
    public CategoryEntity deleteCategory(String id, Account account) {
        UUID cat_id = UUID.fromString(id);
        Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(cat_id);
        if(categoryEntityOptional.isEmpty()){
            throw new BadRequestError("Danh mục không tồn tại");
        }
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        categoryEntity.setIsDeleted(1);
        categoryEntity.setDeletedBy(AccountUtils.convertAccountToJson(account));
        categoryEntity.setDeletedAt(new Date(System.currentTimeMillis()));
        categoryRepository.save(categoryEntity);
        return categoryEntity;
    }

    @Override
    public CategoryEntity restoreCategory(String id, Account account) {
        UUID cat_id = UUID.fromString(id);
        Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(cat_id);
        if(categoryEntityOptional.isEmpty()){
            throw new BadRequestError("Danh mục không tồn tại");
        }
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        categoryEntity.setIsDeleted(0);
        categoryEntity.setDeletedBy(null);
        categoryEntity.setDeletedAt(null);
        categoryRepository.save(categoryEntity);
        return categoryEntity;
    }

    @Override
    public CategoryEntity updateStatus(UpdateStatusCategoryDto updateStatusCategoryDto, Account account) {
        UUID cat_id = UUID.fromString(updateStatusCategoryDto.getCatId());
        Optional<CategoryEntity> categoryEntityOptional = categoryRepository.findById(cat_id);
        if(categoryEntityOptional.isEmpty()){
            throw new BadRequestError("Danh mục không tồn tại");
        }
        CategoryEntity categoryEntity = categoryEntityOptional.get();
        categoryEntity.setCatStatus(updateStatusCategoryDto.getCatStatus());
        categoryEntity.setUpdatedBy(AccountUtils.convertAccountToJson(account));
        categoryRepository.save(categoryEntity);
        return categoryEntity;
    }

    @Override
    public ResPagination<CategoryEntity> getAllCategory(int pageIndex, int pageSize, String catName, Account account) {
        try {
            Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
            Page<CategoryEntity> categoryEntities = categoryRepository.findByFilters(catName, account.getAccountRestaurantId(), 0, pageable);

            return ResPagination.<CategoryEntity>builder()
                    .result(categoryEntities.getContent())
                    .meta(MetaPagination.builder()
                            .current(pageIndex)
                            .pageSize(pageSize)
                            .totalPage(categoryEntities.getTotalPages())
                            .totalItem(categoryEntities.getTotalElements())
                            .build())
                    .build();
        }catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResPagination<CategoryEntity> getAllCategoryRecycleBin(int pageIndex, int pageSize, String catName, Account account) {
        try {
            Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
            Page<CategoryEntity> categoryEntities = categoryRepository.findByFilters(catName, account.getAccountRestaurantId(), 1, pageable);

            return ResPagination.<CategoryEntity>builder()
                    .result(categoryEntities.getContent())
                    .meta(MetaPagination.builder()
                            .current(pageIndex)
                            .pageSize(pageSize)
                            .totalPage(categoryEntities.getTotalPages())
                            .totalItem(categoryEntities.getTotalElements())
                            .build())
                    .build();
        }catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryName> getAllCategoryByRestaurantId(Account account) {
        try {
            return categoryRepository.findByCatResIdAndIsDeletedAndCatStatus(account.getAccountRestaurantId(), 0,EnumStatus.ENABLED);
        }catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }
    }
}
