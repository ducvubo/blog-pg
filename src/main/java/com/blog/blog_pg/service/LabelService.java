package com.blog.blog_pg.service;

import com.blog.blog_pg.dto.request.label.CreateLabelDto;
import com.blog.blog_pg.dto.request.label.UpdateLabelDto;
import com.blog.blog_pg.dto.request.label.UpdateStatusLabelDto;
import com.blog.blog_pg.dto.response.ResPagination;
import com.blog.blog_pg.entities.ArticleEntity;
import com.blog.blog_pg.entities.LabelEntity;
import com.blog.blog_pg.middleware.Account;

import java.util.List;
import java.util.UUID;

public interface LabelService {
    LabelEntity createLabel(CreateLabelDto createLabelDto, Account account);
    LabelEntity getLabel(String id);
    LabelEntity updateLabel(UpdateLabelDto updateLabelDto, Account account);
    LabelEntity deleteLabel(String id, Account account);
    LabelEntity restoreLabel(String id, Account account);
    LabelEntity updateStatus(UpdateStatusLabelDto updateStatusLabelDto, Account account);
    ResPagination<LabelEntity> getAllLabel(int pageIndex , int pageSize, String lb_name, Account account);
    ResPagination<LabelEntity> getAllLabelRecycleBin(int pageIndex , int pageSize, String lb_name, Account account);
    List<LabelEntity> getAllLabelByRestaurantId(Account account);


}
