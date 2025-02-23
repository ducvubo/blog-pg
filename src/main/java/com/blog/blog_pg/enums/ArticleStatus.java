package com.blog.blog_pg.enums;

public enum ArticleStatus {
    DRAFT, // bản nháp
    PENDING_APPROVAL, // chờ phê duyệt
    REJECTED, // bị từ chối
    PENDING_PUBLISH, // chờ xuất bản
    PUBLISH_SCHEDULE, // lên lịch xuất bản
    PUBLISHED, // đã xuất bản
    UNPUBLISHED; // không xuất bản
}
