package com.blog.blog_pg.controller;


import com.blog.blog_pg.dto.response.ApiResponse;
import com.blog.blog_pg.dto.response.UploadFile;
import com.blog.blog_pg.dto.response.UploadImage;
import com.blog.blog_pg.middleware.Account;
import com.blog.blog_pg.service.CloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/image")
    public ApiResponse<UploadImage> uploadImage(
            @RequestParam("file") MultipartFile file) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<UploadImage>builder()
                .statusCode(201)
                .message("Upload image successfully")
                .data(cloudinaryService.uploadImage(file,account))
                .build();
    }

    @PostMapping("/video")
    public ApiResponse<String> uploadVideo(
            @RequestParam("file") MultipartFile file) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<String>builder()
                .statusCode(201)
                .message("Upload video successfully")
                .data(cloudinaryService.uploadVideo(file,account))
                .build();
    }

    @PostMapping("/file")
    public UploadFile uploadFile(
            @RequestHeader("folder-name") String folderName,
            @RequestParam("file") MultipartFile file) {


        log.info("Uploading file to folder: {}", folderName);

        return cloudinaryService.uploadFile(file, folderName);
    }

}
