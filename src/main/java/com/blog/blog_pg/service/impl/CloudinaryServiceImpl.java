package com.blog.blog_pg.service.impl;

import com.blog.blog_pg.dto.response.UploadFile;
import com.blog.blog_pg.dto.response.UploadImage;
import com.blog.blog_pg.exception.BadRequestError;
import com.blog.blog_pg.middleware.Account;
import com.blog.blog_pg.service.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public UploadFile uploadFile(MultipartFile file, String folderName) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", folderName));
            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();
            return new UploadFile(url, publicId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UploadImage uploadImage(MultipartFile file, Account account) {
        try {
//            if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
//                throw new BadRequestError("Chỉ chấp nhận file ảnh!");
//            }
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", account.getAccountRestaurantId(),
                            "resource_type", "image"
                    ));

            String originalUrl = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            String editableUrl = cloudinary.url().secure(true)
                    .transformation(new com.cloudinary.Transformation()
                            .width(300)
                            .height(300)
                    )
                    .format("jpg")
                    .generate(publicId);
            return new UploadImage(originalUrl, editableUrl);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String uploadVideo(MultipartFile file,Account account) {
        try {
//            if (!Objects.requireNonNull(file.getContentType()).startsWith("video/")) {
//                throw new BadRequestError("Chỉ chấp nhận file video!");
//            }
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", account.getAccountRestaurantId(),"resource_type", "video"));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
