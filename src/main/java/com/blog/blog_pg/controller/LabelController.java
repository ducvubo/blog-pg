package com.blog.blog_pg.controller;

import com.blog.blog_pg.dto.request.label.CreateLabelDto;
import com.blog.blog_pg.dto.request.label.UpdateLabelDto;
import com.blog.blog_pg.dto.request.label.UpdateStatusLabelDto;
import com.blog.blog_pg.dto.response.ApiResponse;
import com.blog.blog_pg.dto.response.ResPagination;
import com.blog.blog_pg.entities.LabelEntity;
import com.blog.blog_pg.middleware.Account;
import com.blog.blog_pg.service.LabelService;
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
import java.util.UUID;

@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LabelController {
    @Autowired
    private LabelService labelService;

    @PostMapping
    ApiResponse<LabelEntity> createLabel(@Valid @RequestBody CreateLabelDto createLabelDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<LabelEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Create label successfully")
                .data(labelService.createLabel(createLabelDto,account))
                .build();
    }

    @PatchMapping
    ApiResponse<LabelEntity> updateLabel(@Valid @RequestBody UpdateLabelDto updateLabelDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<LabelEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Update label successfully")
                .data(labelService.updateLabel(updateLabelDto,account))
                .build();
    }

    @GetMapping
    ApiResponse<ResPagination<LabelEntity>> getLabels(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                      @RequestParam(value = "lb_name", required = false) String lb_name) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ResPagination<LabelEntity>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get labels successfully")
                .data(labelService.getAllLabel(pageIndex, pageSize, lb_name, account))
                .build();
    }

    @GetMapping("all")
    ApiResponse<List<LabelEntity>> getAllLabels() {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<List<LabelEntity>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get all labels successfully")
                .data(labelService.getAllLabelByRestaurantId(account))
                .build();
    }


    @GetMapping("recycle")
    ApiResponse<ResPagination<LabelEntity>> getRecycleLabels(@RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                            @RequestParam(value = "lb_name", required = false) String lb_name) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<ResPagination<LabelEntity>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get recycle labels successfully")
                .data(labelService.getAllLabelRecycleBin( pageIndex, pageSize, lb_name, account))
                .build();
    }

    @PatchMapping("/update-status")
    ApiResponse<LabelEntity> updateStatus(@Valid @RequestBody UpdateStatusLabelDto updateStatusLabelDto) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<LabelEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Update status label successfully")
                .data(labelService.updateStatus(updateStatusLabelDto, account))
                .build();
    }

    @PatchMapping("/restore/{lb_id}")
    ApiResponse<LabelEntity> restoreLabel(@PathVariable String lb_id) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<LabelEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Restore label successfully")
                .data(labelService.restoreLabel(lb_id, account))
                .build();
    }

    @DeleteMapping("/{lb_id}")
    ApiResponse<LabelEntity> deleteLabel(@PathVariable String lb_id) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<LabelEntity>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Delete label successfully")
                .data(labelService.deleteLabel(lb_id, account))
                .build();
    }

    @GetMapping("/{lb_id}")
    ApiResponse<LabelEntity> getLabel(@PathVariable String lb_id) {
        return ApiResponse.<LabelEntity>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get label successfully")
                .data(labelService.getLabel(lb_id))
                .build();
    }
}
