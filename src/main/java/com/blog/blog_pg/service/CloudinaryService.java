package com.blog.blog_pg.service;

import com.blog.blog_pg.dto.response.UploadFile;
import com.blog.blog_pg.dto.response.UploadImage;
import com.blog.blog_pg.middleware.Account;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    UploadFile uploadFile(MultipartFile file, String folderName);
    UploadImage uploadImage(MultipartFile file, Account account);
    String uploadVideo(MultipartFile file, Account account);

}
