package com.blog.blog_pg.controller;

import com.blog.blog_pg.dto.request.article.*;
import com.blog.blog_pg.dto.request.category.CreateCategoryDto;
import com.blog.blog_pg.dto.response.ApiResponse;
import com.blog.blog_pg.dto.response.ResPagination;
import com.blog.blog_pg.dto.response.article.ArticleDTO;
import com.blog.blog_pg.dto.response.article.ArticleName;
import com.blog.blog_pg.entities.ArticleEntity;
import com.blog.blog_pg.entities.CategoryEntity;
import com.blog.blog_pg.middleware.Account;
import com.blog.blog_pg.service.ArticleService;
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
@RequestMapping("/articles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/check-slug")
    ApiResponse<Boolean> checkSlug(@RequestParam(value = "slug") String slug) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<Boolean>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Check slug successfully")
                .data(!articleService.checkSlug(slug,account))
                .build();
    }

    @PostMapping("/add/default")
    ApiResponse<ArticleEntity> createArticleDefault(@Valid @RequestBody CreateArticleDefaultDto createArticleDefaultDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Create article default successfully")
                .data(articleService.createArticleDefault(createArticleDefaultDto,account))
                .build();
    }

    @PostMapping("/add/video")
    ApiResponse<ArticleEntity> createArticleVideo(@Valid @RequestBody CreateArticleVideoDto createArticleVideoDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Create article video successfully")
                .data(articleService.createArticleVideo(createArticleVideoDto,account))
                .build();
    }

    @PostMapping("/add/document")
    ApiResponse<ArticleEntity> createArticleDocument(@Valid @RequestBody CreateArticleDocumentDto createArticleDocumentDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Create article document successfully")
                .data(articleService.createArticleDocument(createArticleDocumentDto,account))
                .build();
    }

    @PostMapping("/add/image")
    ApiResponse<ArticleEntity> createArticleImage(@Valid @RequestBody CreateArticleImageDto createArticleImageDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Create article image successfully")
                .data(articleService.createArticleImage(createArticleImageDto,account))
                .build();
    }

    @PatchMapping("/update/default")
    ApiResponse<ArticleEntity> updateArticleDefault(@Valid @RequestBody UpdateArticleDefaultDto updateArticleDefaultDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Update article default successfully")
                .data(articleService.updateArticleDefault(updateArticleDefaultDto,account))
                .build();
    }

    @PatchMapping("/update/video")
    ApiResponse<ArticleEntity> updateArticleVideo(@Valid @RequestBody UpdateArticleVideoDto updateArticleVideoDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Update article video successfully")
                .data(articleService.updateArticleVideo(updateArticleVideoDto,account))
                .build();
    }

    @PatchMapping("/update/document")
    ApiResponse<ArticleEntity> updateArticleDocument(@Valid @RequestBody UpdateArticleDocumentDto updateArticleDocumentDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Update article document successfully")
                .data(articleService.updateArticleDocument(updateArticleDocumentDto,account))
                .build();
    }

    @PatchMapping("/update/image")
    ApiResponse<ArticleEntity> updateArticleImage(@Valid @RequestBody UpdateArticleImageDto updateArticleImageDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Update article image successfully")
                .data(articleService.updateArticleImage(updateArticleImageDto,account))
                .build();
    }

    @DeleteMapping("/delete-draft/{atlId}")
    ApiResponse<ArticleEntity> deleteArticle(@PathVariable String atlId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Delete article successfully")
                .data(articleService.deleteArticleDraft(atlId,account))
                .build();
    }

    @PatchMapping("/restore/{atlId}")
    ApiResponse<ArticleEntity> restoreArticle(@PathVariable String atlId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Restore article successfully")
                .data(articleService.restoreArticle(atlId,account))
                .build();
    }

    @PatchMapping("/send/{id}")
    public ApiResponse<ArticleEntity> sendArticle(@PathVariable String id) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Article sent successfully")
                .data(articleService.sendArticle(id, account))
                .build();
    }

    @PatchMapping("/approve/{id}")
    public ApiResponse<ArticleEntity> approveArticle(@PathVariable String id) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Article approved successfully")
                .data(articleService.approveArticle(id, account))
                .build();
    }

    @PatchMapping("/reject/{id}")
    public ApiResponse<ArticleEntity> rejectArticle(@PathVariable String id) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Article rejected successfully")
                .data(articleService.rejectArticle(id, account))
                .build();
    }

    @PatchMapping("/publish/{id}")
    public ApiResponse<ArticleEntity> publishArticle(@PathVariable String id) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Article published successfully")
                .data(articleService.publishArticle(id, account))
                .build();
    }

    @PatchMapping("/schedule-publish/{id}")
    public ApiResponse<ArticleEntity> publishScheduleArticle(@PathVariable String id, @RequestParam String scheduleTime) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Article scheduled for publishing successfully")
                .data(articleService.publishScheduleArticle(id, account, scheduleTime))
                .build();
    }

    @PatchMapping("/unpublish-schedule/{id}")
    public ApiResponse<ArticleEntity> unpublishScheduleArticle(@PathVariable String id) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Scheduled publishing of article canceled successfully")
                .data(articleService.unpublishScheduleArticle(id, account))
                .build();
    }

    @PatchMapping("/unpublish/{id}")
    public ApiResponse<ArticleEntity> unpublishArticle(@PathVariable String id) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Article unpublished successfully")
                .data(articleService.unpublishArticle(id, account))
                .build();
    }


    @GetMapping("/all")
    ApiResponse<ResPagination<ArticleEntity>> getAllArticle(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                           @RequestParam(value = "atlTitle", required = false) String atl_title
                                                           ) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ResPagination<ArticleEntity>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get articles successfully")
                .data(articleService.getAllArticle(pageIndex, pageSize, atl_title, 0, account))
                .build();
    }

    @GetMapping("/all-name")
    ApiResponse<List<ArticleName>> getAllArticle() {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<List<ArticleName>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get articles successfully")
                .data(articleService.getAllArticleByRestaurantId(account))
                .build();
    }

    @GetMapping("/recycle")
    ApiResponse<ResPagination<ArticleEntity>> getAllArticleRecycle(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                                    @RequestParam(value = "atlTitle", required = false) String atl_title
    ) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ResPagination<ArticleEntity>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get articles successfully")
                .data(articleService.getAllArticle(pageIndex, pageSize, atl_title, 1, account))
                .build();
    }

    @GetMapping("/{atlId}")
    ApiResponse<ArticleEntity> getArticle(@PathVariable String atlId) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ArticleEntity>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get article successfully")
                .data(articleService.getArticle(atlId,account))
                .build();
    }

    @GetMapping("/all-article-view")
    public ApiResponse<List<ArticleDTO>> getAllArticleView(@RequestParam(required = true) String resId,
                                                            @RequestParam(required = true) String catId) {
        log.info("Received resId: {}", resId);
        log.info("Received catId: {}", catId);
        return ApiResponse.<List<ArticleDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get all articles successfully")
                .data(articleService.getArticleAllView(resId,catId))
                .build();
    }

}
