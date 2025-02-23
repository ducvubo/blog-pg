package com.blog.blog_pg.service;

import com.blog.blog_pg.dto.request.article.*;
import com.blog.blog_pg.dto.response.ResPagination;
import com.blog.blog_pg.dto.response.article.ArticleName;
import com.blog.blog_pg.entities.ArticleEntity;
import com.blog.blog_pg.middleware.Account;

import java.util.List;

public interface ArticleService {

    Boolean checkSlug(String slug, Account account);
    ArticleEntity createArticleDefault(CreateArticleDefaultDto createArticleDefaultDto, Account account);
    ArticleEntity createArticleVideo(CreateArticleVideoDto createArticleVideoDto, Account account);
    ArticleEntity createArticleDocument(CreateArticleDocumentDto createArticleDocumentDto, Account account);
    ArticleEntity createArticleImage(CreateArticleImageDto createArticleImage, Account account);
    ArticleEntity updateArticleDefault(UpdateArticleDefaultDto updateArticleDefaultDto, Account account);
    ArticleEntity updateArticleVideo(UpdateArticleVideoDto updateArticleVideoDto, Account account);
    ArticleEntity updateArticleDocument(UpdateArticleDocumentDto updateArticleDocumentDto, Account account);
    ArticleEntity updateArticleImage(UpdateArticleImageDto updateArticleImage, Account account);
    ArticleEntity sendArticle(String id, Account account);
    ArticleEntity approveArticle(String id, Account account);
    ArticleEntity rejectArticle(String id, Account account);
    ArticleEntity publishArticle(String id, Account account);
    ArticleEntity publishScheduleArticle(String id, Account account,String scheduleTime);
    ArticleEntity unpublishScheduleArticle(String id, Account account);
    ArticleEntity unpublishArticle(String id, Account account);
    ArticleEntity deleteArticleDraft(String id, Account account);
    ArticleEntity restoreArticle(String id, Account account);
    ArticleEntity getArticle(String id, Account account);
    ResPagination<ArticleEntity> getAllArticle(int pageIndex , int pageSize, String atlTitle,int isDeleted,  Account account);
    List<ArticleName> getAllArticleByRestaurantId(Account account);
}
